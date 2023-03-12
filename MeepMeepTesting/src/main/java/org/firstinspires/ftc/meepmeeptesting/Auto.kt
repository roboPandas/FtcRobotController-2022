package org.firstinspires.ftc.meepmeeptesting

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.noahbres.meepmeep.MeepMeep
import com.noahbres.meepmeep.MeepMeep.Background
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder
import org.firstinspires.ftc.meepmeeptesting.MeepMeepTrajectories.apply
import org.firstinspires.ftc.teamcode.trajectories.Trajectories.FainterLight
import org.firstinspires.ftc.teamcode.trajectories.Vec
import kotlin.math.PI

val startPose = reversedPose(-36.0, -63.5, -PI / 2)
const val reversed = false
fun negateIfReversed(a: Double) = if (reversed) -a else a
fun reverseAngle(a: Double) = if (reversed) Math.PI - a else a
fun reversedPose(x: Double = 0.0, y: Double = 0.0, heading: Double = 0.0) =
    Pose2d(negateIfReversed(x), y, reverseAngle(heading))

fun reversedVector(x: Double = 0.0, y: Double = 0.0) = Vector2d(negateIfReversed(x), y)
fun main() {
    val meepMeep = MeepMeep(800)
    val robot = DefaultBotBuilder(meepMeep)
        // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
        .setConstraints(45.0, 45.0, 3.7, 4.0, 13.43)
        .setDimensions(15.0, 17.0)
        .followTrajectorySequence {
//            val halfL = (17 + (3 / 8f)) / 2.0
//            val halfW = 15.5 / 2.0
//            val start = Pose2d(-72 + halfL, 72 - halfW, 0.0)
//            it.trajectorySequenceBuilder(start)
//                .splineToSplineHeading(Pose2d(72 - halfL, 72 - 48.0, 0.0), 0.0)
//                .build()
//            it.trajectorySequenceBuilder(Pose2d(-35.0, -62.5, PI))
//                .setReversed(false)
//                .strafeRight(41.8) // strafe partially to pole
//                .splineToSplineHeading(Pose2d(-29.0, -6.0, 5 * PI / 4), PI / 4)
//                .build()
            it.apply(FainterLight.PRELOAD, FainterLight.buildJunctionToGreen(FainterLight.PRELOAD.end)).back(24.0)
                .build()
//            it.apply(
//                 // +1
//                FainterLight.PRELOAD,
//
//                // 1
//                FainterLight.buildToStack(Vec(0.0, 0.0), FainterLight.PRELOAD.end),
//                FainterLight.buildToJunction(Vec(0.0, 0.0), FainterLight.buildToStack(Vec(0.0, 0.0), FainterLight.PRELOAD.end).end),
//                // 2
////                FainterLight.buildToStack(Vec(1.25, -1.25)),
////                FainterLight.buildToJunction(Vec(1.25, -1.25)),
////                // 3
////                FainterLight.TO_STACK,
////                FainterLight.TO_JUNCTION,
////                // 4
////                FainterLight.TO_STACK,
////                FainterLight.TO_JUNCTION,
////                // 5
////                FainterLight.TO_STACK,
////                FainterLight.TO_JUNCTION,
////
////                 park
////                 FainterLight.PARK
//            ).build()
        }

    meepMeep.setBackground(Background.FIELD_POWERPLAY_OFFICIAL)
        .setDarkMode(true)
        .setBackgroundAlpha(0.5f)
        .addEntity(robot)
        .start()
}
