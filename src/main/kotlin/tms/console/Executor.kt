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

class Executor(
    commands: List<Command>
) {
    /**
     * Mapping command names to commands.
     */
    private val commands: Map<String, Command> = commands.associateBy { it.name }

    /**
     * Execute the command and return a text representation of the result of its work.
     *
     * Note that it is worth avoiding explicit printing inside the command, since in the future, for example,
     * logging to a file may be added.
     */
    fun execute(line: String) {
        if (line.trim().isBlank()) {
            return
        }

        val splited = line.split(' ').filter { it.isNotEmpty() }

        val name = splited[0]

        val command = commands[name] ?: throw UnsupportedCommandException(name)

        val requiremArgsMinCount = command.arguments.count { !it.optional }
        val requiremArgsMaxCount = command.arguments.count()
        val actualArgsCount = splited.size - 1

        val errorMessageInfo = {
            val prefix = if (requiremArgsMinCount == requiremArgsMaxCount) {
                "$requiremArgsMinCount"
            } else {
                "$requiremArgsMinCount to $requiremArgsMaxCount"
            }
            "$prefix arguments are expected, and $actualArgsCount are passed."
        }

        if (actualArgsCount < requiremArgsMinCount) {
            throw IncorrectArgumentsCountException("Too few arguments. ${errorMessageInfo()}")
        }

        if (actualArgsCount > requiremArgsMaxCount) {
            throw IncorrectArgumentsCountException("Too many arguments. ${errorMessageInfo()}")
        }

        command.execute(splited)
    }
}
