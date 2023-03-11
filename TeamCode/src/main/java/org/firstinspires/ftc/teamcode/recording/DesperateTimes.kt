package org.firstinspires.ftc.teamcode.recording

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.hardware.LiftInternals
import org.firstinspires.ftc.teamcode.opmodes.ControlledOpMode

@Autonomous
class DesperateTimes : ControlledOpMode() {
    lateinit var gamepad: InputReplayingController

    override fun LiftInternals.init() {
        goToPosition(LiftInternals.Position.ZERO, 0.25).get()
    }

    override fun start() {
        val startTime = runtime
        gamepad = InputReplayingController(gamepad1) { runtime - startTime }
        gamepad.printSequence()
    }

    override fun loop() {
        gamepad.update()
        super.loop()
    }
}