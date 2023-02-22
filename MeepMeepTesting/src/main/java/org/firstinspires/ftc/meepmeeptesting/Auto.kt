package org.firstinspires.ftc.meepmeeptesting

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.noahbres.meepmeep.MeepMeep
import com.noahbres.meepmeep.MeepMeep.Background
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder
import com.noahbres.meepmeep.roadrunner.trajectorysequence.TrajectorySequenceBuilder
import org.firstinspires.ftc.teamcode.trajectories.Trajectories
import org.firstinspires.ftc.teamcode.trajectories.Trajectories.FainterLight
import kotlin.math.PI

val startPose = reversedPose(-36.0, -65.5, -PI / 2)
const val reversed = false
fun negateIfReversed(a: Double) = if (reversed) -a else a
fun reverseAngle(a: Double) = if (reversed) Math.PI - a else a
fun reversedPose(x: Double = 0.0, y: Double = 0.0, heading: Double = 0.0) =
    Pose2d(negateIfReversed(x), y, reverseAngle(heading))

fun reversedVector(x: Double = 0.0, y: Double = 0.0) = Vector2d(negateIfReversed(x), y)
fun TrajectorySequenceBuilder.strafeLeftReversed(a: Double) = strafeLeft(negateIfReversed(a))
fun TrajectorySequenceBuilder.strafeRightReversed(a: Double) = strafeRight(negateIfReversed(a))

fun main() {
    val meepMeep = MeepMeep(700)
    val robot = DefaultBotBuilder(meepMeep)
        // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
        .setConstraints(45.0, 60.0, 4.25, Math.toRadians(360.0), 13.43)
        .setDimensions(15.0, 17.0)
        .followTrajectorySequence {
            MeepMeepTrajectories.apply(
                it,
                FainterLight.PRELOAD,
                FainterLight.TO_STACK,
//
//                FainterLight.TO_JUNCTION,
//                FainterLight.TO_STACK,
//                FainterLight.TO_JUNCTION,
//                FainterLight.TO_STACK,
//                FainterLight.TO_JUNCTION,
//
//                FainterLight.PARK
            ).build()
        }


    meepMeep.setBackground(Background.FIELD_POWERPLAY_OFFICIAL)
        .setDarkMode(true)
        .setBackgroundAlpha(0.5f)
        .addEntity(robot)
        .start()
}
