package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.teamcode.hardware.Lift;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Represents one intake cycle. */
public class Cycle { // TODO add probing
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Lift lift;
    public volatile Stage stage = Stage.WAITING;
    private final Lift.Position targetPosition;

    public Cycle(Lift lift, Lift.Position targetPosition) {
        this.lift = lift;
        this.targetPosition = targetPosition;
    }

    public void start() { // TODO how should we handle preload?
        executor.submit(() -> {
            stage = Stage.IN_START;
            lift.manualControl = false;

            // grab item
            lift.grab();
            delay(500); // this delay is to make sure it's in there TODO test this number

            // TODO make sure that the slide and servo happen simultaneously
            lift.rotateToDrop();

            // wait until lift is done before finishing
            waitForPosition(targetPosition);

            stage = Stage.BETWEEN;
        });
    }

    // TODO this is basically the same as start so can it be refactored somehow?
    public void finish() {
        executor.submit(() -> {
            stage = Stage.IN_FINISH;

            // drop item
            lift.drop();
            delay(500); // this delay is to make sure it's out of our way TODO test this number

            // TODO make sure that the slide and servo happen simultaneously
            lift.rotateToGrab();

            // wait until lift is done before finishing
            waitForPosition(Lift.Position.BEGIN_PROBING);

            stage = Stage.COMPLETE;
            lift.manualControl = true;
        });
    }

    public boolean isBusy() {
        return stage == Stage.IN_START || stage == Stage.IN_FINISH;
    }

    /**
     * @return true if COMPLETE, false if BETWEEN or WAITING
     */
    public boolean await() {
        delay(100); // Make sure that the other thread has a chance to set the state
        while (isBusy()) delay(50);
        return stage == Stage.COMPLETE;
    }

    private void waitForPosition(Lift.Position position) {
        lift.goToPosition(position, 1); // TODO test this power
        while (Math.abs(lift.motor.getCurrentPosition() - position.value) > 20) delay(50); // TODO test the tolerance
    }

    @SuppressWarnings("StatementWithEmptyBody")
    private void delay(long millis) {
        long endTime = System.currentTimeMillis() + millis;
        while (System.currentTimeMillis() < endTime);
    }

    public enum Stage {
        WAITING, IN_START, BETWEEN, IN_FINISH, COMPLETE
    }
}
