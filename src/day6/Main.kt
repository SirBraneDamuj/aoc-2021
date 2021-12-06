package com.zpthacker.aoc21.day6

import com.zpthacker.aoc21.getInputLines

fun main() {
    val initialFish = getInputLines(6)
        .first()
        .split(",")
        .map(String::toInt)
    val groupedFish = MutableList(9) { 0L }
    initialFish.forEach {
        groupedFish[it]++
    }
    var fishTracker: List<Long> = groupedFish
    repeat(256) {
        val birthFish = fishTracker[0]
        fishTracker = listOf(
            fishTracker[1], // 0
            fishTracker[2], // 1
            fishTracker[3], // 2
            fishTracker[4], // 3
            fishTracker[5], // 4
            fishTracker[6], // 5
            fishTracker[7] + birthFish, // 6
            fishTracker[8], // 7
            birthFish // 8
        )
    }
    println(fishTracker.sum())
}