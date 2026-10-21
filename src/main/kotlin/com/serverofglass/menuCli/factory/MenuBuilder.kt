package com.serverofglass.menuCli.factory

import com.serverofglass.menuCli.Menu
import com.serverofglass.menuCli.MenuOption

class MenuBuilder {

    var title: String = "";
    var isStart: Boolean = false
    var optionList: List<MenuOption> = mutableListOf()

    fun build(): Menu = Menu(title, isStart, optionList)
}