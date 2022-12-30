package org.firstinspires.ftc.teamcode.opmodes

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.hardware.LiftInternals
import org.openftc.easyopencv.OpenCvWebcam
import org.openftc.easyopencv.OpenCvCameraFactory
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.teamcode.Cycle
import org.firstinspires.ftc.teamcode.pipelines.SignalPipeline
import org.firstinspires.ftc.teamcode.roadrunner.drive.SampleMecanumDrive
import org.openftc.easyopencv.OpenCvCamera.AsyncCameraOpenListener
import org.openftc.easyopencv.OpenCvCameraRotation

// FIXME refactor this once more info on auto becomes available
@Suppress("NOTHING_TO_INLINE")
abstract class AutonomousTemplate : LinearOpMode() {
    protected lateinit var drive: SampleMecanumDrive
    protected lateinit var currentCycle: Cycle
    protected lateinit var liftInternals: LiftInternals
    private lateinit var webcam: OpenCvWebcam

    protected var parkPosition = 0
    protected open val reversed = false

    abstract val startPose: Pose2d
    abstract fun initializeTrajectories()

    open fun setup() {
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap["Webcam 1"] as WebcamName)

        val pipeline = SignalPipeline()
        webcam.setPipeline(pipeline)

        // TODO what if the camera doesn't open by the time we press init? (test to see if we need to worry about this and potentially use synchronous as a fix)
        // TODO if the camera doesn't open, will the use of the signal pipeline crash anything?

        webcam.setMillisecondsPermissionTimeout(2500) // Timeout for obtaining permission is configurable. Set before opening.
        webcam.openCameraDeviceAsync(object : AsyncCameraOpenListener {
            override fun onOpened() = webcam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT)

            override fun onError(errorCode: Int) {
                telemetry.addData("Camera", "Could not open camera. Will park in Position 2.")
            }
        })

        liftInternals.grab()
        telemetry.addData("Status", "Initialized")

        while (opModeInInit()) { // TODO remove some of this stuff when no longer needed
            telemetry.addData("Frame Count", webcam.frameCount)
            telemetry.addData("FPS", String.format("%.2f", webcam.fps))
            telemetry.addData("Total frame time ms", webcam.totalFrameTimeMs)
            telemetry.addData("Pipeline time ms", webcam.pipelineTimeMs)
            telemetry.addData("Overhead time ms", webcam.overheadTimeMs)
            telemetry.addData("Theoretical max FPS", webcam.currentPipelineMaxFps)
            telemetry.addData("MAGENTA DETECTION", pipeline.detectedMagenta)
            telemetry.addData("GREEN DETECTION", pipeline.detectedGreen)
            telemetry.addData("CYAN DETECTION", pipeline.detectedCyan)
            telemetry.update()
            parkPosition = pipeline.parkPosition // TODO what if camera ngetOverheadTimeMso open.
        }
    }

    abstract fun main()

    override fun runOpMode() {
        drive = SampleMecanumDrive(hardwareMap)
        liftInternals = LiftInternals(hardwareMap)
        currentCycle = Cycle(liftInternals, LiftInternals.Position.HIGH, LiftInternals.Position.STACK_5)
        drive.poseEstimate = startPose

        // TODO do we need separate setup and initialize trajectories functions?
        setup()
        initializeTrajectories()
        waitForStart()
        main()

        // close camera
        webcam.stopStreaming()
        // TODO depending on the pipeline that we use, we may need to call a close method for it here.
    }

    protected inline fun negateIfReversed(a: Double) = if (reversed) -a else a

    protected inline fun createCycle(topPosition: LiftInternals.Position, bottomPosition: LiftInternals.Position)
        = Cycle(liftInternals, topPosition, bottomPosition)
}