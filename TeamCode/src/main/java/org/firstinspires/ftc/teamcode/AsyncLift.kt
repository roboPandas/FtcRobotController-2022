package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.hardware.LiftInternals
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorImpl
import java.util.concurrent.ExecutorService

/** The bridge between the Cycle system and the controller input.  */
class AsyncLift(private val liftInternals: LiftInternals, private val opMode: OpMode, private val cycleExecutor: ExecutorService) : LiftSubsystem {
    private val gamepad = opMode.gamepad1
    private var currentCycle: Cycle? = null
    override var canSwitch = true
    private var topPosition = LiftInternals.Position.HIGH
    private var queuedTopPosition: LiftInternals.Position? = null
    private var bottomPosition = LiftInternals.Position.STACK_1
    private var lastBottomPosChange = BottomPosChange.NONE

    private var reversed = false
    private var reverseCache = false
    private fun checkReverse() {
        val button = gamepad.back
        if (button && !reverseCache) reversed = !reversed
        reverseCache = button
    }

    override fun loop() {
        if (currentCycle != null) {
            loopWithCycle()
        } else {
            loopWithoutCycle()
        }
        checkReverse()
    }

    private fun loopWithoutCycle() {
        // reset encoder
        if (gamepad.left_bumper && gamepad.right_bumper) liftInternals.resetEncoder()

        // set target
        topPosition = getTopPosition(topPosition)
        val oldBottomPosition = bottomPosition
        bottomPosition = getBottomPosition()
        if (oldBottomPosition != bottomPosition) {
            liftInternals.goToPosition(bottomPosition, 1.0)
        }
        if (liftInternals.motor.isBusy) {
            println("motor is busy; cycles not being created")
            return  // do not create cycles while lift is moving
        }
        canSwitch = true

        // create cycle
        if (gamepad.a) {
            currentCycle = Cycle(opMode, cycleExecutor, liftInternals, topPosition, reversed) { bottomPosition }
            opMode.telemetry.addData("top position", topPosition)
            println("A: start cycle")
            currentCycle!!.start()
            canSwitch = false
        }
    }

    private fun loopWithCycle() {
        canSwitch = false

        // cycle is already present
        queuedTopPosition =
            getTopPosition(queuedTopPosition ?: topPosition)
        bottomPosition =
            getBottomPosition()

        // check if the current cycle is done and reset if so
        if (currentCycle!!.stage == Cycle.Stage.COMPLETE) {
            currentCycle = null
            // when a cycle ends, we set our new target from the queue
            topPosition = queuedTopPosition!!
            queuedTopPosition = null
            return
        }
        if (currentCycle!!.isBusy) return
        if (gamepad.a) {
            if (currentCycle!!.stage == Cycle.Stage.WAITING_FOR_TEST) {
                currentCycle!!.test()
            }
        }
    }

    private fun getTopPosition(current: LiftInternals.Position): LiftInternals.Position {
        if (gamepad.b) return LiftInternals.Position.HIGH else if (gamepad.y) return LiftInternals.Position.MIDDLE else if (gamepad.x) return LiftInternals.Position.LOW
        return current
    }

    private fun getBottomPosition(): LiftInternals.Position {
        val change = changeFromTriggers
        if (change == lastBottomPosChange) return bottomPosition
        val newPos: LiftInternals.Position = when (change) {
            BottomPosChange.DOWN -> bottomPosition.dec()
            BottomPosChange.UP -> bottomPosition.inc()
            else -> bottomPosition
        }
        lastBottomPosChange = change
        canSwitch = false
        return newPos
    }

    override fun prepareForSwitch() {
        liftInternals.motor.power = 0.0
        liftInternals.motorMode = DcMotor.RunMode.RUN_USING_ENCODER
    }

    private enum class BottomPosChange {
        DOWN, UP, NONE
    }

    private val changeFromTriggers: BottomPosChange
        get() = if (gamepad.left_trigger > 0.5) BottomPosChange.DOWN else if (gamepad.right_trigger > 0.5) BottomPosChange.UP else BottomPosChange.NONE
}