package org.firstinspires.ftc.teamcode.opmodes.tests;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.opmodes.AutonomousTemplate;

@Autonomous
public class StraightAuto extends AutonomousTemplate {

    private Trajectory trajectory;

    @Override
    public void initializeTrajectories() {
        trajectory = drive.trajectoryBuilder(new Pose2d(0, 0, 0))
                .forward(24)
                .splineTo(new Vector2d(48, 48), Math.PI)
                .build();
    }

    @Override
    public void main() {
        drive.followTrajectory(trajectory);
    }
}
