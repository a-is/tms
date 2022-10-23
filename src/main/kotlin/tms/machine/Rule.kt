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

data class Rule(
    val trigger: RuleTrigger,
    val action: RuleAction,
) {
    constructor(
        currentState: State,
        currentSymbol: Symbol,
        newState: State,
        newSymbol: Symbol,
        direction: Direction,
    ) : this(
        RuleTrigger(currentState, currentSymbol),
        RuleAction(newState, newSymbol, direction),
    )

    fun replaceWildcard(symbol: RealSymbol): Rule {
        var (currentState, currentSymbol) = trigger
        var (newState, newSymbol, direction) = action

        if (currentSymbol is WildcardSymbol) {
            currentSymbol = symbol
        }

        if (newState is WildcardState) {
            newState = currentState
        }

        if (newSymbol is WildcardSymbol) {
            newSymbol = currentSymbol
        }

        return Rule(
            currentState,
            currentSymbol,
            newState,
            newSymbol,
            direction,
        )
    }
}
