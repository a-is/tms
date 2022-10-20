/*
 * TMS: Turing machine simulator
 * Copyright (C) 2022, Alexey Ismagilov.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package tms.reader

import tms.machine.Direction
import tms.machine.Machine
import tms.machine.MachineBuilder
import tms.machine.Rule
import tms.util.Token
import tms.util.tokenize
import java.io.File

private fun String.removeComment(delimiter: Char = ';'): String {
    return this.split(delimiter, limit = 2)[0]
}

private fun <T> List<T>.second(): T {
    return this[1]
}

private fun <T> List<T>.third(): T {
    return this[2]
}

private val DIRECTION_MAP = mapOf(
    "l" to Direction.LEFT,
    "*" to Direction.STAY,
    "r" to Direction.RIGHT
)

private data class FieldParserInfo<T>(
    val validate: (String) -> Boolean,
    val getValue: (String) -> T,
    val errorMessage: (fieldName: String) -> String,
)

private val STATE_PARSER_INFO = FieldParserInfo(
    { true },
    { it },
    { error("This function should never be called") }
)

private val SYMBOL_PARSER_INFO = FieldParserInfo(
    { it.length == 1 },
    { it[0] },
    { name -> "$name should be a single character" }
)

private val DIRECTION_PARSER_INFO = FieldParserInfo(
    { it.lowercase() in DIRECTION_MAP.keys },
    { DIRECTION_MAP[it.lowercase()]!! },
    { name -> "$name should be in ${DIRECTION_MAP.keys}" }
)

class TextReader(
    private val path: String
) {
    /**
     * String representation of tape. In the [build] method, it will be converted to tape.
     *
     * If null, the default value from [MachineBuilder] class is used.
     */
    private var tape: String? = null

    /**
     * The initial position of the tape head.
     */
    private var initialHeadPosition: Int? = null

    /**
     * The program for the machine.
     */
    private var rules: MutableList<Rule> = mutableListOf()

    /**
     * Initial current state of the machine.
     */
    private var initialState: String? = null

    /**
     * A set of states, after reaching which the execution of the program will be halted.
     */
    private var endStates: MutableSet<String> = mutableSetOf()

    /**
     * The wildcard character is used for the simplicity of setting rules.
     */
    private var wildcard: Char? = null

    /**
     * Whitespace symbol.
     */
    private var whitespace: Char? = null

    private var _errors: MutableList<Error> = mutableListOf()

    /**
     * List of errors that occurred when reading the file
     */
    val errors: List<Error>
        get() = _errors

    /**
     * `true` if there are no errors in the file.
     */
    val success: Boolean
        get() = errors.isEmpty()

    /**
     * These variables are used in all string processing methods, so as not to drag them into each method,
     * it was decided to put them in "global" variables.
     */
    private var splited: List<Token> = listOf()
    private var line: String = ""
    private var lineNo: Int = -1

    private fun addSyntaxError(startEnd: Int, message: String, note: String? = null)
            = addSyntaxError(startEnd, startEnd, message, note)

    private fun addSyntaxError(start: Int, end: Int, message: String, note: String? = null) {
        val error = SyntaxError(path, line, lineNo, start, end, message, note)
        _errors.add(error)
    }

    private fun checkMissingArgs(
        argNames: String,
        note: String? = null
    ): Boolean {
        if (splited.size == 1) {
            val startEnd = splited.first().end + 1
            addSyntaxError(startEnd, "missing $argNames", note)
            return false
        }

        return true
    }

    /**
     * Checking the number of arguments of a single argument function
     */
    private fun checkArgumentSingle(
        argName: String,
        note: String? = null
    ): Boolean {
        if (!checkMissingArgs(argName, note)) {
            return false
        }

        if (splited.size != 2) {
            addSyntaxError(splited.third().start, splited.last().end, "extra arguments", note)
            return false
        }

        return true
    }

    private fun processTape() {
        if (!checkArgumentSingle("tape", "to specify an empty tape, just don't use the TAPE keyword")) {
            return
        }

        val start = splited.second().start
        val end = splited.last().end

        tape = line.substring(start, end)
    }

    private fun processHead() {
        if (!checkArgumentSingle("head position")) {
            return
        }

        val token = splited.second()

        try {
            initialHeadPosition = token.value.toInt()
        } catch (e: NumberFormatException) {
            addSyntaxError(token.start, token.end, "the position of the head must be an integer")
        }
    }

    private fun processState() {
        if (!checkArgumentSingle("state")) {
            return
        }

        initialState = splited.second().value
    }

    private fun processHalt() {
        if (!checkMissingArgs("halt states")) {
            return
        }

        endStates += splited.drop(1).map { it.value }
    }

    private fun processWildcard() {
        if (!checkArgumentSingle("wildcard")) {
            return
        }

        val token = splited.second()

        if (token.value.length != 1) {
            addSyntaxError(token.start, token.end, "wildcard should be a single character")
        }

        wildcard = token.value.first()
    }

    private fun processWhitespace() {
        if (!checkArgumentSingle("whitespace")) {
            return
        }

        val token = splited.second()

        if (token.value.length != 1) {
            addSyntaxError(token.start, token.end, "whitespace should be a single character")
        }

        whitespace = token.value.first()
    }

    private fun <T> parseField(fieldName: String, fieldParserInfo: FieldParserInfo<T>, position: Int): T? {
        if (splited.size <= position) {
            val index = splited.getOrNull(position - 1)?.end ?: 0
            val message = "missing $fieldName at poition $position"

            addSyntaxError(index, message)

            return null
        }

        val representation = splited[position].value

        if (!fieldParserInfo.validate(representation)) {
            val message = fieldParserInfo.errorMessage(fieldName)
            addSyntaxError(splited[position].start, splited[position].end, message)
            return null
        }

        return fieldParserInfo.getValue(representation)
    }

    private fun processRule(firstIndex: Int) {
        var index = firstIndex

        val currentState = parseField("CURRENT_STATE", STATE_PARSER_INFO, index++) ?: return
        val currentSymbol = parseField("CURRENT_SYMBOL", SYMBOL_PARSER_INFO, index++) ?: return
        val newSymbol = parseField("NEW_SYMBOL", SYMBOL_PARSER_INFO, index++) ?: return
        val newState = parseField("NEW_STATE", STATE_PARSER_INFO, index++) ?: return
        val direction = parseField("DIRECTION", DIRECTION_PARSER_INFO, index++) ?: return

        if (splited.size > index) {
            val message = "too many entries, require: $index, actual ${splited.size}"
            addSyntaxError(splited[index].start, splited.last().end, message)
        }

        val rule = Rule(currentState, currentSymbol, newState, newSymbol, direction)

        rules.add(rule)
    }

    private fun parseLine() {
        splited = line.removeComment().tokenize()

        if (splited.isEmpty()) {
            return
        }

        when(splited[0].value) {
            "TAPE" -> processTape()
            "HEAD" -> processHead()
            "STATE" -> processState()
            "HALT" -> processHalt()
            "WILDCARD" -> processWildcard()
            "WHITESPACE" -> processWhitespace()
            "RULE" -> processRule(1)
            else -> processRule(0)
        }
    }

    fun read() {
        try {
            File(path).useLines { lines ->
                lines.forEachIndexed { index, _line ->
                    line = _line
                    lineNo = index + 1
                    parseLine()
                }
            }
        } catch (e: java.io.FileNotFoundException) {
            _errors.add(FileNotFoundError("No such file or directory: \"$path\""))
        }
    }

    fun buildMachine(): Machine {
        val builder = MachineBuilder()

        if (!success) {
            return builder.build()
        }

        builder.rules(rules)

        if (tape != null) {
            builder.tape(tape!!)
        }

        if (initialHeadPosition != null) {
            builder.initialHeadPosition(initialHeadPosition!!)
        }

        if (initialState != null) {
            builder.initialState(initialState!!)
        }

        if (endStates.isNotEmpty()) {
            builder.endStates(endStates)
        }

        if (wildcard != null) {
            builder.wildcard(wildcard!!)
        }

        if (whitespace != null) {
            builder.wildcard(whitespace!!)
        }

        return builder.build()
    }
}
