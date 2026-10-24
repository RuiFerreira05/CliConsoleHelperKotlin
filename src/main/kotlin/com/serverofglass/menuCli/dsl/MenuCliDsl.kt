package com.serverofglass.menuCli.dsl

import com.serverofglass.menuCli.Menu
import com.serverofglass.menuCli.MenuCli
import com.serverofglass.menuCli.builders.MenuBuilder
import com.serverofglass.menuCli.builders.MenuCliBuilder
import com.serverofglass.menuCli.exceptions.MenuCliException

@DslMarker
annotation class MenuCliDsl

@MenuCliDsl
interface MenuCliBuilderDsl {
    var prompt: String
    fun prompt(block: MenuCli.MenuCliContext.() -> String)

    fun menu(title: String, start: Boolean = false, block: MenuBuilder.() -> Unit): Menu
}

@MenuCliDsl
interface MenuBuilderDsl {
    fun display(content: String)
    fun option(title: String, block: MenuCli.ActionScope.() -> Unit)
}

@MenuCliDsl
interface MenuCliActionScopeDsl {
    val context: MenuCli.MenuCliContext

    fun <T> ask(
        prompt: String,
        converter: (rawInput: String) -> T,
        inline: Boolean = true,
        validator: (userInput: T) -> Boolean = { true }
    ): T?
    fun pause(prompt: String = "Press enter to continue...")
//    fun resetOption()
//    fun abortOption()
    fun next(menu: () -> Menu)
    fun back()
    fun stop()
}

@Throws(MenuCliException.UnhandledValidatorType::class)
inline fun <reified T : Any> MenuCliActionScopeDsl.ask(
    prompt: String,
    noinline converter: (String) -> T = { defaultConverterFor(it) },
    inline: Boolean = true,
    noinline validator: (userInput: T) -> Boolean = { true }
): T? = ask(prompt, converter, inline, validator)

@Throws(MenuCliException.UnhandledValidatorType::class)
inline fun <reified T : Any> defaultConverterFor(rawInput: String): T {
    return when (T::class) {
        String::class -> rawInput as T
        Int::class -> (rawInput.toIntOrNull()
            ?: throw MenuCliException.InvalidAskUserInput("Expected integer")) as T
        Boolean::class -> rawInput.toBooleanStrict() as T
        else -> throw MenuCliException.UnhandledValidatorType(T::class.java.simpleName)
    }
}

fun cli(block: MenuCliBuilder.() -> Unit): MenuCli {
    val builder = MenuCliBuilder()
    builder.block()
    return builder.build()
}