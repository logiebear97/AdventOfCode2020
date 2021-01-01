package com.logan.adventofcode2020

import android.icu.number.NumberFormatter
import java.io.File
import java.text.NumberFormat
import java.util.ArrayDeque

typealias Tokens = ArrayDeque<Char>

var expressionTokens = mutableListOf<Tokens>()
var values = mutableMapOf<Tokens, Long>()

fun main() {
    val inputFileName = "input/day18_input.txt"
    readMathExpressionsFromFile(inputFileName)

    var sum = 0L
    expressionTokens.forEach {
        values[it] = evaluate(it.clone())
        sum += values[it]!!
    }

    println(sum.toBigDecimal().toPlainString())

    sum = 0L
    expressionTokens.forEach {
        values[it] = advancedEvaluate(it.clone())
        sum += values[it]!!
    }

    println(sum.toBigDecimal().toPlainString())

}

fun readMathExpressionsFromFile(name: String) {
    val lines = File(name).readLines()

    lines.forEach {
        expressionTokens.add(tokenize(it))
    }
}

fun tokenize(expression: String): Tokens {
    var result = """\d+|[+*()]""".toRegex().findAll(expression)
    var tokens = Tokens()
    result.forEach {
        tokens.add(it.value[0])
    }
    return tokens
}

fun evaluate(tokens: Tokens): Long {
    var acc = 0L
    var op = 'a'

    while (tokens.isNotEmpty()) {
        var t = tokens.removeFirst()

        when {
            t.isDigit() -> {
                when (op) {
                    'a' -> acc += t.toString().toLong()
                    'm' -> acc *= t.toString().toLong()
                }
            }
            t == '+' -> op = 'a'
            t == '*' -> op = 'm'
            t == '(' -> {
                var value = evaluate(tokens)
                when (op) {
                    'a' -> acc += value
                    'm' -> acc *= value
                }
            }
            t == ')' -> break
        }
    }

    return acc
}

fun advancedEvaluate(tokens: Tokens): Long {
    var acc = 0L
    var mul = 1L

    while (tokens.isNotEmpty()) {
        var t = tokens.removeFirst()

        when {
            t.isDigit() -> {
                var value = t.toString().toLong() * mul
                acc += value
            }
            t == '*' -> {
                mul = acc
                acc = 0L
            }
            t == '(' -> {
                var value = advancedEvaluate(tokens) * mul
                acc += value
            }
            t == ')' -> break
        }
    }

    return acc
}