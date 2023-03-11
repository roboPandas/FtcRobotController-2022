package org.firstinspires.ftc.teamcode.opmodes.auto

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.hardware.LiftInternals.Position.ZERO
import org.firstinspires.ftc.teamcode.pipelines.QuantizationPipeline.Color.*
import org.firstinspires.ftc.teamcode.roadrunner.trajectorysequence.TrajectorySequence
import org.firstinspires.ftc.teamcode.roadrunner.trajectorysequence.TrajectorySequenceBuilder
import org.firstinspires.ftc.teamcode.trajectories.Pose
import org.firstinspires.ftc.teamcode.trajectories.Trajectories
import kotlin.math.PI

@Autonomous
class Underworld : AutonomousTemplate() {
    lateinit var preload: TrajectorySequence
    lateinit var toJunction: TrajectorySequence
    lateinit var toStack: TrajectorySequence
    lateinit var park: TrajectorySequenceBuilder

    override fun initializeTrajectories(): Pose2d {
        preload = drive.trajectorySequenceBuilder(Pose2d(-34.5, -63.0, PI))
            .strafeTo(Vector2d(-34.5, 0.0))
            .lineTo(Trajectories.claw2Bot(-23.5, 0.0, true, PI).into().vec())
            .build()
        toStack = drive.trajectorySequenceBuilder(preload.end())
            .strafeTo(Vector2d(-34.5, 0.0))
            .strafeTo(Vector2d(-34.5, -12.0))
            .lineTo(Trajectories.claw2Bot(-69.5, -12.0, false, PI).into().vec())
            .build()
        toJunction = drive.trajectorySequenceBuilder(toStack.end())
            .strafeTo(Vector2d(-34.5, -12.0))
            .strafeTo(Vector2d(-34.5, 0.0))
            .lineTo(Trajectories.claw2Bot(-23.5, 0.0, true, PI).into().vec())
            .build()
        park = drive.trajectorySequenceBuilder(toJunction.end())
            .strafeTo(Vector2d(-34.5, 0.0))
            .strafeTo(Vector2d(-34.5, -12.0))
        return preload.start()
    }

    override fun main() {
        runPreload(preload, toStack)
        repeat(CYCLES - 1) {
            runCycle(toJunction, toStack)
        }
        runCycle(
            toJunction,
            park.run {
                when (detectedColor) {
                    MAGENTA -> forward(24.0)
                    GREEN -> this
                    CYAN -> back(24.0)
                }.build()
            }
        )
        liftInternals.goToPosition(ZERO, 1.0)
    }

    companion object {
        const val CYCLES = 3
    }
}

fun Pose.into() = Pose2d(x, y, heading)