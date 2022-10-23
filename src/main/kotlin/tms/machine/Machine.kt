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
    rules: List<Rule>,
    initialState: RealState,
    endStates: Set<RealState>,
    whitespace: RealSymbol,
    headPosition: Int,
    initialTapeValue: String = "",
) {
    /**
     * Tape. Also contains the position of the head.
     */
    val tape: Tape = Tape(whitespace, headPosition, initialTapeValue)

    /**
     * The program for the machine. It is a mapping of [RuleTrigger] to [Rule].
     * To add a new rule, use the [add] function.
     */
    private val rules: Map<Rule.Trigger, Rule> = rules.associateBy { it.trigger }

    /**
     * The current state of the machine. Use this field to set the initial state.
     */
    var state: RealState = initialState
        private set

    /**
     * All possible states.
     */
    val allPossibleStates: Set<RealState>

    init {
        val states = mutableSetOf<RealState>()

        val add = fun(state: State) {
            if (state is RealState) {
                states.add(state)
            }
        }

        for (rule in rules) {
            add(rule.trigger.state)
            add(rule.action.state)
        }

        allPossibleStates = states
    }

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
    val breakStates: MutableSet<RealState> = mutableSetOf()

    /**
     * A set of states, after reaching which the execution of the program will be halted.
     */
    private val endStates: Set<RealState> = endStates

    /**
     * Whitespace symbol.
     */
    private val whitespace: RealSymbol = whitespace

    /**
     * Verbosity
     */
    var verbose: Boolean = true

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
        val trigger = Rule.Trigger(state, symbol)

        var rule = rules[trigger]

        if (rule == null) {
            rule = rules[Rule.Trigger(state, WildcardSymbol)]
        }

        if (rule == null) {
            throw RuleNotFoundException(trigger)
        }

        return rule.replaceWildcard(symbol)
    }

    private fun stepImpl() {
        if (isHalted()) {
            return
        }

        val rule = nextRule()

        tape.write(rule.action.symbol as RealSymbol)
        tape.move(rule.action.direction)
        state = rule.action.state as RealState

        step++
    }

    /**
     * Perform one step.
     *
     * Note that if the machine is already halted (see [isHalted]), nothing will happen.
     */
    fun step() {
        stepImpl()

        if (verbose) {
            prettyPrint(this)
        }
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
            stepImpl()
        } while (!isInterrupted() && !isHalted())

        if (verbose) {
            prettyPrint(this)
        }
    }

    private fun characterCountPrint() {
        val counts = mutableMapOf<RealSymbol, Int>()

        for (position in tape.leftmost..tape.rightmost) {
            val symbol = tape.read(position)

            if (!symbol.isPrintable() || symbol == whitespace) {
                continue
            }

            val count = counts.getOrDefault(symbol, 0)

            counts[symbol] = count + 1
        }

        println("Symbols count: $counts")
    }

    fun printDetailedInfo() {
        prettyPrint(tape)

        try {
            val nextRule = nextRule()
            print("Next rule: ")
            prettyPrint(nextRule)
        } catch (e: RuleNotFoundException) {
            // Ignore
        }

        characterCountPrint()

        println("Step: $step")
        println("State: $state")
        println("Symbol: \'${tape.read()}\'")
        println("Break states: $breakStates")
        println("End states: $endStates")
        println("Whitespace: \'$whitespace\'")
        println("Interrupted: ${isInterrupted()}")
        println("Halted: ${isHalted()}")
    }
}
