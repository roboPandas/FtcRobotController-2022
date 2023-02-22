package org.firstinspires.ftc.teamcode.pipelines

import androidx.core.graphics.ColorUtils
import org.opencv.core.*
import java.lang.Exception
import java.util.*

class KMeans {
    private var data = Mat()
    private val centers = Mat()
    private var draw = Mat()
    private val colors = Mat()
    private val bestLabels = Mat()
    private var roi = Mat()
    private var roiCopy = Mat()

    private var _current: QuantizationPipeline.Color? = null
    private var hasNewData = false
    var hasInit = false

    val current: QuantizationPipeline.Color?
        get() = if (hasNewData) {
            hasNewData = false
            _current
        } else null

    fun releaseAll() {
        data.release()
        centers.release()
        draw.release()
        colors.release()
        bestLabels.release()
        roi.release()
        roiCopy.release()
    }

    private fun colorInRange(color: Scalar, lower: Scalar, upper: Scalar) =
        (0..2).all { color.`val`[it] in lower.`val`[it]..upper.`val`[it] }

    private fun snapshot(): ArrayList<Scalar>? {
        return if (!hasInit) null else try {
            data.convertTo(data, CvType.CV_32F)
            val criteria = TermCriteria(TermCriteria.EPS + TermCriteria.MAX_ITER, 2, 1.0)
            Core.kmeans(data,
                QuantizationPipeline.K, bestLabels, criteria, 15, Core.KMEANS_PP_CENTERS, centers)
            centers.convertTo(centers, CvType.CV_8U)

            val colors = ArrayList<Scalar>()
            for (k in 0 until QuantizationPipeline.K) {
                colors.add(Scalar(centers[k, 0][0], centers[k, 1][0], centers[k, 2][0]))
            }

            draw = Mat(roiCopy.total().toInt(), 1, CvType.CV_32FC3)

            for (i in 0 until QuantizationPipeline.K) {
                val mask = Mat()
                Core.compare(bestLabels, Scalar(i.toDouble()), mask, Core.CMP_EQ)
                draw.setTo(colors[i], mask)
                mask.release()
            }

            val counts = mutableListOf(0, 0, 0)

            for (i in 0 until QuantizationPipeline.K) {
                val hsv = floatArrayOf(0f, 0f, 0f)
                val color = colors[i]

                ColorUtils.RGBToHSL(
                    color.`val`[0].toInt(),
                    color.`val`[1].toInt(),
                    color.`val`[2].toInt(),
                    hsv
                )

                val temp = Mat()

                for (c in QuantizationPipeline.Color.values()) {
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
                _current = QuantizationPipeline.Color.values()[idx]
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
}