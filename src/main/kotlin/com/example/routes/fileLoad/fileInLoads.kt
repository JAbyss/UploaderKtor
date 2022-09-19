package com.example.routes.fileLoad

import com.example.mongo.SystemSettingsDataBase
import com.example.mongo.getPath
import com.example.plugins.SystemRouting.FileUploading
import com.example.plugins.TaskManager
import com.example.plugins.m
import com.example.plugins.s
import com.example.routes.fileLoad.models.BodyFile
import com.example.utils.ClientKtor
import com.example.utils.checkOnExistFolder
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import java.io.File
import java.util.*

val synchronizedList: MutableList<String> = Collections.synchronizedList(mutableListOf<String>())

fun Route.fileLoad() = post(
    path = FileUploading
) {
    try {

        val token = call.request.headers["Auth"] ?: return@post call.respond(HttpStatusCode.BadRequest)

        checkToken(token) {
            val body = call.receive<BodyFile>()

            if (!synchronizedList.contains(body.idUpload)) {
                synchronizedList.add(body.idUpload)
            }

            TaskManager.cancelTask(body.idUpload)

            val id = body.infoData

            val path =
                "${
                    SystemSettingsDataBase.FilesPaths.getPath(body.typeLoad)
                        ?: return@post call.respond(HttpStatusCode.BadGateway)
                }/$id"
            checkOnExistFolder(path)

            val fullPath = "$path/${body.idUpload}.${body.extension}"

            val file = File(fullPath)

            val decodedString = Base64.getDecoder().decode(body.contentFile)
            file.appendBytes(decodedString)

            if (body.status == "finish") {
                synchronizedList.remove(body.idUpload)
                return@post call.respond(status = HttpStatusCode.Created, fullPath)
            }

            TaskManager.addTask(task = TaskManager.Task(
                code = body.idUpload,
                duration = 3.m,
                after_action = {
                    synchronizedList.remove(body.idUpload)
                    File(fullPath).delete()
                }
            ))

            call.respond(HttpStatusCode.OK)
        }
    } catch (e: java.lang.Exception) {
        println(e.stackTraceToString())
        call.respond(HttpStatusCode.InternalServerError)
    }
}

suspend inline fun PipelineContext<Unit, ApplicationCall>.checkToken(token: String, body: () -> Unit) {
    ClientKtor.use {
        val response = it.get("http://token:32162/checkToken") {
            header("Auth", token)
        }
        println("Response: ${response.status}")
        if (response.status.isSuccess())
            body()
        else
            call.respond(HttpStatusCode.Unauthorized)
    }
}