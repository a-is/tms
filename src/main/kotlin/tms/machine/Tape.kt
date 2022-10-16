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

package tms.machine

private val WHITESPACE_SYMBOL: Char = '_'
private val DEFAULT_HEAD_POSITION: Int = 0

/**
 * Infinite in both directions tape of the Turing machine including the position of the head.
 *
 * Implemented based on two lists.
 */
class Tape {
    /**
     * A list for cells with a negative index (-1, -2, ...). The cell with the index `c` is stored in `_left[-(c + 1)]`.
     */
    private var _left: List<Char> = mutableListOf()

    /**
     * A list for cells with a non-negative index (0, 1, ...). The cell with the index `c` is stored in `_right[c]`.
     */
    private var _right: List<Char> = mutableListOf()

    /**
     * Head position.
     */
    var position: Int = DEFAULT_HEAD_POSITION

    /**
     * Bypassing the content part of the tape. The content part of the tape means the part of the tape enclosed between
     * two non-whitespace symbols and containing all non-whitespace symbols.
     */
    fun forEachIndexed(action: (index: Int, symbol: Char) -> Unit) {
        TODO()
    }

    /**
     * Reads the symbol from the current [position] of the tape.
     */
    fun read() = read(position)

    /**
     * Reads the symbol from the specified [position] of the tape.
     */
    fun read(position: Int): Char {
        TODO()
    }

    /**
     * Writes the [symbol] to the current [position] of the tape.
     */
    fun write(symbol: Char) = write(position, symbol)

    /**
     * Writes the symbol to the specified [position] of the tape.
     */
    fun write(position: Int, symbol: Char) {
        TODO()
    }

    /**
     * Moves the head in the specified [direction].
     */
    fun move(direction: Direction) {
        TODO()
    }
}
