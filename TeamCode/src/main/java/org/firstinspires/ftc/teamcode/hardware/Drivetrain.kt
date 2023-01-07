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
    private val all: Array<DcMotor>

    init {
        val hardwareMap = opMode.hardwareMap
        all = arrayOf(
            hardwareMap.dcMotor["frontLeft"],
            hardwareMap.dcMotor["frontRight"],
            hardwareMap.dcMotor["backLeft"],
            hardwareMap.dcMotor["backRight"]
        )
        all.forEach {
            it.mode = DcMotor.RunMode.RUN_USING_ENCODER
            it.direction = DcMotorSimple.Direction.FORWARD
        }
    }

    override fun loop() {
        val x = -gamepad.left_stick_x.toDouble()
        val y = gamepad.left_stick_y.toDouble()
        val z = -gamepad.right_stick_x.toDouble()
        val total = abs(x) + abs(y) + abs(z)
        if (total == 0.0) { // prevent division by 0
            all.forEach { it.power = 0.0 }
            return
        }

        for (i in 0..3) all[i].power = run {
            println("i $i, x ${MULTIPLIERS[i][0] * x}, y ${MULTIPLIERS[i][1] * y}")
            (MULTIPLIERS[i][0] * x + MULTIPLIERS[i][1] * y + z) *
                    max(hypot(x, y), abs(z)) * SCALE_FACTOR / total // Adjust input to never exceed 1
        }
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
