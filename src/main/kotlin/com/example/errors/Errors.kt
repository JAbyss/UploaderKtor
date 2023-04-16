package com.example.errors

import com.example.utils.Logger
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*

sealed class CustomErrors() : Throwable()

/**
 * REQUEST ERRORS
 */

sealed class CRequestError(val context: PipelineContext<Unit, ApplicationCall>, val httpCode: HttpStatusCode) : CustomErrors() {
    suspend fun respondToRequest() {
        this.context.call.respond(this.httpCode)
    }
}

class CNotAuthorizedError(context: PipelineContext<Unit, ApplicationCall>) :
    CRequestError(context, HttpStatusCode.Unauthorized)

class CNotValidError(context: PipelineContext<Unit, ApplicationCall>) :
    CRequestError(context, HttpStatusCode.NotAcceptable)

/**
 * INTERNAL ERRORS
 */

sealed class CInternalError(val description: String) : CustomErrors()

class CInternalRequestError(description: String, val code: Int?) : CInternalError(description) {
    fun logError() {
        Logger.printLog("$description || $code")
    }
}