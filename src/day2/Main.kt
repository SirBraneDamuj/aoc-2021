package com.zpthacker.aoc21.day2

import com.zpthacker.aoc21.getInputLines

fun main() {
    val lines = getInputLines(2)
    var horizontal = 0
    var depth = 0
    var aim = 0
    lines.forEach {
        val tokens = it.split(" ")
        val distance = tokens[1].toInt()
        when (tokens[0]) {
            "forward" -> {
                horizontal += distance
                depth += aim * distance
            }
            "up" -> {
                aim -= distance
            }
            "down" -> {
                aim += distance
            }
        }
    }
    println(horizontal * depth)
}