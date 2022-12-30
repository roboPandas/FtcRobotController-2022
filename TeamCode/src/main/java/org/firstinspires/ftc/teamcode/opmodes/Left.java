package org.firstinspires.ftc.teamcode.opmodes;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.Cycle;
import org.firstinspires.ftc.teamcode.hardware.LiftInternals;
import org.firstinspires.ftc.teamcode.roadrunner.trajectorysequence.TrajectorySequence;

@Autonomous
public class Left extends AutonomousTemplate { // TODO uncomment actual cycle code AFTER this gets tested
    protected TrajectorySequence deliverPreload;
    protected TrajectorySequence intake;
    protected TrajectorySequence delivery;
    protected TrajectorySequence park;
    protected Trajectory finalMovement = null;

    protected final int ADDITIONAL_CYCLES = 2;

    @Override
    public Pose2d startPose() {
        return new Pose2d(negateIfReversed(-36), -65.5, Math.PI / 2);
    }

    @Override
    public void initializeTrajectories() {
        deliverPreload = drive.trajectorySequenceBuilder(startPose())
                .setReversed(true)
                .back(7)
                .splineTo(new Vector2d(negateIfReversed(-42), -34), reversed() ? 0 : Math.PI)
                .setReversed(false)
                .splineTo(new Vector2d(negateIfReversed(-36), -22), Math.PI / 2 + (reversed() ? 0.1 : -0.1))
                .splineTo(new Vector2d(negateIfReversed(-28.6), -4.6), reversed() ? 3 * Math.PI / 4 : Math.PI / 4)
                .build();
        intake = drive.trajectorySequenceBuilder(deliverPreload.end())
                .setReversed(true) // splines always start by moving forward, so we need to call backward forward but only because backwards isn't forwards, so we backward forward
                .splineTo(new Vector2d(negateIfReversed(-46), -12), reversed() ? 0 : Math.PI)
                .back(17.5)
                .build();
        delivery = drive.trajectorySequenceBuilder(intake.end())
                .setReversed(false)
                .forward(10)
                .splineTo(new Vector2d(negateIfReversed(-28.6), -4.6), reversed() ? 3 * Math.PI / 4 : Math.PI / 4)
                .build();
        park = drive.trajectorySequenceBuilder(delivery.end())
                .setReversed(true) // splines always start by moving forward, so we need to call backward forward but only because backwards isn't forwards, so we backward forward
                .splineToLinearHeading(new Pose2d(negateIfReversed(-36), -12, reversed() ? Math.PI : 0), -Math.PI / 2)
                .setReversed(false)
                .strafeRight(negateIfReversed(24))
                .build();
        if (parkPosition != 0) finalMovement = drive.trajectoryBuilder(deliverPreload.end())
                .forward(negateIfReversed(parkPosition))
                .build();
    }

    @Override
    public void main() {
        // preload is sadly handled separately so we don't wait to grab the cone // TODO is the time to grab the cone negligible?
        currentCycle.startPreload();
        drive.followTrajectorySequence(deliverPreload);
        currentCycle.await();
        currentCycle.finish();
        currentCycle.waitUntil(Cycle.Stage.DROPPED);

        for (int i = 0; i < ADDITIONAL_CYCLES; i++) {
            drive.followTrajectorySequence(intake);
            if (!currentCycle.await()) throw new RuntimeException("A cycle should have finished by now, but it did not.");
            currentCycle = createCycle(LiftInternals.Position.HIGH,
                    LiftInternals.Position.fromStackHeight(Math.max(4 - i, 1)));
            currentCycle.start();
            currentCycle.waitUntil(Cycle.Stage.GRABBED);
            drive.followTrajectorySequence(delivery);
            currentCycle.await();
            currentCycle.finish();
            currentCycle.waitUntil(Cycle.Stage.DROPPED);
        }

        drive.followTrajectorySequence(park);
        if (finalMovement != null) drive.followTrajectory(finalMovement);
    }
}
