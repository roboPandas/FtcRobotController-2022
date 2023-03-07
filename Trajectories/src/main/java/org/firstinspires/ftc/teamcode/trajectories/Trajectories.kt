package org.firstinspires.ftc.teamcode.trajectories

import kotlin.math.PI

object Trajectories {
    fun vec(x: Double, y: Double): Vec {
        return Vec(x, y)
    }

    fun pose(x: Double, y: Double, heading: Double): Pose {
        return Pose(x, y, heading)
    }

    fun rad(deg: Double): Double {
        return Math.toRadians(deg)
    }

    object FainterLight {
        val PRELOAD = CommonTrajectorySequence.builder(pose(-35.0, -62.5, PI))
            .setReversed(false)
            .strafeRight(41.8) // strafe partially to pole
            .splineToSplineHeading(pose(-29.5, -6.0, 5 * PI / 4), PI / 4) // spline to pole to line up with it
            .build()
        val TO_STACK = CommonTrajectorySequence.builder(PRELOAD.end)
            .setReversed(false)
            .splineTo(vec(-40.5, -11.9), PI) // spline towards cones
            .forward(18.0) // drive into cones
            .splineToSplineHeading(pose(-62.5, -11.75, PI), PI) // spline to pole to line up with it
            .build()
        val TO_JUNCTION = CommonTrajectorySequence.builder(TO_STACK.end)
            .setReversed(false)
            .back(20.5) // back up from cones
            .splineToSplineHeading(pose(-29.5, -6.0, 5 * PI / 4), PI / 4)
            .build()
        val PARK = CommonTrajectorySequence.builder(TO_JUNCTION.end)
            .splineTo(vec(-38.0, -9.0), PI) // spline back to cones
            .build()
    }
}