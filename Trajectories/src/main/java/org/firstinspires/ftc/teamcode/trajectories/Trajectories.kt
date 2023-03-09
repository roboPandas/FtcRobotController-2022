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
        val PRELOAD = CommonTrajectorySequence.builder(pose(-35.5, -63.0, PI))
            .setReversed(false)
            .strafeRight(45.0) // strafe partially to pole
            .splineToSplineHeading(pose(-29.0, -5.0, 5 * PI / 4), PI / 4) // spline to pole
            .build()

        val TO_STACK = CommonTrajectorySequence.builder(PRELOAD.end)
            .setReversed(false)
            .splineTo(vec(-41.5, -12.3), PI) // spline away from pole
            .splineToSplineHeading(pose(-62.0, -11.5, PI), PI) // spline to cones
            .build()

        fun buildToJunction(extraReverse: Double): CommonTrajectorySequence {
            return CommonTrajectorySequence.builder(TO_STACK.end)
                .setReversed(false)
                .back(20.5) // back up from cones
                .splineToSplineHeading(pose(-29.0 + extraReverse, -5.0, 5 * PI / 4), PI / 4)
                .build()
        }
    }
}