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

private val DEFAULT_WILDCARD: Char = '*'
private val DEFAULT_STATE: String = "0"
private val DEFAULT_WHITESPACE: Char = '_'
private val DEFAULT_HEAD_POSITION: Int = 0
private val DEFAULT_END_STATES: Set<String> = setOf("halt", "H")

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
    private var initialState: String = DEFAULT_STATE

    /**
     * A set of states, after reaching which the execution of the program will be halted.
     */
    private var endStates: Set<String> = DEFAULT_END_STATES

    /**
     * The wildcard character is used for the simplicity of setting rules.
     */
    private var wildcard: Char = DEFAULT_WILDCARD

    /**
     * Whitespace symbol.
     */
    private var whitespace: Char = DEFAULT_WHITESPACE

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

    fun initialState(initialState: String): MachineBuilder {
        this.initialState = initialState
        return this
    }

    fun endStates(endStates: Set<String>): MachineBuilder {
        this.endStates = endStates
        return this
    }

    fun wildcard(wildcard: Char): MachineBuilder {
        this.wildcard = wildcard
        return this
    }

    fun whitespace(whitespace: Char): MachineBuilder {
        this.whitespace = whitespace
        return this
    }

    fun build(): Machine {
        return Machine(
            rules = rules,
            initialState = initialState,
            endStates = endStates,
            wildcard = wildcard,
            whitespace = whitespace,
            headPosition = initialHeadPosition,
            initialTapeValue = tape,
        )
    }
}
