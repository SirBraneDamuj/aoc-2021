package com.zpthacker.aoc21.day18

import com.zpthacker.aoc21.getInput
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.math.ceil

fun main() {
    val input = getInput(18).trim()
    val lines = input.split("\n")
    val sumNumber = lines.fold(null) { acc: RootPair?, line ->
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
    val magnitudes = mutableMapOf<String, Int>()
    for (line1 in lines) {
        for (line2 in lines) {
            for ((left, right) in listOf(line1 to line2, line2 to line1)) {
                val pair = parseNumber(left) + parseNumber(right)
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

fun parseNumber(s: String): RootPair {
    val pairStack = ArrayDeque<EmptyPair>()
    val sideStack = ArrayDeque<SideMode>()
    for (char in s) {
        when (char) {
            '[' -> {
                val parent = if (pairStack.isEmpty()) null else pairStack.last()
                pairStack.add(EmptyPair(parent))
                sideStack.add(SideMode.LEFT)
            }
            ']' -> {
                val currentNumber = pairStack.removeLast()
                sideStack.removeLast()
                if (pairStack.isEmpty()) {
                    return currentNumber.reify() as RootPair
                } else {
                    when (sideStack.last()) {
                        SideMode.LEFT -> pairStack.last().left = currentNumber.reify()
                        SideMode.RIGHT -> pairStack.last().right = currentNumber.reify()
                    }
                }
            }
            ',' -> {
                sideStack.removeLast()
                sideStack.add(SideMode.RIGHT)
            }
            in '0'..'9' -> {
                val currentNumber = pairStack.last()
                when (sideStack.last()) {
                    SideMode.LEFT -> {
                        val literal = currentNumber
                            .left
                            .takeIf { it !is NullNumber }
                            ?.let { it as Literal }
                            ?: Literal("")
                        literal.literalValue += char
                        currentNumber.left = literal
                    }
                    SideMode.RIGHT -> {
                        val literal = currentNumber
                            .right
                            .takeIf { it !is NullNumber }
                            ?.let { it as Literal }
                            ?: Literal("")
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
    abstract var parent: SnailfishPair?
    abstract val magnitude: Int
    val id: String = UUID.randomUUID().toString()
}

abstract class SnailfishPair(): SnailfishNumber() {
    abstract var left: SnailfishNumber
    abstract var right: SnailfishNumber

    fun explodeLeft(x: Int, prevId: String, checkRight: Boolean) {
        if (prevId == this.left.id) {
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
        if (prevId == this.right.id) {
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
        val newLiteral = Literal("0")
        if (left.id == child.id) {
            this.left = newLiteral
        } else {
            this.right = newLiteral
        }
    }

    fun replaceSplitChild(replaced: Literal, replacement: NumberPair) {
        if (left.id == replaced.id) {
            this.left = replacement
        } else {
            this.right = replacement
        }
    }

    fun detectExplodablePair(targetDepth: Int, currentDepth: Int): Pair<SnailfishPair, Int>? {
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

    override val magnitude: Int
        get() = (this.left.magnitude * 3) + (this.right.magnitude * 2)

    override fun toString() =
        "[${this.left.toString() ?: '-'},${this.right.toString() ?: '-'}]"
}

class RootPair(
    left: SnailfishNumber,
    right: SnailfishNumber
): SnailfishPair() {
    init {
        left.parent = this
        right.parent = this
    }

    operator fun plus(other: RootPair): RootPair {
        val newLeft = NumberPair(this.left, this.right)
        val newRight = NumberPair(other.left, other.right)
        return RootPair(newLeft, newRight).also(RootPair::reduce)
    }

    override var left: SnailfishNumber = left
        set(newLeft) {
            newLeft.parent = this
            field = newLeft
        }
    override var right: SnailfishNumber = right
        set(newRight) {
            newRight.parent = this
            field = newRight
        }
    override var parent: SnailfishPair?
        get() = null
        set(value) { throw RuntimeException("This is the root pair.") }

    fun reduce() {
        while (true) {
            val explodablePairDepth = detectExplodablePair(4, 0)
            if (explodablePairDepth != null) {
                val (explodablePair, _) = explodablePairDepth
                val leftLit = explodablePair.left as? Literal
                val rightLit = explodablePair.right as? Literal
                if (leftLit == null) {
                    println("oh dear")
                    throw RuntimeException()
                }
                if (rightLit == null) {
                    println("oh dear")
                    throw RuntimeException()
                }
                explodablePair.parent!!.explodeLeft(leftLit.value, explodablePair.id, false)
                explodablePair.parent!!.explodeRight(rightLit.value, explodablePair.id, false)
                explodablePair.parent!!.implode(explodablePair)
            } else {
                val splittableLiteral = detectSplittableLiteral(10) ?: return
                val value = splittableLiteral.value
                val parent = splittableLiteral.parent!!
                val newPair = NumberPair(
                    Literal((value.div(2)).toString()),
                    Literal((ceil(value.toDouble() / 2.0).toInt()).toString()),
                )
                parent.replaceSplitChild(splittableLiteral, newPair)
            }
        }
    }
}

class NullNumber() : SnailfishNumber() {
    override var parent: SnailfishPair? = null
    override val magnitude: Int
        get() = throw RuntimeException("this number is null")
}

class EmptyPair(
    override var parent: SnailfishPair?,
) : SnailfishPair() {
    override var left: SnailfishNumber = NullNumber()
    override var right: SnailfishNumber = NullNumber()

    private val error = "This number is empty."
    override val magnitude: Int
        get() = throw RuntimeException(error)

    fun reify(): SnailfishPair {
        if (this.left is NullNumber || this.right is NullNumber) {
            throw RuntimeException("Tried to reify an incomplete pair")
        }
        return if (this.parent == null) {
            RootPair(this.left, this.right)
        } else {
            return NumberPair(this.left, this.right, this.parent)
        }
    }
}

class Literal(
    var literalValue: String = ""
) : SnailfishNumber() {
    override fun toString() = literalValue
    override var parent: SnailfishPair? = null

    val value: Int
        get() = literalValue.toInt()

    override val magnitude: Int
        get() = literalValue.toInt()

    fun addNumber(x: Int) {
        this.literalValue = this.literalValue.toInt().plus(x).toString()
    }
}

class NumberPair(
    left: SnailfishNumber,
    right: SnailfishNumber,
    override var parent: SnailfishPair? = null,
) : SnailfishPair() {
    init {
        left.parent = this
        right.parent = this
    }

    override var left: SnailfishNumber = left
        set(n) {
            field = n
            n.parent = this
        }
    override var right: SnailfishNumber = right
        set(n) {
            field = n
            n.parent = this
        }
}