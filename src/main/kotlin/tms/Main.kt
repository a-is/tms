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

package tms

import tms.machine.Direction
import tms.machine.Machine
import tms.machine.Rule

fun ruleFromString(s: String): Rule {
    val (state, symbol, newSymbol, direction, newState) = s.toCharArray()

    val dir = when(direction.lowercaseChar()) {
        'l' -> Direction.LEFT
        'r' -> Direction.RIGHT
        else -> Direction.STAY
    }

    return Rule(state.toString(), symbol, newState.toString(), newSymbol, dir)
}

/**
 * 4 state busy beaver.
 */
fun busyBeaver4() {
    val machine = Machine()

    machine.state = "a"
    machine.whitespace = '0'

    val rules = listOf(
        "a01rb",
        "a11lb",
        "b01la",
        "b10lc",
        "c01rH",
        "c11ld",
        "d01rd",
        "d10ra",
    )

    for (rule in rules) {
        machine.addRule(ruleFromString(rule))
    }

    machine.run()

    var ones = 0

    machine.tape.forEachIndexed { _, symbol ->
        if (symbol == '1')
            ones++
    }

    println("Ones: $ones")
    println("Steps: ${machine.step}")
}

fun main() {
    busyBeaver4()
}
