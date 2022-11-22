package org.firstinspires.ftc.teamcode.opmodes;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Cycle;
import org.firstinspires.ftc.teamcode.hardware.LiftInternals;
import org.firstinspires.ftc.teamcode.roadrunner.drive.SampleMecanumDrive;

// FIXME refactor this once more info on auto becomes available
public abstract class AutonomousTemplate extends LinearOpMode {
    protected SampleMecanumDrive drive;
    protected Cycle currentCycle;
    private LiftInternals liftInternals;

    protected int parkPosition;

    protected boolean reversed() {
        return false;
    }

    public abstract Pose2d startPose();

    public abstract void initializeTrajectories();

    public void setup() {
        // TODO camera code in here - determine end coordinate
    }

    public abstract void main();

    @Override
    public void runOpMode() {
        drive = new SampleMecanumDrive(hardwareMap);
        liftInternals = new LiftInternals(hardwareMap);
        currentCycle = new Cycle(liftInternals, LiftInternals.Position.HIGH, LiftInternals.Position.STACK);

        drive.setPoseEstimate(startPose());

        // TODO do we need separate setup and initialize trajectories functions?
        setup();
        initializeTrajectories();
        waitForStart();
        main();
    }

    protected double negateIfReversed(double a) { return reversed() ? -a : a; }
}
