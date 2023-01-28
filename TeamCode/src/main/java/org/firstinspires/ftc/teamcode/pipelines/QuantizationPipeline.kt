package org.firstinspires.ftc.teamcode.pipelines

import org.openftc.easyopencv.OpenCvPipeline
import androidx.core.graphics.ColorUtils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.lang.Exception
import java.util.*

class QuantizationPipeline : OpenCvPipeline() {
    var hasInit = false
    private var data = Mat()
    private val centers = Mat()
    private var draw = Mat()
    private val colors = Mat()
    private val bestLabels = Mat()
    private var roi = Mat()
    private var ROI = Mat()

    private var _current: Color? = null
    private var hasNewData = false
    val current: Color? get() = if (hasNewData) {
        hasNewData = false
        _current
    } else null

    fun releaseAll() {
        data.release()
        centers.release()
        draw.release()
        colors.release()
        bestLabels.release()
        ROI.release()
        roi.release()
    }

    private fun snapshot(): ArrayList<Scalar>? {
        return if (!hasInit) null else try {
            data.convertTo(data, CvType.CV_32F)
            val criteria = TermCriteria(TermCriteria.EPS + TermCriteria.MAX_ITER, 2, 1.0)
            Core.kmeans(data, K, bestLabels, criteria, 15, Core.KMEANS_PP_CENTERS, centers)
            centers.convertTo(centers, CvType.CV_8U)
            val colors = ArrayList<Scalar>()
            for (k in 0 until K) {
                colors.add(Scalar(centers[k, 0][0], centers[k, 1][0], centers[k, 2][0]))
            }
            draw = Mat(roi.total().toInt(), 1, CvType.CV_32FC3)
            for (i in 0 until K) {
                val mask = Mat()
                Core.compare(bestLabels, Scalar(i.toDouble()), mask, Core.CMP_EQ)
                draw.setTo(colors[i], mask)
                mask.release()
            }
            val counts = mutableListOf(0, 0, 0)
            for (i in 0 until K) {
                val hsv = floatArrayOf(0f, 0f, 0f)
                val color = colors[i]
                ColorUtils.RGBToHSL(
                    color.`val`[0].toInt(),
                    color.`val`[1].toInt(),
                    color.`val`[2].toInt(),
                    hsv
                )
//                telemetry.addData("color" + i, String.valueOf(Arrays.asList(hsv[0], hsv[1], hsv[2])));
                val temp = Mat()

                // TODO: UPPER CLAMP FOR HSV[2]?
                for (c in Color.values()) {
                    hsv[1] = if (hsv[1] * 100 >= 15) 100f else 0f
                    hsv[2] = if (hsv[2] * 100 >= 15) 100f else 0f
                    if (colorInRange(
                            Scalar(
                                hsv[0].toDouble(), hsv[1].toDouble(), hsv[2].toDouble()
                            ), c.lower, c.upper
                        )
                    ) {
                        Core.inRange(draw, color, color, temp)
                        val ord = c.ordinal
                        counts[ord] = counts[ord] + Core.countNonZero(temp)
                    }
                }
                temp.release()
            }
            draw.release()
            val idx: Int
            val color = doubleArrayOf(0.0, 0.0, 0.0)
            if (Collections.frequency(counts, 0) != 3) {
                idx = counts.indexOf(Collections.max(counts))
                _current = Color.values()[idx]
                color[idx] = 255.0
            } else {
                _current = null
            }
            colors.add(Scalar(color))
            hasNewData = true
            colors
        } catch (e: Exception) {
            null
        }
    }


    private fun focus(width: Double, height: Double): Array<Point> {
        val off = height * FOCUS_OFFSET
        return arrayOf(
            Point(width * FOCUS_WIDTH, height * FOCUS_HEIGHT + off),
            Point(width - width * FOCUS_WIDTH, height - height * FOCUS_HEIGHT + off)
        )
    }

    override fun processFrame(input: Mat): Mat {
        hasInit = true
        val size = input.size()
        val height = size.height
        val width = size.width
        val foci = focus(width, height)
        ROI = input.colRange(foci[0].x.toInt(), foci[1].x.toInt())
        ROI = ROI.rowRange(foci[0].y.toInt(), foci[1].y.toInt())
        roi = input.clone()
        ROI.copyTo(roi)
        data = roi.reshape(1, roi.total().toInt())
        val snapshot = snapshot() ?: return input
        for (i in 0 until K) {
            val result = snapshot[i]
            Imgproc.rectangle(
                input, Point((30 * i).toDouble(), 0.0), Point(
                    (30 * (i + 1)).toDouble(), 30.0
                ), result, -1
            )
        }
        Imgproc.rectangle(input, Point(0.0, 30.0), Point(30.0, 60.0), snapshot[snapshot.size - 1], -1)
        Imgproc.rectangle(input, foci[0], foci[1], Scalar(0.0, 0.0, 0.0), 2)
        releaseAll()
        return input
    }

    // the worst "refactor" ever
    private fun colorInRange(color: Scalar, lowerBound: Scalar, upperBound: Scalar) =
        (0..2).all { color.`val`[it] in lowerBound.`val`[it]..upperBound.`val`[it] }

    enum class Color(val lower: Scalar, val upper: Scalar) {
        MAGENTA(Scalar(300.0, 100.0, 100.0), Scalar(350.0, 100.0, 100.0)),
        GREEN(Scalar(90.0, 100.0, 100.0), Scalar(160.0, 100.0, 100.0)),
        CYAN(Scalar(180.0, 100.0, 100.0), Scalar(210.0, 100.0, 100.0));
    }

    companion object {
        const val K = 4
        const val FOCUS_HEIGHT = 0.3
        const val FOCUS_WIDTH = 0.3
        const val FOCUS_OFFSET = 0.0
    }
}