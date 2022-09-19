package com.example.plugins

import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap

object TaskManager {


    @OptIn(DelicateCoroutinesApi::class)
    private val dispatcher = newSingleThreadContext("task-thread")

    data class Task(
        val code: String,
        val duration: Long,
        val before_action: (suspend () -> Unit)? = null,
        val after_action: suspend () -> Unit
    )

    private val tasks = ConcurrentHashMap<String, Job>()

    private fun stop(code: String) {
        tasks.remove(code)?.cancel()
    }

    private suspend fun startTask(task: Task) = CoroutineScope(dispatcher).launch {
            task.before_action?.let { it() }
            delay(task.duration)
            task.after_action()
            stop(task.code)
        }


    fun addTask(task: Task) {
        CoroutineScope(dispatcher).launch {
            tasks[task.code] = startTask(task)
            println("Added task: ${task.code}, All tasks: ${tasks.size}")
        }
    }

    fun cancelTask(nameTask: String) {
        tasks.remove(nameTask)?.cancel()
        println("Deleted task: $nameTask, All tasks: ${tasks.size}")
    }
}