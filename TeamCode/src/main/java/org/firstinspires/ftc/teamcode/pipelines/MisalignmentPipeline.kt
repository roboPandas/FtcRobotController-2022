package org.firstinspires.ftc.teamcode.pipelines

import org.firstinspires.ftc.robotcore.external.Telemetry
import org.opencv.core.*
import org.openftc.easyopencv.OpenCvPipeline
import org.opencv.imgproc.Imgproc
import java.util.ArrayList

class MisalignmentPipeline(var telemetry: Telemetry) : OpenCvPipeline() {
    private val upper = Scalar(20.0, 150.0, 100.0)
    private val lower = Scalar(40.0, 255.0, 255.0)
    private val res = Mat()
    private val mask = Mat()
    private val hierarchy = Mat()
    private val contours = ArrayList<MatOfPoint>()

    override fun processFrame(input: Mat): Mat {
        contours.clear()
        res.release()
        mask.release()
        hierarchy.release()

        Imgproc.cvtColor(input, res, Imgproc.COLOR_RGB2HSV)
        Core.inRange(res, upper, lower, mask)
        Imgproc.findContours(
            mask,
            contours,
            hierarchy,
            Imgproc.RETR_EXTERNAL,
            Imgproc.CHAIN_APPROX_SIMPLE
        )

        if (contours.size == 0) return input

        var maxVal = 0.0
        var contourIdx = 0
        for (i in contours.indices) {
            val cont = contours[i]
            val area = Imgproc.contourArea(cont)
            if (maxVal < area) {
                maxVal = area
                contourIdx = i
            }
        }

        try {
            Imgproc.drawContours(input, contours, contourIdx, Scalar(0.0, 255.0, 0.0))
        } catch (e: CvException) {
            return input
        }

        val boundingBox = Imgproc.boundingRect(contours[contourIdx])
        val centerX = boundingBox.x + boundingBox.width / 2
        val dist = -(input.width() / 2 - centerX)
        telemetry.addData("dist", dist)
        telemetry.update()

        Imgproc.line(
            input, Point((input.width() / 2).toDouble(), 0.0), Point(
                (input.width() / 2).toDouble(), input.height().toDouble()
            ), Scalar(0.0, 0.0, 255.0), 2
        )
        return input
    }
}