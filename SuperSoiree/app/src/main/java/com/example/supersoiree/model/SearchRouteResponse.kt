package com.example.supersoiree.model

data class SearchRouteResponse(
    val hints: Hints,
    val info: Info,
    val paths: List<Path>
)