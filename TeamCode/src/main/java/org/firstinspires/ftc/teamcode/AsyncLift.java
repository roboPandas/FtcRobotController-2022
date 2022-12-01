package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.hardware.LiftInternals;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** The bridge between the Cycle system and the controller input. */
public class AsyncLift implements LiftSubsystem {
    @NotNull private final Gamepad gamepad;
    @Nullable private Cycle currentCycle = null;
    private boolean canSwitch = true;
    private final LiftInternals liftInternals;
    private LiftInternals.Position topPosition = LiftInternals.Position.HIGH;
    private LiftInternals.Position bottomPosition = LiftInternals.Position.GROUND;

    public AsyncLift(LiftInternals liftInternals, @NotNull Gamepad gamepad) {
        this.gamepad = gamepad;
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
            else if (gamepad.a) topPosition = LiftInternals.Position.GROUND;

            if (gamepad.dpad_down) bottomPosition = LiftInternals.Position.GROUND;
            if (gamepad.dpad_up) bottomPosition = LiftInternals.Position.STACK;
            canSwitch = false;
            liftInternals.goToPosition(bottomPosition, 1);
            if (Math.abs(liftInternals.motor.getCurrentPosition() - bottomPosition.value) > 20) return; // do not create cycles while lift is moving
            canSwitch = true;

            // create cycle
            if (gamepad.right_bumper) {
                currentCycle = new Cycle(liftInternals, topPosition, bottomPosition);
                currentCycle.start();
            }

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
        if (gamepad.a) currentCycle.finish();
    }

    @Override
    public boolean canSwitch() { return canSwitch; }

    @Override
    public void prepareForSwitch() {
        liftInternals.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
}