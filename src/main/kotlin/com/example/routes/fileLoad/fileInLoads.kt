package com.example.routes.fileLoad

import com.example.mongo.SystemSettingsDataBase
import com.example.mongo.getPath
import com.example.plugins.SystemRouting.FileUploading
import com.example.plugins.TaskManager
import com.example.plugins.UrlManager.AuthServerURL.CheckTokenPath
import com.example.plugins.m
import com.example.plugins.s
import com.example.routes.fileLoad.models.BodyFile
import com.example.utils.ClientKtor
import com.example.utils.cRoute
import com.example.utils.checkOnExistFolder
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import org.bson.codecs.pojo.annotations.BsonId
import java.io.File
import java.util.*

val synchronizedList: MutableList<String> = Collections.synchronizedList(mutableListOf<String>())

fun Route.fileLoad() = cRoute(
    path = FileUploading,
    method = HttpMethod.Post,
    isCheckToken = true
) { token ->
//    try {
//        val body = call.receive<BodyFile>()
//
//        if (!synchronizedList.contains(body.idUpload)) {
//            synchronizedList.add(body.idUpload)
//        }
//
//        TaskManager.cancelTask(body.idUpload)
//
//        val id = body.infoData
//
//        val path = "cloud/${token.idUser}"
////            "${
////                SystemSettingsDataBase.FilesPaths.getPath(body.typeLoad)
////                    ?: return@post call.respond(HttpStatusCode.BadGateway)
////            }/$id"
//        checkOnExistFolder(path)
//
//        val fullPath = "$path/${body.idUpload}.${body.extension}"
//
//        val file = File(fullPath)
//
//        val decodedString = Base64.getDecoder().decode(body.contentFile)
//        file.appendBytes(decodedString)
//
//        if (body.status == "finish") {
//            synchronizedList.remove(body.idUpload)
//            return@post call.respond(status = HttpStatusCode.Created, fullPath)
//        }
//
//        TaskManager.addTask(task = TaskManager.Task(
//            code = body.idUpload,
//            duration = 3.m,
//            after_action = {
//                synchronizedList.remove(body.idUpload)
//                File(fullPath).delete()
//            }
//        ))
//
//        call.respond(HttpStatusCode.OK)
//    } catch (e: java.lang.Exception) {
//        println(e.stackTraceToString())
//        call.respond(HttpStatusCode.InternalServerError)
//    }
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

//suspend fun checkToken(token: String): Status {
//    return ClientKtor.use {
//        val response = it.post(CheckTokenPath) {
//            header("Authorization", "Bearer $token")
//        }
//        return@use if (response.status.isSuccess()) Success() else Error(
//            description = response.status.description,
//            code = response.status.value
//        )
//    }
//}

open class Status()

class Success(val value: Any = Unit) : Status()

class Error(
    val description: String = "",
    val code: Int? = null
) : Status()

public class CustomRoureSelector(public val names: List<String?>) : RouteSelector() {
    override fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation {
        return RouteSelectorEvaluation.Transparent
    }

    override fun toString(): String = "(authenticate ${names.joinToString { it ?: "\"default\"" }})"
}

private val AuthenticateProviderNamesKey = AttributeKey<List<String?>>("AuthenticateProviderNamesKey")

val SimplePlugin = createRouteScopedPlugin(name = "SimplePlugin") {
    onCall { call ->
        println("SimplePlugin is installed!!!")

        val result = checkToken(call)
        result.onFailure {
            call.respond(HttpStatusCode.Unauthorized)

        }
    }
    println("SimplePlugin is installed!")
}

@kotlinx.serialization.Serializable
data class Token(
    @BsonId
    var token: String,
    var idUser: String
) {
    companion object {
        val Empty: Token = Token(
            "",
            ""
        )
    }
}

suspend fun checkToken(call: ApplicationCall): Result<Token> {

    return ClientKtor.use {
        val token = call.request.headers["Authorization"]
        return if (token == null) {
            call.respond(status = HttpStatusCode.BadRequest, "Токен не получен")
            Result.failure(NullPointerException())
        } else {

            val response = it.post(CheckTokenPath) {
                header("Authorization", "Bearer $token")
            }
            return@use if (response.status.isSuccess())
                Result.success(Token(token, response.bodyAsText()))
            else
                Result.failure(Exception(response.status.description))
        }
    }
}
