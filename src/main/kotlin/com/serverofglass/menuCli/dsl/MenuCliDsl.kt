package com.serverofglass.menuCli.dsl

import com.serverofglass.menuCli.Menu
import com.serverofglass.menuCli.MenuCli
import com.serverofglass.menuCli.builders.MenuBuilder
import com.serverofglass.menuCli.builders.MenuCliBuilder

@DslMarker
annotation class MenuCliDsl

@MenuCliDsl
interface MenuCliBuilderDsl {
    var prompt: String
    fun prompt(block: MenuCli.MenuCliContext.() -> String)

    fun menu(title: String, block: MenuBuilder.() -> Menu): Menu
}

interface MenuBuilderDsl {
    fun option(title: String, block: MenuCli.ActionScope.() -> Unit)
}

fun cli(block: MenuCliBuilder.() -> Unit): MenuCli {
    val builder = MenuCliBuilder()
    builder.block()
    return builder.build()
}