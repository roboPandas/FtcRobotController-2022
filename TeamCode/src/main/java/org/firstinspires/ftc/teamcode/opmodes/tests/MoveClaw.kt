package org.firstinspires.ftc.teamcode.opmodes.tests

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode

@Autonomous
class MoveClaw : LinearOpMode() {
    override fun runOpMode() {
        val claw = hardwareMap.servo["clawServo"]
        claw.scaleRange(0.08, 0.195)
        claw.position = 0.8
        sleep(2000)
        claw.position = 0.2
    }
}
