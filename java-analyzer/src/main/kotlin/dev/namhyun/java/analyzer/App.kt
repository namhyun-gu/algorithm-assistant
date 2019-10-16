package dev.namhyun.java.analyzer

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import java.io.FileWriter
import java.io.PrintWriter

class App : CliktCommand(
    name = "java-analyzer",
    help = "Analyze '.class' file by Java Debug Interface.",
    printHelpOnEmptyArgs = true
) {
    private val defaultWorkingDir = getWorkingDirectory()

    private val mainClassName: String by argument()
        .default("Main")

    private val workingDir: String by option("-d", "--dir", help = "Set working directory, defaults current directory")
        .default(defaultWorkingDir)

    private val inputFile by option("-i", "--input", help = "Redirection standard input using file")
        .file(readable = true, folderOkay = false, exists = true)

    private val outputFile by option("-o", "--output", help = "Redirection analyze output using file")
        .file(folderOkay = false)

    private val jsonResult: Boolean by option("--json", help = "Set output json format")
        .flag()

    private val verbose: Boolean by option("-v", "--verbose", help = "Print analyze logs")
        .flag()

    override fun run() {
        if (verbose) {
            println(
                """
                    Argument, Options Info
                    
                    - mainClassName: $mainClassName
                    - workingDir: $workingDir
                    - inputFile: $inputFile
                    - outputFile: $outputFile
                    - jsonResult: $jsonResult
                    - verbose: $verbose
                """.trimIndent()
            )
        }

        if (workingDir != defaultWorkingDir) {
            updateWorkingDirectory(workingDir)
        }

        var printWriter = PrintWriter(System.out, true)
        if (outputFile != null) {
            printWriter = PrintWriter(FileWriter(outputFile!!.path))
        }

        val analyzer = Analyzer(className = mainClassName, inputFile = inputFile, verbose = verbose)
        val output = analyzer.analyze()

        val resultWriter: ResultWriter
        resultWriter = if (jsonResult) {
            JsonWriter(printWriter)
        } else {
            DefaultWriter(printWriter)
        }
        resultWriter.write(output, analyzer.analyzeFrames, analyzer.lineReferencesMap)
    }
}

fun main(args: Array<String>) {
    App().main(args)
}