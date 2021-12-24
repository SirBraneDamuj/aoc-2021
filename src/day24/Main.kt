package com.zpthacker.aoc21.day24

import com.zpthacker.aoc21.getInput
import java.util.Comparator

fun main() {
    val instructions = getInput(24).trim().split("\n")
    val blocks = instructions.chunked(18)
    val pairs = parseStack(blocks)
    val evaluator = Evaluator(pairs)
    val max = evaluator.evaluateMin()
    println("Part 1: Maximum")
    println("I think the max is: $max")
    val maxAlu = Alu(instructions, max.toString().map(Char::digitToInt).iterator())
    maxAlu.execute()
    val maxDecision = if (maxAlu.getV(Variable.Z) == 0L) {
        "valid"
    } else {
        "invalid"
    }
    println("The ALU says this number is $maxDecision!")
    val min = evaluator.evaluateMax()
    println("Part 2: Maximum")
    println("I think the min is: $min")
    val minAlu = Alu(instructions, max.toString().map(Char::digitToInt).iterator())
    minAlu.execute()
    val minDecision = if (minAlu.getV(Variable.Z) == 0L) {
        "valid"
    } else {
        "invalid"
    }
    println("The ALU says this number is $minDecision!")
}

enum class BlockType {
    PUSH,
    POP,
}

class Block(
    val a: Int,
    val b: Int,
    val type: BlockType,
) {
    fun x(z: Long) =
        (z % 26) + a

    fun y(w: Int) =
        w + b
}

class Evaluator(
    val pairs: List<StackPair>,
) {
    fun evaluateMin(): Long {
         return evaluate { a, b ->
            if (a.first == b.first) {
                a.second.compareTo(b.second)
            } else {
                a.first.compareTo(b.first)
            }
        }
    }

    fun evaluateMax(): Long {
        return evaluate { a, b ->
            if (a.first == b.first) {
                b.second.compareTo(a.second)
            } else {
                b.first.compareTo(a.first)
            }
        }
    }

    private fun evaluate(comparator: Comparator<Triple<Int, Int, Long>>): Long {
        val acc = MutableList(14) { 0 }
        var lastZ = 0L
        for (stackPair in pairs) {
            val valids = evaluatePair(stackPair.push, stackPair.pop, lastZ)
            val (first, second, z) = valids
                .sortedWith(comparator)
                .first()
            acc[stackPair.pushN] = first
            acc[stackPair.popN] = second
            lastZ = z
        }
        return acc.joinToString("").toLong()
    }

    fun evaluatePair(
        push: Block,
        pop: Block,
        z: Long,
    ): List<Triple<Int, Int, Long>> {
        val valids = mutableListOf<Triple<Int, Int, Long>>()
        for (w1 in (1..9)) {
            for (w2 in (1..9)) {
                val x1 = push.x(z)
                val y1 = push.y(w1)
                val z1 = if (x1 == w1.toLong()) {
                    z
                } else {
                    (z * 26) + y1
                }
                val x2 = pop.x(z1)
                val y2 = pop.y(w2)
                val z2 = if (x2 == w2.toLong()) {
                    z
                } else {
                    (z * 26) + y2
                }
                if (z == z2) {
                    valids.add(Triple(w1, w2, z1))
                }
            }
        }
        return valids
    }
}

class StackPair(
    val push: Block,
    val pop: Block,
    val pushN: Int,
    val popN: Int,
)

fun parseStack(blocks: List<List<String>>): List<StackPair> {
    val stack = ArrayDeque<Pair<Block, Int>>()
    val pairs = mutableListOf<StackPair>()
    blocks.forEachIndexed { i, blockInstructions ->
        val block = parseBlock(blockInstructions)
        when (block.type) {
            BlockType.PUSH -> {
                stack.add(block to i)
            }
            BlockType.POP -> {
                val (push, pushI) = stack.removeLast()
                val stackPair = StackPair(
                    push,
                    block,
                    pushI,
                    i
                )
                pairs.add(stackPair)
            }
        }
    }
    return pairs.reversed()
}

