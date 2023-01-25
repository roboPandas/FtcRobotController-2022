package org.firstinspires.ftc.teamcode.hardware

import org.firstinspires.ftc.teamcode.pwm
import org.firstinspires.ftc.teamcode.delay
import org.firstinspires.ftc.teamcode.waitUntil
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.DcMotor.RunMode
import com.qualcomm.robotcore.hardware.TouchSensor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs

class LiftInternals(private val opMode: OpMode) {
    @JvmField val liftExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private val clawExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    @JvmField val rotationServo: Servo
    @JvmField val motor: DcMotor

    private val limitSwitch: TouchSensor
    private val clawServo: Servo
    private val lockServo: Servo

    var motorMode = RunMode.RUN_TO_POSITION
        set(newMode) {
            if (field == newMode) return
            field = newMode
            motor.mode = field
        }

    init {
        val hardwareMap = opMode.hardwareMap

        motor = hardwareMap.dcMotor["liftMotor"]
        rotationServo = hardwareMap.servo["rotationServo"]
        clawServo = hardwareMap.servo["clawServo"]
        lockServo = hardwareMap.servo["lockServo"]
        limitSwitch = hardwareMap.touchSensor["limitSwitch"]

        motor.targetPosition = Position.STACK_1.value
        motor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        // Set an auto-clamp for the servo
        // These all assume that the position scaling is linear, and that we are using the center of the servo's range
        rotationServo.scaleRange(0.17, 0.845)
        clawServo.scaleRange(0.06, 0.195)
        lockServo.scaleRange(0.0, 0.12)
        uncheckedDrop()
    }

    fun grab() {
        internalSetClaw(0, GRAB_DELAY_MS)
    }

    fun drop() {
        internalSetClaw(1, DROP_DELAY_MS)
    }

    // This function is NOT safe to call in a loop.
    // It is used to guarantee that the claw is ALWAYS commanded to open.
    private fun uncheckedDrop() {
        setUnchecked(1, DROP_DELAY_MS)
    }

    private fun internalSetClaw(pos: Int, delayMillis: Long) {
        val currentPos = clawServo.position.toInt()
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
    /** @param reversed if true, enables drop-off from the pickup side */
    fun rotateToDrop(reversed: Boolean = false) {
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

    fun checkLimitSwitch() {
        if (limitSwitch.isPressed) resetEncoder()
    }

    // motor
    fun goToPositionBlocking(targetPosition: Position, power: Double) {
        goToPositionBlocking(targetPosition.value, power)
    }

    fun goToPositionBlocking(pos: Int, power: Double) {
        // I didn't reuse as much code as I could have since I want to avoid multithreading unless needed
        val needsLock = goToPositionInternal(pos, power)
        awaitSlide()
        println("motor finished moving to $pos")
        if (needsLock) lock()
    }

    /** Power MUST be positive.  */
    fun goToPosition(targetPosition: Position, power: Double) {
        goToPosition(targetPosition.value, power)
    }

    /** Power MUST be positive.  */
    private fun goToPosition(targetPosition: Int, power: Double) { // just in case
        liftExecutor.submit {
            if (goToPositionInternal(targetPosition, power)) {
                awaitSlide()
                println("motor finished moving to $targetPosition")
                lock()
            }
        }
    }

    private fun goToPositionInternal(targetPosition: Int, power: Double): Boolean {
        // positive is up
        val needsLock = targetPosition < motor.currentPosition
        if (needsLock) {
            motorMode = RunMode.RUN_USING_ENCODER
            motor.power = MOTOR_UNLOCK_POWER
            delay(100)
            unlock()
        }

        delay(100)

        motor.targetPosition = targetPosition
        motorMode = RunMode.RUN_TO_POSITION
        motor.power = power * MOTOR_SCALE_FACTOR

        println("power set: " + motor.power)
        return needsLock
    }

    fun awaitSlide() {
        waitUntil {
            checkLimitSwitch()
            abs(motor.currentPosition - motor.targetPosition) < 50
        }
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
        STACK_1(145), STACK_2(285), STACK_3(285), STACK_4(430), STACK_5(570),  // the lowest position that allows rotation
        LOW(1140), MIDDLE(1990), HIGH(2850);

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

        companion object {
            @JvmField val CAN_ROTATE = LOW
        }
    }

    companion object {
        const val MOTOR_SCALE_FACTOR = 0.8
        const val MOTOR_UNLOCK_POWER = 0.1

        // TODO for kotlin refactor grab, drop, lock, unlock, rotate into boolean vals (locked, grabbed, etc.)
        // Claw
        // Grab is 0; drop is 1
        const val GRAB_DELAY_MS = 700L
        const val DROP_DELAY_MS = 750L
    }
}