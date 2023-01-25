package org.firstinspires.ftc.teamcode.hardware

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot.LogoFacingDirection.LEFT
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot.UsbFacingDirection.UP
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.Subsystem
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.IMU
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit.AMPS
import org.firstinspires.ftc.teamcode.hardware.Drivetrain.CorrectionState.CORRECTED
import org.firstinspires.ftc.teamcode.hardware.Drivetrain.CorrectionState.CORRECTING
import kotlin.math.*
import org.firstinspires.ftc.teamcode.get

class Drivetrain(private val opMode: OpMode, private val gamepad: Gamepad = opMode.gamepad1) :
    Subsystem {
    private val imu: IMU

    private val multiplierMap: Map<DcMotorEx, MotorMultipliers>
    private val RADIANS_TO_DEGREES = 180 / Math.PI

    private val inputs: ArrayList<Reading> = arrayListOf(Reading(Point(0.0f, 0.0f, 0.0f), 0))
    private val lastInput: Reading
        get() = inputs[inputs.size - 1]

    var target = 0

    val predictedAngle
        get() = intended(gamepad.left_stick_x, gamepad.left_stick_y)

    val measurements: IMUMeasurements
        get() {
            val values = imu.robotYawPitchRollAngles

            val yaw = values.getYaw(AngleUnit.DEGREES).roundToInt()
            val pitch = values.getPitch(AngleUnit.DEGREES).roundToInt()
            val roll = values.getRoll(AngleUnit.DEGREES).roundToInt()

            return IMUMeasurements(pitch, roll, yaw)
        }

    init {
        val hardwareMap = opMode.hardwareMap

        imu = hardwareMap.get<IMU>("imu")
        imu.initialize(IMU.Parameters(RevHubOrientationOnRobot(LEFT, UP)))

        multiplierMap = hashMapOf(
            hardwareMap.get<DcMotorEx>("frontLeft") to MotorMultipliers(+1, +1),
            hardwareMap.get<DcMotorEx>("frontRight") to MotorMultipliers(+1, -1),
            hardwareMap.get<DcMotorEx>("backLeft") to MotorMultipliers(-1, +1),
            hardwareMap.get<DcMotorEx>("backRight") to MotorMultipliers(-1, -1)
        ) // z doesn't need a multiplier since everything is +1

        multiplierMap.keys.forEach {
            it.mode = DcMotor.RunMode.RUN_USING_ENCODER
            it.direction = DcMotorSimple.Direction.FORWARD
            it.zeroPowerBehavior = BRAKE
        }
    }

    override fun loop() {
        val reading = Reading(
            Point(gamepad.left_stick_x, -gamepad.left_stick_y, gamepad.right_stick_x),
            measurements.yaw
        )

        if (lastInput != reading) {
            inputs.add(reading)
        }

//        opMode.telemetry.addData("cryingn rn", inputs.map { Pair(it.point.z, it.heading) })

        if (gamepad.dpad_right) return multiplierMap.forEach { (motor, mults) ->
            motor.power = 0.5 * mults.y * SCALE_FACTOR
        }
        if (gamepad.dpad_left) return multiplierMap.forEach { (motor, mults) ->
            motor.power = -0.5 * mults.y * SCALE_FACTOR
        }
        if (gamepad.back) imu.resetYaw()

        val (x, y, z) = reading.point

        val total = abs(x) + abs(y) + abs(z)

        if (total == 0f) { // prevent division by 0
//            if (lastInput == Point(0.0f, 0.0f)) {
//                if (inputs.size > 1) {
//                    target(intended(inputs[inputs.size - 2]) - measurements.yaw)
//                }
//            }

            return when (correct()) {
                CORRECTED -> brake()
                CORRECTING -> {
                    opMode.telemetry.addLine("attempted to correct")
                    Unit
                }
            }
        }


        multiplierMap.forEach { (motor, mults) ->
            val basePower = max(0.2, max(hypot(x, y), abs(z)).pow(2f).toDouble())
            val power = (mults.x * x + mults.y * y + z) * basePower * SCALE_FACTOR / total

            motor.power = power // Adjust input to never exceed 1
        }
    }

    private fun brake() = multiplierMap.keys.forEach { it.power = 0.0 }

    private fun target(degrees: Int) {
        target = measurements.yaw + degrees
    }

    private fun intended(point: Point): Int {
        return intended(point.x, point.y)
    }

    private fun intended(x: Float, y: Float): Int {
        // Swap y and x and negate x for a -90 degree rotation (unit circle), negate Y because negative Y is North
        return (atan2(-x, -y) * RADIANS_TO_DEGREES).toInt()
    }

    private fun correct(): CorrectionState {
//        if (lastInput.point.z != 0.0f) {
//            imu.resetYaw()
//            return CORRECTED
//        }

        val error = (target - measurements.yaw)
        val correctionFactor = -error.sign * sqrt((abs(error.toDouble()) / 180.0))

        opMode.telemetry.addData("error", error)
        opMode.telemetry.addData("error degrees", error / 360)

        @Suppress("KotlinConstantConditions")
        return when (error.sign) {
            1, -1 -> {
                multiplierMap.keys.forEach { it.power = correctionFactor }
                CORRECTING
            }

            else -> CORRECTED
        }
    }

    fun currents(): List<Pair<MutableSet<String>, Double>> {
        return multiplierMap.keys.map {
            Pair(
                opMode.hardwareMap.getNamesOf(it),
                ((it.getCurrent(AMPS) * 10).roundToInt()) / 10.0
            )
        }
    }

    private data class MotorMultipliers(val x: Int, val y: Int)
    data class IMUMeasurements(val pitch: Int, val roll: Int, val yaw: Int)
    data class Point(val x: Float, val y: Float, val z: Float)
    data class Reading(val point: Point, val heading: Int)

    enum class CorrectionState {
        CORRECTING,
        CORRECTED
    }

    companion object {
        private const val SCALE_FACTOR = 0.7
    }
}
