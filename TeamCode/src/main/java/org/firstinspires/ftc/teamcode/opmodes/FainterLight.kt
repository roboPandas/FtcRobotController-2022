package org.firstinspires.ftc.teamcode.opmodes

import com.acmerobotics.roadrunner.geometry.Vector2d
import com.acmerobotics.roadrunner.trajectory.Trajectory
import kotlin.math.PI
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.Cycle
import org.firstinspires.ftc.teamcode.hardware.LiftInternals
import org.firstinspires.ftc.teamcode.pipelines.QuantizationPipeline
import org.firstinspires.ftc.teamcode.roadrunner.trajectorysequence.TrajectorySequence

@Autonomous
open class FainterLightLeft : AutonomousTemplate() {
    override val startPose = reversedPose(-36.0, -65.5, -PI / 2)

    protected lateinit var preload: TrajectorySequence
    private lateinit var toStackInitial: TrajectorySequence
    private lateinit var toJunction: TrajectorySequence
    private lateinit var toStack: TrajectorySequence
    private lateinit var park: TrajectorySequence

    override fun initializeTrajectories() {
        preload = drive.trajectorySequenceBuilder(startPose)
            .setReversed(true) // robot starts backwards
            .back(4.0) // move off wall
            .strafeLeftReversed(25.0) // move left towards pole
            .back(0.01) // jank to make it spline backwards instead of sideways
            .splineTo(reversedVector(-4.5, -29.5), reverseAngle(PI / 4)) // spline to pole
//                .back(3.0)
//                .forward(5.0)
            .build()
        toStackInitial = drive.trajectorySequenceBuilder(preload.end())
            .setReversed(true)
            .forward(8.0) // move away from pole
            .turn(Math.toRadians(-42.0))// align with tiles to strafe
            .strafeRight(21.0) // move in line with cone stack
            .forward(52.0) // go to cone stack
            .build()
        toJunction = drive.trajectorySequenceBuilder(reversedPose(-63.5, -12.0, -PI)) // manual reset of pose
            .setReversed(true)
            .back(10.0) // back up from cones
            .splineTo(Vector2d(negateIfReversed(-31.0), -6.0), if (reversed) 3 * PI / 4 else PI / 4) // spline to 2nd pole
            .build()
        toStack = drive.trajectorySequenceBuilder(toJunction.end())
            .setReversed(false)
            .splineTo(Vector2d(negateIfReversed(-46.0), -12.0), reverseAngle(PI)) // spline back to cones
            .forward(17.5) // drive into cones
            .build()
        park = drive.trajectorySequenceBuilder(toJunction.end())
            .splineTo(Vector2d(negateIfReversed(-38.0), -12.0), reverseAngle(PI)) // spline back to cones
            .build()
    }

    override fun main() {
        runCycle(preload, toStackInitial, LiftInternals.Position.STACK_5)
        drive.poseEstimate = toJunction.start()

        var bottomPosition = LiftInternals.Position.STACK_4
        repeat(ADDITIONAL_CYCLES) { runCycle(toJunction, if (it == ADDITIONAL_CYCLES - 1) park else toStack, bottomPosition--) }

        // FIXME this ONLY works for left.
        when (detectedColor) {
            QuantizationPipeline.Color.MAGENTA -> {}
            QuantizationPipeline.Color.GREEN -> drive.followTrajectory(drive.trajectoryBuilder(drive.poseEstimate).forward(20.0).build())
            QuantizationPipeline.Color.CYAN -> drive.followTrajectory(drive.trajectoryBuilder(drive.poseEstimate).back(20.0).build())
        }
    }

    private fun runCycle(toJunction: TrajectorySequence, toStack: TrajectorySequence, bottomPosition: LiftInternals.Position) {
        currentCycle = createCycle(LiftInternals.Position.HIGH, bottomPosition)
        val start = currentCycle.start()
        sleep(Cycle.GRAB_DELAY_MS)
        drive.followTrajectorySequence(toJunction)
        start.get()
        val finish = currentCycle.finish()
        sleep(Cycle.DROP_DELAY_MS)
        drive.followTrajectorySequence(toStack)
        finish.get()
    }

    companion object {
        const val ADDITIONAL_CYCLES = 2
    }
}

@Autonomous
class FainterLightRight : FainterLightLeft() {
    override val reversed = true
}