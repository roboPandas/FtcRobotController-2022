package org.firstinspires.ftc.teamcode.opmodes.tests;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.roadrunner.drive.SampleMecanumDrive;

@Autonomous(group = "drive")
public class SplineFirstTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap, telemetry);
        drive.setPoseEstimate(new Pose2d(0, 0, Math.PI / 2));
        waitForStart();

        drive.followTrajectorySequence(
                drive.trajectorySequenceBuilder(new Pose2d(0, 0, Math.PI / 2))
                        .splineToSplineHeading(new Pose2d(-48, 48, Math.PI), Math.PI)
                        .waitSeconds(4)
                        .splineToSplineHeading(new Pose2d(-72, 48, Math.PI), Math.PI)
                        .build()
        );
    }
}
