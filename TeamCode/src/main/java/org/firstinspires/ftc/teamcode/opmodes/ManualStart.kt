package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.hardware.LiftInternals

@TeleOp
class ManualStart : ControlledOpMode() {
    override fun init() {
        super.init()
        liftInternals.motorMode = DcMotor.RunMode.RUN_USING_ENCODER
        currentLiftSubsystem = manualLift
        manualControl = true
    }

    override fun LiftInternals.init() {
        // don't
    }
}