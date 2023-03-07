package org.firstinspires.ftc.teamcode.opmodes.tests

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.DcMotor

@TeleOp
class EncoderTest : LinearOpMode() {
    override fun runOpMode() {
        val encoderX = hardwareMap.dcMotor["encoderX"]
        val encoderY = hardwareMap.dcMotor["encoderY"]

        waitForStart()

        encoderX.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        encoderX.mode = DcMotor.RunMode.RUN_USING_ENCODER

        encoderY.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        encoderY.mode = DcMotor.RunMode.RUN_USING_ENCODER

        while (opModeIsActive()) {
            telemetry.addData("encoderX", encoderX.currentPosition)
            telemetry.addData("encoderY", encoderY.currentPosition)
            telemetry.update()
        }
    }
}