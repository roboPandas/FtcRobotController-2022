package org.firstinspires.ftc.teamcode.opmodes.auto

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.teamcode.Cycle
import org.firstinspires.ftc.teamcode.delay
import org.firstinspires.ftc.teamcode.hardware.LiftInternals
import org.firstinspires.ftc.teamcode.hardware.LiftInternals.Position.*
import org.firstinspires.ftc.teamcode.pipelines.QuantizationPipeline
import org.firstinspires.ftc.teamcode.roadrunner.RoadRunnerTrajectories.apply
import org.firstinspires.ftc.teamcode.roadrunner.trajectorysequence.TrajectorySequence
import org.firstinspires.ftc.teamcode.trajectories.Pose
import org.firstinspires.ftc.teamcode.trajectories.Trajectories
import org.firstinspires.ftc.teamcode.waitUntil
import java.lang.Math.floor
import java.util.concurrent.Future
import kotlin.collections.ArrayList
import kotlin.math.PI

@Autonomous
open class FainterLightLeft : AutonomousTemplate() {
    private lateinit var toJunctions: Array<TrajectorySequence>
    protected lateinit var preload: TrajectorySequence
    private lateinit var toStacks: Array<TrajectorySequence>

    override fun initializeTrajectories(): Pose2d? {
        preload = drive.apply(Trajectories.FainterLight.PRELOAD).build()
        val trajectories = Jank.buildTrajectories(CYCLES, Trajectories.FainterLight.PRELOAD)
        toStacks = trajectories.first.map { drive.apply(it).build() }.toTypedArray()
        toJunctions = trajectories.second.map { drive.apply(it).build() }.toTypedArray()

        return preload.start()
    }

    override fun main() {
        repeat(CYCLES + 1) {
            if (it == 0) runPreload(preload, toStacks[it])
            else if (it == CYCLES - 1) // last one
                runCycle(toJunctions[it], buildPark(detectedColor))
            else runCycle(toJunctions[it], toStacks[it])
        }

        val lift = liftInternals.goToPosition(ZERO, LiftInternals.MOTOR_SCALE_FACTOR / 4)

        lift.get()
    }

    // FIXME this ONLY works for left.
    fun buildPark(color: QuantizationPipeline.Color): TrajectorySequence {
        return when (color) {
            QuantizationPipeline.Color.GREEN -> drive.apply(Trajectories.FainterLight.buildJunctionToGreen(pose(drive.poseEstimate))).build()
            QuantizationPipeline.Color.MAGENTA -> toStacks[CYCLES - 1] // gross
            QuantizationPipeline.Color.CYAN -> drive.apply(Trajectories.FainterLight.buildJunctionToGreen(pose(drive.poseEstimate))).strafeLeft(24.0).build()
        }
    }

    fun pose(pose: Pose2d): Pose {
        return Pose(pose.x, pose.y, pose.heading)
    }

    companion object {
        const val CYCLES = 2
    }
}

@Autonomous
class FainterLightRight : FainterLightLeft() {
    override val reversed = true
}