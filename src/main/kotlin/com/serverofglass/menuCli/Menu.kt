package com.serverofglass.menuCli

class Menu(
    val title: String,
) {
    private val _headers: MutableList<String> = mutableListOf()
    val headers: List<String> = _headers

    private val _optionList: MutableList<MenuOption> = mutableListOf()
    val options: List<MenuOption> = _optionList

    fun displayHeader() {
        println(title)
        println()
        for (header in headers) {
            println(header)
        }
    }

    fun displayOptions() {
        for ((i, option) in options.withIndex()) {
            println("${i + 1}. $option")
        }
    }

    fun addHeader(header: String) {
        _headers.add(header)
    }

    fun addOption(option: MenuOption) {
        _optionList.add(option)
    }

    fun addOption(title: String, action: MenuCli.ActionScope.() -> Unit) {
        _optionList.add(MenuOption(title, action))
    }
}

// val mainMenu = menu("main menu", start = true) {
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