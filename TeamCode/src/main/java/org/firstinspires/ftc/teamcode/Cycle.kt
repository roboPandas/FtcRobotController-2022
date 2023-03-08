package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.hardware.LiftInternals
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import kotlin.jvm.Volatile
import kotlin.jvm.JvmOverloads

/** Represents one intake cycle.  */
class Cycle @JvmOverloads constructor(
    private val opMode: OpMode,
    private val executor: ExecutorService,
    private val liftInternals: LiftInternals,
    private val topPosition: LiftInternals.Position,
    private val reversed: Boolean = false,
    private val bottomPosition: () -> LiftInternals.Position
) {
    @Volatile
    var stage = Stage.WAITING

    @Volatile
    var forceTestPass = false

    // TODO refactor the reversed code same as the kotlin was
    init {
        // validate positions
        require(reversed || topPosition.value >= LiftInternals.Position.CAN_ROTATE.value) { "invalid top position: " + topPosition.value }
    }

    fun start(): Future<*> {
        stage = Stage.GRABBING
        println("start")
        return executor.submit {
            // grab item
            liftInternals.uncheckedGrab()
            liftInternals.awaitClaw()
            whenGrabbed()
        }
    }

    fun startPreload(): Future<*> = executor.submit(::whenGrabbed)

    private fun whenGrabbed() {
        stage = Stage.GRABBED
        liftInternals.goToPosition(topPosition, 1.0)
        if (!reversed) {
            waitUntil { liftInternals.motor.currentPosition >= LiftInternals.Position.CAN_ROTATE.value - 50 }
        }
        liftInternals.rotateToDrop(reversed) // just in case
        liftInternals.awaitTargetHit()
        stage = Stage.WAITING_FOR_TEST
    }

    fun test(): Future<*> {
        println("test start")
        stage = Stage.TEST_DROP

        return executor.submit {
            println("going just below ${topPosition.value}")

            liftInternals.goToPositionBlocking(topPosition.value - 350, 1.0)
            stage = Stage.TEST_WAITING

            while (stage == Stage.TEST_WAITING) {
                if (opMode.gamepad1.start) {
                    stage = Stage.TEST_REVERTING
                    println("reverting to $topPosition")

                    liftInternals.goToPositionBlocking(topPosition, 1.0)
                    stage = Stage.WAITING_FOR_TEST
                } else if (opMode.gamepad1.a || forceTestPass) {
                    println("continuing to $topPosition")

                    liftInternals.drop()

                    if (!reversed) {
                        delay(300) // small extra delay to let cone fall
                        liftInternals.goToPositionBlocking(topPosition, 1.0)
                    }

                    internalFinish(true) // TODO for the future we probably want to call get() here
                }
            }
        }
    }

    fun finish(): Future<*> = internalFinish(false)

    private fun internalFinish(dropOptimized: Boolean): Future<*> {
        stage = Stage.DROPPING
        println("finishing")
        return executor.submit {
            // drop item
            if (!dropOptimized) {
                println("drop")
                liftInternals.drop()
                liftInternals.awaitClaw()
            }
            stage = Stage.DROPPED
            println("start rotating")
            liftInternals.rotateToGrab()
            // if lift is too far down, we need to wait for rotation to finish before dropping more
            if (!reversed && topPosition.value < LiftInternals.Position.MIDDLE.value) delay(600)

            // wait until lift is done before finishing
            println("go to final position")
            liftInternals.goToPositionBlocking(bottomPosition(), 1.0)
            stage = Stage.COMPLETE
        }
    }

    /**
     * Combines the speed of [finish] with the accuracy of [test].
     * The cost of this is that this function is harder to use correctly.
     */
    internal fun autonomousMagicFinish(): Future<*> {
        require(topPosition == LiftInternals.Position.HIGH) {
            "This code is untested on junctions other than HIGH. Rotation safety is not checked, so this can break the robot."
        }
        println("autonomous magic finish")
        return executor.submit() {
            liftInternals.goToPosition(bottomPosition(), 1.0)
            delay(600) // TODO tune
            println("drop")
            stage = Stage.DROPPING
            liftInternals.drop()
            delay(400) // TODO maybe we can remove this. it's to prevent driving while we are hooked.
            stage = Stage.DROPPED // wait for this to know when driving is okay
            println("start rotating")
            liftInternals.rotateToGrab()
            liftInternals.awaitTargetHit()
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

    enum class Stage {
        WAITING, GRABBING, GRABBED, WAITING_FOR_TEST, TEST_DROP, TEST_WAITING, TEST_REVERTING, DROPPING, DROPPED, COMPLETE
    }

    companion object {
        const val GRAB_DELAY_MS = 550L
        const val DROP_DELAY_MS = 500L
    }
}