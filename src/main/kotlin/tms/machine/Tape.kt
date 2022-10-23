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

/**
 * Auxiliary functions for converting the index of the tape to the indexes of the lists of the left and right parts.
 * And vice versa.
 *
 * Short names are chosen, since the functions are intended for internal use only. If it is necessary to make them
 * public, then refactoring is necessary.
 */
private fun cell2left(index: Int) = -(index + 1)
private fun left2cell(index: Int) = -(index + 1)
private fun cell2right(index: Int) = index
private fun right2cell(index: Int) = index

private fun <T> removeTrailingWhitespace(list: MutableList<T>, whitespace: T) {
    while (list.lastOrNull() == whitespace) {
        list.removeLast()
    }
}

private fun <T> expandAndFillWithWhitespace(list: MutableList<T>, newSize: Int, whitespace: T) {
    while (list.size < newSize) {
        list.add(whitespace)
    }
}

/**
 * Infinite in both directions tape of the Turing machine including the position of the head.
 *
 * Implemented based on two lists.
 */
class Tape(
    whitespace: RealSymbol,
    headPosition: Int,
    initialState: String = "",
) {
    /**
     * A list for cells with a negative index (-1, -2, ...). The cell with the index `c` is stored in `_left[-(c + 1)]`,
     * see [cell2left] and [left2cell].
     */
    private val _left: MutableList<RealSymbol> = mutableListOf()

    /**
     * A list for cells with a non-negative index (0, 1, ...). The cell with the index `c` is stored in `_right[c]`,
     * see [cell2right] and [right2cell].
     */
    private val _right: MutableList<RealSymbol> = initialState.map { RealSymbol(it) }.toMutableList()

    /**
     * Head position.
     */
    var position: Int = headPosition
        private set

    /**
     * Whitespace symbol.
     */
    private val whitespace: RealSymbol = whitespace

    /**
     * The left border of the content part of the tape.
     * The "content part" of the tape is the smallest subset of the tape containing all non-whitespace characters.
     */
    val leftmost: Int
        get() = left2cell(_left.size - 1)

    /**
     * The right border of the content part of the tape.
     * The "content part" of the tape is the smallest subset of the tape containing all non-whitespace characters.
     */
    val rightmost: Int
        get() = right2cell(_right.size - 1)

    /**
     * Bypassing the content part of the tape.
     * The "content part" of the tape is the smallest subset of the tape containing all non-whitespace characters.
     */
    fun forEachIndexed(action: (index: Int, symbol: RealSymbol) -> Unit) {
        for (i in _left.size - 1 downTo 0) {
            action(left2cell(i), _left[i])
        }

        for (i in 0 until _right.size) {
            action(right2cell(i), _right[i])
        }
    }

    /**
     * Reads the symbol from the current [position] of the tape.
     */
    fun read() = read(position)

    /**
     * Reads the symbol from the specified [position] of the tape.
     */
    fun read(position: Int): RealSymbol {
        return if (position >= 0) {
            _right.getOrElse(cell2right(position)) { whitespace }
        } else {
            _left.getOrElse(cell2left(position)) { whitespace }
        }
    }

    /**
     * Writes the [symbol] to the current [position] of the tape.
     */
    fun write(symbol: RealSymbol) = write(position, symbol)

    /**
     * Writes the symbol to the specified [position] of the tape.
     */
    fun write(position: Int, symbol: RealSymbol) {
        val writeImpl = fun(list: MutableList<RealSymbol>, index: Int, symbol: RealSymbol) {
            expandAndFillWithWhitespace(list, index + 1, whitespace)
            list[index] = symbol
            removeTrailingWhitespace(list, whitespace)
        }

        if (position >= 0) {
            val index = cell2right(position)
            writeImpl(_right, index, symbol)
        } else {
            val index = cell2left(position)
            writeImpl(_left, index, symbol)
        }
    }

    /**
     * Moves the head in the specified [direction].
     */
    fun move(direction: Direction) {
        position += direction.offset
    }
}
