package com.example.routes.fileLoad

import com.example.plugins.TaskManager
import com.example.plugins.m
import com.example.utils.BuildPath
import com.example.utils.cRoute
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.io.File
import java.util.*

@Serializable
data class UploadingFile(
    var idUpload: Int,
    val data: String
)

val basePath = "users"

/**
 * users/1/chats/
/profiles/
/avatars/
/cloud/
 */

fun Route.uploadingFile() = cRoute(
    path = "uploadingFile",
    method = HttpMethod.Post,
    isCheckToken = true
) { token ->

    val data = call.receive<UploadingFile>()

    val infoUpload =
        listUploads.find { it.idUpload == data.idUpload } ?: return@cRoute call.respond(HttpStatusCode.NotFound)

    val decodedString = Base64.getDecoder().decode(data.data)

    val file = File(infoUpload.pathTo)

    file.appendBytes(decodedString)

    TaskManager.updateTaskById(idTask = infoUpload.idInTaskManager!!, 10.m)

    call.respond(HttpStatusCode.OK)
}