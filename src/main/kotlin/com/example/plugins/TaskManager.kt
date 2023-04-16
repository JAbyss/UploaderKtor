package com.example.plugins

import com.example.plugins.TaskManager.SmallListSort.quickSortLomuto
import com.example.routes.fileLoad.UploadingFile
import com.example.utils.generateUUID
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.properties.Delegates

object TaskManager {

    private val Scope = CoroutineScope(newSingleThreadContext("task-thread"))
    private const val CHECK_DELAY = 100L
    private var lastExecutionTask = 0L
    private val listQueue = mutableListOf<Task>()
    private val nowTime
        get() = System.currentTimeMillis()


    class Task(
    ) {
        val id = generateUUID(10)

        var time by Delegates.notNull<Long>()
        lateinit var action: () -> Unit

        constructor(time: Long, action: () -> Unit) : this() {
            this.time = time + nowTime
            this.action = action
            return
        }
    }

    fun addTaskToQueue(task: Task): String {
        listQueue.add(task)
        return task.id
    }

    fun updateTaskById(idTask: String, timeToUpdate: Long){
        listQueue.find { it.id == idTask }?.apply {
            time = nowTime + timeToUpdate
        }
    }

    fun removeTaskById(idTask: String){
        listQueue.removeIf { it.id == idTask }
    }

    init {
        Scope.launch {
            while (true) {
                if (listQueue.isEmpty()) {
                    delay(1.s)
                } else {
                    val sortedList = listQueue.quickSortLomuto()
                    val listForDelete = mutableListOf<Task>()
                    sortedList.forEach { task ->
                        val currentTime = nowTime
                        if (task.time < currentTime) {
                            task.action()
                            lastExecutionTask = nowTime
                            listForDelete.add(task)
                        } else
                            return@forEach
                    }

                    listForDelete.forEach {
                        listQueue.remove(it)
                    }

                    listForDelete.clear()
                    delay(if (lastExecutionTask + 10.m >= System.currentTimeMillis()) 1.s else CHECK_DELAY)
                }
            }
        }
    }

    object SmallListSort {

        fun sortByJ(array: MutableList<Task>, start: Int, end: Int): Int {
            var left = start
            var current = start
            while (current < end) {
                if (array[current].time <= array[end].time) {
                    val temp = array[left]
                    array[left] = array[current]
                    array[current] = temp
                    left++
                }
                current++
            }
            val temp = array[left]
            array[left] = array[end]
            array[end] = temp
            return left
        }

        fun quickSortLomuto(
            arr: MutableList<Task>,
            start: Int,
            end: Int
        ): MutableList<Task> {
            if (start >= end) return arr
            val rightStart = sortByJ(arr, start, end)
            quickSortLomuto(arr, start, rightStart - 1)
            quickSortLomuto(arr, rightStart + 1, end)
            return arr
        }

        fun MutableList<Task>.quickSortLomuto(): MutableList<Task> {
            return quickSortLomuto(this, 0, this.size - 1)
        }
    }
}