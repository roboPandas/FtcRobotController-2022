package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.teamcode.hardware.LiftInternals;
import static org.firstinspires.ftc.teamcode.Utils.delay;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/** Represents one intake cycle. */
public class Cycle {
    private final OpMode opMode;
    private final ExecutorService executor;
    private final LiftInternals liftInternals;
    public volatile Stage stage = Stage.WAITING;
    private final LiftInternals.Position topPosition;
    private final LiftInternals.Position bottomPosition;
    public static int GRAB_DELAY_MS = LiftInternals.GRAB_DELAY_MS + 100;
    public static int DROP_DELAY_MS = LiftInternals.DROP_DELAY_MS + 100;

    public Cycle(OpMode opMode, ExecutorService executor, LiftInternals liftInternals, LiftInternals.Position topPosition, LiftInternals.Position bottomPosition) {
        this.opMode = opMode;
        this.executor = executor;
        this.liftInternals = liftInternals;
        this.topPosition = topPosition;
        this.bottomPosition = bottomPosition;
    }

    public Future<?> start() { return start(false); }

    public Future<?> start(boolean reversed) {
        stage = Stage.GRABBING;
        System.out.println("start");
        return executor.submit(() -> {
            // grab item
            liftInternals.grab();
            System.out.println("grabbed?");
            delay(GRAB_DELAY_MS); // this delay is to make sure it's in there

            whenGrabbed(reversed);
        });
    }

    public void startPreload() { startPreload(false);}

    public void startPreload(boolean reversed) { executor.submit(() -> whenGrabbed(reversed)); }

    private void whenGrabbed(boolean reversed) {
        stage = Stage.GRABBED;

        // don't rotate until safe to do so
        liftInternals.goToPositionBlocking(LiftInternals.Position.CAN_ROTATE, 1);

        // TODO make sure that the slide and servo happen simultaneously
        liftInternals.rotateToDrop(reversed);

        // wait until lift is done before finishing
        liftInternals.goToPositionBlocking(topPosition, 1);

        stage = Stage.BETWEEN;
    }

    public Future<?> finish() { return finish(false); }

    // TODO this is basically the same as start so can it be refactored somehow?
    public Future<?> finish(boolean reversed) {
        stage = Stage.DROPPING;
        return executor.submit(() -> {
            // drop item
            liftInternals.drop();
            delay(DROP_DELAY_MS); // this delay is to make sure it's out of our way

            stage = Stage.DROPPED;

            // TODO make sure that the slide and servo happen simultaneously
            liftInternals.rotateToGrab(reversed);
            if (topPosition.value < LiftInternals.Position.MIDDLE.value) delay(600);

            // wait until lift is done before finishing
            liftInternals.goToPositionBlocking(bottomPosition, 1);

            stage = Stage.COMPLETE;
        });
    }

    public boolean isBusy() {
        return !(stage == Stage.WAITING || stage == Stage.BETWEEN || stage == Stage.COMPLETE);
    }

    /**
     * @return true if COMPLETE, false if BETWEEN or WAITING
     */
    public boolean await() {
        delay(100); // Make sure that the other thread has a chance to set the state
        while (isBusy()) delay(50);
        return stage == Stage.COMPLETE;
    }

    public void waitUntil(Stage stage) {
        delay(100); // Make sure that the other thread has a chance to set the state
        while (this.stage != stage) delay(50);
    }

    public enum Stage {
        WAITING, GRABBING, GRABBED, BETWEEN, DROPPING, DROPPED, COMPLETE
    }
}
