package com.example.utils

fun generateUUID(countSymbols: Int): String {
    var string = ""
    repeat(countSymbols) {
        val bit = kotlin.random.Random.nextBits(2)
        val (from, until) = if (bit == 0) Pair(48, 57) else Pair(97, 122)
        string += kotlin.random.Random.nextInt(from = from, until = until).toChar()
    }
    return string
}