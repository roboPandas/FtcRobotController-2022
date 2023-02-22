package org.firstinspires.ftc.teamcode.opmodes

import com.acmerobotics.roadrunner.geometry.Vector2d
import com.acmerobotics.roadrunner.trajectory.Trajectory
import kotlin.math.PI
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.Cycle
import org.firstinspires.ftc.teamcode.delay
import org.firstinspires.ftc.teamcode.hardware.LiftInternals
import org.firstinspires.ftc.teamcode.hardware.LiftInternals.Position.*
import org.firstinspires.ftc.teamcode.pipelines.QuantizationPipeline
import org.firstinspires.ftc.teamcode.roadrunner.trajectorysequence.TrajectorySequence

@Autonomous
open class FainterLightLeft : AutonomousTemplate() {
    override val startPose = reversedPose(-37.0, -65.5, -PI / 2)

    protected lateinit var preload: TrajectorySequence
    private lateinit var toJunction: TrajectorySequence
    private lateinit var toStack: TrajectorySequence
    private lateinit var park: TrajectorySequence

    override fun initializeTrajectories() {
        preload = drive.trajectorySequenceBuilder(startPose)
//            .setReversed(true) // robot starts backwards
//            .back(2.0) // back up from wall to not hit when rotating                    // random offset for inaccuracy
//            .splineToSplineHeading(reversedPose(startPose.x + 1, startPose.y + 12, PI + Math.toRadians(10.0)), PI / 2)// rotate and move up a bit
//            .strafeRight(30.0) // strafe up to pole
//            .splineToSplineHeading(reversedPose(-25.5, -3.0, 5 * PI / 4), PI / 4) // finish and spline to pole
            .setReversed(true) // robot starts backwards
            .back(2.0) // back up from wall to not hit when rotating
            .splineToSplineHeading(reversedPose(startPose.x, startPose.y + 12, PI), PI / 2) // rotate and move up a bit
            .splineToSplineHeading(reversedPose(startPose.x - 2, startPose.y + 42, 11 * PI / 8), PI / 2)
//                .strafeRight(30.0) // strafe up to pole
            .splineToSplineHeading(reversedPose(-31.0, -4.0, 5 * PI / 4), PI / 4) // finish and spline to pole
//            .back(3.0)
            .build()
        toStack = drive.trajectorySequenceBuilder(preload.end())
            .setReversed(false)
            .splineTo(Vector2d(negateIfReversed(-41.0), -15.0), reverseAngle(PI)) // spline towards cones
            .forward(21.0) // drive into cones
            .build()
        toJunction = drive.trajectorySequenceBuilder(toStack.end()) // manual reset of pose
            .setReversed(true)
            .back(20.5) // back up from cones
            .splineToSplineHeading(reversedPose(-25.5, -5.0, 5 * PI / 4), PI / 4)
            .build()
        park = drive.trajectorySequenceBuilder(toJunction.end())
            .splineTo(Vector2d(negateIfReversed(-38.0), -12.0), reverseAngle(PI)) // spline back to cones
            .build()
    }

    override fun main() {
        runCycle(preload, toStack, STACK_5)
        for (i in 4 downTo 1) {
            drive.poseEstimate = toJunction.end()
            runCycle(toJunction, toStack, valueOf("STACK_$i"))
        }
//        val lift = liftInternals.goToPosition(ZERO, LiftInternals.MOTOR_SCALE_FACTOR / 4)
//
//        // todo park
//
//        // FIXME this ONLY works for left.
//        when (detectedColor) {
//            QuantizationPipeline.Color.GREEN -> {}
//            QuantizationPipeline.Color.MAGENTA -> drive.followTrajectory(drive.trajectoryBuilder(drive.poseEstimate).forward(24.0).build())
//            QuantizationPipeline.Color.CYAN -> drive.followTrajectory(drive.trajectoryBuilder(drive.poseEstimate).back(20.0).build())
//        }
//
//        lift.get()
    }

    private fun runCycle(toJunction: TrajectorySequence, toStack: TrajectorySequence, bottomPosition: LiftInternals.Position) {
        currentCycle = createCycle(HIGH, bottomPosition)
        val start = currentCycle.start()
        delay(Cycle.GRAB_DELAY_MS + 100)
        drive.followTrajectorySequence(toJunction)
        start.get()
        val test = currentCycle.test()
        currentCycle.forceTestPass = true
        test.get()
        delay(Cycle.DROP_DELAY_MS)
        drive.followTrajectorySequence(toStack)
        // test includes finish
        currentCycle.await()
    }

    companion object {
        const val ADDITIONAL_CYCLES = 2
    }
}

@Autonomous
class FainterLightRight : FainterLightLeft() {
    override val reversed = true
}