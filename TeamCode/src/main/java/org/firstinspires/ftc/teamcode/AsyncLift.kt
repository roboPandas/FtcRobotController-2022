package org.firstinspires.ftc.teamcode

import org.firstinspires.ftc.teamcode.hardware.LiftInternals
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.DcMotor
import kotlin.math.max
import kotlin.math.min

/** The bridge between the Cycle system and the controller input.  */
class AsyncLift(private val liftInternals: LiftInternals, private val gamepad: Gamepad) : LiftSubsystem {
    private var currentCycle: Cycle? = null
    private var topPosition = LiftInternals.Position.HIGH
    private var bottomPositionIndex = 1

    override fun loop(): Boolean {
        if (currentCycle != null) {
            // cycle is already present
            when {
                currentCycle!!.stage == Cycle.Stage.COMPLETE -> currentCycle = null
                !currentCycle!!.isBusy && gamepad.a -> currentCycle!!.finish()
            }
            return false
        }

        // reset encoder
        if (gamepad.left_bumper && gamepad.right_bumper) liftInternals.resetEncoder()

        // set top target
        topPosition = when {
            gamepad.b -> LiftInternals.Position.HIGH
            gamepad.y -> LiftInternals.Position.MIDDLE
            gamepad.x -> LiftInternals.Position.LOW
            else -> topPosition
        }

        // set bottom target
        bottomPositionIndex = when {
            gamepad.dpad_down -> max(bottomPositionIndex - 1, 1)
            gamepad.dpad_up -> min(bottomPositionIndex + 1, 5)
            else -> bottomPositionIndex
        }

        val bottomPosition = LiftInternals.Position.fromStackHeight(bottomPositionIndex)
        liftInternals.goToPosition(bottomPosition, 1.0)

        // create cycle
        if (!liftInternals.motor.isBusy && gamepad.a) {
            currentCycle = Cycle(liftInternals, topPosition, bottomPosition)
            currentCycle!!.start()
            return false
        }

        return true
    }

    override fun prepareForSwitch() {
        liftInternals.mode = DcMotor.RunMode.RUN_USING_ENCODER
    }
}