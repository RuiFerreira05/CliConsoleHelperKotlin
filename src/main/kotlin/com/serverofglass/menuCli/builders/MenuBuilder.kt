package com.serverofglass.menuCli.builders

import com.serverofglass.menuCli.Menu
import com.serverofglass.menuCli.MenuCli
import com.serverofglass.menuCli.MenuOption
import com.serverofglass.menuCli.dsl.MenuBuilderDsl

class MenuBuilder : MenuBuilderDsl {
    private val _options: MutableList<MenuOption> = mutableListOf()
    private var title: String? = null

    fun setTitle(title: String) {
        this.title = title
    }

    override fun option(title: String, block: MenuCli.ActionScope.() -> Unit) {
        _options.add(MenuOption(title, block))
    }

    fun build(): Menu {
        require(!title.isNullOrEmpty()) { "Title cannot be null or empty." }

        return Menu(title!!)
    }
}