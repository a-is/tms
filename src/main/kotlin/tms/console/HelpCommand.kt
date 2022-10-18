package tms.console

private val OFFSET: String = "    "

class HelpCommand(
    commands: List<Command>
) : Command {
    private var helpMessage: String

    override val name: String = "help"

    override val description: String = "show this message"

    override val arguments: List<CommandArgument> = listOf()

    init {
        val commands = (commands + this).sortedBy { it.name }

        val helpBuilder = StringBuilder()

        helpBuilder.append("Commands:\n")

        for (command in commands.sortedBy { it.name }) {
            helpBuilder.append("$OFFSET${command.name}")

            for (argument in command.arguments) {
                helpBuilder.append(" <${argument.name}>")
            }

            helpBuilder.append('\n')

            helpBuilder.append("$OFFSET$OFFSET${command.description}\n")

            val maxArgumentLength = command.arguments.maxOfOrNull { it.name.length } ?: 0

            if (!command.arguments.isEmpty()) {
                for (argument in command.arguments) {
                    val formattedName = String.format("%-${maxArgumentLength}d", argument.name)
                    helpBuilder.append("$OFFSET$OFFSET$formattedName - ${argument.description}")
                }

                helpBuilder.append('\n')
            }
        }

        helpMessage = helpBuilder.dropLast(1).toString()
    }

    override fun execute(args: List<String>): String = helpMessage
}
