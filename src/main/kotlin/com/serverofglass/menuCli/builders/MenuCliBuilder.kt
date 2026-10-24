package com.serverofglass.menuCli.builders

import com.serverofglass.menuCli.Menu
import com.serverofglass.menuCli.MenuCli
import com.serverofglass.menuCli.dsl.MenuCliBuilderDsl

class MenuCliBuilder : MenuCliBuilderDsl {
    private var startMenu: Menu? = null
    private var promptProvider: MenuCli.MenuCliContext.() -> String = { ">" }

    override var prompt: String = ">"
        set(value) {
            promptProvider = { value }
            field = value
        }

    override fun prompt(block: MenuCli.MenuCliContext.() -> String) {
        promptProvider = block
    }

    override fun menu(title: String, start: Boolean, block: MenuBuilder.() -> Unit): Menu {
        val builder = MenuBuilder()
        builder.setTitle(title)
        builder.block()
        val menu = builder.build()
        if (start || startMenu == null) {startMenu = menu}
        return menu
    }

    fun build(): MenuCli {
        require(startMenu != null) { "startMenu can not be null" }

        val cli = MenuCli(
            startMenu!!
        )
        cli.changePrompt(promptProvider)

        return cli
    }

}