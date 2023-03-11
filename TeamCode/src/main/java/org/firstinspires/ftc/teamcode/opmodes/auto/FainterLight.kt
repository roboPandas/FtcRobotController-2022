package org.firstinspires.ftc.teamcode.opmodes.auto

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.teamcode.Cycle
import org.firstinspires.ftc.teamcode.delay
import org.firstinspires.ftc.teamcode.hardware.LiftInternals
import org.firstinspires.ftc.teamcode.hardware.LiftInternals.Position.*
import org.firstinspires.ftc.teamcode.roadrunner.RoadRunnerTrajectories.apply
import org.firstinspires.ftc.teamcode.roadrunner.trajectorysequence.TrajectorySequence
import org.firstinspires.ftc.teamcode.trajectories.Trajectories
import org.firstinspires.ftc.teamcode.waitUntil
import java.lang.Math.floor
import java.util.concurrent.Future
import kotlin.collections.ArrayList

@Autonomous
open class FainterLightLeft : AutonomousTemplate() {
    private lateinit var toJunctions: Array<TrajectorySequence>
    protected lateinit var preload: TrajectorySequence
    private lateinit var toStacks: Array<TrajectorySequence>
//    private lateinit var park: TrajectorySequence

    override fun initializeTrajectories(): Pose2d? {
        preload = drive.apply(Trajectories.FainterLight.PRELOAD).build()
        val trajectories = Jank.buildTrajectories(CYCLES, Trajectories.FainterLight.PRELOAD)
        toStacks = trajectories.first.map { drive.apply(it).build() }.toTypedArray()
        toJunctions = trajectories.second.map { drive.apply(it).build() }.toTypedArray()
//        park = drive.apply(Trajectories.FainterLight.PARK).build()

//        // SEAN, MAKE SURE YOU KNOW THIS IS X
//        val x = run {
//            val start = System.currentTimeMillis()
//            val distances = ArrayList<Double>(40)
//            while (System.currentTimeMillis() - start < 2000) {
//                val distance = distanceSensor.getDistance(DistanceUnit.INCH)
//                telemetry.addData("distance", distance)
//                telemetry.update()
//                distances += distance
//            }
//
//
//
//            telemetry.addData("distances", distances).setRetained(true)
//
//            // 26, 31
//            // min, max
//            // avg 28.5
//
//            val distance = (distances).sum() / distances.size
//
//
//            log("raw distance", distance, true)
//        } - 72 + DISTANCE_OFFSET
//
//        return preload.start().copy(x = x)
        return preload.start()
    }

    override fun main() {
//        drive.apply {
//            followTrajectory(trajectoryBuilder(poseEstimate).lineTo(preload.start().vec()).build())
//        }

        repeat(CYCLES + 1) {
            if (it == 0) runPreload(preload, toStacks[it])
            else runCycle(toJunctions[it], toStacks[it])
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

    companion object {
        const val CYCLES = 5
        const val DISTANCE_OFFSET = 8.5
    }
}

@Autonomous
class FainterLightRight : FainterLightLeft() {
    override val reversed = true
}