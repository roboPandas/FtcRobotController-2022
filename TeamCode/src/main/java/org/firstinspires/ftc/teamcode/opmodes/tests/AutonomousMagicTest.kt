package org.firstinspires.ftc.teamcode.opmodes.tests

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.Cycle
import org.firstinspires.ftc.teamcode.delay
import org.firstinspires.ftc.teamcode.opmodes.auto.AutonomousTemplate
import org.firstinspires.ftc.teamcode.roadrunner.trajectorysequence.TrajectorySequence
import org.firstinspires.ftc.teamcode.waitUntil

@Autonomous(group = "tests")
class AutonomousMagicTest : AutonomousTemplate() {
    lateinit var traj: TrajectorySequence

    override fun initializeTrajectories(): Pose2d {
        traj = drive.trajectorySequenceBuilder(Pose2d())
            .forward(5.0)
            .build()
        return Pose2d()
    }

    override fun main() {
        currentCycle.startPreload().get()
        delay(2000)
        val finish = currentCycle.autonomousMagicFinish()
        waitUntil { currentCycle.stage == Cycle.Stage.DROPPING }
        drive.followTrajectorySequence(traj)
        finish.get()
        delay(2000)
    }
}