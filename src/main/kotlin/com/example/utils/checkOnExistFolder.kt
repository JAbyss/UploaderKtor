package com.example.utils

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

fun checkOnExistFolder(path: String) {
    val pathLink = Paths.get(path)
    val file = File(path)
    if (!Files.exists(pathLink)) file.mkdirs()
}