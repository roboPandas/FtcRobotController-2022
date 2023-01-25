package org.firstinspires.ftc.teamcode.opmodes.tests

import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp
@Disabled
class JoystickValueTest : OpMode() {
    private val inputs: ArrayList<Pair<Float, Float>> = ArrayList()

    override fun init() { }

    override fun loop() {
        val reading = Pair(gamepad1.left_stick_x, gamepad1.left_stick_y)

        if (inputs.size == 0) {
            inputs.add(reading)
        } else if (inputs[inputs.size - 1] != reading) {
            inputs.add(reading)
        }

        telemetry.addData("last ten", inputs.toString())
    }
}