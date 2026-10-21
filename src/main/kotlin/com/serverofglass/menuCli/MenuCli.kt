package com.serverofglass.menuCli

import com.serverofglass.menuCli.exceptions.NoMenuToDisplayException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.time.TimeSource

class MenuCli(
    private var currentMenu: Menu? = null,
    private var promptProvider: () -> String = { ">" },
) {
    private var running: Boolean = false
    private var startTime: Instant = Clock.System.now()
    private var timeMark: TimeSource.Monotonic.ValueTimeMark = TimeSource.Monotonic.markNow()

    suspend fun start() {
        running = true
        loop()
    }

    fun stop() {
        running = false
    }

    private suspend fun loop() = withContext(Dispatchers.IO) {
        while (running && isActive) {
            currentMenu?.display() ?: throw NoMenuToDisplayException()
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