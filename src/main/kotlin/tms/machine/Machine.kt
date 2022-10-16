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
private val DEFAULT_BREAK_STATES: Set<String> = setOf()
private val DEFAULT_END_STATES: Set<String> = setOf("halt", "H")

/**
 * The executor of the Turing machine.
 */
class Machine {
    /**
     * Tape. Also contains the position of the head.
     */
    var tape: Tape = Tape()

    /**
     * The program for the machine. It is a mapping of [RuleTrigger] to [Rule].
     * To add a new rule, use the [add] function.
     */
    var rules: MutableMap<RuleTrigger, Rule> = mutableMapOf()

    /**
     * The current state of the machine. Use this field to set the initial state.
     */
    var state: String = DEFAULT_STATE

    /**
     * A set of states, after reaching which execution of the program will be suspended.
     *
     * Note that it only affects the [run] function. The [step] function will execute the step even if the current state
     * is contained in [breakStates].
     */
    var breakStates: MutableSet<String> = DEFAULT_BREAK_STATES.toMutableSet()

    /**
     * A set of states, after reaching which the execution of the program will be halted.
     */
    var endStates: MutableSet<String> = DEFAULT_END_STATES.toMutableSet()

    /**
     * The wildcard character is used for the simplicity of setting rules. If a wildcard character is specified instead
     * of the current character, then any character fits this rule. For the new state and new symbol, the wildcard
     * symbol means "the state/symbol does not change".
     */
    var wildcard: Char = DEFAULT_WILDCARD

    /**
     * Adds a new rule to the machine program ([rules]).
     */
    fun MutableMap<RuleTrigger, Rule>.add(rule: Rule) {
        this[rule.trigger] = rule
    }

    /**
     * Has the machine reached one of the [endStates].
     */
    fun isHalted(): Boolean = state in endStates

    /**
     * Has the machine reached one of the [breakStates].
     */
    fun isInterrupted(): Boolean = state in breakStates

    private fun nextRule(): Rule {
        TODO()
    }

    /**
     * Perform one step.
     *
     * Note that if the machine is already halted (see [isHalted]), nothing will happen.
     */
    fun step() {
        if (isHalted()) {
            return
        }

        TODO()
    }

    /**
     * Execute the steps either until one of the states contained in [breakStates] or [endStates] is reached.
     *
     * Note that the states from [breakStates] are not taken into account when performing the first step inside this
     * function (the implementation is based on a do-while loop). If the machine is already halted (see [isHalted]),
     * nothing will happen.
     */
    fun run() {
        do {
            step()
        } while (!isInterrupted())
    }
}
