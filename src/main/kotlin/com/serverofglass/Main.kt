package com.serverofglass

import com.serverofglass.menuCli.dsl.ask
import com.serverofglass.menuCli.dsl.cli
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val cli = cli {
        prompt = ">>"

        val testMenu = menu("TEST MENU") {
            display("This is the test menu")

            option("back") {
                back()
            }
        }

        val startMenu = menu("START MENU", start = true) {
            display("This is the starting menu")
            display("Please choose one of the below options :)")

            option("test") {
                println("PRINTED TEST")
            }
            option("form") {
                val name: String? = ask("name:", inline = false)

                val age: Int? = ask("age:")

                println()
                println("Name: $name")
                println("Age: $age")
                println()

                pause()
            }
            option("switch menu") {
                next { testMenu }
            }
            option("shutdown") {
                println("Shutting down...")
                stop()
            }
        }
    }

    cli.start()
}