package org.firstinspires.ftc.teamcode.opmodes.auto

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.Cycle
import org.firstinspires.ftc.teamcode.delay
import org.firstinspires.ftc.teamcode.hardware.LiftInternals
import org.firstinspires.ftc.teamcode.hardware.LiftInternals.Position.*
import org.firstinspires.ftc.teamcode.roadrunner.RoadRunnerTrajectories.apply
import org.firstinspires.ftc.teamcode.roadrunner.trajectorysequence.TrajectorySequence
import org.firstinspires.ftc.teamcode.trajectories.Trajectories
import org.firstinspires.ftc.teamcode.waitUntil
import java.util.concurrent.Future

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
        runPreload(preload, toStack)

        repeat(5) {
            // correct for slow drift
            drive.poseEstimate = drive.poseEstimate + Pose2d(1.0, 1.0, 0.0)
            runCycle(toJunction, toStack)
        }

        drive.followTrajectorySequence(
            drive.trajectorySequenceBuilder(drive.poseEstimate).back(5.0).build()
        )

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

    private fun runPreload(toJunction: TrajectorySequence, toStack: TrajectorySequence) =
        runCycle(toJunction, toStack, Cycle::startPreload)

    private fun runCycle(toJunction: TrajectorySequence, toStack: TrajectorySequence) =
        runCycle(toJunction, toStack, Cycle::start)

    private inline fun runCycle(toJunction: TrajectorySequence, toStack: TrajectorySequence, startFunc: Cycle.() -> Future<*>) {
        currentCycle = createCycle(HIGH, bottomPosition)

        val start = currentCycle.startFunc()
        delay(Cycle.GRAB_DELAY_MS)

        drive.followTrajectorySequence(toJunction)
        start.get()

//        val test = currentCycle.test()
//        currentCycle.forceTestPass = true
//        test.get()
//        delay(Cycle.DROP_DELAY_MS)
        currentCycle.autonomousMagicFinish()
        waitUntil { currentCycle.stage == Cycle.Stage.DROPPING }

        drive.followTrajectorySequence(toStack)

        // test includes finish
        currentCycle.await()

        bottomPosition--
    }
}

@Autonomous
class FainterLightRight : FainterLightLeft() {
    override val reversed = true
}