package com.example.plugins

inline val Int.ms get() = this

inline val Int.s get() = this * 1000L

inline val Int.m get() = this * 60 * 1000L

inline val Int.h get() = this * 60 * 60 * 1000L

inline val Int.d get() = this * 24 * 60 * 60 * 1000L