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
        val PRELOAD = CommonTrajectorySequence.builder(pose(-36.0, -65.5, -PI / 2))
            .setReversed(true) // robot starts backwards
            .back(2.0) // back up from wall to not hit when rotating
            .splineToSplineHeading(
                pose(-36.0, -53.5, PI),
                PI / 2
            ) // rotate and move up a bit
            .splineToSplineHeading(pose(-38.0, -23.5, 11 * PI / 8), PI / 2)
            .splineToSplineHeading(
                pose(-31.0, -4.0, 5 * PI / 4),
                PI / 4
            ) // finish and spline to pole
            .build()
        val TO_STACK = CommonTrajectorySequence.builder(PRELOAD.end)
            .setReversed(false)
            .splineTo(vec(-38.0, -16.0), PI) // spline towards cones
            .forward(22.0) // drive into cones
            .build()
        val TO_JUNCTION = CommonTrajectorySequence.builder(TO_STACK.end)
            .setReversed(true)
            .back(20.5) // back up from cones
            .splineToSplineHeading(pose(-25.5, -5.0, 5 * PI / 4), PI / 4)
            .build()
        val PARK = CommonTrajectorySequence.builder(TO_JUNCTION.end)
            .setReversed(false)
            .splineTo(vec(-38.0, -12.0), PI) // spline back to cones
            .build()
    }
}