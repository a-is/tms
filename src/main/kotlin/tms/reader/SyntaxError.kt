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

class SyntaxError(
    private val filename: String,
    private val line: String,
    private val lineNo: Int,
    private val start: Int,
    private val end: Int,
    private val message: String,
    private val note: String? = null,
) : Error() {
    /**
     * gcc like error message
     *
     * Example:
     * ```
     * machine.txt:12:5: CURRENT_STATE should be a single character
     *   12 | left 0042 _ * *
     *      |      ^~~~
     * ```
     */
    override fun toString(): String {
        val builder = StringBuilder()

        val formattedLineNo = "%4d".format(lineNo)

        builder
            .append("$filename:$lineNo:$start: $message\n")

        if (note != null) {
            builder
                .append("note: $note\n")
        }

        builder
            .append("$formattedLineNo | $line\n")

        var tildesCount = (end - start - 1).coerceAtLeast(0)

        builder
            .append("     | ")
            .append(" ".repeat(start))
            .append('^')
            .append("~".repeat(tildesCount))
            .append('\n')

        return builder.toString()
    }
}
