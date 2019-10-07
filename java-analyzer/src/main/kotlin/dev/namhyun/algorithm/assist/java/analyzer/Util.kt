package dev.namhyun.algorithm.assist.java.analyzer

fun getWorkingDirectory(): String {
    return System.getProperty("user.dir")
}

fun updateWorkingDirectory(directory: String) {
    System.setProperty("user.dir", directory)
}