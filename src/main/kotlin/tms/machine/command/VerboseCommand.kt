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

package tms.machine.command

import Wrapper
import org.jline.reader.impl.completer.StringsCompleter
import tms.console.Command
import tms.console.CommandArgument
import tms.machine.Machine

class VerboseCommand(
    private val machine: Wrapper<Machine>
) : Command {
    override val name: String = "verbose"

    override val description: String = "change verbosity"

    override val arguments: List<CommandArgument> = listOf(
        CommandArgument("status", "parameter value (ON or OFF)", StringsCompleter("ON", "OFF"))
    )

    override fun execute(args: List<String>) {
        when (args[1]) {
            "ON" -> machine.value.verbose = true
            "OFF" -> machine.value.verbose = false
            else -> {}
        }
    }
}
