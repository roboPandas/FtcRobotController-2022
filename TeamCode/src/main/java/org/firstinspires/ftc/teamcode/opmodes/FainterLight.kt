package org.firstinspires.ftc.teamcode.opmodes

import android.os.Build
import androidx.annotation.RequiresApi
import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.acmerobotics.roadrunner.trajectory.Trajectory
import kotlin.math.PI
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.Cycle
import org.firstinspires.ftc.teamcode.delay
import org.firstinspires.ftc.teamcode.hardware.LiftInternals
import org.firstinspires.ftc.teamcode.hardware.LiftInternals.Position.*
import org.firstinspires.ftc.teamcode.pipelines.QuantizationPipeline
import org.firstinspires.ftc.teamcode.roadrunner.RoadRunnerTrajectories.apply
import org.firstinspires.ftc.teamcode.roadrunner.trajectorysequence.TrajectorySequence
import org.firstinspires.ftc.teamcode.trajectories.CommonTrajectorySequence
import org.firstinspires.ftc.teamcode.trajectories.Trajectories

@Autonomous
open class FainterLightLeft : AutonomousTemplate() {
    protected lateinit var preload: TrajectorySequence
    private lateinit var toJunction: TrajectorySequence
    private lateinit var toStack: TrajectorySequence
    private var bottomPosition = STACK_5
//    private lateinit var park: TrajectorySequence

    override fun initializeTrajectories(): Pose2d? {
        preload = drive.apply(Trajectories.FainterLight.PRELOAD).build()
        toStack = drive.apply(Trajectories.FainterLight.TO_STACK).build()
        toJunction = drive.apply(Trajectories.FainterLight.TO_JUNCTION).build()

//        park = drive.apply(Trajectories.FainterLight.PARK).build()
        return preload.start()
    }

    override fun main() {
        runCycle(preload, toStack)

        repeat(1) {
            runCycle(toJunction, toStack)
        }

        val lift = liftInternals.goToPosition(ZERO, LiftInternals.MOTOR_SCALE_FACTOR / 4)

        // todo park

        // FIXME this ONLY works for left.
//        when (detectedColor) {
//            QuantizationPipeline.Color.GREEN -> {}
//            QuantizationPipeline.Color.MAGENTA -> drive.followTrajectory(drive.trajectoryBuilder(drive.poseEstimate).forward(24.0).build())
//            QuantizationPipeline.Color.CYAN -> drive.followTrajectory(drive.trajectoryBuilder(drive.poseEstimate).back(20.0).build())
//        }

        lift.get()
    }

    private fun runCycle(toJunction: TrajectorySequence, toStack: TrajectorySequence) {
        currentCycle = createCycle(HIGH, bottomPosition)

        val start = currentCycle.start()
        delay(Cycle.GRAB_DELAY_MS)

        drive.followTrajectorySequence(toJunction)
        start.get()

        val test = currentCycle.test()
        currentCycle.forceTestPass = true
        test.get()

        delay(Cycle.DROP_DELAY_MS)
        drive.followTrajectorySequence(toStack)

        // test includes finish
        currentCycle.await()

        bottomPosition--
    }

    companion object {
        const val ADDITIONAL_CYCLES = 2
    }
}

@Autonomous
class FainterLightRight : FainterLightLeft() {
    override val reversed = true
}