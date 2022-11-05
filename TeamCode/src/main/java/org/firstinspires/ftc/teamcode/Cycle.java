package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.teamcode.hardware.LiftInternals;
import static org.firstinspires.ftc.teamcode.Utils.delay;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Represents one intake cycle. */
public class Cycle {
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final LiftInternals liftInternals;
    public volatile Stage stage = Stage.WAITING;
    private final LiftInternals.Position topPosition;
    private final LiftInternals.Position bottomPosition;

    public Cycle(LiftInternals liftInternals, LiftInternals.Position topPosition, LiftInternals.Position bottomPosition) {
        this.liftInternals = liftInternals;
        this.topPosition = topPosition;
        this.bottomPosition = bottomPosition;
    }

    public void start() { // TODO how should we handle preload?
        stage = Stage.IN_START;
        executor.submit(() -> {
            // grab item
            liftInternals.grab();
            delay(500); // this delay is to make sure it's in there TODO test this number

            // don't rotate until safe to do so
            liftInternals.goToPositionBlocking(LiftInternals.Position.CAN_ROTATE, 1);

            // TODO make sure that the slide and servo happen simultaneously
            liftInternals.rotateToDrop();

            // wait until lift is done before finishing
            liftInternals.goToPositionBlocking(topPosition, 1);

            stage = Stage.BETWEEN;
        });
    }

    // TODO this is basically the same as start so can it be refactored somehow?
    public void finish() {
        stage = Stage.IN_FINISH;
        executor.submit(() -> {
            // drop item
            liftInternals.drop();
            delay(500); // this delay is to make sure it's out of our way TODO test this number

            // TODO make sure that the slide and servo happen simultaneously
            liftInternals.rotateToGrab();

            // wait until lift is done before finishing
            liftInternals.goToPositionBlocking(bottomPosition, 1);

            stage = Stage.COMPLETE;
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

    public enum Stage {
        WAITING, IN_START, BETWEEN, IN_FINISH, COMPLETE
    }
}
