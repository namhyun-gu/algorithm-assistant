package dev.namhyun.java.analyzer

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import dev.namhyun.java.analyzer.model.ExceptionFrame
import dev.namhyun.java.analyzer.model.StepFrame
import java.io.FileWriter
import java.io.PrintWriter

class App : CliktCommand() {
    private val defaultWorkingDir = getWorkingDirectory()

    private val mainClassName: String by argument()
        .default("Main")

    private val verbose: Boolean by option("-v", "--verbose", help = "")
        .flag()

    private val workingDir: String by option("-d", "--dir", help = "")
        .default(defaultWorkingDir)

    private val inputFile by option("-i", "--input", help = "")
        .file(readable = true, folderOkay = false, exists = true)

    private val outputFile by option("-o", "--output", help = "")
        .file(folderOkay = false)

    override fun run() {
        println(
            """
            Java Analyzer
            
            - mainClassName: $mainClassName
            - inputFile: $inputFile
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

        val analyzer = Analyzer(className = mainClassName, inputFile = inputFile, verbose = verbose)
        val output = analyzer.analyze()

        writter.println("=== Output ===\n")
        writter.println(output)

        writter.println("=== Analyze frames (size: ${analyzer.analyzeFrames.size}) ===\n")

        analyzer.analyzeFrames.forEach {
            writter.println("${it.methodName}:${it.line}")
            if (it is StepFrame) {
                it.variables.forEach {
                    writter.println("\t$it")
                }
            } else if (it is ExceptionFrame) {
                writter.println("\t${it.exceptionName}")
            }
            writter.println()
        }

        writter.println("\n=== References per line ===\n")
        analyzer.lineReferencesMap.forEach { line, referenceCount ->
            writter.println("\t$line: $referenceCount")
        }

        if (outputFile != null) {
            writter.flush()
        }
    }
}

fun main(args: Array<String>) {
    App().main(args)
}