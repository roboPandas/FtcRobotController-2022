package org.firstinspires.ftc.teamcode.trajectories

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

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

    // rotated claw pos to bot pos given bot heading
    fun claw2Bot(x: Double, y: Double, clawRotated: Boolean, heading: Double): Pose {
        val mul = if (clawRotated) 1 else -1
        val midToClaw = 7 * mul
        val botX = x + cos(heading) * midToClaw
        val botY = y + sin(heading) * midToClaw
        return Pose(botX, botY, heading)
    }

    object FainterLight {

        val PRELOAD = CommonTrajectorySequence.builder(pose(-35.5, -63.0, PI))
            .setReversed(false)
            .strafeRight(45.0) // strafe partially to pole
            .splineToSplineHeading(claw2Bot(-24.0, 0.0, true, 5 * PI / 4), PI / 4) // spline to pole
            .build()

        val TO_STACK = CommonTrajectorySequence.builder(PRELOAD.end)
            .setReversed(false)
            .splineTo(vec(-41.5, -12.3), PI) // spline away from pole
            .splineToSplineHeading(claw2Bot((-24.0 * 3) + 2, -12.0, false, PI), PI) // spline to cones
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