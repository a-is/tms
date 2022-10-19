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

import org.jline.reader.Candidate
import org.jline.reader.Completer
import org.jline.reader.LineReader
import org.jline.reader.ParsedLine
import org.jline.reader.impl.completer.ArgumentCompleter
import org.jline.reader.impl.completer.NullCompleter
import org.jline.reader.impl.completer.StringsCompleter

class CommandCompleter(
    commands: List<Command>
) : Completer {

    private val commandCompleter: Completer = StringsCompleter(commands.map { it.name })

    private val argumentsCompleter: Map<String, Completer>

    init {
        val argCompleter = mutableMapOf<String, Completer>()

        for (command in commands) {
            val commandArgumentCompleter = mutableListOf<Completer>()

            commandArgumentCompleter += commandCompleter

            for (argument in command.arguments) {
                commandArgumentCompleter += argument.completer
            }

            commandArgumentCompleter += NullCompleter.INSTANCE

            argCompleter[command.name] = ArgumentCompleter(commandArgumentCompleter)
        }

        argumentsCompleter = argCompleter
    }

    override fun complete(reader: LineReader, line: ParsedLine, candidates: MutableList<Candidate>) {
        if (line.cursor() == 0) {
            commandCompleter.complete(reader, line, candidates)
        }

        val commandName = line.words()[0]

        val completer = argumentsCompleter[commandName] ?: return

        return completer.complete(reader, line, candidates)
    }
}
