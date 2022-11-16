package org.firstinspires.ftc.teamcode.pipelines;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class SignalPipeline extends OpenCvPipeline {
    Mat hsv = new Mat();

    private static Scalar rangeHSV(int[] hsv) {
        return new Scalar(
                ((int) hsv[0]) / 2 - 1,
                255 * (((int) hsv[1]) / 100),
                255 * (((int) hsv[2]) / 100)
        );
    }

    private void extract(Mat frame, Color color) {
        Core.inRange(frame, color.lower, color.upper, color.mask);
    }

    @Override
    public Mat processFrame(Mat frame) {
        Imgproc.cvtColor(frame, hsv, Imgproc.COLOR_RGB2HSV);

        for (Color c : Color.values()) extract(hsv, c);

        return Color.MAGENTA.mask;
    }

    private enum Color {
        CYAN(rangeHSV(new int[] {316, 20, 20}), rangeHSV(new int[] {356, 100, 100}), new Mat()),
        GREEN(rangeHSV(new int[] {316, 20, 20}), rangeHSV(new int[] {356, 100, 100}), new Mat()),
        MAGENTA(rangeHSV(new int[] {316, 20, 20}), rangeHSV(new int[] {356, 100, 100}), new Mat());

        private final Scalar lower;
        private final Scalar upper;
        private final Mat mask;

        // NOTE: the mask parameter breaks things if you create more than one of these pipelines
        Color(Scalar lower, Scalar upper, Mat mask) {
            this.lower = lower;
            this.upper = upper;
            this.mask = mask;
        }
    }
}