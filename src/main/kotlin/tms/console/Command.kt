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
