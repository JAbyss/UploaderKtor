package com.example

import com.example.plugins.configureRouting
import com.example.plugins.configureSerialization
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.text.SimpleDateFormat
import java.util.*


object ServerDate {
    private val formatFull = SimpleDateFormat("d MMM yyyy г. HH:mm:ss")
    private val formatMute = SimpleDateFormat("ddhhmm")

    val fullDate: String
        get() =
            formatFull.format(Date())

    val muteDate: String
        get() = formatMute.format(Date())
}

fun main() {

    embeddedServer(Netty, port = 38142, host = "0.0.0.0") {
        configureSerialization()
        configureRouting()
    }.start(wait = true)
}
