package com.zpthacker.aoc21.day3

import com.zpthacker.aoc21.binaryToDecimal

fun lifeSupportRating(diagnosticReport: List<List<Int>>): Int {
    val oxygenGeneratorRating = filterByBitCriteria(diagnosticReport, ::mostCommonDigit).let(List<Int>::binaryToDecimal)
    val co2ScrubberRating = filterByBitCriteria(diagnosticReport, ::leastCommonDigit).let(List<Int>::binaryToDecimal)
    return oxygenGeneratorRating * co2ScrubberRating
}

fun filterByBitCriteria(list: List<List<Int>>, bitCriteria: (List<List<Int>>, Int) -> Int): List<Int> {
    var filteredList = list
    var bitIndex = 0
    while (filteredList.count() > 1) {
        val selectedBit = bitCriteria(filteredList, bitIndex)
        filteredList = filteredList.filter {
            it[bitIndex] == selectedBit
        }
        bitIndex++
    }
    return filteredList.single()
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
