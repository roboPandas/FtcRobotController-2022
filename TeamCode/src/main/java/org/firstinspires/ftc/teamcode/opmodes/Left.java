package org.firstinspires.ftc.teamcode.opmodes;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

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
    /**
     * number of cycles to complete before switching from STACK to GROUND
     */
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
                .forward(59.5) // 2 tiles + 5.5 inches to reach the center + 6 inches to push cone out of the way
                .back(6)
                .turn(negateIfReversed(-Math.PI / 4))
                .forward(10.47) // takes us to (-28.6, -4.6) - claw is at (-24, 0)
                .build();
        intake = drive.trajectorySequenceBuilder(deliverPreload.end())
                // Intake
                .setReversed(true) // splines always start by moving forward, so we need to call backward forward
                .splineToLinearHeading(new Pose2d(negateIfReversed(-46), -12, reversed() ? Math.PI : 0), reversed() ? 0 : Math.PI)
                .setReversed(false)
                .back(17.5)
                .build();
        delivery = drive.trajectorySequenceBuilder(intake.end())
                // Delivery
                .forward(22.5)
                .splineToLinearHeading(new Pose2d(negateIfReversed(-28.6), -4.6, reversed() ? 3 * Math.PI / 4 : Math.PI / 4), Math.PI / 2)
                .build();
        park = drive.trajectorySequenceBuilder(delivery.end())
                .setReversed(true) // splines always start by moving forward, so we need to call backward forward
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
            if (i > STACK_THRESHOLD) bottomPosition = LiftInternals.Position.GROUND;
            currentCycle = createCycle(LiftInternals.Position.HIGH, bottomPosition);
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