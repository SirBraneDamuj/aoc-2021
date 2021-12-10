package com.zpthacker.aoc21.day10

import com.zpthacker.aoc21.getInput

fun main() {
    val input = getInput(10)
//    val input = """
//        [({(<(())[]>[[{[]{<()<>>
//        [(()[<>])]({[<{<<[]>>(
//        {([(<{}[<>[]}>{[]{[(<()>
//        (((({<>}<{<{<>}{[]{[]{}
//        [[<[([]))<([[{}[[()]]]
//        [{[{({}]{}}([{[{{{}}([]
//        {<[[]]>}<{[{[{[]{()[[[]
//        [<(<(<(<{}))><([]([]()
//        <{([([[(<>()){}]>(<<{{
//        <{([{{}}[<[[[<>{}]]]>[]]
//    """.trimIndent()
    val lines = input.split("\n")
    val nonCorrupted = lines
        .filter {
            val tokens = it.toList()
            val stack = ArrayDeque<Char>()
            for (token in tokens) {
                when (token) {
                    '(', '[', '<', '{' -> stack.add(token)
                    else -> {
                        val expected = when (stack.removeLast()) {
                            '(' -> ')'
                            '<' -> '>'
                            '[' -> ']'
                            '{' -> '}'
                            else -> throw RuntimeException("oh no")
                        }
                        if (expected != token) {
                            return@filter false
                        }
                    }
                }
            }
            true
        }
    val scores = nonCorrupted
        .map {
            val tokens = it.toList()
            val stack = ArrayDeque<Char>()
            for (token in tokens) {
                when (token) {
                    '(', '[', '<', '{' -> stack.add(token)
                    else -> {
                        stack.removeLast()
                    }
                }
            }
            var score = 0L
            stack
                .reversed()
                .forEach { ch ->
                    val baseScore = when (ch) {
                        '(' -> 1
                        '<' -> 4
                        '[' -> 2
                        '{' -> 3
                        else -> throw RuntimeException("oh no")
                    }.toInt()
                    score *= 5
                    score += baseScore
                }
            score
        }
        .sorted()
    val answer = scores[scores.count() / 2]
    println(answer)
}