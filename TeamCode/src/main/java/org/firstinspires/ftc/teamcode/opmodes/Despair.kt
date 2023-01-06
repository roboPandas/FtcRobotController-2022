package org.firstinspires.ftc.teamcode.opmodes

import com.acmerobotics.roadrunner.geometry.Pose2d
import org.firstinspires.ftc.teamcode.AutonomousTemplate
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.roadrunner.drive.SampleMecanumDrive
import org.firstinspires.ftc.teamcode.pipelines.QuantizationPipeline.Color

/**
 * A backup OpMode that only detects a color from the camera and parks.
 * 20 points
 * This is only meant for extenuating circumstances where a better OpMode is not available.
 */
@Autonomous
class Despair : AutonomousTemplate() {
    override val startPose = Pose2d() // dummy
    override fun initializeTrajectories() {}
    override fun setup() {
        drive.motors.forEach { it.setMode(DcMotor.RunMode.RUN_USING_ENCODER) } // just in case
        super.setup()
    }
    override fun main() {
        // TODO if weighted drive power doesn't work refactor the Drivetrain class to use inputs other than controllers, OR create a dummy controller
        val POWER = 0.5 // TODO tune this

        // strafe left or right one tile if necessary
        drive.weightedDrivePower = Pose2d(
                // TODO check which color goes to which position
                when(detectedColor) {
                    Color.MAGENTA -> -1
                    Color.GREEN -> 0
                    Color.CYAN -> 1
                } * POWER,
                0.0,
                0.0
        )
        sleep(1000) // TODO tune this

        // move forward one tile
        drive.weightedDrivePower = Pose2d(0.0, POWER, 0.0)
        sleep(1000) // TODO tune this

        // turn robot off
        drive.weightedDrivePower = Pose2d()
    }
}
