package com.zpthacker.aoc21.day6

import com.zpthacker.aoc21.getInputLines

fun main() {
    val lines = getInputLines(6)
//    val lines = """
//       3,4,3,1,2
//    """.trimIndent().split("\n")
    val fish = lines.first().split(",").map(String::toLong)
    val min = fish.minOrNull()!!
    val max = fish.maxOrNull()!!
    val groupedFish = MutableList(9) { 0L }
    fish.forEach {
        groupedFish[it.toInt()]++
    }
    var fishTracker: List<Long> = groupedFish
    (0 until 256).forEach { dayNumber ->
        val birthFish = fishTracker[0]
        fishTracker = listOf(
            fishTracker[1],
            fishTracker[2],
            fishTracker[3],
            fishTracker[4],
            fishTracker[5],
            fishTracker[6],
            fishTracker[7] + birthFish,
            fishTracker[8],
            birthFish
        )
    }
    println(fishTracker.sum())
}