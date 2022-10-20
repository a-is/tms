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

    private fun checkMissingArgs(
        splited: List<Token>,
        line: String,
        lineNo: Int,
        argNames: String,
        note: String? = null
    ): Boolean {
        val startEnd = splited.first().end + 1
        if (splited.size == 1) {
            _errors.add(SyntaxError(
                path, line, lineNo,
                startEnd, startEnd,
                "missing $argNames", note
            ))
            return false
        }

        return true
    }

    /**
     * Checking the number of arguments of a single argument function
     */
    private fun checkArgumentSingle(
        splited: List<Token>,
        line: String,
        lineNo: Int,
        argName: String,
        note: String? = null
    ): Boolean {
        if (!checkMissingArgs(splited, line, lineNo, argName, note)) {
            return false
        }

        if (splited.size != 2) {
            _errors.add(SyntaxError(
                path, line, lineNo,
                splited.third().start, splited.last().end,
                "extra arguments", note
            ))
            return false
        }

        return true
    }

    private fun processTape(splited: List<Token>, line: String, lineNo: Int) {
        if (!checkArgumentSingle(splited, line, lineNo, "tape",
                                 "to specify an empty tape, just don't use the TAPE keyword")) {
            return
        }

        val start = splited.second().start
        val end = splited.last().end

        tape = line.substring(start, end)
    }

    private fun processHead(splited: List<Token>, line: String, lineNo: Int) {
        if (!checkArgumentSingle(splited, line, lineNo, "head position")) {
            return
        }

        val token = splited.second().token

        try {
            initialHeadPosition = token.toInt()
        } catch (e: NumberFormatException) {
            _errors.add(SyntaxError(
                path, line, lineNo,
                splited.second().start, splited.second().end,
                "the position of the head must be an integer"
            ))
        }
    }

    private fun processState(splited: List<Token>, line: String, lineNo: Int) {
        if (!checkArgumentSingle(splited, line, lineNo, "state")) {
            return
        }

        initialState = splited.second().token
    }

    private fun processHalt(splited: List<Token>, line: String, lineNo: Int) {
        if (!checkMissingArgs(splited, line, lineNo, "halt states")) {
            return
        }

        endStates += splited.drop(1).map { it.token }
    }

    private fun processWildcard(splited: List<Token>, line: String, lineNo: Int) {
        if (!checkArgumentSingle(splited, line, lineNo, "wildcard")) {
            return
        }

        val token = splited.second().token

        if (token.length != 1) {
            _errors.add(SyntaxError(
                path, line, lineNo,
                splited.second().start, splited.second().end,
                "wildcard should be a single character"
            ))
        }

        wildcard = token.first()
    }

    private fun processWhitespace(splited: List<Token>, line: String, lineNo: Int) {
        if (!checkArgumentSingle(splited, line, lineNo, "whitespace")) {
            return
        }

        val token = splited.second().token

        if (token.length != 1) {
            _errors.add(SyntaxError(
                path, line, lineNo,
                splited.second().start, splited.second().end,
                "whitespace should be a single character"
            ))
        }

        whitespace = token.first()
    }

    private fun <T> parseField(
        splited: List<Token>,
        line: String,
        lineNo: Int,
        fieldName: String,
        fieldParserInfo: FieldParserInfo<T>,
        position: Int
    ): T? {
        if (splited.size <= position) {
            val index = splited.getOrNull(position - 1)?.end ?: 0
            val message = "missing $fieldName at poition $position"

            _errors.add(SyntaxError(path, line, lineNo, index, index, message))

            return null
        }

        val representation = splited[position].token

        if (!fieldParserInfo.validate(representation)) {
            val message = fieldParserInfo.errorMessage(fieldName)
            _errors.add(SyntaxError(path, line, lineNo, splited[position].start, splited[position].end, message))
            return null
        }

        return fieldParserInfo.getValue(representation)
    }

    private fun processRule(splited: List<Token>, line: String, lineNo: Int, firstIndex: Int) {
        var index = firstIndex

        val currentState = parseField(splited, line, lineNo, "CURRENT_STATE", STATE_PARSER_INFO, index++) ?: return
        val currentSymbol = parseField(splited, line, lineNo, "CURRENT_SYMBOL", SYMBOL_PARSER_INFO, index++) ?: return
        val newSymbol = parseField(splited, line, lineNo, "NEW_SYMBOL", SYMBOL_PARSER_INFO, index++) ?: return
        val newState = parseField(splited, line, lineNo, "NEW_STATE", STATE_PARSER_INFO, index++) ?: return
        val direction = parseField(splited, line, lineNo, "DIRECTION", DIRECTION_PARSER_INFO, index++) ?: return

        if (splited.size > index) {
            val message = "too many entries, require: $index, actual ${splited.size}"
            _errors.add(SyntaxError(path, line, lineNo, splited[index].start, splited.last().end, message))
        }

        val rule = Rule(currentState, currentSymbol, newState, newSymbol, direction)

        rules.add(rule)
    }

    private fun parseLine(line: String, lineNo: Int) {
        val splited = line.removeComment().tokenize()

        if (splited.isEmpty()) {
            return
        }

        when(splited[0].token) {
            "TAPE" -> processTape(splited, line, lineNo)
            "HEAD" -> processHead(splited, line, lineNo)
            "STATE" -> processState(splited, line, lineNo)
            "HALT" -> processHalt(splited, line, lineNo)
            "WILDCARD" -> processWildcard(splited, line, lineNo)
            "WHITESPACE" -> processWhitespace(splited, line, lineNo)
            "RULE" -> processRule(splited, line, lineNo, 1)
            else -> processRule(splited, line, lineNo, 0)
        }
    }

    fun read() {
        File(path).useLines { lines ->
            lines.forEachIndexed { index, line -> parseLine(line, index + 1) }
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
