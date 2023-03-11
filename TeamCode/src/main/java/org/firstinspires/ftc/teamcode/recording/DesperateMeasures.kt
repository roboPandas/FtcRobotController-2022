package org.firstinspires.ftc.teamcode.recording

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.hardware.LiftInternals
import org.firstinspires.ftc.teamcode.opmodes.ControlledOpMode
import kotlin.properties.Delegates

@TeleOp
class DesperateMeasures : ControlledOpMode() {
    lateinit var gamepad: InputRecordingController

    override fun LiftInternals.init() {
        goToPosition(LiftInternals.Position.ZERO, 0.25).get()
    }

    override fun start() {
        val startTime = runtime
        gamepad = InputRecordingController(gamepad1) { runtime - startTime }
    }

    override fun loop() {
        gamepad.copy(gamepad1)
        super.loop()
    }

    override fun stop() {
        gamepad.export()
    }
}