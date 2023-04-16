package com.example.plugins

object UrlManager{

    private const val baseUrl = "http://"

    object AuthServerURL {
        private const val ip = "0.0.0.0"
        private const val port = 22221

        const val AuthServer = "$baseUrl$ip:$port"

        const val checkToken = "verifyToken"
        const val verifyTokenLocal = "verifyTokenLocal"

        val CheckTokenPath
            get() = "$AuthServer/$verifyTokenLocal"
    }


}