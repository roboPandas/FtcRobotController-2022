package org.firstinspires.ftc.teamcode.hardware

import org.firstinspires.ftc.teamcode.pwm
import org.firstinspires.ftc.teamcode.delay
import org.firstinspires.ftc.teamcode.waitUntil
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.DcMotor.RunMode
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.math.abs
import kotlin.math.roundToInt

class LiftInternals(private val opMode: OpMode) {
    @JvmField val liftExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private val clawExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    @JvmField val motor: DcMotor
    @JvmField val rotationServo: Servo
    val clawServo: Servo
    private val lockServo: Servo

    var motorMode = RunMode.RUN_TO_POSITION
        set(newMode) {
            if (field == newMode) return
            println("changing mode from $field to $newMode")
            field = newMode
            motor.mode = field
        }

    // 5 3/4, y wheel
    //

    init {
        val hardwareMap = opMode.hardwareMap
        motor = hardwareMap.dcMotor["liftMotor"]
        rotationServo = hardwareMap.servo["rotationServo"]
        clawServo = hardwareMap.servo["clawServo"]
        lockServo = hardwareMap.servo["lockServo"]
        motor.targetPosition = Position.STACK_1.value // need to have a pos set before mode is set (by resetEncoder)
        resetEncoder()
        motor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        // Set an auto-clamp for the servo
        // These all assume that the position scaling is linear, and that we are using the center of the servo's range
        rotationServo.scaleRange(0.1, 0.78) // (+)
        clawServo.scaleRange(0.38, 0.8)
        lockServo.scaleRange(0.0, 0.12)

        lock() // needed to fix a bug where unlocking fails on the first cycle
        rotateToGrab()
        uncheckedDrop()
    }

    @Suppress("NOTHING_TO_INLINE")
    inline fun initSlide() = goToPosition(Position.STACK_1, 0.25)

    fun grab() {
        internalSetClaw(1, GRAB_DELAY_MS)
    }

    fun drop() {
        internalSetClaw(0, DROP_DELAY_MS)
    }

    // This function is NOT safe to call in a loop.
    // It is used to guarantee that the claw is ALWAYS commanded to open.
    internal fun uncheckedGrab() {
        setUnchecked(1, GRAB_DELAY_MS)
    }

    internal fun uncheckedDrop() {
        setUnchecked(0, DROP_DELAY_MS)
    }

    private fun internalSetClaw(pos: Int, delayMillis: Long) {
        val currentPos = clawServo.position.roundToInt()
        if (currentPos == pos) return
        setUnchecked(pos, delayMillis)
    }

    // NOT safe for a loop
    private fun setUnchecked(pos: Int, delayMillis: Long) {
        clawServo.pwm = true
        clawServo.position = pos.toDouble()
        println("position set " + clawServo.position)
        clawExecutor.submit {
            delay(delayMillis)
            clawServo.pwm = false
        }
    }

    // Rotation
    // Grab is 1; drop is 0
    fun rotateToDrop(reversed: Boolean) {
        rotationServo.position = if (reversed) 1.0 else 0.0
    }

    fun rotateToGrab() {
        rotationServo.position = 1.0
    }

    // Lock
    // Lock is 0; unlock is 1
    fun lock() {
        lockServo.position = 0.0
    }

    fun unlock() {
        lockServo.position = 1.0
    }

    // motor
    @Suppress("NOTHING_TO_INLINE")
    inline fun goToPositionBlocking(targetPosition: Position, power: Double) {
        goToPositionBlocking(targetPosition.value, power)
    }

    fun goToPositionBlocking(pos: Int, power: Double) {
        // I didn't reuse as much code as I could have since I want to avoid multithreading unless needed
        val needsLock = goToPositionInternal(pos, power)
        awaitTargetHit()
        println("motor finished moving to $pos")
        if (needsLock) lock()
    }

    /** Power MUST be positive.  */
    fun goToPosition(targetPosition: Position, power: Double): Future<*> {
        return goToPosition(targetPosition.value, power)
    }

    /** Power MUST be positive.  */
    fun goToPosition(targetPosition: Int, power: Double): Future<*> { // just in case
        return liftExecutor.submit {
            val needsLock = goToPositionInternal(targetPosition, power)
            awaitTargetHit()
            println("motor finished moving to $targetPosition")
            if (needsLock) lock()
        }
    }

    private fun goToPositionInternal(targetPosition: Int, power: Double): Boolean {
        // positive is up
        val needsLock = targetPosition < motor.currentPosition
        if (needsLock) {
            motorMode = RunMode.RUN_USING_ENCODER
            motor.power = MOTOR_UNLOCK_POWER
            delay(LOCK_DELAY_MS)
            unlock()
        }

        delay(100)

        motor.targetPosition = targetPosition
        motorMode = RunMode.RUN_TO_POSITION
        motor.power = power * MOTOR_SCALE_FACTOR

        println("power set: " + motor.power)
        return needsLock
    }

    @Suppress("NOTHING_TO_INLINE")
    inline fun awaitTargetHit() {
        waitUntil { abs(motor.currentPosition - motor.targetPosition) < 50 }
    }

    fun awaitClaw() {
        waitUntil { !clawServo.pwm } // only true while powered
    }

    fun resetEncoder() {
        motor.mode = RunMode.STOP_AND_RESET_ENCODER
        motor.mode = motorMode
    }

    enum class Position(@JvmField val value: Int) {
        // TODO document the math for why we need 5 separate stack positions
        // TODO test if we need to explicitly disable locking for the GROUND position
        // STACK_N is a stack containing N cones
        // STACK_1 is for a single cone, and should be the default bottom position

        STACK_1(210), STACK_2(260), STACK_3(350), STACK_4(490), STACK_5(630),  // the lowest position that allows rotation

        CAN_ROTATE(1530),  // junction heights

        LOW(1530), MIDDLE(2250), HIGH(3100),

        ZERO(0);

        operator fun inc(): Position {
            val ordinal = ordinal
            return if (ordinal < 4) { // only allow stack ones, except top
                values()[ordinal + 1]
            } else this // how did we get here?
        }

        operator fun dec(): Position {
            val ordinal = ordinal
            return if (ordinal in 1..4) { // only allow stack ones, except bottom
                values()[ordinal - 1]
            } else this
        }
    }

    companion object {
        const val MOTOR_SCALE_FACTOR = 0.8
        const val MOTOR_UNLOCK_POWER = 0.2
        const val LOCK_DELAY_MS = 200L

        // TODO for kotlin refactor grab, drop, lock, unlock, rotate into boolean vals (locked, grabbed, etc.)
        // TODO also remove the reverse functions during said refactor
        // Claw
        // Grab is 0; drop is 1
        const val GRAB_DELAY_MS = 600L
        const val DROP_DELAY_MS = 700L
    }
}