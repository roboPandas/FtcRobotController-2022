package org.firstinspires.ftc.teamcode.hardware

import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.teamcode.Subsystem
import com.qualcomm.robotcore.hardware.DcMotor
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.max

class Drivetrain(hardwareMap: HardwareMap, private val gamepad: Gamepad) : Subsystem {
    private val all: Array<DcMotor>

    init {
        all = arrayOf(
                hardwareMap.dcMotor["frontLeft"],
                hardwareMap.dcMotor["frontRight"],
                hardwareMap.dcMotor["backLeft"],
                hardwareMap.dcMotor["backRight"]
        )
    }

    override suspend fun loop() {
        val x = -gamepad.left_stick_x.toDouble()
        val y = gamepad.left_stick_y.toDouble()
        val z = -gamepad.right_stick_x.toDouble()
        val total = abs(x) + abs(y) + abs(z)
        if (total == 0.0) { // prevent division by 0
            for (motor in all) motor.power = 0.0
            return
        }

        // Adjust input to never exceed 1
        val factor = max(hypot(x, y), abs(z)) * SCALE_FACTOR / total
        for (i in 0..3) all[i].power = (MULTIPLIERS[i][0] * x + MULTIPLIERS[i][1] * y + z) * factor
    }

    companion object {
        private const val SCALE_FACTOR = -0.8
        private val MULTIPLIERS = arrayOf(
                intArrayOf(+1, +1),
                intArrayOf(+1, -1),
                intArrayOf(-1, +1),
                intArrayOf(-1, -1)
        ) // z doesn't need a multiplier since everything is +1
    }
}