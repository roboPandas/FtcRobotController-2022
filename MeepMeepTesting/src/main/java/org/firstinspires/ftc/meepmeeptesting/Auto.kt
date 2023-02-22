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
        .setConstraints(45.0, 60.0, 4.25, Math.toRadians(360.0), 13.43)
        .setDimensions(15.0, 17.0)
        .followTrajectorySequence {
            it.trajectorySequenceBuilder(startPose)
                .setReversed(true) // robot starts backwards
//                .back(2.0) // back up from wall to not hit when rotating
//                .splineToSplineHeading(reversedPose(startPose.x, startPose.y + 12, PI), PI / 2) // rotate and move up a bit
//                .splineToSplineHeading(reversedPose(startPose.x - 2, startPose.y + 42, 9 * PI / 8), PI / 2)
////                .strafeRight(30.0) // strafe up to pole
//                .splineToSplineHeading(reversedPose(-31.0, -4.0, 5 * PI / 4), PI / 4) // finish and spline to pole
                .back(2.0) // back up from wall to not hit when rotating
                .splineToSplineHeading(reversedPose(startPose.x, startPose.y + 12, PI), PI / 2) // rotate and move up a bit
                .splineToSplineHeading(reversedPose(startPose.x - 2, startPose.y + 42, 11 * PI / 8), PI / 2)
//                .strafeRight(30.0) // strafe up to pole
                .splineToSplineHeading(reversedPose(-31.0, -4.0, 5 * PI / 4), PI / 4) // finish and spline to pole

                .setReversed(false)
                .splineTo(Vector2d(negateIfReversed(-38.0), -16.0), reverseAngle(PI)) // spline towards cones
                .forward(22.0) // drive into cones

                .setReversed(true)
                .back(20.5) // back up from cones
                .splineToSplineHeading(reversedPose(-25.5, -5.0, 5 * PI / 4), PI / 4)

//                .splineTo(Vector2d(negateIfReversed(-38.0), -12.0), reverseAngle(PI)) // spline back to cones
                .build()
        }


    meepMeep.setBackground(Background.FIELD_POWERPLAY_OFFICIAL)
        .setDarkMode(true)
        .setBackgroundAlpha(0.5f)
        .addEntity(robot)
        .start()
}
