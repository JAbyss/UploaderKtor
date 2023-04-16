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
import java.util.concurrent.atomic.AtomicInteger

val countUploads = AtomicInteger(0)

@Serializable
data class RegistrationUpload(
    var idUpload: Int?,
    val nameFile: String,
    val extension: String,
    var pathTo: String,
    var idInTaskManager: String?
)

val listUploads: MutableList<RegistrationUpload> = Collections.synchronizedList(mutableListOf<RegistrationUpload>())

fun Route.registrationUpload() = cRoute(
    path = "registrationUpload",
    method = HttpMethod.Post,
    isCheckToken = true
) { token ->

    val registrationData = call.receive<RegistrationUpload>()
    registrationData.idUpload = countUploads.addAndGet(1)
    if (!listUploads.contains(registrationData)) {
        File(BuildPath(basePath, token.idUser.toString(), registrationData.pathTo)).mkdirs()
        var fullPath = BuildPath(basePath, token.idUser.toString(), registrationData.pathTo, registrationData.nameFile + "." + registrationData.extension)
        var file = File(fullPath)

        while(file.exists()){
            var count = 1
            fullPath = BuildPath(basePath, token.idUser.toString(), registrationData.pathTo, registrationData.nameFile + " ($count)" + "." + registrationData.extension)
            file = File(fullPath)
            count += 1
        }

        val idTask = TaskManager.addTaskToQueue(TaskManager.Task(time = 10.m) { file.delete() })

        registrationData.idInTaskManager = idTask
        registrationData.pathTo = fullPath.dropLast(1)

        listUploads.add(registrationData)

        call.respond(HttpStatusCode.Created, registrationData.idUpload!!)
    } else {
        call.respond(HttpStatusCode.Conflict)
    }
}