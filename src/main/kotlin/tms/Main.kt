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

package tms

import Wrapper
import tms.console.Console
import tms.machine.MachineBuilder
import tms.machine.command.InfoCommand
import tms.machine.command.LoadCommand
import tms.machine.command.toString
import tms.reader.TextReader

fun interactive() {
    val dumbMachine = MachineBuilder().build()
    val machine = Wrapper(dumbMachine)

    val commands = listOf(
        InfoCommand(machine),
        LoadCommand(machine),
    )

    val console = Console(commands)

    console.run()
}

fun fromFile(path: String) {
    val reader = TextReader(path)

    reader.read()

    if (!reader.success) {
        for (error in reader.errors) {
            println(error)
        }
        return
    }

    val machine = reader.buildMachine()
    machine.breakStates.clear()
    machine.run()
    println(toString(machine.tape))
}

fun main(args: Array<String>) {
    when(args.size) {
        0 -> interactive()
        1 -> fromFile(args.first())
        else -> println("Incorrect argument length")
    }
}
