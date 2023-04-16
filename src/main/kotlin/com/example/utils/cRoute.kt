package com.example.utils

import com.auth0.jwt.JWT
import com.example.errors.CInternalRequestError
import com.example.errors.CNotAuthorizedError
import com.example.routes.fileLoad.Success
import com.example.routes.fileLoad.checkToken
import com.example.utils.JWTToken.Companion.EmptyJWT
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class JWTToken(
    val token: String? = null
) {
    private val decodedToken = token?.let { JWT.decode(it) }
    val idUser = decodedToken?.getClaim("idUser")?.asInt()

    companion object {
        val EmptyJWT = JWTToken(null)
    }
}

fun Route.cRoute(
    path: String,
    method: HttpMethod,
    isCheckToken: Boolean = false,
    body: suspend PipelineContext<Unit, ApplicationCall>.(JWTToken) -> Unit
) = this.route(path, method) {

    cHandle {
//        if (isCheckToken) {
        
            val token = this.call.request.header("Authorization") ?: throw CNotAuthorizedError(this)
//
//            when (val status = checkToken(token)) {
//                is Success -> {
                    val _token = JWTToken(token)
                    body(this, _token)
//                }
//
//                is Error -> {
//                    throw CInternalRequestError("status.description", 1)
//                }
//
//                else -> {}
//            }
//        } else
//            body(this, EmptyJWT)
    }
}

fun Route.cHandle(body: suspend PipelineContext<Unit, ApplicationCall>.() -> Unit) = kotlin.runCatching {
    handle { body() }
}.onFailure { error ->
    CoroutineScope(Dispatchers.IO).launch {
        when (error) {

            is NullPointerException -> {
                Logger.printLog(error.stackTraceToString())
            }

//            is CRequestError -> {
//                error.respondToRequest()
//            }
//
//            is CInternalRequestError -> {
//                error.logError()
//            }
//
//            is CInternalError -> {
//                Logger.printLog(error.description)
//            }
        }
    }
}



//suspend fun main() {
//
//    fun generateList(): MutableList<Task> {
//        val list = mutableListOf<Task>()
//        while (list.size < 1000){
//            list.add(Task(time = Random.nextLong(), {
//
//            }))
//        }
//        return list
//    }
//
//    val arr = generateList()
//    val arr1 = generateList()
//    println(measureTimeMillis {
//        arr.quickSortLomuto()
//    })
//    println(measureTimeMillis {
//        arr1.sortBy { it.time }
//    })
//    while (true){
//
//    }
//
//}
