package org.firstinspires.ftc.teamcode.opmodes.tests

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.whileWatingFor

@Autonomous
class MoveClaw : LinearOpMode() {
    override fun runOpMode() {
        val claw = hardwareMap.servo["clawServo"]
        claw.scaleRange(
            0.45, // out
            0.65 // in
        )
        waitForStart()
        claw.position = 0.0
        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < 2000)  claw.position = 0.0
        claw.position = 1.0
        sleep(2000)
//        while (opModeIsActive()) {
//            claw.position = gamepad1.left_trigger.toDouble()
//        }
    }
}
