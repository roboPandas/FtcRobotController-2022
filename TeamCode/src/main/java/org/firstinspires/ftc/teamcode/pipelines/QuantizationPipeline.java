package org.firstinspires.ftc.teamcode.pipelines;

import static org.opencv.core.CvType.CV_32F;
import static org.opencv.core.CvType.CV_8U;

import androidx.core.graphics.ColorUtils;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QuantizationPipeline extends OpenCvPipeline {
    public final int K = 4;
    public boolean hasInit = false;
    public double FOCUS_HEIGHT = 0.3;
    public double FOCUS_WIDTH = 0.3;
    public double FOCUS_OFFSET = 0.0;
    private Mat data = new Mat();
    private Mat centers = new Mat();
    private Mat draw = new Mat();
    private Mat colors = new Mat();
    private Mat bestLabels = new Mat();
    private Mat roi = new Mat();
    private Mat ROI = new Mat();
    public List<Integer> totals = Arrays.asList(0, 0, 0);
    public Color current = Color.GREEN;

    public void releaseAll() {
        data.release();
        centers.release();
        draw.release();
        colors.release();
        bestLabels.release();
        ROI.release();
        roi.release();
    }

    public ArrayList<Scalar> snapshot() {
        if (!hasInit) return null;

        try {
            data.convertTo(data, CV_32F);
            TermCriteria criteria = new TermCriteria(TermCriteria.EPS + TermCriteria.MAX_ITER, 2, 1.0);
            Core.kmeans(data, K, bestLabels, criteria, 15, Core.KMEANS_PP_CENTERS, centers);

            centers.convertTo(centers, CV_8U);

            ArrayList<Scalar> colors = new ArrayList<>();
            for (int k = 0; k < K; k++) {
                colors.add(new Scalar(centers.get(k, 0)[0], centers.get(k, 1)[0], centers.get(k, 2)[0]));
            }

            draw = new Mat((int) roi.total(), 1, CvType.CV_32FC3);
            for (int i = 0; i < K; i++) {
                Mat mask = new Mat();
                Core.compare(bestLabels, new Scalar(i), mask, Core.CMP_EQ);
                draw.setTo(colors.get(i), mask);
                mask.release();
            }

            List<Integer> counts = Arrays.asList(0, 0, 0);
            for (int i = 0; i < K; i++) {
                float[] hsv = {0, 0, 0};
                Scalar color = colors.get(i);
                ColorUtils.RGBToHSL((int) color.val[0], (int) color.val[1], (int) color.val[2], hsv);
//            telemetry.addData("color" + i, String.valueOf(Arrays.asList(hsv[0], hsv[1], hsv[2])));
                Mat temp = new Mat();

                // TODO: UPPER CLAMP FOR HSV[2]?
                for (Color c : Color.values()) {
                    hsv[1] = (hsv[1] * 100 >= 15) ? 100 : 0;
                    hsv[2] = (hsv[2] * 100 >= 15) ? 100 : 0;
                    if (colorInRange(new Scalar(hsv[0], hsv[1], hsv[2]), c.lower, c.upper)) {
                        Core.inRange(draw, color, color, temp);
                        int ord = c.ordinal();
                        counts.set(ord, counts.get(ord) + Core.countNonZero(temp));
                    }
                }

                temp.release();
            }
            draw.release();

            int idx;
            double[] color = {0, 0, 0};
            if (Collections.frequency(counts, 0) != 3) {
                idx = counts.indexOf(Collections.max(counts));
                totals.set(idx, totals.get(idx) + 1);
                current = Color.values()[idx];
                color[idx] = 255;
            } else {
                current = null;
            }
            colors.add(new Scalar(color));

            return colors;
        } catch (Exception e) {
            return null;
        }
    }

    public Color getDetectedColor() {
        if (Collections.frequency(totals, 0) == 3)
            return Color.GREEN; // if no color is seen we just go straight
        return Color.values()[totals.indexOf(Collections.max(totals))];
    }

    private Point[] focus(double width, double height) {
        double off = height * FOCUS_OFFSET;
        return new Point[]{
                new Point(width * FOCUS_WIDTH, height * FOCUS_HEIGHT + off),
                new Point(width - (width * FOCUS_WIDTH), height - (height * FOCUS_HEIGHT) + off),
        };
    }

//    public static void RGBToHSL(int r, int g, int b, float[] outHsl) {
//        final float rf = r / 255f;
//        final float gf = g / 255f;
//        final float bf = b / 255f;
//
//        final float max = Math.max(rf, Math.max(gf, bf));
//        final float min = Math.min(rf, Math.min(gf, bf));
//        final float deltaMaxMin = max - min;
//
//        float h, s;
//        float l = (max + min) / 2f;
//
//        if (max == min) {
//            // Monochromatic
//            h = s = 0f;
//        } else {
//            if (max == rf) {
//                h = ((gf - bf) / deltaMaxMin) % 6f;
//            } else if (max == gf) {
//                h = ((bf - rf) / deltaMaxMin) + 2f;
//            } else {
//                h = ((rf - gf) / deltaMaxMin) + 4f;
//            }
//
//            s = deltaMaxMin / (1f - Math.abs(2f * l - 1f));
//        }
//
//        h = (h * 60f) % 360f;
//        if (h < 0) {
//            h += 360f;
//        }
//
//        outHsl[0] = constrain(h, 0f, 360f);
//        outHsl[1] = constrain(s, 0f, 1f);
//        outHsl[2] = constrain(l, 0f, 1f);
//    }
//
//    private static float constrain(float amount, float low, float high) {
//        return amount < low ? low : Math.min(amount, high);
//    }

    @Override
    public Mat processFrame(Mat input) {
        hasInit = true;
        Size size = input.size();
        double height = size.height;
        double width = size.width;

        Point[] foci = focus(width, height);

        ROI = input.colRange((int) foci[0].x, (int) foci[1].x);
        ROI = ROI.rowRange((int) foci[0].y, (int) foci[1].y);
        roi = input.clone();
        ROI.copyTo(roi);
        data = roi.reshape(1, (int) roi.total());

        ArrayList<Scalar> snapshot = snapshot();
        if (snapshot == null) return input;

        for (int i = 0; i < K; i++) {
            Scalar result = snapshot.get(i);
            float[] hsv = {0, 0, 0};
            Imgproc.rectangle(input, new Point(30 * i, 0), new Point(30 * (i + 1), 30), result, -1);
        }
        Imgproc.rectangle(input, new Point(0, 30), new Point(30, 60), snapshot.get(snapshot.size() - 1), -1);

        Imgproc.rectangle(input, foci[0], foci[1], new Scalar(0, 0, 0), 2);
        releaseAll();
        return input;
    }

    private boolean numberInRange(double x, double min, double max) {
        return (x - min) * (max - x) >= 0;
    }

    private boolean colorInRange(Scalar color, Scalar lowerBound, Scalar upperBound) {
        return (
                numberInRange(color.val[0], lowerBound.val[0], upperBound.val[0]) &&
                        numberInRange(color.val[1], lowerBound.val[1], upperBound.val[1]) &&
                        numberInRange(color.val[2], lowerBound.val[2], upperBound.val[2])
        );
    }

    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    private static Scalar rangeHSV(int[] hsv) {
        return new Scalar(
                hsv[0] / 2 - 1,
                255 * (hsv[1] / 100),
                255 * (hsv[2] / 100)
        );
    }

    public enum Color {
        MAGENTA(new Scalar(300, 100, 100), new Scalar(350, 100, 100)),
        GREEN(new Scalar(90, 100, 100), new Scalar(160, 100, 100)),
        CYAN(new Scalar(180, 100, 100), new Scalar(210, 100, 100));

        private final Scalar lower;
        private final Scalar upper;

        // NOTE: the mask parameter breaks things if you create more than one of these pipelines
        Color(Scalar lower, Scalar upper) {
            this.lower = lower;
            this.upper = upper;
        }
    }
}
