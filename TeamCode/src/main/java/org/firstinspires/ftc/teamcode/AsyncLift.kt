package org.firstinspires.ftc.teamcode

import org.firstinspires.ftc.teamcode.hardware.LiftInternals.Position.Companion.fromStackHeight
import org.firstinspires.ftc.teamcode.hardware.LiftInternals
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.DcMotor
import kotlin.math.max
import kotlin.math.min;

/** The bridge between the Cycle system and the controller input.  */
class AsyncLift(private val liftInternals: LiftInternals, private val gamepad: Gamepad) : LiftSubsystem {
    private var currentCycle: Cycle? = null
    override var canSwitch = true
    private var topPosition = LiftInternals.Position.HIGH
    private var bottomPositionValue = 1
    override suspend fun loop() {
        if (currentCycle == null) {
            // reset encoder
            if (gamepad.left_bumper && gamepad.right_bumper) liftInternals.resetEncoder()

            // set target
            if (gamepad.b) topPosition = LiftInternals.Position.HIGH
            else if (gamepad.y) topPosition = LiftInternals.Position.MIDDLE
            else if (gamepad.x) topPosition = LiftInternals.Position.LOW
            if (gamepad.dpad_down) bottomPositionValue = max(bottomPositionValue - 1, 1)
            if (gamepad.dpad_up) bottomPositionValue = min(bottomPositionValue + 1, 5)
            canSwitch = false
            val bottomPosition = fromStackHeight(bottomPositionValue)
            liftInternals.goToPosition(bottomPosition, 1.0)
            if (liftInternals.motor.isBusy) return  // do not create cycles while lift is moving
            canSwitch = true

            // create cycle
            if (gamepad.a) {
                currentCycle = Cycle(liftInternals, topPosition, bottomPosition)
                currentCycle!!.start()
            }
            return
        }
        canSwitch = false

        // cycle is already present
        if (currentCycle!!.stage == Cycle.Stage.COMPLETE) {
            currentCycle = null
            return
        }
        if (currentCycle!!.isBusy) return

        // stage cannot be WAITING, so must be BETWEEN
        if (gamepad.a) currentCycle!!.finish()
    }

    override fun prepareForSwitch() {
        liftInternals.setMode(DcMotor.RunMode.RUN_USING_ENCODER)
    }
}