package com.zpthacker.aoc21.day3

import com.zpthacker.aoc21.getInputLines

fun main() {
    val lines = getInputLines(3)
//    val lines = """
//        00100
//        11110
//        10110
//        10111
//        10101
//        01111
//        00111
//        11100
//        10000
//        11001
//        00010
//        01010
//    """.trimIndent().split("\n")
    val size = lines.first().count()
    val frequencies = lines.fold(List(size) { 0 to 0 }) { frequencies, reportLine ->
        frequencies.mapIndexed { i, (zeroes, ones) ->
            if (reportLine[i] == '0') {
                (zeroes + 1) to ones
            } else {
                zeroes to (ones + 1)
            }
        }
    }
    val gamma = frequencies.map { (zeroes, ones) ->
        if (zeroes > ones) 0 else 1
    }
    val epsilon = frequencies.map { (zeroes, ones) ->
        if (zeroes > ones) 1 else 0
    }
    val gammaDec = gamma.reversed().foldIndexed(0) { i, acc, digit ->
        acc + (digit * Math.pow(2.0, i.toDouble()).toInt())
    }
    val epDec = epsilon.reversed().foldIndexed(0) { i, acc, digit ->
        acc + (digit * Math.pow(2.0, i.toDouble()).toInt())
    }
    println(gammaDec * epDec)
    var oxGenList = lines.map { line -> line.map { digit -> if (digit == '0') 0 else 1 } }
    var oxIndex = 0
    while (oxGenList.count() > 1) {
        val mostCommon = mostCommonDigit(oxGenList, oxIndex)
        oxGenList = oxGenList.filter {
            it[oxIndex] == mostCommon
        }
        oxIndex++
    }
    var co2List = lines.map { line -> line.map { digit -> if (digit == '0') 0 else 1 } }
    var co2Index = 0
    while (co2List.count() > 1) {
        val mostCommon = leastCommonDigit(co2List, co2Index)
        co2List = co2List.filter {
            it[co2Index] == mostCommon
        }
        co2Index++
    }
    val oxDec = oxGenList.single().reversed().foldIndexed(0) { i, acc, digit ->
        acc + (digit * Math.pow(2.0, i.toDouble()).toInt())
    }
    val co2Dec = co2List.single().reversed().foldIndexed(0) { i, acc, digit ->
        acc + (digit * Math.pow(2.0, i.toDouble()).toInt())
    }
    println(oxDec * co2Dec)
}

fun mostCommonDigit(list: List<List<Int>>, index: Int): Int {
    var zeroes = 0
    var ones = 0
    for (s in list) {
        val digit = s[index]
        if (digit == 0) zeroes++
        else if (digit == 1) ones++
    }
    return if (ones >= zeroes) 1 else 0
}

fun leastCommonDigit(list: List<List<Int>>, index: Int): Int {
    var zeroes = 0
    var ones = 0
    for (s in list) {
        val digit = s[index]
        if (digit == 0) zeroes++
        else if (digit == 1) ones++
    }
    return if (zeroes <= ones) 0 else 1
}