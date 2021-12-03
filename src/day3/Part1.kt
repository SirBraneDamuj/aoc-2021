package com.zpthacker.aoc21.day3

import com.zpthacker.aoc21.binaryToDecimal

fun powerConsumption(diagnosticReport: List<List<Int>>): Int {
    val size = diagnosticReport.first().count()
    val frequencies = diagnosticReport.fold(List(size) { 0 to 0 }) { frequencies, reportLine ->
        frequencies.mapIndexed { i, (zeroes, ones) ->
            if (reportLine[i] == 0) {
                (zeroes + 1) to ones
            } else {
                zeroes to (ones + 1)
            }
        }
    }
    val gammaBinary = frequencies.map { (zeroes, ones) ->
        if (zeroes > ones) 0 else 1
    }
    val epsilonBinary = frequencies.map { (zeroes, ones) ->
        if (zeroes > ones) 1 else 0
    }
    val gammaDecimal = gammaBinary.binaryToDecimal()
    val epsilonDecimal = epsilonBinary.binaryToDecimal()
    return gammaDecimal * epsilonDecimal
}
