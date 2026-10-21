package com.serverofglass.menuCli.factory

import com.serverofglass.menuCli.Menu
import com.serverofglass.menuCli.MenuCli
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.time.TimeSource

class MenuCliBuilder {
    var promptProvider: () -> String = { ">" }
    var startMenu: Menu? = null

    var prompt: String
        get() = promptProvider()
        set(value) { promptProvider = { value } }
    fun prompt(promptProvider: () -> String) {
        this.promptProvider = promptProvider
    }

    fun menu(title: String, isStart: Boolean = false, block: MenuBuilder.() -> Unit): Menu {
        val builder = MenuBuilder()
        builder.title = title;
        builder.block()
        val menuCli = builder.build()
        if (isStart || startMenu == null) {startMenu = menuCli}
        return menuCli
    }

    fun build(): MenuCli = MenuCli(
        startMenu,
        promptProvider
    )

    companion object {
        fun menuCli(block: MenuCliBuilder.() -> Unit): MenuCli {
            val builder = MenuCliBuilder()
            builder.block()
            return builder.build()
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