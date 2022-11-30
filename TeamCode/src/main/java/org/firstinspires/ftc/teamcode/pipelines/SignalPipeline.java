package org.firstinspires.ftc.teamcode.pipelines;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SignalPipeline extends OpenCvPipeline {
    Mat hsv = new Mat();

    int detectedMagenta;
    int detectedGreen;
    int detectedCyan;
    Mat imgEdges = new Mat();
    List<MatOfPoint> imgContours = new ArrayList<MatOfPoint>();
    List<MatOfPoint> magentaContours;
    List<MatOfPoint> greenContours;
    List<MatOfPoint> cyanContours;

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

    private void removeNoise(Mat mask) {
        Mat structuringElement = Imgproc.getStructuringElement(1,  new Size(1, 1));
        Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_OPEN, structuringElement);
        Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_CLOSE, structuringElement);
        structuringElement.release();
    }

    private Mat largestContour(Mat img) {
        Mat hierarchy = new Mat();
        MatOfPoint maxContour;
        ArrayList<Double> contourAreas = new ArrayList<Double>();

        Imgproc.Canny(img, imgEdges, 100, 200);
        Imgproc.findContours(img, imgContours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        hierarchy.release();

        if (imgContours.size() == 0) return img;
        for (MatOfPoint contour : imgContours) contourAreas.add(Imgproc.contourArea(contour));
        maxContour = imgContours.get(contourAreas.indexOf(Collections.max(contourAreas)));
        Imgproc.drawContours(img, Collections.singletonList(maxContour), -1, new Scalar(0, 255, 0), 3);
        return img;
    }

    @Override
    public Mat processFrame(Mat frame) {
        Imgproc.GaussianBlur(frame, frame, new Size(0,0), 7, 7);
        Imgproc.cvtColor(frame, hsv, Imgproc.COLOR_RGB2HSV);

        for (Color c : Color.values()) extract(hsv, c);
        for (Color c : Color.values()) removeNoise(c.mask);

        detectedMagenta = Core.countNonZero(Color.MAGENTA.mask);
        detectedCyan = Core.countNonZero(Color.CYAN.mask);
        detectedGreen = Core.countNonZero(Color.GREEN.mask);

        return Color.CYAN.mask;
    }

    public int getDetectedMagenta() {
        return Core.countNonZero(Color.MAGENTA.mask);
    }

    public int getDetectedCyan() {
        return Core.countNonZero(Color.CYAN.mask);
    }

    public int getDetectedGreen() {
        return Core.countNonZero(Color.GREEN.mask);
    }

    public int getParkPosition() {
        int cyan = getDetectedCyan();
        int magenta = getDetectedMagenta();
        int green = getDetectedGreen();
        int max = Math.max(cyan, Math.max(magenta, green));
        // Priority goes in number order (Position 1, 2, 3)
        if (max == magenta) return Color.MAGENTA.position;
        if (max == green) return Color.GREEN.position;
        return Color.CYAN.position;
    }

    private enum Color {
        MAGENTA(rangeHSV(new int[] { 306, 50, 50 }), rangeHSV(new int[] { 326, 100, 100 }), new Mat(), -24),
        GREEN(rangeHSV(new int[] { 90, 50, 50 }), rangeHSV(new int[] { 150, 100, 100 }), new Mat(), 0),
        CYAN(rangeHSV(new int[] { 180, 75, 50 }), rangeHSV(new int[] { 220, 100, 100 }), new Mat(), 24);

        private final Scalar lower;
        private final Scalar upper;
        private final Mat mask;
        private final int position;

        // NOTE: the mask parameter breaks things if you create more than one of these pipelines
        Color(Scalar lower, Scalar upper, Mat mask, int position) {
            this.lower = lower;
            this.upper = upper;
            this.mask = mask;
            this.position = position;
        }
    }
}