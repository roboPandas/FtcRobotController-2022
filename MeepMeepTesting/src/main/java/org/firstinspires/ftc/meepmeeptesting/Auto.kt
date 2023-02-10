package org.firstinspires.ftc.meepmeeptesting

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.noahbres.meepmeep.MeepMeep
import com.noahbres.meepmeep.MeepMeep.Background
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder
import com.noahbres.meepmeep.roadrunner.trajectorysequence.TrajectorySequenceBuilder
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
        .setConstraints(48.45 * 0.95, 50.46434527240892, 3.25, Math.toRadians(231.31152), 14.25)
        .setDimensions(15.0, 17.0)
        .followTrajectorySequence {
            it.trajectorySequenceBuilder(startPose)
                .setReversed(true) // robot starts backwards
                .back(4.0) // move off wall
                .strafeLeftReversed(25.0) // move left towards pole
                .back(0.01) // jank to make it spline backwards instead of sideways
                .splineTo(reversedVector(-5.0, -30.0), reverseAngle(Math.PI / 4)) // spline to pole
//                .back(3.0)
//                .forward(5.0)

                // TODO reversing is not rn
                .setReversed(true)
                .forward(6.0) // move away from pole
                .turn(Math.toRadians(-42.0))// align with tiles to strafe
                .strafeRight(21.0) // move in line with cone stack
                .forward(52.0) // go to cone stack

                .setReversed(true)
                .back(10.0) // back up from cones
                .splineTo(Vector2d(negateIfReversed(-30.5), -5.5), if (reversed) 3 * PI / 4 else PI / 4) // spline to 2nd pole

                .setReversed(false)
                .splineTo(Vector2d(negateIfReversed(-46.0), -12.0), reverseAngle(PI)) // spline back to cones
                .forward(17.5) // drive into cones
                .build()
        }


    meepMeep.setBackground(Background.FIELD_POWERPLAY_OFFICIAL)
        .setDarkMode(true)
        .setBackgroundAlpha(0.5f)
        .addEntity(robot)
        .start()
}
