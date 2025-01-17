package org.firstinspires.ftc.teamcode.trajectories

data class Pose(val x: Double, val y: Double, val heading: Double) {
    constructor(vec: Vec, heading: Double) : this(vec.x, vec.y, heading)

    fun withPos(x: Double, y: Double) = copy(x, y)

    fun withPos(vec: Vec) = withPos(vec.x, vec.y)

    fun add(offset: Vec): Pose {
        return Pose(x + offset.x, y + offset.y, heading)
    }

    override fun toString() = "($x, $y) @ ${Math.toDegrees(heading)}"
}