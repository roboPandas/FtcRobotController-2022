package org.firstinspires.ftc.teamcode.opmodes.tests

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.recording.RecordingConstants
import java.io.File

@Autonomous(group = "tests")
class AutoInfoTest : LinearOpMode() {
    override fun runOpMode() {
        waitForStart()
        println(String(File(RecordingConstants.DEFAULT_OUTPUT_FILE_NAME).readBytes()))
        sleep(10000)
    }
}