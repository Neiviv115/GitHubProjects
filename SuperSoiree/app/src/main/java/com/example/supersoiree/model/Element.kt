package com.example.supersoiree.model

data class Element(
    val center: Center,
    val id: Long,
    val lat: Double,
    val lon: Double,
    val nodes: List<Long>,
    val tags: Tags,
    val type: String
)