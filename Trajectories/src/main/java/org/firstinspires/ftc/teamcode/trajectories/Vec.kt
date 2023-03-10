package org.firstinspires.ftc.teamcode.trajectories

data class Vec(val x: Double, val y: Double) {
    fun add(offset: Vec): Vec {
        return Vec(x + offset.x, y + offset.y)
    }
}