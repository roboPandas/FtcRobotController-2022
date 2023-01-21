package org.firstinspires.ftc.teamcode.hardware

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.Subsystem
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Gamepad
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.pow

class Drivetrain(private val opMode: OpMode, private val gamepad: Gamepad = opMode.gamepad1) : Subsystem {
    private val multiplierMap: Map<DcMotor, MotorMultipliers>

    init {
        val hardwareMap = opMode.hardwareMap
        multiplierMap = hashMapOf(
            hardwareMap.dcMotor["frontLeft"] to MotorMultipliers(+1, +1),
            hardwareMap.dcMotor["frontRight"] to MotorMultipliers(+1, -1),
            hardwareMap.dcMotor["backLeft"] to MotorMultipliers(-1, +1),
            hardwareMap.dcMotor["backRight"] to MotorMultipliers(-1, -1)
        ) // z doesn't need a multiplier since everything is +1

        multiplierMap.keys.forEach {
            it.mode = DcMotor.RunMode.RUN_USING_ENCODER
            it.direction = DcMotorSimple.Direction.FORWARD
        }
    }

    override fun loop() {
        if (gamepad.dpad_right) return multiplierMap.forEach { (motor, mults) -> motor.power = 0.5 * mults.y * SCALE_FACTOR }
        if (gamepad.dpad_left) return multiplierMap.forEach { (motor, mults) -> motor.power = -0.5 * mults.y * SCALE_FACTOR }

        val x = gamepad.left_stick_x
        val y = -gamepad.left_stick_y // for some atrocious reason, down is positive
        val z = gamepad.right_stick_x

        val total = abs(x) + abs(y) + abs(z)

        if (total == 0f) { // prevent division by 0
            multiplierMap.keys.forEach { it.power = 0.0 }
            return
        }

        multiplierMap.forEach { (motor, mults) ->
            val basePower = max(0.2 , max(hypot(x, y), abs(z)).pow(2f).toDouble())
            val power = (mults.x * x + mults.y * y + z) * basePower * SCALE_FACTOR / total

            motor.power = power // Adjust input to never exceed 1
        }
    }

    private data class MotorMultipliers(val x: Int, val y: Int)

    companion object {
        private const val SCALE_FACTOR = 0.9
    }
}
