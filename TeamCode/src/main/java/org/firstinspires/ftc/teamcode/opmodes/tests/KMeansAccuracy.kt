package org.firstinspires.ftc.teamcode.opmodes.tests

import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.teamcode.delay
import org.firstinspires.ftc.teamcode.pipelines.QuantizationPipeline
import org.firstinspires.ftc.teamcode.pipelines.QuantizationPipeline.Color
import org.openftc.easyopencv.OpenCvCamera
import org.openftc.easyopencv.OpenCvCameraFactory
import org.openftc.easyopencv.OpenCvCameraRotation
import org.openftc.easyopencv.OpenCvWebcam

@TeleOp
@Disabled
class KMeansAccuracy : OpMode() {
    private lateinit var webcam: OpenCvWebcam
    private lateinit var pipeline: QuantizationPipeline
    private var detectedColor: Color = Color.MAGENTA

    private var angles: ArrayList<Int> = arrayListOf(0)
    private val subdivisions = 4

    private var success = 0
    private var fails = 0

    private val total
        get() = if (success + fails == 0) 1 else success + fails

    var index = 0

    override fun init() {
        val increment = 360 / subdivisions

        for (i in 0..subdivisions) {
            angles.add(increment * i)
        }

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
    }

    override fun loop() {
        if (pipeline.hasInit) {
            telemetry.addData("Detected color", pipeline.detectedColor)
            telemetry.addData("Current color", pipeline.current)
            telemetry.addData("totals", pipeline.totals)
        }

        if (pipeline.current == Color.MAGENTA) {
            success++
        } else {
            fails++
        }
        delay(500)

        when {
            gamepad1.a -> success++
            gamepad1.b -> fails++

            gamepad1.x -> {
                if (pipeline.hasInit) {
                    detectedColor = pipeline.detectedColor
                }
            }

            gamepad1.dpad_right -> index++
            gamepad1.dpad_left -> index--
        }

        telemetry.addData("accuracy", "${success / total * 100}%%")
        telemetry.addLine()
        telemetry.addData("success", success)
        telemetry.addData("fails", fails)
        telemetry.addData("total", total)
        telemetry.addLine()
        telemetry.addData("index", index)
        telemetry.addData("selected", angles[index])
        telemetry.addData("angles", angles)
        telemetry.addLine()
    }

}