package tms.console

import org.jline.reader.impl.completer.NullCompleter

private val OFFSET: String = "    "

class HelpCommand(
    commands: List<Command>
) : Command {
    override val name: String = "help"

    override val description: String = "print help for commands"

    // TODO: optional command name completer
    override val arguments: List<CommandArgument> = listOf()

    private val helpMessage: String

    private val commandsHelpMessage: Map<String, String>

    init {
        val commands = (commands + this).sortedBy { it.name }

        val helpBuilder = StringBuilder()
        val commandsHelp = mutableMapOf<String, String>()

        helpBuilder.append("Commands:\n")

        val maxCommandLength = commands.maxOfOrNull { it.name.length } ?: 0

        for (command in commands.sortedBy { it.name }) {
            val commandFormattedName = String.format("%-${maxCommandLength}s", command.name)

            helpBuilder
                .append(OFFSET)
                .append(commandFormattedName)
                .append(" - ")
                .append(command.description)
                .append('\n')

            val commandHelpBuilder = StringBuilder()

            commandHelpBuilder.append(command.name).append('\n')

            val maxArgumentLength = command.arguments.maxOfOrNull { it.name.length } ?: 0

            for (argument in command.arguments) {
                val argumentFormattedName = String.format("%-${maxArgumentLength}s", argument.name)

                commandHelpBuilder
                    .append(OFFSET)
                    .append(argumentFormattedName)
                    .append(" - ")
                    .append(argument.description)
                    .append('\n')
            }

            commandsHelp[command.name] = commandHelpBuilder.dropLast(1).toString()
        }

        helpMessage = helpBuilder.dropLast(1).toString()

        commandsHelpMessage = commandsHelp
    }

    override fun execute(args: List<String>): String {
        return if (args.size == 1) {
            helpMessage
        } else {
            commandsHelpMessage[args[1]] ?: throw UnsupportedCommandException(args[1])
        }
    }
}
