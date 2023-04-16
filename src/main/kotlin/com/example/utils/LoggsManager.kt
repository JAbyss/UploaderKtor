package com.example.utils

import com.example.ServerDate
import java.util.concurrent.ConcurrentHashMap

object Logger {

    //    @kotlinx.serialization.Serializable
    data class LoggBody(
        val method: String,
        val path: String,
        val isCheckToken: Boolean,
        val ipAddress: String,
        val token: String,
        val errorLogs: MutableList<LoggDc> = mutableListOf(),
        val infoLogs: MutableList<LoggDc> = mutableListOf()
    )

    //    @kotlinx.serialization.Serializable
    data class LoggDc(
        val time: String,
        val message: Any,
        val status: StatusCodes,
    )

    val logs = ConcurrentHashMap<String, LoggBody>()

    enum class StatusCodes {
        INFO, ERROR
    }

    fun initLog(
        idRequest: String,
        method: String,
        path: String,
        isCheckToken: Boolean,
        ipAddress: String,
        token: String
    ) {

        val value = LoggBody(
            method,
            path,
            isCheckToken,
            ipAddress,
            token
        )

        logs[idRequest] = value
    }

    fun addLog(idRequest: String, message: Any, status: StatusCodes) {

        val messageLog = LoggDc(
            time = ServerDate.fullDate,
            message,
            status
        )
        if (messageLog.status == StatusCodes.ERROR)
            logs.errorPut(idRequest, messageLog)
        else
            logs.infoPut(idRequest, messageLog)
    }

    suspend fun saveLog(idLog: String) {
//        SystemSettingsDataBase.Logs.insertLog(logs[idLog]!!)
        logs.remove(idLog)
    }

    private fun <K : Any> ConcurrentHashMap<K, LoggBody>.errorPut(key: K, value: LoggDc) {
        this[key]?.errorLogs?.add(value)
    }

    private fun <K : Any> ConcurrentHashMap<K, LoggBody>.infoPut(key: K, value: LoggDc) {
        this[key]?.infoLogs?.add(value)
    }

    fun printLog(description: String) {
        println(ServerDate.fullDate + "||" + description)
    }
}