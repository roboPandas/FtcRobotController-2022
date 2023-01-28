package org.firstinspires.ftc.teamcode.opmodes.tests

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.DcMotor

@Autonomous(group = "tests")
class BusyTest : LinearOpMode() {
    override fun runOpMode() {
        val motor = hardwareMap.dcMotor["liftMotor"]
        motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        motor.mode = DcMotor.RunMode.RUN_TO_POSITION
        motor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        waitForStart()
        while (opModeIsActive()) {
            motor.targetPosition = 500
            motor.power = 0.4
            while (!isStopRequested && 500 - motor.currentPosition > 50) telemetry.run {
                addData("is busy", motor.isBusy)
                addData("target position", motor.targetPosition)
                addData("current position", motor.currentPosition)
                update()
            }
            telemetry.run {
                addData("is busy", motor.isBusy)
                addData("target position", motor.targetPosition)
                addData("current position", motor.currentPosition)
                update()
            }
        }
    }
}