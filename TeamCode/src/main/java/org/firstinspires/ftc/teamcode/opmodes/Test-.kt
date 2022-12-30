package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.hardware.Drivetrain

@TeleOp
class Test : OpMode() {
    lateinit var motor: DcMotor
    lateinit var drivetrain: Drivetrain

    override fun init() {
        motor = hardwareMap.dcMotor["liftMotor"]
        drivetrain = Drivetrain(hardwareMap, gamepad1)
    }

    override fun loop() {
        motor.power = if (gamepad1.dpad_up) 0.8 else if (gamepad1.dpad_down) -0.8 else 0.0
        drivetrain.loop()
        telemetry.addData("encoder position", motor.currentPosition)
    }
}
