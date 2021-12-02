package com.zpthacker.aoc21.day2

import com.zpthacker.aoc21.getInputLines

fun main() {
    val lines = getInputLines(2)
    val part1Answer = NaiveSubPosition().performInstructions(lines).answerKey
    val part2Answer = AdvancedSubPosition().performInstructions(lines).answerKey
    println("Day 2: Dive!")
    println("Part 1 answer: $part1Answer")
    println("Part 2 answer: $part2Answer")
}

abstract class SubPosition {
    protected var horizontal: Int = 0
    protected var depth: Int = 0
    val answerKey: Int
        get() = horizontal * depth

    fun performInstructions(instructions: List<String>): SubPosition {
        instructions
            .forEach {
                val tokens = it.split(" ")
                val command = tokens[0]
                val argument = tokens[1].toInt()
                when (command) {
                    "forward" -> forward(argument)
                    "up" -> up(argument)
                    "down" -> down(argument)
                }
            }
        return this
    }

    protected abstract fun forward(distance: Int)
    protected abstract fun up(distance: Int)
    protected abstract fun down(distance: Int)
}

class NaiveSubPosition: SubPosition() {
    override fun forward(distance: Int) {
        horizontal += distance
    }

    override fun up(distance: Int) {
        depth -= distance
    }

    override fun down(distance: Int) {
        depth += distance
    }
}

class AdvancedSubPosition: SubPosition() {
    private var aim: Int = 0

    override fun forward(distance: Int) {
        horizontal += distance
        depth += distance * aim
    }

    override fun up(distance: Int) {
        aim -= distance
    }

    override fun down(distance: Int) {
        aim += distance
    }
}
