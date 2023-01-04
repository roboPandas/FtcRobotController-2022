package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.hardware.DcMotor

class ManualStart : ControlledOpMode() {
    override fun init() {
        super.init()
        liftInternals.setMode(DcMotor.RunMode.RUN_USING_ENCODER)
        all[1] = manualLift
    }
}