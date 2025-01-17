package org.firstinspires.ftc.teamcode.opmodes.auto

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.Cycle
import org.firstinspires.ftc.teamcode.delay
import org.firstinspires.ftc.teamcode.hardware.LiftInternals.Position.*
import org.firstinspires.ftc.teamcode.roadrunner.drive.DriveConstants
import org.firstinspires.ftc.teamcode.roadrunner.drive.SampleMecanumDrive
import org.firstinspires.ftc.teamcode.roadrunner.trajectorysequence.TrajectorySequence
import org.firstinspires.ftc.teamcode.waitUntil
import java.util.concurrent.Future

import kotlin.math.PI

@Autonomous
open class Epiphany : AutonomousTemplate() {
    private lateinit var preload: TrajectorySequence
    private lateinit var toJunction: Array<TrajectorySequence>
    private lateinit var toStack: Array<TrajectorySequence>

    override fun initializeTrajectories(): Pose2d {
        preload = drive.trajectorySequenceBuilder(Pose2d(-35.0, -62.5, PI))
            .setReversed(false)
            .strafeRight(41.8) // strafe partially to pole
            .splineToSplineHeading(Pose2d(-28.0, -4.0, 5 * PI / 4), PI / 4)
            .build()

        val toStack1 = drive.trajectorySequenceBuilder(preload.end())
            .setReversed(false)
            .splineTo(Vector2d(-40.5, -12.0), PI) // spline towards cones
            .forward(18.0) // drive into cones
            .forward(4.0,
                SampleMecanumDrive.getVelocityConstraint(10.0, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL),
            ) // spline to pole to line up with it
            .build()

        val toStack2 = drive.trajectorySequenceBuilder(preload.end())
            .setReversed(false)
            .splineTo(Vector2d(-40.5, -12.25), PI) // spline towards cones
            .forward(18.0) // drive into cones
            .strafeLeft(1.25)
            .forward(4.0,
                SampleMecanumDrive.getVelocityConstraint(10.0, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL),
            ) // spline to pole to line up with it
            .build()

        val toStack3 = drive.trajectorySequenceBuilder(preload.end())
            .setReversed(false)
            .splineTo(Vector2d(-40.5, -12.50), PI) // spline towards cones
            .forward(18.0) // drive into cones
            .strafeLeft(2.0)
            .forward(4.0,
                SampleMecanumDrive.getVelocityConstraint(10.0, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL),
            ) // spline to pole to line up with it
            .build()

        val toStack4 = drive.trajectorySequenceBuilder(preload.end())
            .setReversed(false)
            .splineTo(Vector2d(-40.5, -12.75), PI) // spline towards cones
            .forward(18.0) // drive into cones
            .strafeLeft(3.0)
            .forward(4.0,
                SampleMecanumDrive.getVelocityConstraint(10.0, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL),
            ) // spline to pole to line up with it
            .build()

        val toStack5 = drive.trajectorySequenceBuilder(preload.end())
            .setReversed(false)
            .splineTo(Vector2d(-40.5, -13.0), PI) // spline towards cones
            .forward(18.0) // drive into cones
            .strafeLeft(3.0)
            .forward(4.0,
                SampleMecanumDrive.getVelocityConstraint(10.0, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL),
            ) // spline to pole to line up with it
            .build()

        toStack = arrayOf(toStack1, toStack2, toStack3, toStack4, toStack5)

        // TODO don't cry and make an initializer that does this for me

        val junction1 = drive.trajectorySequenceBuilder(toStack1.end())
            .setReversed(false)
            .back(20.5) // back up from cones
            .splineToSplineHeading(Pose2d(-29.0, -5.0, 5 * PI / 4), PI / 4)
            .build()

        val junction2 = drive.trajectorySequenceBuilder(toStack2.end())
            .setReversed(false)
            .back(20.5) // back up from cones
            .splineToSplineHeading(Pose2d(-29.0, -5.0, 5 * PI / 4), PI / 4)
            .build()

        val junction3 = drive.trajectorySequenceBuilder(toStack3.end())
            .setReversed(false)
            .back(20.5) // back up from cones
            .splineToSplineHeading(Pose2d(-29.0, -5.0, 5 * PI / 4), PI / 4)
            .build()

        val junction4 = drive.trajectorySequenceBuilder(toStack4.end())
            .setReversed(false)
            .back(20.5) // back up from cones
            .splineToSplineHeading(Pose2d(-29.0, -5.0, 5 * PI / 4), PI / 4)
            .build()

        val junction5 = drive.trajectorySequenceBuilder(toStack5.end())
            .setReversed(false)
            .back(20.5) // back up from cones
            .splineToSplineHeading(Pose2d(-29.0, -5.0, 5 * PI / 4), PI / 4)
            .build()

        toJunction = arrayOf(junction1, junction2, junction3, junction4, junction5)

        return preload.start()
    }

    override fun main() {
        runPreload(preload, toStack[0])

        runCycle(toJunction[0], toStack[0])

//        liftInternals.goToPositionBlocking(ZERO, LiftInternals.MOTOR_SCALE_FACTOR / 4)
    }
}