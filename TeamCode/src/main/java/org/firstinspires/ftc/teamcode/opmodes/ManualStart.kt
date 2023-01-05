package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor

@TeleOp
class ManualStart : ControlledOpMode() {
    override fun init() {
        super.init()
        liftInternals.setMode(DcMotor.RunMode.RUN_USING_ENCODER)
        all[1] = manualLift
        manualControl = true
    }
}