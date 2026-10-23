package com.serverofglass

import com.serverofglass.menuCli.Menu
import com.serverofglass.menuCli.MenuCli
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val startMenu = Menu("Start Menu")

    startMenu.addOption("shutdown") {
        stop()
    }

    val cli = MenuCli(startMenu)
    cli.changePrompt {
        "$startTime>"
    }

    cli.start()
}