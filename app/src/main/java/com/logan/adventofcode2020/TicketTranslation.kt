package com.logan.adventofcode2020

import java.io.File

val fields = mutableMapOf<String, List<Int>>()
val myTicket = mutableListOf<Int>()
val nearbyTickets = mutableListOf<List<Int>>()

val invalidTickets = mutableListOf<Int>()

val fieldOrder = mutableListOf<MutableList<String>>()

val number = """(\d+)""".toRegex()

fun main() {
    val inputFileName = "input/day16_input.txt"
    readTicketDataFromFile(inputFileName)

    val errorRate = findErrorRate()
    println("Error Rate: $errorRate")

    removeInvalidTickets()
    determineFieldOrder()
    narrowDownFields()

    val product = productOfDepartureFields()

    println("Product: $product")
}

fun readTicketDataFromFile(name: String) {
    val lines = File(name).readLines()

    val fieldLines = lines.subList(0, lines.indexOfFirst { it == "" })
    val myTicketLines = lines.subList(fieldLines.size+1, fieldLines.size+3)
    val nearbyTicketLines = lines.subList(lines.indexOfLast { it == "" }+1, lines.size)

    populateFields(fieldLines)
    populateMyTicket(myTicketLines)
    populateNearbyTickets(nearbyTicketLines)
}

fun populateFields(fieldLines: List<String>) {
    fieldLines.forEach {
        val field = it.substringBefore(':')

        var endpoint = number.find(it)
        var validNumbers = mutableListOf<Int>()
        do {
            validNumbers.add(endpoint?.value?.toInt()!!)
            endpoint = endpoint.next()

            for (i in validNumbers.last()+1..endpoint?.value?.toInt()!!) {
                validNumbers.add(i)
            }

            endpoint = endpoint?.next()
        } while (endpoint != null)

        fields[field] = validNumbers
    }
}

fun populateMyTicket(myTicketLines: List<String>) {
    val numbers = myTicketLines[1].split(',')
    numbers.forEach {
        myTicket.add(it.toInt())
    }
}

fun populateNearbyTickets(nearbyTicketLines: List<String>) {
    nearbyTicketLines.forEach {
        if (it == "nearby tickets:") { return@forEach }
        val numbers = it.split(',')
        val ticket = mutableListOf<Int>()
        numbers.forEach { num ->
            ticket.add(num.toInt())
        }

        nearbyTickets.add(ticket)
    }
}

fun findErrorRate(): Int {
    var errorRate = 0

    for (idx in nearbyTickets.indices) {
        nearbyTickets[idx].forEach { num ->
            var invalid = true

            fields.forEach { field ->
                if (field.value.contains(num)) {
                    invalid = false
                }

                if (!invalid) { return@forEach }
            }

            if (invalid) {
                invalidTickets.add(0, idx)
                errorRate += num
            }
        }
    }

    return errorRate
}

fun removeInvalidTickets() {
    invalidTickets.forEach { idx ->
        nearbyTickets.removeAt(idx)
    }
}

fun determineFieldOrder() {
    for (idx in nearbyTickets.first().indices) {
        val possibleFields = mutableListOf<String>()

        fields.forEach { (field, values) ->
            if (values.contains(nearbyTickets.first()[idx])) {
                possibleFields.add(field)
            }
        }

        if (possibleFields.size > 1) {
            val removeFields = mutableListOf<String>()
            for (ticketIdx in 1..nearbyTickets.lastIndex) {
                possibleFields.forEach { field ->
                    if (!fields[field]?.contains(nearbyTickets[ticketIdx][idx])!!) {
                        removeFields.add(field)
                    }
                }
            }

            removeFields.forEach {
                possibleFields.remove(it)
            }
        }

        fieldOrder.add(possibleFields)
    }
}

fun narrowDownFields() {
    var narrowedDown = mutableListOf<List<String>>()
    while (fieldOrder.any { it.size > 1 }) {
        var fieldName = fieldOrder.first { it.size == 1 && !narrowedDown.contains(it)}
        var fieldToRemove = fieldOrder.indexOf(fieldName)
        for (idx in fieldOrder.indices) {
            if (idx != fieldToRemove) {
                if (fieldOrder[idx].contains(fieldName.first())) {
                    fieldOrder[idx].remove(fieldName.first())
                }
            }
        }

        narrowedDown.add(fieldName)
    }
}

fun productOfDepartureFields(): Long {
    var product: Long = 1

    for (idx in fieldOrder.indices) {
        if (fieldOrder[idx].first().contains("departure")) {
            product *= myTicket[idx].toLong()
        }
    }

    return product
}