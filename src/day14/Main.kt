package com.zpthacker.aoc21.day14

import com.zpthacker.aoc21.getInput

fun main() {
    val input = getInput(14)
    val (template, pairs) = input
        .trim()
        .split("\n")
        .filter(String::isNotBlank)
        .partition {
            !it.contains("->")
        }
    val rules = pairs.associate {
        val (left, right) = it.split(" -> ")
        left to right
    }
    var counts = mutableMapOf<String, Long>()
    for (pair in template.single().windowed(2)) {
        counts[pair] = (counts[pair] ?: 0L) + 1
    }
    for (i in (0 until 40)) {
        val newCounts = mutableMapOf<String, Long>()
        counts.forEach { (pair, count) ->
            val rule = rules[pair]!!
            val newPairs = "${pair[0]}$rule${pair[1]}".windowed(2)
            for (newPair in newPairs) {
                newCounts[newPair] = (newCounts[newPair] ?: 0) + count
            }
        }
        counts = newCounts
    }
    val firstLetter = template.single().first().toString()
    val lastLetter = template.single().last().toString()
    val letterCounts = counts
        .flatMap { (pair, count) ->
            listOf(
                pair[0].toString() to count,
                pair[1].toString() to count,
            )
        }
        .groupBy { (letter, _) -> letter }
        .map { (letter, letterCounts) ->
            var sum = letterCounts.sumOf { (_, count) -> count } / 2
            if (letter == lastLetter || letter == firstLetter) {
                sum += 1
            }
            letter to sum
        }
        .toMap()

    val max = letterCounts.maxOf { (_, count) -> count }
    val min = letterCounts.minOf { (_, count) -> count }
    println(max - min)
}
