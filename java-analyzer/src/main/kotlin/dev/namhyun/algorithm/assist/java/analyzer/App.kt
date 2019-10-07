package dev.namhyun.algorithm.assist.java.analyzer

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import dev.namhyun.algorithm.assist.java.analyzer.model.ExceptionFrame
import dev.namhyun.algorithm.assist.java.analyzer.model.StepFrame
import java.io.FileWriter
import java.io.PrintWriter

class App : CliktCommand() {
    private val defaultWorkingDir = getWorkingDirectory()

    private val className: String by argument()
        .default("Main")

    private val workingDir: String by option("-d", "--dir", help = "")
        .default(defaultWorkingDir)

    private val verbose: Boolean by option("-v", "--verbose", help = "")
        .flag()

    private val outputFile by option("-o", "--output", help = "")
        .file(writable = true, folderOkay = false)

    override fun run() {
        println(
            """
            Algorithm Assistant
            (Java Analyzer)
            
            - className: $className
            - workingDir: $workingDir
            - verbose: $verbose
            - outputFile: $outputFile
            
            """.trimIndent()
        )

        if (workingDir != defaultWorkingDir) {
            updateWorkingDirectory(workingDir)
        }

        var writter = PrintWriter(System.out, true)
        if (outputFile != null) {
            writter = PrintWriter(FileWriter(outputFile!!.path))
        }

        val analyzer = Analyzer(className = className, verbose = verbose, writter = writter)
        val frames = analyzer.analyze()
        frames.forEach {
            println("${it.javaClass.simpleName}=${it.methodName}:${it.line}")
            if (it is StepFrame) {
                it.variables.forEach {
                    println(it)
                }
                println()
            } else if (it is ExceptionFrame) {
                println(it.exceptionName)
                println()
            }
        }
        println("References per line")
        println(analyzer.lineReferencesMap)
    }
}


fun main(args: Array<String>) {
    App().main(args)
}