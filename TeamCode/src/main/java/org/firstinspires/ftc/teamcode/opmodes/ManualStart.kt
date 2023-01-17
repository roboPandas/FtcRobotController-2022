package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor

@TeleOp
class ManualStart : ControlledOpMode() {
    override fun init() {
        super.init()
        liftInternals.motorMode = DcMotor.RunMode.RUN_USING_ENCODER
        currentLiftSubsystem = manualLift
        manualControl = true
    }
}