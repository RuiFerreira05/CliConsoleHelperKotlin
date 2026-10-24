package com.serverofglass.menuCli

import com.serverofglass.menuCli.dsl.MenuCliActionScopeDsl
import com.serverofglass.menuCli.exceptions.MenuCliException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant
import kotlin.time.TimeSource

class MenuCli(
    startMenu: Menu,
) {
    private var promptProvider: MenuCliContext.() -> String = { ">" }
    private var currentMenu: Menu = startMenu
        set(value) {
            history.add(field)
            field = value
        }
    private var running: Boolean = false
    private var startTime: Instant = Clock.System.now()
    private var timeMark: TimeSource.Monotonic.ValueTimeMark = TimeSource.Monotonic.markNow()
    private var history = ArrayDeque<Menu>()
    val context = MenuCliContext()
    val actionScope = ActionScope()

    fun changePrompt(prompt: String) {
        promptProvider = { prompt }
    }

    fun changePrompt(prompt: MenuCliContext.() -> String) {
        promptProvider = prompt
    }

    inner class MenuCliContext {
        val startTime: Instant get() = this@MenuCli.startTime
        val runTime: Duration get() = this@MenuCli.timeMark.elapsedNow()
        val history: List<Menu> get() = this@MenuCli.history.toList()
    }

    inner class ActionScope : MenuCliActionScopeDsl {
        override val context = this@MenuCli.context

        override fun <T> ask(
            prompt: String,
            converter: (rawInput: String) -> T,
            inline: Boolean,
            validator: (userInput: T) -> Boolean
        ): T? = this@MenuCli.ask(
            prompt,
            converter,
            inline,
            validator,
        )

        override fun pause(prompt: String) = this@MenuCli.pause(prompt)
        override fun next(menu: () -> Menu) = this@MenuCli.next(menu())
        override fun back() = this@MenuCli.back()
        override fun stop() = this@MenuCli.stop()
    }

    @Throws(MenuCliException.UnhandledValidatorType::class)
    fun <T> ask(
        prompt: String,
        converter: (rawInput: String) -> T,
        inline: Boolean,
        validator: (userInput: T) -> Boolean,
    ): T? {
        println()
        print("$prompt ")
        if (!inline) {print("\n${promptProvider(context)} ")}
        val rawInput = readlnOrNull()
        if (rawInput == null) {
            stop()
            return null
        }

        // propagate exception if thrown
        val input = converter(rawInput)

        if (!validator(input)) {
            return null
        }

        return input
    }

    fun pause(prompt: String) {
        println(prompt)
        readlnOrNull()
    }

    @Throws(MenuCliException::class)
    suspend fun start() {
        running = true
        loop()
    }

    private fun stop() {
        running = false
    }

    private fun next(menu: Menu) {
        currentMenu = menu
    }

    private fun back() {
        currentMenu = this@MenuCli.history.removeLastOrNull() ?: return
    }

    @Throws(MenuCliException.NoOptionsInMenuException::class)
    private suspend fun loop() = withContext(Dispatchers.IO) {
        while (running && isActive) {

            if (currentMenu.options.isEmpty()) {
                throw MenuCliException.NoOptionsInMenuException(currentMenu.title)
            }

            currentMenu.displayHeader()
            currentMenu.displayOptions()

            print("\n${promptProvider(context)} ")

            val userString = readlnOrNull()
            if (userString.isNullOrBlank()) {
                stop()
                continue
            }

            val userChoice = userString.toIntOrNull()
            if (userChoice == null) {
                println("User choice must be an integer")
                continue
            }
            if (userChoice !in 1..currentMenu.options.size) {
                println("User choice must be between 1 and ${currentMenu.options.size}")
                continue
            }

            currentMenu.options[userChoice-1](actionScope)

            println()
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