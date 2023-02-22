//package org.firstinspires.ftc.teamcode.opmodes
//
//import com.acmerobotics.roadrunner.geometry.Pose2d
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous
//import org.firstinspires.ftc.teamcode.hardware.Drivetrain
//import org.firstinspires.ftc.teamcode.pipelines.QuantizationPipeline.Color
//
///**
// * A backup OpMode that only detects a color from the camera and parks.
// * 20 points
// * This is only meant for extenuating circumstances where a better OpMode is not available.
// */
//@Autonomous
//class Despair : AutonomousTemplate() {
//    lateinit var drivetrain: Drivetrain
//
//    override val startPose = Pose2d() // dummy
//    override fun initializeTrajectories() {}
//    override fun setup() {
//        drivetrain = Drivetrain(this)
//        super.setup()
//    }
//
//    override fun main() {
//        move(y = -POWER / 2)
//        sleep(400)
//
//        drivetrain.stop()
//
//        // strafe left or right one tile if necessary
//        move(x = when (detectedColor) {
//            Color.MAGENTA -> 1 // left
//            Color.GREEN -> 0 // middle
//            Color.CYAN -> -1 // right
//        } * POWER)
//        sleep(2000)
//
//        drivetrain.stop()
//
//        // move forward one tile
//        move(y = -POWER)
//        sleep(1800)
//
//        // turn robot off
//        drivetrain.stop()
//    }
//
//    @Suppress("NAME_SHADOWING")
//    private fun move(x: Float = 0f, y: Float = 0f, z: Float = 0f) =
//        drivetrain.move(x, y, z, Drivetrain.LINEAR_SCALING)
//
//    companion object {
//        const val POWER = 0.4f // +y is forward on the controller
//    }
//}
