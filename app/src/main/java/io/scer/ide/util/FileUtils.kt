package io.scer.ide.util

import java.io.File
import java.io.IOException

const val CANT_READ_FILE = "CAN'T READ FILE"
const val CANT_WRITE_FILE = "CAN'T WRITE FILE"

fun readFile(file: File): String {
    if (!file.canRead()) throw IOException(CANT_READ_FILE)
    return file.inputStream().bufferedReader().use { it.readText() }
}

fun writeFile(file: File, text: String) {
    if (!file.canWrite()) throw IOException(CANT_WRITE_FILE)
    file.outputStream().bufferedWriter().use { it.write(text) }
}