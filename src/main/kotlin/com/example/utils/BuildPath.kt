package com.example.utils

fun BuildPath(vararg args: String): String {
    var finalString = ""
    args.forEach { item ->
            finalString += "${item}/"
    }
    return finalString
}