fun parseBlock(block: List<String>): Block {
    val lastToken = { s: String -> s.split(" ")[2].toInt() }
    val a = lastToken(block[5])
    val b = lastToken(block[15])
    val blockType = when (lastToken(block[4])) {
        26 -> BlockType.POP
        1 -> BlockType.PUSH
        else -> throw RuntimeException("boo")
    }
    return Block(a, b, blockType)
}

enum class Variable {
    W,
    X,
    Y,
    Z,
}

class DivideByZeroException : RuntimeException()

class Alu(
    private val instructions: List<String>,
    private val input: Iterator<Int>,
    w: Long = 0,
    x: Long = 0,
    y: Long = 0,
    z: Long = 0,
) {
    val variables = mutableMapOf(
        Variable.W to w,
        Variable.X to x,
        Variable.Y to y,
        Variable.Z to z,
    )

    fun getV(variable: Variable) = variables[variable]!!
    private fun setV(variable: Variable, n: Long) = variables.put(variable, n)

    fun execute() {
        for (instruction in instructions) {
            val tokens = instruction.split(" ")
            val command = tokens.first()
            val firstArgument = Variable.valueOf(tokens[1].uppercase())
            if (command == "inp") {
                val newInput = input.next()
                inp(firstArgument, newInput)
            } else {
                val secondArgument = tokens[2].let {
                    when (it) {
                        "w" -> Variable.W
                        "x" -> Variable.X
                        "y" -> Variable.Y
                        "z" -> Variable.Z
                        else -> it.toLong()
                    }
                }
                when (secondArgument) {
                    is Long -> {
                        when (command) {
                            "add" -> add(firstArgument, secondArgument)
                            "mul" -> mul(firstArgument, secondArgument)
                            "div" -> div(firstArgument, secondArgument)
                            "mod" -> mod(firstArgument, secondArgument)
                            "eql" -> eql(firstArgument, secondArgument)
                            else -> throw RuntimeException()
                        }
                    }
                    is Variable -> {
                        when (command) {
                            "add" -> add(firstArgument, secondArgument)
                            "mul" -> mul(firstArgument, secondArgument)
                            "div" -> div(firstArgument, secondArgument)
                            "mod" -> mod(firstArgument, secondArgument)
                            "eql" -> eql(firstArgument, secondArgument)
                            else -> throw RuntimeException()
                        }
                    }
                    else -> throw RuntimeException("what even happened here")
                }
            }
        }
    }

    fun inp(v: Variable, n: Int) {
        setV(v, n.toLong())
    }

    fun add(augend: Variable, addend: Long) {
        val sum = getV(augend) + addend
        setV(augend, sum)
    }

    fun add(augend: Variable, addend: Variable) {
        val sum = getV(augend) + getV(addend)
        setV(augend, sum)
    }

    fun mul(multiplicand: Variable, multiplier: Long) {
        val product = getV(multiplicand) * multiplier
        setV(multiplicand, product)
    }

    fun mul(multiplicand: Variable, multiplier: Variable) {
        val product = getV(multiplicand) * getV(multiplier)
        setV(multiplicand, product)
    }

    fun div(dividend: Variable, divisor: Long) {
        if (divisor == 0L) {
            throw DivideByZeroException()
        }
        val quotient = getV(dividend) / divisor
        setV(dividend, quotient)
    }

    fun div(dividend: Variable, divisor: Variable) {
        if (getV(divisor) == 0L) {
            throw DivideByZeroException()
        }
        val quotient = getV(dividend) / getV(divisor)
        setV(dividend, quotient)
    }

    fun mod(dividend: Variable, divisor: Long) {
        if (divisor == 0L) {
            throw DivideByZeroException()
        }
        val remainder = getV(dividend) % divisor
        setV(dividend, remainder)
    }

    fun mod(dividend: Variable, divisor: Variable) {
        if (getV(divisor) == 0L) {
            throw DivideByZeroException()
        }
        val remainder = getV(dividend) % getV(divisor)
        setV(dividend, remainder)
    }

    fun eql(a: Variable, b: Long) {
        val result = getV(a) == b
        setV(a, if (result) 1 else 0)
    }

    fun eql(a: Variable, b: Variable) {
        val result = getV(a) == getV(b)
        setV(a, if (result) 1 else 0)
    }
}