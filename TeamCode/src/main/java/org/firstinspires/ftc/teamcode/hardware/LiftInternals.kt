package org.firstinspires.ftc.teamcode.hardware

import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.DcMotor.RunMode
import org.firstinspires.ftc.teamcode.Utils
import org.firstinspires.ftc.teamcode.Utils.pwmEnabled
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Suppress("NOTHING_TO_INLINE")
class LiftInternals(hardwareMap: HardwareMap) {
    val motor: DcMotor
    val rotationServo: Servo
    val clawServo: Servo
    val lockServo: Servo
    
    var mode = RunMode.RUN_TO_POSITION
        set(value) { // prevents constantly setting a new mode
            if (field == value) return
            motor.mode = value
            field = value
        }

    init {
        motor = hardwareMap.dcMotor["liftMotor"]
        rotationServo = hardwareMap.servo["rotationServo"]
        clawServo = hardwareMap.servo["clawServo"]
        lockServo = hardwareMap.servo["lockServo"]
        motor.targetPosition = Position.STACK_1.value
        resetEncoder() // TODO do we need this?
        motor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        // Set an auto-clamp for the servo
        // These all assume that the position scaling is linear, and that we are using the center of the servo's range
        rotationServo.scaleRange(0.17, 0.845)
        clawServo.scaleRange(0.1, 0.195)
        lockServo.scaleRange(0.0, 0.15)
    }

    // Claw
    // Grab is 0; drop is 1
    fun grab() = internalSetClaw(0.0)

    fun drop() = internalSetClaw(1.0)

    private fun internalSetClaw(pos: Double) {
        if (clawServo.position == pos) return
        clawServo.pwmEnabled = true
        clawServo.position = pos
        clawExecutor.submit {
            Utils.delay(650)
            clawServo.pwmEnabled = false
        }
    }

    // Rotation TODO test these numbers
    inline fun rotateToDrop(reversed: Boolean = false) { rotationServo.position = if (reversed) 1.0 else 0.0 }

    inline fun rotateToGrab(reversed: Boolean = false) { rotationServo.position = if (reversed) 0.0 else 1.0 }

    // Lock
    // Lock is 0; unlock is 1
    inline fun lock() { lockServo.position = 0.0 }

    inline fun unlock() { lockServo.position = 1.0 }

    // motor
    fun goToPositionBlocking(targetPosition: Position, power: Double) {
        // I didn't reuse as much code as I could have since I want to avoid multithreading unless needed
        val needsLock = goToPositionInternal(targetPosition.value, power)
        while (motor.isBusy) Utils.delay(50) // TODO i changed this from a manual position check to isBusy - should I change it back?
        if (needsLock) lock()
    }

    /** Power MUST be positive.  */
    fun goToPosition(targetPosition: Position, power: Double) = goToPosition(targetPosition.value, power)

    /** Power MUST be positive.  */
    private fun goToPosition(targetPosition: Int, power: Double) { // just in case
        if (goToPositionInternal(targetPosition, power)) liftExecutor.submit {
            while (motor.isBusy) Utils.delay(50) // TODO i changed this from a manual position check to isBusy - should I change it back?
            lock()
        }
    }

    private fun goToPositionInternal(targetPosition: Int, power: Double): Boolean {
        mode = RunMode.RUN_TO_POSITION
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

    enum class Position(val value: Int) {
        // TODO document the math for why we need 5 separate stack positions
        // TODO test if we need to explicitly disable locking for the GROUND position
        // STACK_N is a stack containing N cones
        // STACK_1 is for a single cone, and should be the default bottom position
        STACK_1(0), STACK_2(0), STACK_3(0), STACK_4(0), STACK_5(0),  // the lowest position that allows rotation
        CAN_ROTATE(0),  // junction heights
        LOW(0), MIDDLE(0), HIGH(0);

        companion object {
            val GROUND = STACK_2
            fun fromStackHeight(height: Int): Position {
                return valueOf("STACK_$height")
            }
        }
    }

    companion object {
        val liftExecutor: ExecutorService = Executors.newSingleThreadExecutor()
        val clawExecutor: ExecutorService = Executors.newSingleThreadExecutor()
        const val SCALE_FACTOR = 0.8
    }
}