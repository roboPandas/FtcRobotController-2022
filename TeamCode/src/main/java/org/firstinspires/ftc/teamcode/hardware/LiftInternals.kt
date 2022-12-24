package org.firstinspires.ftc.teamcode.hardware

import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.DcMotor.RunMode
import com.qualcomm.robotcore.hardware.DcMotorSimple
import kotlinx.coroutines.delay
import org.firstinspires.ftc.teamcode.Utils
import org.firstinspires.ftc.teamcode.hardware.LiftInternals
import java.util.concurrent.Executors

class LiftInternals(hardwareMap: HardwareMap) {
    @JvmField val motor: DcMotor
    @JvmField val rotationServo: Servo
    @JvmField val clawServo: Servo
    @JvmField val lockServo: Servo

    /** @see .setMode */
    private var mode = RunMode.RUN_TO_POSITION

    init {
        motor = hardwareMap.dcMotor["liftMotor"]
        rotationServo = hardwareMap.servo["rotationServo"]
        clawServo = hardwareMap.servo["clawServo"]
        lockServo = hardwareMap.servo["lockServo"]
        motor.targetPosition = Position.STACK_1.value
        motor.mode = RunMode.RUN_TO_POSITION
        motor.direction = DcMotorSimple.Direction.REVERSE
        motor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        // Set an auto-clamp for the servo
        // These all assume that the position scaling is linear, and that we are using the center of the servo's range
        rotationServo.scaleRange(0.17, 0.845)
        clawServo.scaleRange(0.6, 0.8)
        lockServo.scaleRange(0.0, 0.15)
    }

    // Claw
    // Grab is 0; drop is 1
    fun grab() {
        internalSetClaw(0.0)
    }

    fun drop() {
        internalSetClaw(1.0)
    }

    private suspend fun internalSetClaw(pos: Double) {
        if (clawServo.position == pos) return
        Utils.pwmEnable(clawServo, true)
        clawServo.position = pos
        delay(600)
        Utils.pwmEnable(clawServo, false)
    }

    // Rotation TODO test these numbers
    fun rotateToDrop(reversed: Boolean) {
        rotationServo.position = if (reversed) 0.0 else 1.0
    }

    fun rotateToGrab(reversed: Boolean) {
        rotationServo.position = if (reversed) 1.0 else 0.0
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
    // Prevents constantly setting a new mode
    fun setMode(newMode: RunMode) {
        if (mode == newMode) return
        motor.mode = mode
        mode = newMode
    }

    fun goToPositionBlocking(targetPosition: Position, power: Double) {
        // I didn't reuse as much code as I could have since I want to avoid multithreading unless needed
        val needsLock = goToPositionInternal(targetPosition.value, power)
        while (motor.isBusy) Utils.delay(50) // TODO i changed this from a manual position check to isBusy - should I change it back?
        if (needsLock) lock()
    }

    /** Power MUST be positive.  */
    fun goToPosition(targetPosition: Position, power: Double) {
        goToPosition(targetPosition.value, power)
    }

    /** Power MUST be positive.  */
    private fun goToPosition(targetPosition: Int, power: Double) { // just in case
        if (goToPositionInternal(targetPosition, power)) executor.submit {
            while (motor.isBusy) Utils.delay(50) // TODO i changed this from a manual position check to isBusy - should I change it back?
            lock()
        }
    }

    private fun goToPositionInternal(targetPosition: Int, power: Double): Boolean {
        setMode(RunMode.RUN_TO_POSITION)
        // positive is up
        val needsLock = targetPosition < motor.currentPosition
        if (needsLock) unlock()
        motor.power = power
        motor.targetPosition = targetPosition
        return needsLock
    }

    fun resetEncoder() {
        motor.mode = RunMode.STOP_AND_RESET_ENCODER
        motor.mode = mode
    }

    enum class Position(@JvmField val value: Int) {
        // TODO document the math for why we need 5 separate stack positions
        // TODO test if we need to explicitly disable locking for the GROUND position
        // STACK_N is a stack containing N cones
        // STACK_1 is for a single cone, and should be the default bottom position
        STACK_1(0), STACK_2(0), STACK_3(0), STACK_4(0), STACK_5(0),  // the lowest position that allows rotation
        CAN_ROTATE(0),  // junction heights
        LOW(0), MIDDLE(0), HIGH(0);

        companion object {
            val GROUND = STACK_2
            @JvmStatic fun fromStackHeight(height: Int): Position = valueOf("STACK_$height")
        }
    }

    companion object {
        val executor = Executors.newSingleThreadExecutor() // TODO can we find a way to do this without multithreading? are interrupts a thing?
        const val SCALE_FACTOR = 0.8
    }
}