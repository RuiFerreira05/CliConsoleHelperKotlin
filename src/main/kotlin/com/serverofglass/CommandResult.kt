package com.serverofglass

sealed interface CommandResult {

    data object Success : CommandResult

    data class Error(
        val message: String? = null,
        val cause: Throwable? = null
    ) : CommandResult
}