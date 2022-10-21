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


private val SCREEN_WIDTH: Int = 80
private val LEGEND_STEP: Int = 10

private fun StringBuilder.fillUntil(length: Int, fill: Char = ' ') {
    while (this.length < length) {
        this.append(fill)
    }
}

fun prettyPrint(tape: Tape) {
    /**
     * ```
     * line1:     0                   10    | < position numbers, multiples of LEGEND_STEP
     * line2:     |                   |     |
     * line3: 0 0 0 0 0 0 1 2 3 0 0 0 0 0 0 | < tape
     * line4:             |                 |
     * line5:             4                 | < current position
     * ```
     */
    val line1 = StringBuilder()
    val line2 = StringBuilder()
    val line3 = StringBuilder()
    val line4 = StringBuilder()
    val line5 = StringBuilder()

    var anchor = -1

    val leftmost = tape.position - SCREEN_WIDTH / 2 - LEGEND_STEP
    val rightmost = tape.position + SCREEN_WIDTH / 2 + 1

    for (position in leftmost..rightmost) {
        if (position % LEGEND_STEP == 0) {
            line1.append(position)
            line2.append('|')
        }

        if (position == tape.position) {
            line4.append('|')
            line5.append(position)
            anchor = line3.length
        }

        line3.append(tape.read(position))

        val n = line3.length

        line1.fillUntil(n)
        line2.fillUntil(n)

        line4.fillUntil(n)
        line5.fillUntil(n)
    }

    val mid = SCREEN_WIDTH / 2

    val resultLine = { line: StringBuilder ->
        line.substring(anchor - mid, anchor) + line.substring(anchor, anchor + SCREEN_WIDTH - mid) + '\n'
    }

    val result = StringBuilder()
        .append(resultLine(line1))
        .append(resultLine(line2))
        .append(resultLine(line3))
        .append(resultLine(line4))
        .append(resultLine(line5))
        .toString()

    print(result)
}

fun prettyPrintStepState(machine: Machine) {
    val step = "Step: ${machine.step}"
    val state = "State: ${machine.state}"

    print(step)
    print(" ".repeat(SCREEN_WIDTH - step.length - state.length))
    println(state)
}

fun prettyPrint(machine: Machine) {
    prettyPrint(machine.tape)
    prettyPrintStepState(machine)
}
