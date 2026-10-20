package com.serverofglass

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant
import kotlin.time.TimeSource

class Cli {
    private var running = true
    private var promptProvider: suspend CliContext.() -> String = { ">" }
    private val cliContext = CliContext()
    private val cmdList = mutableListOf<Command>();

    private var startTime = Clock.System.now()
    private var timeMark = TimeSource.Monotonic.markNow()
    private var history = mutableListOf<HistoryRow>()

    inner class CliContext {
        val startTime: Instant get() = this@Cli.startTime
        val runTime: Duration get() = this@Cli.timeMark.elapsedNow()
        val history: List<HistoryRow> get() = this@Cli.history
        val lastResult: CommandResult? get() = this@Cli.history.lastOrNull()?.result
        val cmdCount: Int get() = this@Cli.history.size

        fun stop() = this@Cli.stop()
    }

    suspend fun start() = withContext(Dispatchers.IO) {
        startTime = Clock.System.now()
        timeMark = TimeSource.Monotonic.markNow()

        while (running && isActive) {
            println("Choose a command:\n")

            for ((i, command) in cmdList.withIndex()) {
                println("${i + 1}: ${command.cmd}")
            }

            val renderedPrompt = cliContext.promptProvider().trimEnd()
            print("\n$renderedPrompt ")

            val line = readlnOrNull() ?: break
            val input: Int? = line.trim().toIntOrNull()
            if (input == null) {
                println("please enter only an integer number")
                continue
            }
            if (input !in 1..cmdList.size) {
                println("please choose only a valid number")
                continue
            }

            val cmd = cmdList[input-1]
            val cmdResult = try {
                cmd(cliContext)
                success()
            } catch (e: Exception) {
                println("Command threw exception: ${e.message}")
                error(cause = e)
            }

            history.add(HistoryRow(cmd, cmdResult, Clock.System.now()))
        }
    }

    fun command(cmd: String, action: suspend CliContext.() -> Unit) {
        cmdList.add(Command(cmd, action))
    }

    fun prompt(prompt: suspend CliContext.() -> String) {
        this@Cli.promptProvider = prompt
    }
    fun prompt(prompt: String) {
        this@Cli.promptProvider = { prompt }
    }

    private fun stop() {
        running = false
    }

    companion object {
        operator fun invoke(block: Cli.() -> Unit): Cli {
            return Cli().apply(block)
        }
    }

    data class Command(val cmd: String, val action: suspend CliContext.() -> Unit) {
        suspend operator fun invoke(cliContext: CliContext) = action(cliContext)
    }

    data class HistoryRow(val cmd: Command, val result: CommandResult, val timeStamp: Instant)

    private fun success(): CommandResult = CommandResult.Success
    private fun error(message: String): CommandResult = CommandResult.Error(message)
    private fun error(cause: Throwable): CommandResult = CommandResult.Error(cause = cause)
}