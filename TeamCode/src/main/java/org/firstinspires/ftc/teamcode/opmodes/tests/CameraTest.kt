package org.firstinspires.ftc.teamcode.opmodes.tests

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import org.firstinspires.ftc.teamcode.opmodes.AutonomousTemplate

@Autonomous
@Disabled
class CameraTest : AutonomousTemplate() {
    override fun main() {
        telemetry.addData("detected color", detectedColor)
        telemetry.update()
    }
}