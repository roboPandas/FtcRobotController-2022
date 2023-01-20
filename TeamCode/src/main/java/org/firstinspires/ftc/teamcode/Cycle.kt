package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.hardware.LiftInternals
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

/** Represents one intake cycle.  */
class Cycle(
    private val opMode: OpMode,
    private val executor: ExecutorService,
    private val liftInternals: LiftInternals,
    private val topPosition: LiftInternals.Position,
    private val bottomPosition: LiftInternals.Position,
    private val reversedDropoff: Boolean = false
) {
    @Volatile var stage = Stage.WAITING

    init {
        // validate position
        require(reversedDropoff || topPosition.value > LiftInternals.Position.CAN_ROTATE.value) { "invalid top position: ${topPosition.value}" }
    }

    fun start(): Future<*> {
        stage = Stage.GRABBING
        println("start")
        return executor.submit {
            // grab item
            liftInternals.grab()
            println("grabbed?")
            delay(GRAB_DELAY_MS) // this delay is to make sure it's in there
            whenGrabbed()
        }
    }

    fun startPreload() {
        executor.submit { whenGrabbed() }
    }

    private fun whenGrabbed() {
        stage = Stage.GRABBED
        liftInternals.goToPosition(topPosition, 1.0)
        waitUntil { liftInternals.motor.currentPosition >= LiftInternals.Position.CAN_ROTATE.value - 50 }
        liftInternals.rotateToDrop(reversedDropoff)
        liftInternals.awaitSlide()
        stage = Stage.WAITING_FOR_TEST
    }

    fun test(): Future<*> {
        if (topPosition.value < 400) return finish()
        stage = Stage.TEST_DROP
        return executor.submit {
            liftInternals.goToPositionBlocking(topPosition.value - 350, 1.0)
            stage = Stage.TEST_WAITING
            while (stage == Stage.TEST_WAITING) {
                if (opMode.gamepad1.start) {
                    stage = Stage.TEST_REVERTING
                    liftInternals.goToPositionBlocking(topPosition, 1.0)
                    stage = Stage.WAITING_FOR_TEST
                } else if (opMode.gamepad1.a) {
                    liftInternals.goToPositionBlocking(topPosition, 1.0)
                    finish()
                }
            }
        }
    }

    // TODO this is basically the same as start so can it be refactored somehow?
    fun finish(): Future<*> {
        stage = Stage.DROPPING
        println("finish")
        return executor.submit {
            // drop item
            println("drop")
            liftInternals.drop()
            delay(DROP_DELAY_MS) // this delay is to make sure it's out of our way
            stage = Stage.DROPPED
            println("start rotating")
            liftInternals.rotateToGrab()
            if (!reversedDropoff && topPosition.value < LiftInternals.Position.MIDDLE.value) delay(600)

            // wait until lift is done before finishing
            println("go to final position")
            liftInternals.goToPositionBlocking(bottomPosition, 1.0)
            stage = Stage.COMPLETE
        }
    }

    val isBusy: Boolean
        get() = !(stage == Stage.WAITING || stage == Stage.WAITING_FOR_TEST || stage == Stage.TEST_WAITING || stage == Stage.COMPLETE)

    /**
     * @return true if COMPLETE, false if BETWEEN or WAITING
     */
    fun await(): Boolean {
        delay(100) // Make sure that the other thread has a chance to set the state
        while (isBusy) delay(50)
        return stage == Stage.COMPLETE
    }

    fun waitUntil(stage: Stage) {
        delay(100) // Make sure that the other thread has a chance to set the state
        while (this.stage != stage) delay(50)
    }

    enum class Stage {
        WAITING, GRABBING, GRABBED, WAITING_FOR_TEST, TEST_DROP, TEST_WAITING, TEST_REVERTING, DROPPING, DROPPED, COMPLETE
    }

    companion object {
        const val GRAB_DELAY_MS = LiftInternals.GRAB_DELAY_MS
        const val DROP_DELAY_MS = 350L
    }
}