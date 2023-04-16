package com.example.plugins

import com.example.routes.fileLoad.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.request.*

fun Application.configureRouting() {
    routing {
        route("123"){
            fileLoad()
        }
        static("/") {
            files(".")
        }
        route("uploading"){
            install(SimplePlugin)
            registrationUpload()
            uploadingFile()
            finishUpload()
        }
    }
}
