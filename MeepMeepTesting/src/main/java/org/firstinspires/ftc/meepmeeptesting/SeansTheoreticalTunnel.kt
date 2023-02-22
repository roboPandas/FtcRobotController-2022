package org.firstinspires.ftc.meepmeeptesting

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.noahbres.meepmeep.MeepMeep
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder
import kotlin.math.PI

fun main() {
    val meepMeep = MeepMeep(700)
    val robot = DefaultBotBuilder(meepMeep)
        // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
        .setConstraints(45.0, 60.0, 4.25, Math.toRadians(360.0), 13.43)
        .setDimensions(15.0, 17.0)
        .followTrajectorySequence {
            it.trajectorySequenceBuilder(startPose)
                .setReversed(true) // robot starts backwards
                .back(2.0) // back up from wall to not hit when rotating
                .splineToSplineHeading(reversedPose(startPose.x, startPose.y + 10, PI), PI / 2) // rotate and move up a bit
                .splineToSplineHeading(reversedPose(startPose.x + 1, startPose.y + 42, 3 * PI / 2), PI / 2)

                .splineToSplineHeading(reversedPose(-30.0, -6.0, 5 * PI / 4), PI / 4) // finish and spline to pole

                .setReversed(false)
                .splineTo(reversedVector(-40.0, -12.0), PI) // spline towards cones
                .forward(20.0) // drive into cones

                .setReversed(true)
                .back(20.0)
                .splineTo(reversedVector(-33.5, -10.0), Math.toRadians(45.0)) // spline towards cones
//                .lineToSplineHeading(Pose2d(-33.5, -10.0, 45.0))
                .back(5.5) // drive into cones

                .setReversed(false)
                .splineTo(reversedVector(-40.0, -12.0), PI) // spline towards cones
                .forward(20.0) // drive into cones

                .setReversed(true)
                .back(20.0)
                .splineTo(reversedVector(-33.5, -10.0), Math.toRadians(45.0)) // spline towards cones
//                .lineToSplineHeading(Pose2d(-33.5, -10.0, 45.0))
                .back(5.5) // drive into cones

                .setReversed(false)
                .splineTo(reversedVector(-40.0, -12.0), PI) // spline towards cones
                .forward(20.0) // drive into cones

                .setReversed(true)
                .back(20.0)
                .splineTo(reversedVector(-33.5, -10.0), Math.toRadians(45.0)) // spline towards cones
//                .lineToSplineHeading(Pose2d(-33.5, -10.0, 45.0))
                .back(5.5) // drive into cones

                .setReversed(false)
                .splineTo(reversedVector(-40.0, -12.0), PI) // spline towards cones
                .forward(20.0) // drive into cones

                .setReversed(true)
                .back(20.0)
                .splineTo(reversedVector(-33.5, -10.0), Math.toRadians(45.0)) // spline towards cones
//                .lineToSplineHeading(Pose2d(-33.5, -10.0, 45.0))
                .back(5.5) // drive into cones

                .setReversed(false)
                .splineTo(reversedVector(-40.0, -12.0), PI) // spline towards cones
                .forward(20.0) // drive into cones

                .setReversed(true)
                .back(20.0)
                .splineTo(reversedVector(-33.5, -10.0), Math.toRadians(45.0)) // spline towards cones
//                .lineToSplineHeading(Pose2d(-33.5, -10.0, 45.0))
                .back(5.5) // drive into cones



//                .setReversed(true)
//                .back(20.5) // back up from cones
//                .splineToSplineHeading(reversedPose(-25.5, -5.0, 5 * PI / 4), PI / 4)

//                .splineTo(Vector2d(negateIfReversed(-38.0), -12.0), reverseAngle(PI)) // spline back to cones
                .build()
        }


    meepMeep.setBackground(MeepMeep.Background.FIELD_POWERPLAY_OFFICIAL)
        .setDarkMode(true)
        .setBackgroundAlpha(0.5f)
        .addEntity(robot)
        .start()
}