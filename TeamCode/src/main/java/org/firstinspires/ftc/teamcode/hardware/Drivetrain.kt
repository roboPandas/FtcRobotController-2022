package org.firstinspires.ftc.teamcode.hardware

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.Subsystem
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Gamepad
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.max

class Drivetrain(private val opMode: OpMode, private val gamepad: Gamepad = opMode.gamepad1) : Subsystem {
    private val multiplierMap: Map<DcMotor, IntArray>

    init {
        val hardwareMap = opMode.hardwareMap
        multiplierMap = mapOf(
            hardwareMap.dcMotor["frontLeft"] to intArrayOf(+1, +1),
            hardwareMap.dcMotor["frontRight"] to intArrayOf(+1, -1),
            hardwareMap.dcMotor["backLeft"] to intArrayOf(-1, +1),
            hardwareMap.dcMotor["backRight"] to intArrayOf(-1, -1)
        ) // z doesn't need a multiplier since everything is +1

        multiplierMap.keys.forEach {
            it.mode = DcMotor.RunMode.RUN_USING_ENCODER
            it.direction = DcMotorSimple.Direction.FORWARD
        }
    }

    override fun loop() {
        if (gamepad.dpad_right) return multiplierMap.forEach { (motor, mults) -> motor.power = 0.5 * mults[1] * SCALE_FACTOR }
        if (gamepad.dpad_left) return multiplierMap.forEach { (motor, mults) -> motor.power = -0.5 * mults[1] * SCALE_FACTOR }

        val x = gamepad.left_stick_x
        val y = -gamepad.left_stick_y // for some atrocious reason, down is positive
        val z = gamepad.right_stick_x

        val total = abs(x) + abs(y) + abs(z)

        if (total == 0f) { // prevent division by 0
            multiplierMap.keys.forEach { it.power = 0.0 }
            return
        }

        multiplierMap.forEach { (motor, mults) ->
           motor.power = (mults[0] * x + mults[1] * y + z) *
                    max(hypot(x, y), abs(z)) * SCALE_FACTOR / total // Adjust input to never exceed 1
        }
    }

    companion object {
        private const val SCALE_FACTOR = 0.8
    }
}
