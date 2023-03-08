package org.firstinspires.ftc.teamcode.opmodes.tests

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.delay


@Autonomous
class ClawTuning : OpMode() {
    private lateinit var claw: Servo

    override fun init() {
        claw = hardwareMap.servo["clawServo"]

        claw.scaleRange(0.415, 0.66)

        claw.position = 1.0
        delay(1000)
        claw.position = 0.0
    }

    override fun loop() {

    }
}