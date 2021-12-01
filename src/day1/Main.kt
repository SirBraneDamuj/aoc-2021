package com.zpthacker.aoc21.day1

import com.zpthacker.aoc21.getInputLines

fun main() {
    val depths = getInputLines("day1").map(String::toInt)
    val part1Solution = singleDepthIncreases(depths)
    val part2Solution = windowedDepthIncreases(depths)
    println("Part 1 Solution: $part1Solution")
    println("Part 2 Solution: $part2Solution")
}

fun singleDepthIncreases(depths: List<Int>) =
    depths
        .windowed(2)
        .count { (first, second) ->
            second > first
        }

fun windowedDepthIncreases(depths: List<Int>) =
    depths
        .windowed(3)
        .windowed(2)
        .count { (firstWindow, secondWindow) ->
            secondWindow.sum() > firstWindow.sum()
        }
