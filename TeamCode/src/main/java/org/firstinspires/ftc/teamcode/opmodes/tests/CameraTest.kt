import com.acmerobotics.roadrunner.geometry.Pose2d
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.teamcode.opmodes.AutonomousTemplate
import org.firstinspires.ftc.teamcode.pipelines.QuantizationPipeline
import org.openftc.easyopencv.OpenCvCamera
import org.openftc.easyopencv.OpenCvCameraFactory
import org.openftc.easyopencv.OpenCvCameraRotation

@Autonomous
@Disabled
class CameraTest : AutonomousTemplate() {
    override val startPose = Pose2d()

    override fun setup() {
        webcam = OpenCvCameraFactory.getInstance().createWebcam(
            hardwareMap[WebcamName::class.java, "Webcam 1"]
        )

        pipeline = QuantizationPipeline()
        webcam.setPipeline(pipeline)
        webcam.setMillisecondsPermissionTimeout(2500)
        webcam.openCameraDeviceAsync(object : OpenCvCamera.AsyncCameraOpenListener {
            override fun onOpened() {
                webcam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT)
            }

            override fun onError(errorCode: Int) {
                telemetry.addData("Camera", "Could not open camera. Will park in Position 2.")
            }
        })

        telemetry.addData("Status", "Initialized")

        while (opModeInInit()) {
            if (pipeline.hasInit) {
                val currentColor = pipeline.current
                detectedColor = pipeline.detectedColor
                telemetry.addData("Current color", currentColor)
                telemetry.addData("Detected color", detectedColor)
                telemetry.addData("totals", pipeline.totals)
                telemetry.addData("FPS", String.format("%.2f", webcam.fps))
                telemetry.addData("Total frame time ms", webcam.totalFrameTimeMs)
                telemetry.addData("Pipeline time ms", webcam.pipelineTimeMs)
                telemetry.addData("Overhead time ms", webcam.overheadTimeMs)
                telemetry.addData("Theoretical max FPS", webcam.currentPipelineMaxFps)
                telemetry.update()
            } else {
                telemetry.addLine("Pipeline not yet initialized: DO NOT PRESS START")
            }
        }
    }

    override fun initializeTrajectories() {}

    override fun main() {}
}
