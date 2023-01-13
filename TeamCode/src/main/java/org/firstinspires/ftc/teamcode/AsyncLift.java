package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.hardware.LiftInternals;
import org.firstinspires.ftc.teamcode.opmodes.CycleUsingOpMode;

/** The bridge between the Cycle system and the controller input. */
public class AsyncLift implements LiftSubsystem {
    private final CycleUsingOpMode<?> opMode;
    private final Gamepad gamepad;
    private Cycle currentCycle = null;
    private boolean canSwitch = true;
    private final LiftInternals liftInternals;
    private LiftInternals.Position topPosition = LiftInternals.Position.HIGH;
    private LiftInternals.Position queuedPosition = null;
    private int lastBottomPositionValue = 1;
    private DpadState lastDpadState = DpadState.NEUTRAL;

    public AsyncLift(LiftInternals liftInternals, CycleUsingOpMode<?> opMode) {
        this.opMode = opMode;
        this.gamepad = this.opMode.getSelf().gamepad1;
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

            DpadState state = getDpadState();
            int bottomPositionValue;
            if (state != lastDpadState) {
                switch (state) {
                    case DOWN:
                        bottomPositionValue = Math.max(lastBottomPositionValue - 2, 1);
                        break;
                    case UP:
                        bottomPositionValue = Math.min(lastBottomPositionValue + 2, 5);
                        break;
                    default:
                        bottomPositionValue = lastBottomPositionValue;
                        break;
                }
                canSwitch = false;
            } else {
                bottomPositionValue = lastBottomPositionValue;
            }

            LiftInternals.Position bottomPosition = LiftInternals.Position.fromStackHeight(bottomPositionValue);
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
                currentCycle = new Cycle(opMode, liftInternals, topPosition, bottomPosition);
                System.out.println("A: start cycle");
                currentCycle.start();
                canSwitch = false;
            }

            lastBottomPositionValue = bottomPositionValue;
            lastDpadState = state;
            return;
        }
        canSwitch = false;

        // cycle is already present

        // do not allow setting the position while in a cycle.
        // any changes will be queued to be applied after it finishes.
        if (gamepad.b) queuedPosition = LiftInternals.Position.HIGH;
        else if (gamepad.y) queuedPosition = LiftInternals.Position.MIDDLE;
        else if (gamepad.x) queuedPosition = LiftInternals.Position.LOW;

        // check if the current cycle is done and reset if so
        if (currentCycle.stage == Cycle.Stage.COMPLETE) {
            currentCycle = null;
            if (queuedPosition != null) {
                topPosition = queuedPosition;
                queuedPosition = null;
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
