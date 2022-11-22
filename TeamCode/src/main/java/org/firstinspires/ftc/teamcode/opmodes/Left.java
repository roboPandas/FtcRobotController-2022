package org.firstinspires.ftc.teamcode.opmodes;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.hardware.LiftInternals;
import org.firstinspires.ftc.teamcode.roadrunner.trajectorysequence.TrajectorySequence;

@Autonomous
public class Left extends AutonomousTemplate { // TODO uncomment actual cycle code AFTER this gets tested
    protected TrajectorySequence deliverPreload;
    protected TrajectorySequence cycle;
    protected TrajectorySequence park;
    protected Trajectory finalMovement = null;

    protected final int ADDITIONAL_CYCLES = 2;
    /** number of cycles to complete before switching from STACK to GROUND */
    protected final int STACK_THRESHOLD = 2;
    protected LiftInternals.Position bottomPosition = LiftInternals.Position.STACK;

    @Override
    public Pose2d startPose() {
        return new Pose2d(negateIfReversed(-36), -65.5, Math.PI / 2);
    }

    @Override
    public void initializeTrajectories() {
        // copied from meepmeep
        deliverPreload = drive.trajectorySequenceBuilder(startPose()) // TODO is this 72-based or 70-based?
                // FIXME if splines don't work use linear
                // Preload
                .forward(53.5) // 2 tiles + 5.5 inches to reach the center
                .turn(negateIfReversed(-Math.PI / 4))
                .forward(5) // takes us to (-32.46, -8.46)
                .build();
        cycle = drive.trajectorySequenceBuilder(deliverPreload.end())
                // Intake
                .setReversed(true) // splines always start by moving forward, so we need to call backward forward
                .splineToLinearHeading(new Pose2d(negateIfReversed(-46), -12, reversed() ? Math.PI : 0), reversed() ? 0 : Math.PI)
                .setReversed(false)
                .back(10)
                // Delivery
                .forward(15)
                .splineToLinearHeading(new Pose2d(negateIfReversed(-32.46), -8.46, reversed() ? 3 * Math.PI / 4 : Math.PI / 4), Math.PI / 2)
                .build();
        park = drive.trajectorySequenceBuilder(cycle.end())
                .splineToLinearHeading(new Pose2d(negateIfReversed(-36), -12, reversed() ? Math.PI : 0), -Math.PI / 2)
                .strafeRight(negateIfReversed(24))
                .build();
        if (parkPosition != 0) finalMovement = drive.trajectoryBuilder(deliverPreload.end())
                .forward(negateIfReversed(parkPosition))
                .build();
    }

    @Override
    public void main() {
        drive.followTrajectorySequence(deliverPreload);
        for (int i = 0; i < ADDITIONAL_CYCLES; i++) {
            if (i > STACK_THRESHOLD) bottomPosition = LiftInternals.Position.GROUND;
            drive.followTrajectorySequence(cycle);
        }
        drive.followTrajectorySequence(park);
        if (finalMovement != null) drive.followTrajectory(finalMovement);
    }
}
