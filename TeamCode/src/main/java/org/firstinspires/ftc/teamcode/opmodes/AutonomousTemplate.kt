package org.firstinspires.ftc.teamcode.opmodes

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.teamcode.Cycle
import org.firstinspires.ftc.teamcode.hardware.LiftInternals
import org.firstinspires.ftc.teamcode.pipelines.QuantizationPipeline
import org.firstinspires.ftc.teamcode.pipelines.QuantizationPipeline.Color
import org.firstinspires.ftc.teamcode.roadrunner.drive.SampleMecanumDrive
import org.firstinspires.ftc.teamcode.roadrunner.trajectorysequence.TrajectorySequenceBuilder
import org.openftc.easyopencv.OpenCvCamera.AsyncCameraOpenListener
import org.openftc.easyopencv.OpenCvCameraFactory
import org.openftc.easyopencv.OpenCvCameraRotation
import org.openftc.easyopencv.OpenCvWebcam
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// FIXME refactor this once more info on auto becomes available
abstract class AutonomousTemplate : LinearOpMode() {
    val cycleExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    protected lateinit var drive: SampleMecanumDrive
    protected lateinit var currentCycle: Cycle
    protected lateinit var pipeline: QuantizationPipeline
    private lateinit var liftInternals: LiftInternals
    protected lateinit var webcam: OpenCvWebcam
    protected lateinit var detectedColor: Color
    protected open val reversed = false

    open val startPose: Pose2d? = null
    open fun initializeTrajectories() {}
    open fun setup() {
        webcam = OpenCvCameraFactory.getInstance().createWebcam(
            hardwareMap[WebcamName::class.java, "Webcam 1"]
        )

        pipeline = QuantizationPipeline()
        webcam.setPipeline(pipeline)

        webcam.setMillisecondsPermissionTimeout(2500)
        webcam.openCameraDeviceAsync(object : AsyncCameraOpenListener {
            override fun onOpened() {
                webcam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT)
            }

            override fun onError(errorCode: Int) {
                telemetry.addData("Camera", "Could not open camera. Will park in Position 2.")
            }
        })

        telemetry.addData("Status", "Initialized")

        telemetry.addData("f", "x")
        drive = SampleMecanumDrive(hardwareMap)
        telemetry.addData("drive workie", "yes")

//        val colors = ArrayDeque(arrayOfNulls<Color>(6).asList())
//        while (run { telemetry.update(); opModeInInit() }) {
//            if (!pipeline.hasInit) {
//                telemetry.addLine("Pipeline not yet initialized: DO NOT PRESS START")
//                continue
//            }
//            colors += pipeline.current ?: continue
//            colors.removeFirst()
//
//            telemetry.addData("Detected color", pipeline.current)
//            telemetry.addData("Recent colors", colors)
//            telemetry.addLine()
//
//            telemetry.addData("FPS", String.format("%.2f", webcam.fps))
//            telemetry.addData("Total frame time ms", webcam.totalFrameTimeMs)
//            telemetry.addData("Pipeline time ms", webcam.pipelineTimeMs)
//            telemetry.addData("Overhead time ms", webcam.overheadTimeMs)
//            telemetry.addData("Theoretical max FPS", webcam.currentPipelineMaxFps)
//            telemetry.addLine()
//
////            telemetry.addData("Detected hue", pipeline.current.)
//        }
        // on start
//        val totals = IntArray(3)
//        colors.forEach { it?.run { totals[ordinal]++ } }
//        detectedColor = if (totals contentEquals IntArray(3)) Color.GREEN else Color.values()[totals.indexOf(totals.max())]
    }

    abstract fun main()
    override fun runOpMode() {
        telemetry.addData("setting up", "yes")
        setup()
        telemetry.addData("checking pos", "yes")
        if (startPose != null) drive.poseEstimate = startPose!!

        liftInternals = LiftInternals(this).apply { initAuto().get() }
        currentCycle = Cycle(
            this,
            cycleExecutor,
            liftInternals,
            LiftInternals.Position.HIGH,
            LiftInternals.Position.STACK_5
        )

        telemetry.addData("initing", "yes")
        initializeTrajectories()

        waitForStart()
        main()

        // close camera
        pipeline.releaseAll()
        webcam.stopStreaming()
    }

    protected fun negateIfReversed(a: Double) = if (reversed) -a else a
    protected fun reverseAngle(a: Double) = if (reversed) Math.PI - a else a
    protected fun reversedPose(x: Double = 0.0, y: Double = 0.0, heading: Double = 0.0) = Pose2d(negateIfReversed(x), y, reverseAngle(heading))
    protected fun reversedVector(x: Double = 0.0, y: Double = 0.0) = Vector2d(negateIfReversed(x), y)
    protected fun TrajectorySequenceBuilder.strafeLeftReversed(a: Double) = strafeLeft(negateIfReversed(a))
    protected fun TrajectorySequenceBuilder.strafeRightReversed(a: Double) = strafeRight(negateIfReversed(a))

    protected fun createCycle(
        topPosition: LiftInternals.Position,
        bottomPosition: LiftInternals.Position
    ): Cycle {
        return Cycle(this, cycleExecutor, liftInternals, topPosition, bottomPosition)
    }
}