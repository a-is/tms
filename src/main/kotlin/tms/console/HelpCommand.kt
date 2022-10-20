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

import org.jline.reader.impl.completer.StringsCompleter

private val OFFSET: String = "    "

class HelpCommand(
    commands: List<Command>
) : Command {
    override val name: String = "help"

    override val description: String = "print help for commands"

    override val arguments: List<CommandArgument>

    init {
        val sortedCommands = (commands + this).map { it.name }.sorted()
        val argument = CommandArgument("cmd", "command name", StringsCompleter(sortedCommands))
        arguments = listOf(argument)
    }

    private val helpMessage: String

    init {
        val sortedCommands = (commands + this).sortedBy { it.name }

        val helpBuilder = StringBuilder()

        helpBuilder.append("Commands:\n")

        val maxCommandLength = sortedCommands.maxOfOrNull { it.name.length } ?: 0

        for (command in sortedCommands) {
            val commandFormattedName = String.format("%-${maxCommandLength}s", command.name)

            helpBuilder
                .append(OFFSET)
                .append(commandFormattedName)
                .append(" - ")
                .append(command.description)
                .append('\n')
        }

        helpMessage = helpBuilder.dropLast(1).toString()
    }

    private val commandsHelpMessage: Map<String, String>

    init {
        val sortedCommands = (commands + this).sortedBy { it.name }

        val commandsHelp = mutableMapOf<String, String>()

        for (command in sortedCommands) {

            val helpBuilder = StringBuilder()

            helpBuilder
                .append(command.name)

            for (argument in command.arguments) {
                helpBuilder
                    .append(" <")
                    .append(argument.name)
                    .append(">")
            }

            helpBuilder
                .append(" - ")
                .append(command.description)
                .append('\n')

            val maxArgumentLength = command.arguments.maxOfOrNull { it.name.length } ?: 0

            for (argument in command.arguments) {
                val argumentFormattedName = String.format("%-${maxArgumentLength}s", argument.name)

                helpBuilder
                    .append(OFFSET)
                    .append(argumentFormattedName)
                    .append(" - ")
                    .append(argument.description)
                    .append('\n')
            }

            commandsHelp[command.name] = helpBuilder.dropLast(1).toString()
        }

        commandsHelpMessage = commandsHelp
    }

    override fun execute(args: List<String>) {
        val message = if (args.size == 1) {
            helpMessage
        } else {
            commandsHelpMessage[args[1]] ?: throw UnsupportedCommandException(args[1])
        }

        println(message)
    }
}
