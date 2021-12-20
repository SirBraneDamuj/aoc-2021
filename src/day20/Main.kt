package com.zpthacker.aoc21.day20

import com.zpthacker.aoc21.Grid
import com.zpthacker.aoc21.binaryToDecimal
import com.zpthacker.aoc21.getInput

fun main() {
    val input = getInput(20)
    val (enhancementString, imageString) = input.trim().split("\n\n")
    val imageLines = imageString.split("\n")
    val image = Grid(imageLines.count(), imageLines.first().length)
    imageLines.forEachIndexed { y, line ->
        line.forEachIndexed { x, ch ->
            image.addPoint(x, y, if (ch == '#') 1 else 0)
        }
    }
    var currentImage = image
    repeat(2) {
        currentImage = enhance(currentImage, enhancementString, it)
    }
    println("Part 1: ${currentImage.allPoints.count { it.value == 1 }}")
    currentImage = image
    repeat(50) {
        currentImage = enhance(currentImage, enhancementString, it)
    }
    println("Part 2: ${currentImage.allPoints.count { it.value == 1 }}")
}

fun enhance(image: Grid, enhancementString: String, step: Int): Grid {
    val referenceGrid = Grid(
        height = image.height + 4,
        width = image.width + 4,
        defaultValue = if (enhancementString[0] == '#') {
            if (step % 2 == 1) 1 else 0
        } else {
            0
        }
    )
    for (point in image.allPoints) {
        referenceGrid.addPoint(
            x = point.x+2,
            y = point.y+2,
            value = point.value
        )
    }
    val newGrid = Grid(
        width = referenceGrid.width - 2,
        height = referenceGrid.height - 2,
        defaultValue = referenceGrid.defaultValue
    )
    for (newPoint in newGrid.allPoints) {
        val referencePoint = referenceGrid.getPoint((newPoint.x + 1) to (newPoint.y + 1))
        val neighbors = (referencePoint.allNeighbors + referencePoint).sortedWith { a, b ->
            if (a.y == b.y) {
                a.x.compareTo(b.x)
            } else {
                a.y.compareTo(b.y)
            }
        }
        val binaryString = neighbors.map(Grid.Point::value).joinToString("")
        val enhancementPoint = binaryString.binaryToDecimal()
        newGrid.addPoint(
            x = newPoint.x,
            y = newPoint.y,
            value = if (enhancementString[enhancementPoint] == '#') 1 else 0
        )
    }
    return newGrid
}
