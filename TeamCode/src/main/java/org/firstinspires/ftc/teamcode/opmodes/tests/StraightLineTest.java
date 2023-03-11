package org.firstinspires.ftc.teamcode.opmodes.tests;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.roadrunner.drive.SampleMecanumDrive;

@Autonomous(group = "drive")
public class StraightLineTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap, telemetry);
        drive.setPoseEstimate(new Pose2d());
        waitForStart();

        drive.followTrajectorySequence(
                drive.trajectorySequenceBuilder(new Pose2d())
                        .splineToSplineHeading(new Pose2d(72, 0, 0), 0)
                        .build()
        );
    }
}
