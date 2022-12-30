package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.Utils
import org.firstinspires.ftc.teamcode.hardware.Drivetrain
import org.firstinspires.ftc.teamcode.hardware.LiftInternals

@TeleOp
class Test2 : OpMode() {
    lateinit var liftInternals: LiftInternals
    lateinit var drivetrain: Drivetrain

    override fun init() {
        liftInternals = LiftInternals(hardwareMap)
        drivetrain = Drivetrain(hardwareMap, gamepad1)
    }

    override fun loop() {
        if (gamepad1.dpad_up) {
            liftInternals.lock()
                Utils.delay(50)
                liftInternals.motor.power = LiftInternals.SCALE_FACTOR
        } else if (gamepad1.dpad_down) {
            liftInternals.unlock()
                Utils.delay(50)
                liftInternals.motor.power = -LiftInternals.SCALE_FACTOR
        } else {
            liftInternals.lock()
                Utils.delay(50)
                liftInternals.motor.power = 0.0
        }

        drivetrain.loop()
        telemetry.addData("encoder position", liftInternals.motor.currentPosition)
    }
}