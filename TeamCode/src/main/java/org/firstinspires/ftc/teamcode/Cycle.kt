package org.firstinspires.ftc.teamcode

import org.firstinspires.ftc.teamcode.Utils.delay
import org.firstinspires.ftc.teamcode.hardware.LiftInternals
import java.util.concurrent.Executors

/** Represents one intake cycle.  */
class Cycle(private val liftInternals: LiftInternals, private val topPosition: LiftInternals.Position, private val bottomPosition: LiftInternals.Position) {
    @Volatile var stage = Stage.WAITING
    fun start(reversed: Boolean = false) {
        stage = Stage.GRABBING
        executor.submit {
            // grab item
            liftInternals.grab()
            delay(500) // this delay is to make sure it's in there TODO test this number
            whenGrabbed(reversed)
        }
    }

    fun startPreload(reversed: Boolean = false) { executor.submit { whenGrabbed(reversed) } }

    private fun whenGrabbed(reversed: Boolean) {
        stage = Stage.GRABBED

        // don't rotate until safe to do so
        liftInternals.goToPositionBlocking(LiftInternals.Position.CAN_ROTATE, 1.0)

        // TODO make sure that the slide and servo happen simultaneously
        liftInternals.rotateToDrop(reversed)

        // wait until lift is done before finishing
        liftInternals.goToPositionBlocking(topPosition, 1.0)
        stage = Stage.BETWEEN
    }

    // TODO this is basically the same as start so can it be refactored somehow?
    fun finish(reversed: Boolean = false) {
        stage = Stage.DROPPING
        executor.submit {
            // drop item
            liftInternals.drop()
            delay(500) // this delay is to make sure it's out of our way TODO test this number
            stage = Stage.DROPPED

            // TODO make sure that the slide and servo happen simultaneously
            liftInternals.rotateToGrab(reversed)

            // wait until lift is done before finishing
            liftInternals.goToPositionBlocking(bottomPosition, 1.0)
            stage = Stage.COMPLETE
        }
    }

    val isBusy: Boolean
        get() = !(stage == Stage.WAITING || stage == Stage.BETWEEN || stage == Stage.COMPLETE)

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
        WAITING, GRABBING, GRABBED, BETWEEN, DROPPING, DROPPED, COMPLETE
    }

    companion object {
        private val executor = Executors.newSingleThreadExecutor()
    }
}