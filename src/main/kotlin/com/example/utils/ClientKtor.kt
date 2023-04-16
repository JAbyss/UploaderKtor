package com.example.utils

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*

val ClientKtor
    get() = HttpClient(CIO) {
        expectSuccess = false
        install(HttpTimeout) {
            requestTimeoutMillis = 30000
        }
    }