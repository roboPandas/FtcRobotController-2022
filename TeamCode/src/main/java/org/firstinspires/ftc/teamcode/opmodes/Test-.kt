package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.ManualLift
import org.firstinspires.ftc.teamcode.hardware.Drivetrain
import org.firstinspires.ftc.teamcode.hardware.LiftInternals

@TeleOp
class Test : OpMode() {
    lateinit var lift: ManualLift
    lateinit var drivetrain: Drivetrain

    override fun init() {
        lift = ManualLift(LiftInternals(hardwareMap), gamepad1)
        drivetrain = Drivetrain(hardwareMap, gamepad1)
    }

    override fun loop() {
        lift.loop()
        drivetrain.loop()
        telemetry.addData("encoder position", lift.liftInternals.motor.currentPosition)
    }
}
