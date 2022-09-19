package com.example.routes.fileLoad.models

enum class TypeLoadFile {
    CHAT, PROFILE, AVATAR, CONTENT_PROFILE
}

@kotlinx.serialization.Serializable
data class BodyFile(
    val idUpload: String,
    val nameFile: String,
    val contentFile: String,
    val status: String,
    val extension: String,
    val typeLoad: String,
    val infoData: String
)