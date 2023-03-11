package org.firstinspires.ftc.teamcode.opmodes.tests;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.roadrunner.drive.SampleMecanumDrive;

@Autonomous(group = "drive")
public class CrossFieldSplineTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap, telemetry);
        double halfL = (17 + (3 / 8f)) / 2f;
        double halfW = 15.5 / 2;
        Pose2d start = new Pose2d(-72 + halfL, 72 - halfW, 0);
        drive.setPoseEstimate(start);
        waitForStart();

        drive.followTrajectorySequence(
                drive.trajectorySequenceBuilder(start)
                        .splineToSplineHeading(new Pose2d(72 - halfL, 72 - 48, 0), 0)
                        .build()
        );
    }
}
