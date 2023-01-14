package org.firstinspires.ftc.teamcode;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.hardware.LiftInternals;
import org.firstinspires.ftc.teamcode.opmodes.CycleUsingOpMode;

/** The bridge between the Cycle system and the controller input. */
@RequiresApi(api = Build.VERSION_CODES.N)
public class AsyncLift implements LiftSubsystem {
    private final CycleUsingOpMode<?> opMode;
    private final Gamepad gamepad;
    private Cycle currentCycle = null;
    private boolean canSwitch = true;
    private final LiftInternals liftInternals;
    private LiftInternals.Position topPosition = LiftInternals.Position.HIGH;
    private LiftInternals.Position queuedTopPosition = null;
    private LiftInternals.Position bottomPosition = LiftInternals.Position.STACK_1;
    private LiftInternals.Position queuedBottomPosition = null;
    private DpadState lastDpadState = DpadState.NEUTRAL;

    public AsyncLift(LiftInternals liftInternals, CycleUsingOpMode<?> opMode) {
        this.opMode = opMode;
        this.gamepad = this.opMode.getSelf().gamepad1;
        this.liftInternals = liftInternals;
    }

    @Override
    public void loop() {
        if (currentCycle != null) {
            loopWithCycle();
        } else {
            loopWithoutCycle();
        }
    }

    private void loopWithoutCycle() {
        // reset encoder
        if (gamepad.left_bumper && gamepad.right_bumper) liftInternals.resetEncoder();

        // set target
        topPosition = getTopPosition(topPosition);
        LiftInternals.Position oldBottomPosition = this.bottomPosition;
        this.bottomPosition = getBottomPosition(oldBottomPosition);
        if (oldBottomPosition != bottomPosition) {
            liftInternals.goToPosition(this.bottomPosition, 1);
        }

        if (liftInternals.motor.isBusy()) {
            System.out.println("motor is busy; cycles not being created");
            return; // do not create cycles while lift is moving
        }
        canSwitch = true;

        // create cycle
        if (gamepad.a) {
            currentCycle = new Cycle(opMode, liftInternals, topPosition, this.bottomPosition);
            System.out.println("A: start cycle");
            currentCycle.start();
            canSwitch = false;
        }
    }

    private void loopWithCycle() {
        canSwitch = false;

        // cycle is already present

        queuedTopPosition = getTopPosition(queuedTopPosition != null ? queuedTopPosition : topPosition);
        queuedBottomPosition = getBottomPosition(queuedBottomPosition != null ? queuedBottomPosition : bottomPosition);

        // check if the current cycle is done and reset if so
        if (currentCycle.stage == Cycle.Stage.COMPLETE) {
            currentCycle = null;
            // when a cycle ends, we set our new targets from the queues
            if (queuedTopPosition != null) {
                topPosition = queuedTopPosition;
                queuedTopPosition = null;
            }
            if (queuedBottomPosition != null) {
                bottomPosition = queuedBottomPosition;
                queuedBottomPosition = null;
            }
            return;
        }
        if (currentCycle.isBusy()) return;

        // stage cannot be WAITING, so must be BETWEEN
        if (gamepad.a) {
            System.out.println("A: finish cycle");
            currentCycle.finish();
        }
    }

    private LiftInternals.Position getTopPosition(LiftInternals.Position current) {
        if (gamepad.b) return LiftInternals.Position.HIGH;
        else if (gamepad.y) return LiftInternals.Position.MIDDLE;
        else if (gamepad.x) return LiftInternals.Position.LOW;
        return current;
    }

    private LiftInternals.Position getBottomPosition(LiftInternals.Position current) {
        DpadState state = getDpadState();
        if (state == lastDpadState)
            return current;
        LiftInternals.Position newPos;
        switch (state) {
            case DOWN:
                newPos = LiftInternals.Position.getBelow(current);
                break;
            case UP:
                newPos = LiftInternals.Position.getAbove(current);
                break;
            default:
                newPos = current;
                break;
        }
        lastDpadState = state;
        canSwitch = false;
        return newPos;
    }

    @Override
    public boolean canSwitch() { return canSwitch; }

    @Override
    public void prepareForSwitch() {
        liftInternals.motor.setPower(0);
        liftInternals.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    private enum DpadState {
        DOWN, UP, NEUTRAL
    }

    private DpadState getDpadState() {
        return gamepad.dpad_down ? DpadState.DOWN : gamepad.dpad_up ? DpadState.UP : DpadState.NEUTRAL;
    }
}
