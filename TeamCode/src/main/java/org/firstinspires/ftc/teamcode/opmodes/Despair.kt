package org.firstinspires.ftc.teamcode.opmodes

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.teamcode.hardware.Drivetrain
import org.firstinspires.ftc.teamcode.pipelines.QuantizationPipeline.Color

/**
 * A backup OpMode that only detects a color from the camera and parks.
 * 20 points
 * This is only meant for extenuating circumstances where a better OpMode is not available.
 * basically gg queue next we screwed
 */
@Autonomous
class Despair : AutonomousTemplate() {
    lateinit var drivetrain: Drivetrain
    val gamepad = Gamepad()

    override val startPose = Pose2d() // dummy
    override fun initializeTrajectories() {}
    override fun setup() {
        drivetrain = Drivetrain(this, gamepad)
        super.setup()
    }

    override fun main() {
        gamepad.left_stick_y = POWER / 2
        drivetrain.loop()
        sleep(400)

        gamepad.left_stick_y = 0f
        drivetrain.loop()

        // strafe left or right one tile if necessary
        gamepad.left_stick_x = when (detectedColor) {
            Color.MAGENTA -> 1 // left
            Color.GREEN -> 0 // middle
            Color.CYAN -> -1 // right
        } * POWER
        drivetrain.loop()
        sleep(1800)

        gamepad.left_stick_x = 0f
        drivetrain.loop()

        // move forward one tile
        gamepad.left_stick_y = POWER
        drivetrain.loop()
        sleep(1800)

        // turn robot off
        gamepad.left_stick_y = 0f
        drivetrain.loop()
    }

    companion object {
        const val POWER = 0.5f // +y is forward on the controller
    }
}
