package com.example.routes.fileLoad

import com.example.plugins.TaskManager
import com.example.utils.cRoute
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.finishUpload() = cRoute(
    path = "finishUpload",
    method = HttpMethod.Post,
    isCheckToken = true
){ token ->

    val idUpload = call.receive<Int>()

    val item = listUploads.find { it.idUpload == idUpload } ?: return@cRoute call.respond(HttpStatusCode.Conflict)

    TaskManager.removeTaskById(item.idInTaskManager!!)

    listUploads.remove(item)

    println(item.pathTo)
    call.respond(HttpStatusCode.OK, item.pathTo)
}