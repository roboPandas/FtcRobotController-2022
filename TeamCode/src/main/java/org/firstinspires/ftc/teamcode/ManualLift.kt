package org.firstinspires.ftc.teamcode

import org.firstinspires.ftc.teamcode.hardware.LiftInternals
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.Utils.delay

class ManualLift(private val liftInternals: LiftInternals, private val gamepad: Gamepad) : LiftSubsystem {
    override fun loop(): Boolean {
        // reset encoder
        if (gamepad.left_bumper && gamepad.right_bumper) liftInternals.resetEncoder()

        // rotation
        liftInternals.rotationServo.position = 1.0 - gamepad.left_trigger

        // slide
        if (gamepad.dpad_up) {
            liftInternals.lock()
            LiftInternals.liftExecutor.submit {
                delay(50)
                liftInternals.motor.power = LiftInternals.SCALE_FACTOR
            }
        } else if (gamepad.dpad_down) {
            liftInternals.unlock()
            LiftInternals.liftExecutor.submit {
                delay(50)
                liftInternals.motor.power = -LiftInternals.SCALE_FACTOR
            }
        } else {
            liftInternals.lock()
            LiftInternals.liftExecutor.submit {
                delay(50)
                liftInternals.motor.power = 0.0
            }
        }

        // claw (closed by default)
        if (gamepad.right_trigger > 0.5) liftInternals.drop() else liftInternals.grab()
        return true
    }

    override fun prepareForSwitch() { // FIXME fix this to be more elegant and to not break things. as of now this function is NOT safe to call.
        liftInternals.motor.targetPosition = LiftInternals.Position.STACK_1.value
        liftInternals.motor.mode = DcMotor.RunMode.RUN_TO_POSITION
        liftInternals.drop()
        liftInternals.rotateToGrab(false)
        liftInternals.goToPosition(LiftInternals.Position.STACK_1, 1.0)
    }
}