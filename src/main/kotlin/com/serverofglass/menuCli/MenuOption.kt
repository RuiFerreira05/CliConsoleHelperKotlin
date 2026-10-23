package com.serverofglass.menuCli

class MenuOption(
    private val cmdTitle: String,
    private val action: MenuCli.ActionScope.() -> Unit
) {
    operator fun invoke(actionScope: MenuCli.ActionScope) = action(actionScope)

    override fun toString(): String {
        return cmdTitle
    }
}