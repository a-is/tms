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

package tms.console

interface Command {
    /**
     * The name of the command. Used to identify the command. Must be unique.
     */
    val name: String

    /**
     * Description of the command that is used in the `help` command.
     */
    val description: String

    /**
     * Command arguments.
     */
    val arguments: List<CommandArgument>

    /**
     * Execute the program. The [args] list contains the name of the command (the same as the [name]).
     */
    fun execute(args: List<String>): String
}
