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

package tms.util

data class Token(
    val value: String,
    val start: Int,
) {
    val end
        get() = start + value.length
}

fun String.tokenize(): List<Token> {
    val result = mutableListOf<Token>()

    var index = 0

    while (true) {
        while (this.getOrNull(index)?.isWhitespace() == true) {
            index++
        }

        if (index >= this.length) {
            break
        }

        val start = index

        while (this.getOrNull(index)?.isWhitespace() == false) {
            index++
        }

        result.add(Token(this.substring(start, index), start))
    }

    return result
}
