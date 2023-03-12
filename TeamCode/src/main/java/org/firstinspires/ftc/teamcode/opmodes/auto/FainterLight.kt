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
    private lateinit var parking: Array<TrajectorySequence>

    override fun initializeTrajectories(): Pose2d? {
        preload = drive.apply(Trajectories.FainterLight.PRELOAD).build()
        val trajectories = Jank.buildTrajectories(CYCLES, Trajectories.FainterLight.PRELOAD)
        toStacks = trajectories.first.map { drive.apply(it).build() }.toTypedArray()
        toJunctions = trajectories.second.map { drive.apply(it).build() }.toTypedArray()
        parking = Array(3) {
            when (it) {
                // EW
                QuantizationPipeline.Color.MAGENTA.ordinal -> drive.apply(Trajectories.FainterLight.buildJunctionToGreen(pose(toJunctions[CYCLES - 2].end()))).strafeRight(25.0).build()
                QuantizationPipeline.Color.GREEN.ordinal -> drive.apply(Trajectories.FainterLight.buildJunctionToGreen(pose(toJunctions[CYCLES - 2].end()))).build()
                QuantizationPipeline.Color.CYAN.ordinal -> drive.apply(Trajectories.FainterLight.buildJunctionToGreen(pose(toJunctions[CYCLES - 2].end()))).strafeLeft(24.0).build()
                // ???
                else -> drive.trajectorySequenceBuilder(Pose2d()).build()
            }
        }
        return preload.start()
    }

    override fun main() {
        repeat(CYCLES + 1) {
            when (it) {
                0 -> runPreload(preload, toStacks[it])
                // last one
                CYCLES -> runCycle(toJunctions[it], parking[detectedColor.ordinal])
                else -> runCycle(toJunctions[it], toStacks[it])
            }
        }

        val lift = liftInternals.goToPosition(ZERO, LiftInternals.MOTOR_SCALE_FACTOR / 4)

        lift.get()
    }

    override fun initLift() {
        // once again: don't
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