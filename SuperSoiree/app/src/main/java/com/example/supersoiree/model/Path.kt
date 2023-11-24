package com.example.supersoiree.model

data class Path(
    val ascend: Double,
    val bbox: List<Double>,
    val descend: Double,
    val details: Details,
    val distance: Double,
    val instructions: List<Instruction>,
    val legs: List<Any>,
    val points: Points,
    val snapped_waypoints: SnappedWaypoints,
    val time: Int,
    val transfers: Int,
    val weight: Double
)