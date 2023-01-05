package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.hardware.LiftInternals;
import org.firstinspires.ftc.teamcode.opmodes.CycleContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** The bridge between the Cycle system and the controller input. */
public class AsyncLift implements LiftSubsystem {
    private final OpMode opMode;
    @NotNull private final Gamepad gamepad;
    @Nullable private Cycle currentCycle = null;
    private boolean canSwitch = true;
    private final LiftInternals liftInternals;
    private LiftInternals.Position topPosition = LiftInternals.Position.HIGH;
    private int lastBottomPositionValue = 1;

    public AsyncLift(LiftInternals liftInternals, OpMode opMode) {
        this.opMode = opMode;
        this.gamepad = this.opMode.gamepad1;
        this.liftInternals = liftInternals;
    }

    @Override
    public void loop() {
        if (currentCycle == null) {
            // reset encoder
            if (gamepad.left_bumper && gamepad.right_bumper) liftInternals.resetEncoder();

            // set target
            if (gamepad.b) topPosition = LiftInternals.Position.HIGH;
            else if (gamepad.y) topPosition = LiftInternals.Position.MIDDLE;
            else if (gamepad.x) topPosition = LiftInternals.Position.LOW;

            int bottomPositionValue = gamepad.dpad_down ? Math.max(lastBottomPositionValue - 1, 1)
                    : gamepad.dpad_up ? Math.min(lastBottomPositionValue + 1, 5) : lastBottomPositionValue;
            canSwitch = false;
            LiftInternals.Position bottomPosition = LiftInternals.Position.fromStackHeight(lastBottomPositionValue);
            if (bottomPositionValue != lastBottomPositionValue) {
                liftInternals.goToPosition(bottomPosition, 1);
                System.out.println("going to position " + bottomPosition);
            }
            if (liftInternals.motor.isBusy()) {
                System.out.println("motor is busy; cycles not being created");
                return; // do not create cycles while lift is moving
            }
            canSwitch = true;

            // create cycle
            if (gamepad.a) {
                currentCycle = new Cycle((CycleContainer) opMode, liftInternals, topPosition, bottomPosition);
                System.out.println("A: start cycle");
                currentCycle.start();
                canSwitch = false;
            }

            lastBottomPositionValue = bottomPositionValue;
            return;
        }
        canSwitch = false;

        // cycle is already present
        if (currentCycle.stage == Cycle.Stage.COMPLETE) {
            currentCycle = null;
            return;
        }
        if (currentCycle.isBusy()) return;

        // stage cannot be WAITING, so must be BETWEEN
        if (gamepad.a) {
            System.out.println("A: finish cycle");
            currentCycle.finish();
        }
    }

    @Override
    public boolean canSwitch() { return canSwitch; }

    @Override
    public void prepareForSwitch() {
        liftInternals.motor.setPower(0);
        liftInternals.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
}
