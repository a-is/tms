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

private val DEFAULT_STATE: RealState = RealState("0")
private val DEFAULT_WHITESPACE: RealSymbol = RealSymbol('_')
private val DEFAULT_HEAD_POSITION: Int = 0
private val DEFAULT_END_STATES: Set<RealState> = listOf("halt", "H").map { RealState(it) }.toSet()

class MachineBuilder {
    /**
     * String representation of tape. In the [build] method, it will be converted to [Tape].
     *
     * Default: empty
     */
    private var tape: String = ""

    /**
     * The initial position of the tape head.
     */
    private var initialHeadPosition: Int = DEFAULT_HEAD_POSITION

    /**
     * The program for the machine.
     */
    private var rules: List<Rule> = listOf()

    /**
     * Initial current state of the machine.
     */
    private var initialState: RealState = DEFAULT_STATE

    /**
     * A set of states, after reaching which the execution of the program will be halted.
     */
    private var endStates: Set<RealState> = DEFAULT_END_STATES

    /**
     * Whitespace symbol.
     */
    private var whitespace: RealSymbol = DEFAULT_WHITESPACE

    fun tape(tape: String): MachineBuilder {
        this.tape = tape
        return this
    }

    fun initialHeadPosition(initialHeadPosition: Int): MachineBuilder {
        this.initialHeadPosition = initialHeadPosition
        return this
    }

    fun rules(rules: List<Rule>): MachineBuilder {
        this.rules = rules
        return this
    }

    fun initialState(initialState: RealState): MachineBuilder {
        this.initialState = initialState
        return this
    }

    fun endStates(endStates: Set<RealState>): MachineBuilder {
        this.endStates = endStates
        return this
    }

    fun whitespace(whitespace: RealSymbol): MachineBuilder {
        this.whitespace = whitespace
        return this
    }

    fun build(): Machine {
        return Machine(
            rules = rules,
            initialState = initialState,
            endStates = endStates,
            whitespace = whitespace,
            headPosition = initialHeadPosition,
            initialTapeValue = tape,
        )
    }
}
