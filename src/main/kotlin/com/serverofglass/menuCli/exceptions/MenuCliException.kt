package com.serverofglass.menuCli.exceptions

sealed class MenuCliException(override val message: String = "") : Exception(message) {
    class NoOptionsInMenuException(menuName: String) : MenuCliException("The menu \"$menuName\" has no options to display")
}