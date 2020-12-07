package com.logan.adventofcode2020

import java.io.File
import java.util.*

open class Bag(val color: String, val children: List<String>) {
    var childBags: Map<Bag, Int> = emptyMap<Bag, Int>()
}

fun main() {
    val inputFileName = "input/day7_input.txt"
    val bags = readBagRulesFromFile(inputFileName)

    val ancestors = findAncestors(bags, "shiny gold")
    println(ancestors.count())

    println(findTotalNumberDescendants(bags, "shiny gold"))
}

fun readBagRulesFromFile(name: String): MutableList<Bag> {
    val bags = mutableListOf<Bag>()

    val lines = File(name).readLines()
    lines.forEach { line ->
        var components = line.split(" bags contain ", " bag, ", " bags, ", " bag.", " bags.", "no other bags." )
        bags.add(Bag(components.first(), components.subList(1, components.lastIndex+1)))
    }

    bags.forEach { bag ->
        if (bag.children.isEmpty()) { return@forEach }
        val children = mutableMapOf<Bag, Int>()
        bag.children.forEach { child ->
            if (child.isEmpty()) { return@forEach }
            val number = child.substringBefore(' ').toInt()
            val color = child.substringAfter(' ')
            val childBag = bags.first { it.color == color }

            children[childBag] = number
        }

        bag.childBags = children
    }

    return bags
}

fun findAncestors(bags: List<Bag>, color: String): List<Bag> {
    if (bags.isEmpty()) { return emptyList<Bag>() }

    var ancestors = mutableListOf<Bag>()

    bags.forEach { bag ->
        val children = bag.childBags.keys.toList()
        if (children.find { it.color == color } != null) {
            ancestors.add(bag)
        }
    }

    val filtered = bags.filter { !ancestors.contains(it) }
    val moreAncestors = mutableListOf<Bag>()
    for (bag in ancestors) {
        moreAncestors.addAll(findAncestors(filtered, bag.color))
    }

    ancestors.addAll(moreAncestors)
    ancestors = ancestors.distinctBy { it.color } as MutableList<Bag>

    return ancestors
}

fun findTotalNumberDescendants(bags: List<Bag>, color: String): Int {
    if (bags.isEmpty()) { return 0 }

    var numDescendants = 0
    var descendants = mutableMapOf<Bag, Int>()
    val parent = bags.first { it.color == color }

    if (parent.childBags.count() == 0) { return 0 }

    parent.childBags.forEach { child ->
        numDescendants += child.value
        descendants[child.key] = child.value
    }

    descendants.forEach { bag ->
        numDescendants += (findTotalNumberDescendants(bags, bag.key.color) * bag.value)
    }

    return numDescendants
}