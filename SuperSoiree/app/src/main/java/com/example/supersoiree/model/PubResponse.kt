package com.example.supersoiree.model

data class PubResponse(
    val elements: List<Element>,
    val generator: String,
    val osm3s: Osm3s,
    val version: Double
)