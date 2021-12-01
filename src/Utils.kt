package com.zpthacker.aoc21

import java.io.File

fun getFile(s: String) = File("input/${s}.txt")

fun getInput(s: String) = getFile(s).readText()

fun getInputLines(s: String) = getFile(s).readLines()

// apply transform to each element
// return the first result that is not null
// based on https://clojuredocs.org/clojure.core/some
fun <T, S> List<T>.some(transform: (T) -> S?): S? {
    for (i in this) {
        val result = transform(i)
        if (result != null) {
            return result
        }
    }
    return null
}