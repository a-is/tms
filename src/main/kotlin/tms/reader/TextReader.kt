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
import tms.machine.Rule
import java.io.File

/* Order matters */
private enum class Fields{
    CURRENT_STATE,
    CURRENT_SYMBOL,
    NEW_SYMBOL,
    DIRECTION,
    NEW_STATE
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

private class LineParser(line: String) {
    private val lineContent = line.removeComment().split(' ').filter { it.isNotEmpty() }

    private fun String.removeComment(delimiter: Char = ';'): String {
        return this.split(delimiter, limit = 2)[0]
    }

    private fun <T> parseField(
        field: Fields,
        fieldParser: FieldParserInfo<T>,
    ): T {
        val representation = lineContent.getOrNull(field.ordinal)
            ?: throw ReaderException("missing ${field.name} at position ${field.ordinal + 1}")

        if (!fieldParser.validate(representation)) {
            val message = fieldParser.errorMessage(field.name)
            throw ReaderException("$message, actual: $representation")
        }

        return fieldParser.getValue(representation)
    }

    private val _fieldsLazyParsers = mutableMapOf<Fields, Lazy<Any?>>()

    private fun <T> lazyParseField(
        field: Fields,
        fieldParser: FieldParserInfo<T>,
    ): Lazy<T> {
        val lazyInitializer = lazy { parseField(field, fieldParser) }
        _fieldsLazyParsers[field] = lazyInitializer
        return lazyInitializer
    }

    private fun parseFieldsInCorrectOrder() {
        for (field in Fields.values()) {
            _fieldsLazyParsers[field]?.value
        }
    }

    fun parse(): Rule? {
        if (lineContent.isEmpty()) {
            /* Blank or comment line */
            return null
        }

        val currentState by lazyParseField(Fields.CURRENT_STATE, STATE_PARSER_INFO)
        val currentSymbol by lazyParseField(Fields.CURRENT_SYMBOL, SYMBOL_PARSER_INFO)
        val newState by lazyParseField(Fields.NEW_STATE, STATE_PARSER_INFO)
        val newSymbol by lazyParseField(Fields.NEW_SYMBOL, SYMBOL_PARSER_INFO)
        val direction by lazyParseField(Fields.DIRECTION, DIRECTION_PARSER_INFO)

        parseFieldsInCorrectOrder()

        if (lineContent.size > Fields.values().size) {
            throw ReaderException("too many entries, require: ${Fields.values().size}, actual ${lineContent.size}")
        }

        return Rule(
            currentState = currentState,
            currentSymbol = currentSymbol,
            newState = newState,
            newSymbol = newSymbol,
            direction = direction
        )
    }
}

private fun parseLine(line: String): Rule? = LineParser(line).parse()

class TextReader : Reader {
    override fun read(path: String): Result<List<Rule>> {
        val validLineTemplate
            = "Each line should contain one tuple of the form: " + Fields.values().joinToString(" ")

        return try {
            File(path).useLines { lines ->
                Result.success(lines
                    .mapIndexed { index, s ->
                        try {
                            parseLine(s)
                        } catch (e: ReaderException) {
                            throw ReaderException("$path: ${index + 1}: ${e.message}\n$validLineTemplate")
                        }
                    }
                    .filterNotNull()
                    .toList()
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
