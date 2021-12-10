package com.zpthacker.aoc21.day10

import com.zpthacker.aoc21.getInput

fun main() {
    val input = getInput(10)
    val lines = input.split("\n")
    val scores = processLines(lines)
    println("Day 10: Syntax Scoring")
    println("Part 1 Answer: ${scores["errorScore"]!!}")
    println("Part 2 Answer: ${scores["autocompleteScore"]!!}")
}

fun processLines(lines: List<String>) =
    lines
        .map {
            val tokens = it.toList()
            val stack = ArrayDeque<Char>()
            for (token in tokens) {
                when (token) {
                    '(', '[', '<', '{' -> stack.add(token)
                    else -> {
                        val expected = closer(stack.removeLast())
                        if (token != expected) {
                            return@map "corrupted" to errorScore(token)
                        }
                    }
                }
            }
            "incomplete" to autoCompleteScore(stack)
        }
        .groupBy(
            keySelector = Pair<String, Long>::first,
            valueTransform = Pair<String, Long>::second
        )
        .let { scores ->
            mapOf(
                "errorScore" to scores["corrupted"]!!.sum(),
                "autocompleteScore" to scores["incomplete"]!!.let {
                    it.sorted()[it.count() / 2]
                }
            )
        }

fun closer(char: Char) =
    when (char) {
        '(' -> ')'
        '[' -> ']'
        '{' -> '}'
        '<' -> '>'
        else -> throw RuntimeException()
    }

fun errorScore(char: Char) =
    when (char) {
        ')' -> 3
        ']' -> 57
        '}' -> 1197
        '>' -> 25137
        else -> throw RuntimeException()
    }.toLong()

fun autoCompleteScore(stack: List<Char>) =
    stack
        .reversed()
        .fold(0L) { score, ch ->
            (score * 5) + autoCompleteBaseScore(ch)
        }


fun autoCompleteBaseScore(char: Char) =
    when (char) {
        '(' -> 1
        '[' -> 2
        '{' -> 3
        '<' -> 4
        else -> throw RuntimeException()
    }.toInt()
