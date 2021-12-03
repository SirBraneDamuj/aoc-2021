package com.zpthacker.aoc21.day3

import com.zpthacker.aoc21.getInputLines

fun main() {
    val lines = getInputLines(3).map { number -> number.map { if (it == '0') 0 else 1 } }
    val powerConsumption = powerConsumption(lines)
    val lifeSupportRating = lifeSupportRating(lines)
    println("Day 3: Binary Diagnostic")
    println("Part 1: $powerConsumption")
    println("Part 2: $lifeSupportRating")
}
