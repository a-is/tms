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
 * The executor of the Turing machine.
 */
class Machine(
    tape: Tape,
    rules: List<Rule>,
    initialState: String,
    endStates: Set<String>,
    wildcard: Char,
    whitespace: Char,
) {
    /**
     * Tape. Also contains the position of the head.
     */
    val tape: Tape = tape

    /**
     * The program for the machine. It is a mapping of [RuleTrigger] to [Rule].
     * To add a new rule, use the [add] function.
     */
    private val rules: Map<RuleTrigger, Rule> = rules.associateBy { it.trigger }

    /**
     * The current state of the machine. Use this field to set the initial state.
     */
    var state: String = initialState
        private set

    /**
     * Current step number.
     */
    var step: Int = 0
        private set

    /**
     * A set of states, after reaching which execution of the program will be suspended.
     *
     * Note that it only affects the [run] function. The [step] function will execute the step even if the current state
     * is contained in [breakStates].
     */
    val breakStates: MutableSet<String> = mutableSetOf()

    /**
     * A set of states, after reaching which the execution of the program will be halted.
     */
    private val endStates: Set<String> = endStates

    /**
     * The wildcard character is used for the simplicity of setting rules. If a wildcard character is specified instead
     * of the current character, then any character fits this rule. For the new state and new symbol, the wildcard
     * symbol means "the state/symbol does not change".
     */
    private val wildcard: Char = wildcard

    /**
     * Whitespace symbol.
     */
    private val whitespace: Char = whitespace

    /**
     * Has the machine reached one of the [endStates].
     */
    fun isHalted(): Boolean = state in endStates

    /**
     * Has the machine reached one of the [breakStates].
     */
    fun isInterrupted(): Boolean = state in breakStates

    private fun nextRule(): Rule {
        val symbol = tape.read()
        val trigger = RuleTrigger(state, symbol)

        var rule = rules[trigger]

        if (rule == null) {
            rule = rules[RuleTrigger(state, wildcard)]
        }

        if (rule == null) {
            throw RuleNotFoundException(trigger)
        }

        return rule
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

        val rule = nextRule()

        tape.write(rule.action.symbol)
        tape.move(rule.action.direction)
        state = rule.action.state

        step++
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
        } while (!isInterrupted() && !isHalted())
    }
}
