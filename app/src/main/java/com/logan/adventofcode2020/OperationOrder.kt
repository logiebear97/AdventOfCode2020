package com.logan.adventofcode2020

import java.io.File
import kotlin.math.exp

var expressions = mutableListOf<TreeNode<Char>>()

class TreeNode<T>(var value: T) {
    var parent: TreeNode<T>? = null

    var left: TreeNode<T>? = null
    var right: TreeNode<T>? = null
}

fun main() {
    val inputFileName = "input/day18_input.txt"
    readMathExpressionsFromFile(inputFileName)

//    expressions.forEach { println("${evaluateExpression(it)}") }

}

fun readMathExpressionsFromFile(name: String) {
    val lines = File(name).readLines()

    lines.forEach { convertExpressionToTree(it) }
}

fun convertExpressionToTree(expression: String) {
    var expTree = mutableListOf<TreeNode<Char>>()
    var charStack = mutableListOf<Char>()

    var t: TreeNode<Char> = TreeNode(' ')
    var t1: TreeNode<Char>
    var t2: TreeNode<Char>

    expression.forEach {
        when {
            it == '(' -> {
                charStack.add(it)
            }
            it.isDigit() -> {
                t = TreeNode(it)
                expTree.add(t)
            }
            it == ')' -> {
                while (charStack.isNotEmpty() && charStack.last() != '(') {
                    t = TreeNode(charStack.last())
                    charStack.removeLast()

                    t1 = expTree.last()
                    expTree.removeLast()

                    t2 = expTree.last()
                    expTree.removeLast()

                    t.left = t2
                    t.right = t1
                    expTree.add(t)
                }

                charStack.add(it)

            }
            else -> {
                while (charStack.isNotEmpty() && charStack.last() != '(' && it != ' ') {
                    t = TreeNode(charStack.last())
                    charStack.removeLast()

                    t1 = expTree.last()
                    expTree.removeLast()

                    t2 = expTree.last()
                    expTree.removeLast()

                    t.left = t2
                    t.right = t1

                    expTree.add(t)
                }

                charStack.add(it)
            }
        }
    }

    expressions.add(t)
}

fun evaluateExpression(exp: String): Int {
    var lhs = 0
    var rhs = 0

    if (exp.contains('(')) {
        rhs = evaluateExpression(exp.substringAfter('('))
    }
    val ops = exp.split(' ')

    for (i in ops.indices) {
//        if (ops[i].contains('(')) {
////            val levels = ops[i].length
//            val lastClose = exp.substringAfter(ops[i])
//            rhs = evaluateExpression(lastClose)
//        }
        if (ops[i].contains(')')) {
            return lhs
        }

        when {
            ops[i] == "*" -> {
                lhs *= rhs
            }
            ops[i] == "+" -> {
                lhs += rhs
            }
            else -> {
                rhs = ops[i].toInt()
            }
        }

    }

    return lhs
}