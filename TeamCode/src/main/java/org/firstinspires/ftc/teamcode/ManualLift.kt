package org.firstinspires.ftc.teamcode

import org.firstinspires.ftc.teamcode.hardware.LiftInternals
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.DcMotor

class ManualLift(private val liftInternals: LiftInternals, private val gamepad: Gamepad) : LiftSubsystem {
    override suspend fun loop() {
        // rotation
        liftInternals.rotationServo.position = gamepad.left_trigger.toDouble()

        // slide
        liftInternals.motor.power =
                if (gamepad.dpad_up) LiftInternals.SCALE_FACTOR else if (gamepad.dpad_down) -LiftInternals.SCALE_FACTOR else 0.0 // TODO perhaps make a method to automatically scale the motor power

        // claw (closed by default)
        if (gamepad.right_trigger > 0.5) liftInternals.drop() else liftInternals.grab()
    }

    override var canSwitch = true

    override fun prepareForSwitch() {
        liftInternals.motor.targetPosition = LiftInternals.Position.STACK_1.value
        liftInternals.motor.mode = DcMotor.RunMode.RUN_TO_POSITION
        liftInternals.drop()
        liftInternals.rotateToGrab(false)
        liftInternals.goToPosition(LiftInternals.Position.STACK_1, 1.0)
    }
}