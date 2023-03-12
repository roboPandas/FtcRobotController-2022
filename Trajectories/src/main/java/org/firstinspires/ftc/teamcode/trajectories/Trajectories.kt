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

    // claw pos to bot pos given bot heading
    fun claw2Bot(x: Double, y: Double, clawRotated: Boolean, heading: Double): Pose {
        val mul = if (clawRotated) 1 else -1
        val midToClaw = 7 * mul
        val botX = x + cos(heading) * midToClaw
        val botY = y + sin(heading) * midToClaw
        return Pose(botX, botY, heading)
    }

    object FainterLight {

        val PRELOAD = CommonTrajectorySequence.builder(pose(-35.0, -63.0, PI))
            .setReversed(false)
            .strafeRight(49.0) // strafe partially to pole
            .splineToSplineHeading(claw2Bot(-26.0, 2.0, true, 5 * PI / 4), PI / 4) // spline to pole
            .build()

        fun buildJunctionToGreen(start: Pose): CommonTrajectorySequence {
            return CommonTrajectorySequence.builder(start)
                .setReversed(false)
                .splineToSplineHeading(pose(-36.0, -12.0, PI), PI)
                .build()
        }


        fun buildToStack(offset: Vec, start: Pose): CommonTrajectorySequence {
            return CommonTrajectorySequence.builder(start)
                .setReversed(false)
                .splineTo(vec(-41.5, -10.0).add(offset), PI) // spline away from pole
                .splineToSplineHeading(claw2Bot((-24.0 * 3) /*+ 2*/, -10.0, false, PI).add(offset), PI) // spline to cones
                .build()
        }

        fun buildToJunction(offset: Vec, start: Pose): CommonTrajectorySequence {
            return CommonTrajectorySequence.builder(start)
                .setReversed(false)
                .back(20.5 + offset.x) // back up from cones
                .splineToSplineHeading(claw2Bot(-26.0, 2.0, true, 5 * PI / 4).add(offset), PI / 4) // spline to pole
                .build()
        }
    }
}