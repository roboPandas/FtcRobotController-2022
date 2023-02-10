package org.firstinspires.ftc.teamcode.opmodes

import org.firstinspires.ftc.teamcode.hardware.Drivetrain
import kotlin.math.abs
import kotlin.math.sign

abstract class HardcodedAuto : AutonomousTemplate() {
    lateinit var drivetrain: Drivetrain

    override fun setup() {
        drivetrain = Drivetrain(this)
        super.setup()
    }

    // + is toward front or to the right
    @Suppress("NAME_SHADOWING")
    protected fun setDrivePower(x: Double = 0.0, y: Double = 0.0, z: Double = 0.0) =
        drivetrain.setPower(x, y, z, Drivetrain.LINEAR_SCALING)

    // z is in revolutions
    protected fun move(tiles: Double, movementType: MovementType) {
        val signedPower = POWER * sign(tiles)

        when (movementType) {
            MovementType.X -> setDrivePower(x = signedPower)
            MovementType.Y -> setDrivePower(y = signedPower)
            MovementType.Z -> setDrivePower(z = signedPower)
        }

        sleep((abs(tiles) * movementType.delay).toLong())

        drivetrain.stop()
    }

    protected enum class MovementType(val delay: Long) {
        X(SIDE_1_DELAY), Y(FORWARD_1_DELAY), Z(ROT_1_DELAY)
    }

    companion object {
        const val POWER = 0.4 / Drivetrain.SCALE_FACTOR // +y is forward on the controller
        const val FORWARD_1_DELAY = 1250L // TODO write in terms of POWER
        const val SIDE_1_DELAY = 1250L // TODO write in terms of POWER
        const val ROT_1_DELAY = 0L  // TODO write in terms of POWER
    }
}