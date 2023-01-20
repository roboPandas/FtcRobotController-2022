package org.firstinspires.ftc.teamcode.opmodes.tests

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.opmodes.AutonomousTemplate

@Autonomous
class CameraTest : AutonomousTemplate() {
    override fun main() {
        telemetry.addData("detected color", detectedColor)
        telemetry.update()
    }
}