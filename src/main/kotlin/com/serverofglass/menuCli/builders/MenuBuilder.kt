package com.serverofglass.menuCli.builders

import com.serverofglass.menuCli.Menu
import com.serverofglass.menuCli.MenuCli
import com.serverofglass.menuCli.MenuOption
import com.serverofglass.menuCli.dsl.MenuBuilderDsl

class MenuBuilder : MenuBuilderDsl {
    private var title: String? = null
    private val options: MutableList<MenuOption> = mutableListOf()
    private val headers: MutableList<String> = mutableListOf()

    fun setTitle(title: String) {
        this.title = title
    }

    override fun display(content: String) {
        headers.add(content)
    }

    override fun option(title: String, block: MenuCli.ActionScope.() -> Unit) {
        options.add(MenuOption(title, block))
    }

    fun build(): Menu {
        require(!title.isNullOrEmpty()) { "Title cannot be null or empty." }

        val menu = Menu(title!!)
        for (option in options) {
            menu.addOption(option)
        }
        for (header in headers) {
            menu.addHeader(header)
        }

        return menu
    }
}