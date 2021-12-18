package com.zpthacker.aoc21.day18

import com.zpthacker.aoc21.getInput
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.math.ceil

fun main() {
    val input = getInput(18).trim()
    val lines = input.split("\n")
    val sumNumber = lines.fold(null) { acc: NumberPair?, line ->
        val newNumber = parseNumber(line)
        if (acc == null) {
            newNumber
        } else {
            acc + newNumber
        }
    }!!
    sumNumber.reduce()
    println(sumNumber)
    println(sumNumber.magnitude)
    val magnitudes = mutableMapOf<String, Long>()
    for (line1 in lines) {
        for (line2 in lines) {
            for ((left, right) in listOf(line1 to line2, line2 to line1)) {
                val pair = NumberPair(null)
                pair.left = parseNumber(left)
                pair.right = parseNumber(right)
                pair.reduce()
                magnitudes[left+right] = pair.magnitude
            }
        }
    }
    println(magnitudes.values.maxOrNull())
}

enum class SideMode {
    LEFT,
    RIGHT
}

fun parseNumber(s: String): NumberPair {
    val pairStack = ArrayDeque<NumberPair>()
    val sideStack = ArrayDeque<SideMode>()
    for (char in s) {
        when (char) {
            '[' -> {
                pairStack.add(NumberPair(if (pairStack.isEmpty()) null else pairStack.last()))
                sideStack.add(SideMode.LEFT)
            }
            ']' -> {
                val currentNumber = pairStack.removeLast()
                sideStack.removeLast()
                if (pairStack.isEmpty()) {
                    return currentNumber
                } else {
                    when (sideStack.last()) {
                        SideMode.LEFT -> pairStack.last().left = currentNumber
                        SideMode.RIGHT -> pairStack.last().right = currentNumber
                    }
                }
            }
            ',' -> {
                sideStack.removeLast()
                sideStack.add(SideMode.RIGHT)
            }
            in '0'..'9' -> {
                val currentNumber: NumberPair = pairStack.last()
                when (sideStack.last()) {
                    SideMode.LEFT -> {
                        val literal = currentNumber.left?.let { it as Literal } ?: Literal(currentNumber)
                        literal.literalValue += char
                        currentNumber.left = literal
                    }
                    SideMode.RIGHT -> {
                        val literal = currentNumber.right?.let { it as Literal } ?: Literal(currentNumber)
                        literal.literalValue += char
                        currentNumber.right = literal
                    }
                }
            }
        }
    }
    throw RuntimeException("should've found a root by now")
}

sealed class SnailfishNumber {
    abstract val value: Int
    abstract var parent: NumberPair?
    abstract val magnitude: Long
    val id: String = UUID.randomUUID().toString()
}

class EmptyPair(
    override var parent: NumberPair?,
    var left: SnailfishNumber?,
    var right: SnailfishNumber?
) : SnailfishNumber() {
    private val error = "This number is empty."
    override val value: Int
        get() = throw RuntimeException(error)
    override val magnitude: Long
        get() = throw RuntimeException(error)
}

class Literal(
    override var parent: NumberPair?,
    var literalValue: String = ""
) : SnailfishNumber() {
    override fun toString() = literalValue

    override val value: Int
        get() = literalValue.toInt()

    override val magnitude: Long
        get() = literalValue.toLong()

    fun addNumber(x: Int) {
        this.literalValue = this.literalValue.toInt().plus(x).toString()
    }
}

