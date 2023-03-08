package org.firstinspires.ftc.teamcode.opmodes.tests

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.teamcode.opmodes.auto.AutonomousTemplate
import org.firstinspires.ftc.teamcode.pipelines.MisalignmentPipeline
import org.firstinspires.ftc.teamcode.roadrunner.drive.SampleMecanumDrive
import org.openftc.easyopencv.OpenCvCamera
import org.openftc.easyopencv.OpenCvCameraFactory
import org.openftc.easyopencv.OpenCvCameraRotation

@Autonomous
class CameraTest : AutonomousTemplate() {
    override fun main() {
//        telemetry.addData("detected color", detectedColor)
//        telemetry.update()
        drive = SampleMecanumDrive(hardwareMap)
        val start = initializeTrajectories()
        if (start != null) drive.poseEstimate = start

        webcam = OpenCvCameraFactory.getInstance().createWebcam(
            hardwareMap[WebcamName::class.java, "Webcam 1"]
        )

        val pipeline2 = MisalignmentPipeline()
        webcam.setPipeline(pipeline2)

        webcam.setMillisecondsPermissionTimeout(2500)
        webcam.openCameraDeviceAsync(object : OpenCvCamera.AsyncCameraOpenListener {
            override fun onOpened() {
                webcam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT)
            }

            override fun onError(errorCode: Int) {
                telemetry.addData("Camera", "Could not open camera. Will park in Position 2.")
            }
        })
    }
}