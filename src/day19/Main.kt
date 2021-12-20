package com.zpthacker.aoc21.day19

import com.zpthacker.aoc21.getInput
import kotlin.math.abs
import kotlin.math.max

fun main() {
    val input = getInput(19)

    println("Locating scanners")
    val scanners = input
        .trim()
        .split("\n\n")
        .mapIndexed { i, scannerReport ->
            val coordinates = scannerReport
                .split("\n")
                .drop(1) // the header
                .map {
                    val (x, y, z) = it.split(",").map(String::toInt)
                    Vector3(x, y, z)
                }
                .toSet()
            Scanner(i, coordinates)
        }
    val origin = 0
    println("Scanner $origin is at (0, 0, 0) at rotation index 0")
    scanners[origin].delta = Vector3(0, 0, 0)
    scanners[origin].rotationIndex = 0

    val testCache = mutableMapOf<Pair<Int, Int>, Boolean>()
    while (scanners.any { it.delta == null }) {
        var found = false
        for (i in (0 until scanners.count())) {
            if (scanners[i].delta == null) continue
            for (j in (0 until scanners.count())) {
                if (i == j) continue
                if (scanners[j].delta != null) continue
                if (testCache[i to j] == false) continue
                println("Checking overlap: Known scanner $i vs unknown scanner $j")
                val overlap = detectOverlap(scanners[i], scanners[j])
                if (overlap != null) {
                    scanners[j].delta = overlap.first
                    scanners[j].rotationIndex = overlap.second
                    println("Located scanner: $j ; $overlap")
                    found = true
                } else {
                    testCache[i to j] = false
                }
            }
        }
        println("Status: ${scanners.count { it.delta != null }} scanners located.")
        if (!found) {
            println("Failed to locate all scanners with $origin as the origin.")
            scanners.forEachIndexed { i, scanner ->
                if (scanner.delta == null) {
                    println("Scanner $i: NOT FOUND")
                } else {
                    val s = scanner.delta!!.invert.let { (x, y, z) -> "($x, $y, $z)" }
                    println("Scanner $i: $s ; ${scanner.rotationIndex}")
                }

            }
            throw RuntimeException()
        }
    }
    if (scanners.all { it.delta != null }) {
        val allBeacons = scanners.fold(mutableSetOf<Vector3>()) { beacons, scanner ->
            beacons.also { it.addAll(scanner.realCoordinates) }
        }
        println("Part 1: ${allBeacons.count()}")
        var maxDistance = Int.MIN_VALUE
        for (i in (0 until scanners.count())) {
            for (j in (0 until scanners.count())) {
                if (i == j) continue
                val scanner1 = scanners[i].delta!!.invert
                val scanner2 = scanners[j].delta!!.invert
                val distance =
                    abs(scanner2.x - scanner1.x) + abs(scanner2.y - scanner1.y) + abs(scanner2.z - scanner1.z)
                maxDistance = max(distance, maxDistance)
            }
        }
        println("Part 2: $maxDistance")
    }
}

data class Scanner(
    val number: Int,
    val coordinates: Set<Vector3>,
) {
    val realCoordinates: Set<Vector3>
        get() {
            if (delta == null || rotationIndex == null) throw RuntimeException("Have not identified this scanner's position yet")
            return this.coordinates
                .map {
                    it.rotate(rotationIndex!!) - delta!!
                }
                .toSet()
        }
    var delta: Vector3? = null
    var rotationIndex: Int? = null
}

data class Vector3(
    val x: Int,
    val y: Int,
    val z: Int,
) {
    fun rotate(rotationIndex: Int) = rotators[rotationIndex](this)

    val invert: Vector3
        get() = Vector3(-x, -y, -z)

    operator fun plus(other: Vector3) = Vector3(x + other.x, y + other.y, z + other.z)

    operator fun minus(other: Vector3) = Vector3(x - other.x, y - other.y, z - other.z)
}

fun detectOverlap(scanner1: Scanner, scanner2: Scanner): Pair<Vector3, Int>? {
    val realCoordinates = scanner1.realCoordinates
    (0 until 24).forEach { rotationIndex ->
        for (testCoordinate in scanner2.coordinates) {
            for (rootCoordinate in realCoordinates) {
                val rotation = testCoordinate.rotate(rotationIndex)
                val testDifference = rotation - rootCoordinate
                val matches = mutableSetOf(rootCoordinate)
                val testCoordinates = (scanner2.coordinates - testCoordinate)
                    .map { it.rotate(rotationIndex) }
                    .toMutableSet()
                val rootCoordinates = (realCoordinates - rootCoordinate).toMutableSet()
                var matchFound: Boolean
                do {
                    matchFound = false
                    for (test2 in testCoordinates.toSet()) {
                        for (root2 in rootCoordinates.toSet()) {
                            if (test2 - root2 == testDifference) {
                                matches.add(root2)
                                testCoordinates.remove(test2)
                                rootCoordinates.remove(root2)
                                matchFound = true
                                break
                            }
                        }
                        if (matchFound) break
                    }
                } while (matchFound)
                if (matches.count() >= 12) {
                    return testDifference to rotationIndex
                }
            }
        }
    }
    return null
}

private val rotators = listOf<(Vector3) -> Vector3>(
    { (x, y, z) -> Vector3(x, y, z) },
    { (x, y, z) -> Vector3(x, z, -y) },
    { (x, y, z) -> Vector3(x, -y, -z) },
    { (x, y, z) -> Vector3(x, -z, y) },

    { (x, y, z) -> Vector3(y, -x, z) },
    { (x, y, z) -> Vector3(y, z, x) },
    { (x, y, z) -> Vector3(y, x, -z) },
    { (x, y, z) -> Vector3(y, -z, -x) },

    { (x, y, z) -> Vector3(-x, -y, z) },
    { (x, y, z) -> Vector3(-x, -z, -y) },
    { (x, y, z) -> Vector3(-x, y, -z) },
    { (x, y, z) -> Vector3(-x, z, y) },

    { (x, y, z) -> Vector3(-y, x, z) },
    { (x, y, z) -> Vector3(-y, -z, x) },
    { (x, y, z) -> Vector3(-y, -x, -z) },
    { (x, y, z) -> Vector3(-y, z, -x) },

    { (x, y, z) -> Vector3(z, y, -x) },
    { (x, y, z) -> Vector3(z, x, y) },
    { (x, y, z) -> Vector3(z, -y, x) },
    { (x, y, z) -> Vector3(z, -x, -y) },

    { (x, y, z) -> Vector3(-z, -y, -x) },
    { (x, y, z) -> Vector3(-z, -x, y) },
    { (x, y, z) -> Vector3(-z, y, x) },
    { (x, y, z) -> Vector3(-z, x, -y) },
)
