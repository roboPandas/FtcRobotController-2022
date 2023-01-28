package org.firstinspires.ftc.teamcode

import org.firstinspires.ftc.teamcode.hardware.LiftInternals
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.Gamepad
import java.util.concurrent.ExecutorService

class ManualLift(private val liftInternals: LiftInternals, private val opMode: OpMode, private val cycleExecutor: ExecutorService) : LiftSubsystem {
    private val gamepad: Gamepad = opMode.gamepad1

    private var lastPower = 0.0

    override fun loop() {
        // reset encoder
        if (gamepad.left_bumper && gamepad.right_bumper) liftInternals.resetEncoder()

        // rotation
        liftInternals.rotationServo.position = 1.0 - gamepad.left_trigger

        // slide
        val (needsUnlock, power) = when {
            gamepad.dpad_up -> Pair(false,
                if (liftInternals.motor.currentPosition > LiftInternals.Position.HIGH.value + 50) 0.0
                else LiftInternals.MOTOR_SCALE_FACTOR
            )
            gamepad.dpad_down -> Pair(true, -LiftInternals.MOTOR_SCALE_FACTOR)
            else -> Pair(false, 0.0)
        }

        if (lastPower != power) {
            liftInternals.liftExecutor.submit {
                if (needsUnlock) {
                    liftInternals.motor.power = LiftInternals.MOTOR_UNLOCK_POWER
                    delay(LiftInternals.LOCK_DELAY_MS)
                    liftInternals.unlock()
                } else {
                    liftInternals.lock()
                }
                delay(100)
                liftInternals.motor.power = power
            }
        }
        lastPower = power

        // claw (closed by default)
        if (gamepad.right_trigger > 0.5) liftInternals.drop() else liftInternals.grab()
    }

    override val canSwitch get() =
        liftInternals.rotationServo.position >= 0.9 || liftInternals.motor.currentPosition > LiftInternals.Position.CAN_ROTATE.value

    override fun prepareForSwitch() {
        Cycle(
            opMode,
            cycleExecutor,
            liftInternals,
            LiftInternals.Position.LOW,
            LiftInternals.Position.STACK_1
        ).finish()
    }
}