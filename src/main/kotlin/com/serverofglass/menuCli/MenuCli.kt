package com.serverofglass.menuCli

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant
import kotlin.time.TimeSource

class MenuCli(startMenu: Menu) {
    private var currentMenu: Menu = startMenu
        set(value) {
            field = value
            history.add(field)
        }
    private var promptProvider: () -> String = { ">" }
    private var running: Boolean = false
    private var startTime: Instant = Clock.System.now()
    private var timeMark: TimeSource.Monotonic.ValueTimeMark = TimeSource.Monotonic.markNow()
    private var history = ArrayDeque<Menu>()

    inner class Context {
        val startTime: Instant get() = this@MenuCli.startTime
        val runTime: Duration get() = this@MenuCli.timeMark.elapsedNow()
        val history: List<Menu> get() = this@MenuCli.history.toList()

        fun stop() = this@MenuCli.stop()
    }

    suspend fun start() {
        running = true
        loop()
    }

    fun stop() {
        running = false
    }

    private suspend fun loop() = withContext(Dispatchers.IO) {
        while (running && isActive) {
            currentMenu.display()
        }
    }
}

// cli {
//    prompt = ">"
//    // ou
//    prompt {
//        "${cmdCount}>"
//    }
//
//    // By default first menu is start, can be overriden
//    val mainMenu = menu("main menu", start = true) {
//        display("This is a menu")
//        display("Choose an option:")
//
//        option("login") {
//            val username = ask<String>("username:", validate = { isValidUsername(it) }, inline = true)
//
//            // default generic to String
//            val password = ask(
//                "password:",
//                validate = { isValidPassword(it) },
//                onError = { attempt -> // if not given, just reprompt
//                    pause("wrong Password")
//                    resetFlow()
//                    // or
//                    abort()
//                },
//                converter = defaultConverterFor(), // (String) -> T?, defaultConverterFor() does automatic conversion
//                inline = true, //defaults to true
//                secret = true  //defaults to false
//            )
//
//            pause("login successfull!")
//        }
//        option("next menu") {
//            next { testMenu }
//        }
//        option("shutdown") {
//            stop()
//        }
//    }
//    val testMenu = menu("test menu") {
//        display("test menu")
//        option("back") { back() }
//    }
//}