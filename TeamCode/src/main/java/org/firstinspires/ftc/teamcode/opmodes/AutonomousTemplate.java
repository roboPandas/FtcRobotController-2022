package org.firstinspires.ftc.teamcode.opmodes;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.Cycle;
import org.firstinspires.ftc.teamcode.hardware.LiftInternals;
import org.firstinspires.ftc.teamcode.pipelines.SignalPipeline;
import org.firstinspires.ftc.teamcode.roadrunner.drive.SampleMecanumDrive;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

// FIXME refactor this once more info on auto becomes available
public abstract class AutonomousTemplate extends LinearOpMode {
    protected SampleMecanumDrive drive;
    protected Cycle currentCycle;
    private LiftInternals liftInternals;
    private OpenCvWebcam webcam;

    protected int parkPosition = 0;

    protected boolean reversed() {
        return false;
    }

    public abstract Pose2d startPose();

    public abstract void initializeTrajectories();

    public void setup() {
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
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"));

        /*
         * Specify the image processing pipeline we wish to invoke upon receipt
         * of a frame from the camera. Note that switching pipelines on-the-fly
         * (while a streaming session is in flight) *IS* supported.
         */
        SignalPipeline pipeline = new SignalPipeline();
        webcam.setPipeline(pipeline);

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
        webcam.setMillisecondsPermissionTimeout(2500); // Timeout for obtaining permission is configurable. Set before opening.
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
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
                webcam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {
                /*
                 * This will be called if the camera could not be opened
                 */
                telemetry.addData("Camera", "Could not open camera. Will park in Position 2.");
            }
        });

        liftInternals.grab();

        telemetry.addData("Status", "Initialized");

        while (opModeInInit()) { // TODO remove some of this stuff when no longer needed
            telemetry.addData("Frame Count", webcam.getFrameCount());
            telemetry.addData("FPS", String.format("%.2f", webcam.getFps()));
            telemetry.addData("Total frame time ms", webcam.getTotalFrameTimeMs());
            telemetry.addData("Pipeline time ms", webcam.getPipelineTimeMs());
            telemetry.addData("Overhead time ms", webcam.getOverheadTimeMs());
            telemetry.addData("Theoretical max FPS", webcam.getCurrentPipelineMaxFps());

            telemetry.addData("MAGENTA DETECTION", pipeline.getDetectedMagenta());
            telemetry.addData("GREEN DETECTION", pipeline.getDetectedGreen());
            telemetry.addData("CYAN DETECTION", pipeline.getDetectedCyan());
            telemetry.update();
            parkPosition = pipeline.getParkPosition(); // TODO what if camera no open.
        }
    }

    public abstract void main();

    @Override
    public void runOpMode() {
        drive = new SampleMecanumDrive(hardwareMap);
        liftInternals = new LiftInternals(hardwareMap);
        currentCycle = new Cycle(liftInternals, LiftInternals.Position.HIGH, LiftInternals.Position.STACK_5);

        drive.setPoseEstimate(startPose());

        // TODO do we need separate setup and initialize trajectories functions?
        setup();
        initializeTrajectories();
        waitForStart();
        main();
    }

    protected double negateIfReversed(double a) { return reversed() ? -a : a; }
    protected Cycle createCycle(LiftInternals.Position topPosition, LiftInternals.Position bottomPosition) { return new Cycle(liftInternals, topPosition, bottomPosition); }
}
