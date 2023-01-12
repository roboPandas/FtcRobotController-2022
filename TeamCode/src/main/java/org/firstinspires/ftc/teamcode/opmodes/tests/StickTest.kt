package org.firstinspires.ftc.teamcode.opmodes.tests

import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp
@Disabled
class StickTest : OpMode() {
    override fun init() {}

    override fun loop() {
        telemetry.addData("gp1 left x", gamepad1.left_stick_x)
        telemetry.addData("gp1 left y", gamepad1.left_stick_y)
    }
}