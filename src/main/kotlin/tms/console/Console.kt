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

import org.jline.reader.EndOfFileException
import org.jline.reader.LineReaderBuilder
import org.jline.reader.UserInterruptException

private val PROMPT = "tms> "
private val LICENSE_NOTICE = """
    Copyright (C) 2022, Alexey Ismagilov.
    License GPLv3+: GNU GPL version 3 or later, see <https://gnu.org/licenses/>
    This is free software: you are free to change and redistribute it.
    There is NO WARRANTY, to the extent permitted by law.
    """.trimIndent()

class Console(
    commands: List<Command>
) {
    private val executor: Executor

    private val completer: CommandCompleter

    init {
        val commands = commands.toMutableList()
        commands += QuitCommand()
        commands += HelpCommand(commands)

        executor = Executor(commands)
        completer = CommandCompleter(commands)
    }

    fun run() {
        val reader = LineReaderBuilder.builder()
            .completer(completer)
            .build()

        println(LICENSE_NOTICE)

        var previousCommand = ""

        while (true) {
            try {
                val command = reader.readLine(PROMPT)

                if (command.trim().isNotEmpty()) {
                    previousCommand = command
                }

                executor.execute(previousCommand)
            } catch (e: UserInterruptException) {
                return
            } catch (e: EndOfFileException) {
                return
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }
}
