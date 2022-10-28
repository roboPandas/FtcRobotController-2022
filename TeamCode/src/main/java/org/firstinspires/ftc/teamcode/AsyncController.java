package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** The bridge between the Cycle system and the controller input. */
public class AsyncController implements Subsystem {
    @NotNull private final Gamepad gamepad;
    @Nullable private Cycle currentCycle = null;
    private final Lift lift;
    private Lift.Position targetPosition = Lift.Position.HIGH;

    public AsyncController(@NotNull Gamepad gamepad, Lift lift) {
        this.gamepad = gamepad;
        this.lift = lift;
    }

    @Override
    public void loop() {
        // TODO we need a new button layout to allow for ground junctions :despair:
        if (currentCycle == null) {
            // set target
            if (gamepad.b) targetPosition = Lift.Position.HIGH;
            else if (gamepad.y) targetPosition = Lift.Position.MIDDLE;
            else if (gamepad.x) targetPosition = Lift.Position.LOW;

            // create cycle
            if (gamepad.a) {
                currentCycle = new Cycle(lift, targetPosition);
                currentCycle.start();
            }

            return;
        }

        // cycle is already present
        if (currentCycle.stage == Cycle.Stage.COMPLETE) {
            currentCycle = null;
            return;
        }
        if (currentCycle.isBusy()) return;

        // stage cannot be WAITING, so must be BETWEEN
        if (gamepad.a) currentCycle.finish();
    }
}
