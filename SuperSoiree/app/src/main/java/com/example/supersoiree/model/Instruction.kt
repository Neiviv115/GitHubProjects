package com.example.supersoiree.model

data class Instruction(
    val distance: Double,
    val heading: Double,
    val interval: List<Int>,
    val last_heading: Double,
    val points: List<List<Double>>,
    val sign: Int,
    val street_destination: String,
    val street_name: String,
    val text: String,
    val time: Int
)