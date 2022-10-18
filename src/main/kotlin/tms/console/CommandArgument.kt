package tms.console

import org.jline.reader.Completer

/**
 * A class for storing information about an argument.
 */
data class CommandArgument(
    /** The name of the argument. Used in the `help` command. */
    val name: String,
    /** Description of the argument. Used in the `help` command. */
    val description: String,
    /** Complements for command arguments. */
    val completer: Completer,
)
