package org.firstinspires.ftc.teamcode.opmodes

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.teamcode.hardware.Drivetrain
import org.firstinspires.ftc.teamcode.pipelines.QuantizationPipeline
import org.firstinspires.ftc.teamcode.pipelines.QuantizationPipeline.Color
import org.openftc.easyopencv.OpenCvCamera
import org.openftc.easyopencv.OpenCvCameraFactory
import org.openftc.easyopencv.OpenCvCameraRotation

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
//        super.setup()
    }
    override fun main() {
        // TODO if weighted drive power doesn't work refactor the Drivetrain class to use inputs other than controllers, OR create a dummy controller
        // strafe left or right one tile if necessary
        gamepad.left_stick_y = /*when (detectedColor) {
            Color.MAGENTA -> -1 // left
            Color.GREEN -> 0 // middle
            Color.CYAN -> 1 // right
        }*/-1 * POWER
        drivetrain.loop()
        sleep(1000) // TODO tune this

        gamepad.left_stick_y = 0f
        drivetrain.loop()
        @Suppress("ControlFlowWithEmptyBody")
        while (!gamepad1.a);

        // move forward one tile
        gamepad.left_stick_x = POWER
        drivetrain.loop()
        sleep(1000) // TODO tune this

        // turn robot off
        gamepad.left_stick_x = 0f
    }

    companion object {
        const val POWER = -0.5f // robot is backwards
    }
}

@Autonomous
class CameraTest : AutonomousTemplate() {
    override val startPose = Pose2d()

    override fun setup() {
        val startTime = System.currentTimeMillis()
        /*
         * Instantiate an OpenCvCamera object for the camera we'll be using.
         * In this sample, we're using a webcam. Note that you will need to
         * make sure you have added the webcam to your configuration file and
         * adjusted the name here to match what you named it in said config file.
         *
         * We pass it the view that we wish to use for camera monitor (on
         * the RC phone). If no camera monitor is desired, use the alternate
         * single-parameter constructor instead (commented out below)
         */
//        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
//        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);

        // OR...  Do Not Activate the Camera Monitor View
        webcam = OpenCvCameraFactory.getInstance().createWebcam(
            hardwareMap[WebcamName::class.java, "Webcam 1"]
        )

        /*
         * Specify the image processing pipeline we wish to invoke upon receipt
         * of a frame from the camera. Note that switching pipelines on-the-fly
         * (while a streaming session is in flight) *IS* supported.
         */
        pipeline = QuantizationPipeline()
        webcam.setPipeline(pipeline)

        /*
         * Open the connection to the camera device. New in v1.4.0 is the ability
         * to open the camera asynchronously, and this is now the recommended way
         * to do it. The benefits of opening async include faster init time, and
         * better behavior when pressing stop during init (i.e. less of a chance
         * of tripping the stuck watchdog)
         *
         * If you really want to open synchronously, the old method is still available.
         */
        // TODO what if the camera doesn't open by the time we press init? (test to see if we need to worry about this and potentially use synchronous as a fix)
        // TODO if the camera doesn't open, will the use of the signal pipeline crash anything?
        webcam.setMillisecondsPermissionTimeout(2500) // Timeout for obtaining permission is configurable. Set before opening.
        webcam.openCameraDeviceAsync(object : OpenCvCamera.AsyncCameraOpenListener {
            override fun onOpened() {
                /*
                 * Tell the webcam to start streaming images to us! Note that you must make sure
                 * the resolution you specify is supported by the camera. If it is not, an exception
                 * zarif will be thrown. // TODO throw zarif - Sean
                 *
                 * Keep in mind that the SDK's UVC driver (what OpenCvWebcam uses under the hood) only
                 * supports streaming from the webcam in the uncompressed YUV image format. This means
                 * that the maximum resolution you can stream at and still get up to 30FPS is 480p (640x480).
                 * Streaming at e.g. 720p will limit you to up to 10FPS and so on and so forth.
                 *
                 * Also, we specify the rotation that the webcam is used in. This is so that the image
                 * from the camera sensor can be rotated such that it is always displayed with the image upright.
                 * For a front facing camera, rotation is defined assuming the user is looking at the screen.
                 * For a rear facing camera or a webcam, rotation is defined assuming the camera is facing
                 * away from the user.
                 */
                webcam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT)
            }

            override fun onError(errorCode: Int) {
                /*
                 * This will be called if the camera could not be opened
                 */
                telemetry.addData("Camera", "Could not open camera. Will park in Position 2.")
            }
        })
        telemetry.addData("Status", "Initialized")
        while (opModeInInit()) { // TODO remove some of this stuff when no longer needed
            if (System.currentTimeMillis() - startTime in 3000..6000) {
                pipeline.snapshot()
                detectedColor = pipeline.detectedColor
                telemetry.addData("Detected color", detectedColor)
                telemetry.addData("FPS", String.format("%.2f", webcam.fps))
                telemetry.addData("Total frame time ms", webcam.totalFrameTimeMs)
                telemetry.addData("Pipeline time ms", webcam.pipelineTimeMs)
                telemetry.addData("Overhead time ms", webcam.overheadTimeMs)
                telemetry.addData("Theoretical max FPS", webcam.currentPipelineMaxFps)
                telemetry.update() // TODO what if camera no open.
            }
        }
    }

    override fun initializeTrajectories() {}

    override fun main() {}
}


/*
* 2023-01-06 18:51:22.851  2104-2238  System.out              com.qualcomm.ftcrobotcontroller      I  0 x -0.0 y -0.007077048998326063
2023-01-06 18:51:22.851  2104-2238  System.out              com.qualcomm.ftcrobotcontroller      I  1 x -0.0 y 0.007077048998326063
2023-01-06 18:51:22.851  2104-2238  System.out              com.qualcomm.ftcrobotcontroller      I  2 x 0.0 y -0.007077048998326063
2023-01-06 18:51:22.851  2104-2238  System.out              com.qualcomm.ftcrobotcontroller      I  3 x 0.0 y 0.007077048998326063

* */