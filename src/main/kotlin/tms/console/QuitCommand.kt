package tms.console

import org.jline.reader.Completer
import org.jline.reader.UserInterruptException

class QuitCommand : Command {
    override val name: String = "quit"

    override val description: String = "close console"

    override val arguments: List<CommandArgument> = listOf()

    override fun execute(args: List<String>) = throw UserInterruptException(name)
}
