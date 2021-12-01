package com.zpthacker.aoc21

import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun getInputLines(puzzleNumber: Int) =
    getInput(puzzleNumber)
        .trim()
        .split("\n")

fun getInput(puzzleNumber: Int): String =
    File("input/day$puzzleNumber.txt")
        .takeIf(File::exists)
        ?.readText()
        ?: getRemoteInput(puzzleNumber)

fun getRemoteInput(puzzleNumber: Int): String =
    client
        .send(
            HttpRequest
                .newBuilder(URI.create("https://adventofcode.com/2021/day/$puzzleNumber/input"))
                .header("Cookie", "session=$credentials")
                .GET()
                .build(),
            HttpResponse.BodyHandlers.ofString()
        )
        .let { response ->
            val body = response.body()
            val status = response.statusCode()
            response
                .takeIf { status in (200..299) }
                ?: throw RuntimeException("Error retrieving remote input: $status\n$body")
        }
        .body()
        .also {
            File("input/day$puzzleNumber.txt").writeText(it)
        }

private val client: HttpClient by lazy {
    HttpClient.newHttpClient()
}

private val credentials: String by lazy {
    File("credentials.txt").readText()
}
