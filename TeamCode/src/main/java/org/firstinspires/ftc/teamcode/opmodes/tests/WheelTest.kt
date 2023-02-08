package org.firstinspires.ftc.teamcode.opmodes.tests

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple

@Autonomous(group = "tests")
class WheelTest : LinearOpMode() {
    override fun runOpMode() {
        val motors = arrayOf(
            hardwareMap.dcMotor["frontLeft"],
            hardwareMap.dcMotor["frontRight"],
            hardwareMap.dcMotor["backLeft"],
            hardwareMap.dcMotor["backRight"]
        )
        waitForStart()
        if (isStopRequested) return
        motors[1].direction = DcMotorSimple.Direction.REVERSE
        motors[3].direction = DcMotorSimple.Direction.REVERSE
        motors.forEach {
            it.mode = DcMotor.RunMode.RUN_USING_ENCODER
            it.power = 0.8
        }
        sleep(2000)
        motors.forEach { it.power = 0.0 }
    }
}