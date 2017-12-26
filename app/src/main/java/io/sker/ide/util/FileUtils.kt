package io.sker.ide.util

import java.io.File
import java.io.IOException

const val CANT_READ_FILE = "CAN'T READ FILE"

fun readFile(file: File): String {
    if (!file.canRead()) throw IOException(CANT_READ_FILE)
    return file.inputStream().bufferedReader().use { it.readText() }
}