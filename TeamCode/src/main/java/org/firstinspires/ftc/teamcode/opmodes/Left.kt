package org.firstinspires.ftc.teamcode.opmodes

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.trajectory.Trajectory
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import org.firstinspires.ftc.teamcode.Cycle
import org.firstinspires.ftc.teamcode.hardware.LiftInternals
import org.firstinspires.ftc.teamcode.roadrunner.trajectorysequence.TrajectorySequence
import kotlin.math.PI

@Autonomous
@Disabled
open class Left : AutonomousTemplate() {
    // TODO uncomment actual cycle code AFTER this gets tested
    protected lateinit var deliverPreload: TrajectorySequence
    protected lateinit var intake: TrajectorySequence
    protected lateinit var delivery: TrajectorySequence
    protected lateinit var park: TrajectorySequence
    protected lateinit var finalMovement: Trajectory
    protected val ADDITIONAL_CYCLES = 2
    override val startPose: Pose2d = Pose2d(negateIfReversed(-36.0), -65.5, PI / 2)

    override fun initializeTrajectories() {
        deliverPreload =
            drive.trajectorySequenceBuilder(startPose) // TODO is this 72-based or 70-based?
                // FIXME if splines don't work use linear
                // Preload
                .forward(59.5) // 2 tiles + 5.5 inches to reach the center + 6 inches to push cone out of the way
                .back(6.0)
                .turn(negateIfReversed(-PI / 4))
                .forward(10.47) // takes us to (-28.6, -4.6) - claw is at (-24, 0)
                .build()
        intake = drive.trajectorySequenceBuilder(deliverPreload.end()) // Intake
            .setReversed(true) // splines always start by moving forward, so we need to call backward forward
            .splineToLinearHeading(Pose2d(negateIfReversed(-46.0), -12.0, if (reversed) PI else 0.0), if (reversed) 0.0 else PI)
            .setReversed(false)
            .back(17.5)
            .build()
        delivery = drive.trajectorySequenceBuilder(intake.end()) // Delivery
            .forward(22.5)
            .splineToLinearHeading(Pose2d(negateIfReversed(-28.6), -4.6, if (reversed) 3 * PI / 4 else PI / 4), PI / 2)
            .build()
        park = drive.trajectorySequenceBuilder(delivery.end())
            .setReversed(true) // splines always start by moving forward, so we need to call backward forward
            .splineToLinearHeading(Pose2d(negateIfReversed(-36.0), -12.0, if (reversed) PI else 0.0), -PI / 2)
            .setReversed(false)
            .strafeRight(negateIfReversed(24.0))
            .build()
        finalMovement = drive.trajectoryBuilder(deliverPreload.end())
            .forward(negateIfReversed(detectedColor.ordinal.toDouble()))
            .build()
    }

    override fun main() {
        // preload is sadly handled separately so we don't wait to grab the cone // TODO is the time to grab the cone negligible?
        currentCycle.startPreload()
        drive.followTrajectorySequence(deliverPreload)
        currentCycle.await()
        currentCycle.finish()
        currentCycle.waitUntil(Cycle.Stage.DROPPED)
        for (i in 0 until ADDITIONAL_CYCLES) {
            drive.followTrajectorySequence(intake)
            if (!currentCycle.await()) throw RuntimeException("A cycle should have finished by now, but it did not.")
            currentCycle = createCycle(
                LiftInternals.Position.HIGH,
                LiftInternals.Position.values()[4 - i]
            )
            currentCycle.start()
            currentCycle.waitUntil(Cycle.Stage.GRABBED)
            drive.followTrajectorySequence(delivery)
            currentCycle.await()
            currentCycle.finish()
            currentCycle.waitUntil(Cycle.Stage.DROPPED)
        }
        drive.followTrajectorySequence(park)
        drive.followTrajectory(finalMovement)
    }
}