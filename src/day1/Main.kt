package com.zpthacker.aoc21.day1

import com.zpthacker.aoc21.getInputLines

fun main() {
    val lines = getInputLines("day1")
    val ints = lines.map(String::toInt)
    var previousValue: Int = ints.first()
    var increases = 0
    ints.drop(1).forEach {
        if (it > previousValue)
            increases++
        previousValue = it
    }
    println(increases)
    println(windowed(ints))
}

fun windowed(ints: List<Int>): Int {
    val windows = ints.windowed(3)
    var previousWindow = windows.first()
    var increases = 0
    windows.drop(1).forEach {
        if (it.count() != 3) return@forEach
        if (it.sum() > previousWindow.sum())
            increases++
        previousWindow = it
    }
    return increases
}