class NumberPair(
    override var parent: NumberPair?
) : SnailfishNumber() {
    var left: SnailfishNumber? = null
        set(n) {
            field = n
            if (n != null) {
                n.parent = this
            }
        }
    var right: SnailfishNumber? = null
        set(n) {
            field = n
            if (n != null) {
                n.parent = this
            }
        }

    override fun toString() =
        "[${this.left?.toString() ?: '-'},${this.right?.toString() ?: '-'}]"

    override val value: Int
        get() {
            return 0
        }

    operator fun plus(other: NumberPair): NumberPair {
        val pair = NumberPair(null)
        pair.left = this
        pair.right = other
        pair.reduce()
        return pair
    }

    fun reduce() {
        while (true) {
            val explodablePairDepth = detectExplodablePair(4, 0)
            if (explodablePairDepth != null) {
                val (explodablePair, _) = explodablePairDepth
                val leftLit = explodablePair.left as Literal
                val rightLit = explodablePair.right as Literal
                explodablePair.parent!!.explodeLeft(leftLit.value, explodablePair.id, false)
                explodablePair.parent!!.explodeRight(rightLit.value, explodablePair.id, false)
                explodablePair.parent!!.implode(explodablePair)
            } else {
                val splittableLiteral = detectSplittableLiteral(10) ?: return
                val value = splittableLiteral.value
                val parent = splittableLiteral.parent!!
                val newPair = NumberPair(parent)
                newPair.left = Literal(newPair, (value.div(2)).toString())
                newPair.right = Literal(newPair, (ceil(value.toDouble() / 2.0).toInt()).toString())
                parent.replaceSplitChild(splittableLiteral, newPair)
            }
        }
    }

    fun detectExplodablePair(targetDepth: Int, currentDepth: Int): Pair<NumberPair, Int>? {
        return if (currentDepth >= targetDepth) {
            this to currentDepth
        } else {
            val leftPairDepth = (left as? NumberPair)?.detectExplodablePair(targetDepth, currentDepth + 1)
            val rightPairDepth = (right as? NumberPair)?.detectExplodablePair(targetDepth, currentDepth + 1)
            if (rightPairDepth == null) {
                leftPairDepth
            } else {
                if (rightPairDepth.second > (leftPairDepth?.second ?: Int.MIN_VALUE)) {
                    rightPairDepth
                } else {
                    leftPairDepth
                }
            }
        }
    }

    fun detectSplittableLiteral(targetSize: Int): Literal? {
        return (left as? Literal)?.takeIf { it.value >= targetSize }
            ?: (left as? NumberPair)?.detectSplittableLiteral(targetSize)
            ?: (right as? Literal)?.takeIf { it.value >= targetSize }
            ?: (right as? NumberPair)?.detectSplittableLiteral(targetSize)
    }

    fun explodeLeft(x: Int, prevId: String, checkRight: Boolean) {
        if (prevId == this.left!!.id) {
            parent?.explodeLeft(x, this.id, false)
        } else if (!checkRight) {
            (left as? Literal)?.addNumber(x)
                ?: (left as? NumberPair)?.explodeLeft(x, this.id, true)
                ?: parent?.explodeLeft(x, this.id, false)
        } else {
            (right as? Literal)?.addNumber(x)
                ?: (right as? NumberPair)?.explodeLeft(x, this.id, true)
                ?: (left as? NumberPair)?.explodeLeft(x, this.id, true)
                ?: (left as? Literal)?.addNumber(x)
        }
    }

    fun explodeRight(x: Int, prevId: String, checkLeft: Boolean) {
        if (prevId == this.right!!.id) {
            parent?.explodeRight(x, this.id, false)
        } else if (!checkLeft) {
            (right as? Literal)?.addNumber(x)
                ?: (right as? NumberPair)?.explodeRight(x, this.id, true)
                ?: parent?.explodeRight(x, this.id, false)
        } else {
            (left as? Literal)?.addNumber(x)
                ?: (left as? NumberPair)?.explodeRight(x, this.id, true)
                ?: (right as? NumberPair)?.explodeRight(x, this.id, true)
                ?: (right as? Literal)?.addNumber(x)
        }
    }

    fun implode(child: SnailfishNumber) {
        val newLiteral = Literal(this).also { it.literalValue = "0" }
        if (left!!.id == child.id) {
            this.left = newLiteral
        } else {
            this.right = newLiteral
        }
    }

    fun replaceSplitChild(replaced: Literal, replacement: NumberPair) {
        if (left!!.id == replaced.id) {
            this.left = replacement
        } else {
            this.right = replacement
        }
    }

    override val magnitude: Long
        get() = (this.left!!.magnitude * 3) + (this.right!!.magnitude * 2)
}