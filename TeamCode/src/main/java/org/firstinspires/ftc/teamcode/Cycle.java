package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.teamcode.hardware.LiftInternals;
import org.firstinspires.ftc.teamcode.hardware.LiftInternals.Position;

import static org.firstinspires.ftc.teamcode.Utils.delay;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Supplier;

/** Represents one intake cycle. */
public class Cycle {
    private final OpMode opMode;
    private final ExecutorService executor;
    private final LiftInternals liftInternals;
    public volatile Stage stage = Stage.WAITING;
    private final LiftInternals.Position topPosition;
    private final Supplier<Position> bottomPosition;
    public static long GRAB_DELAY_MS = LiftInternals.GRAB_DELAY_MS;
    public static long DROP_DELAY_MS = 500;
    public volatile boolean forceTestPass = false;

    // TODO refactor the reversed code same as the kotlin was
    public Cycle(OpMode opMode, ExecutorService executor, LiftInternals liftInternals, LiftInternals.Position topPosition, Supplier<LiftInternals.Position> bottomPosition) {
        this.opMode = opMode;
        this.executor = executor;
        this.liftInternals = liftInternals;
        this.topPosition = topPosition;
        this.bottomPosition = bottomPosition;

        // validate positions
        if (topPosition.value < LiftInternals.Position.CAN_ROTATE.value) throw new IllegalArgumentException("invalid top position: " + topPosition.value);
    }

    public Future<?> start() { return start(false); }

    public Future<?> start(boolean reversed) {
        stage = Stage.GRABBING;
        System.out.println("start");
        return executor.submit(() -> {
            // grab item
            liftInternals.uncheckedGrab();
            System.out.println("grabbed?");
            delay(GRAB_DELAY_MS); // this delay is to make sure it's in there

            whenGrabbed(reversed);
        });
    }

    private void whenGrabbed(boolean reversed) {
        stage = Stage.GRABBED;

        liftInternals.goToPosition(topPosition, 1);
        Utils.waitUntil(() -> liftInternals.motor.getCurrentPosition() >= LiftInternals.Position.CAN_ROTATE.value - 50);
        liftInternals.rotateToDrop(reversed);
        liftInternals.awaitTargetHit();

        stage = Stage.WAITING_FOR_TEST;
    }

    public Future<?> test() {
        System.out.println("test start");
        stage = Stage.TEST_DROP;
        return executor.submit(() -> {
            System.out.println("going just below " + topPosition.value);
            liftInternals.goToPositionBlocking(topPosition.value - 350, 1);
            stage = Stage.TEST_WAITING;
            while (stage == Stage.TEST_WAITING) {
                if (opMode.gamepad1.start) {
                    stage = Stage.TEST_REVERTING;
                    System.out.println("reverting to " + topPosition);
                    liftInternals.goToPositionBlocking(topPosition, 1);
                    stage = Stage.WAITING_FOR_TEST;
                } else if (opMode.gamepad1.a || forceTestPass) {
                    System.out.println("continuing to " + topPosition);
                    liftInternals.drop();
                    liftInternals.goToPositionBlocking(topPosition, 1);
                    internalFinish(true); // TODO for the future we probably want to call get() here
                }
            }
        });
    }

    public Future<?> finish() { return internalFinish(false); }

    private Future<?> internalFinish(boolean dropOptimized) {
        stage = Stage.DROPPING;
        System.out.println("finishing");
        return executor.submit(() -> {
            // drop item
            if (!dropOptimized) {
                System.out.println("drop");
                liftInternals.drop();
                delay(DROP_DELAY_MS); // this delay is to make sure it's out of our way
            }

            stage = Stage.DROPPED;

            System.out.println("start rotating");
            liftInternals.rotateToGrab();
            // if lift is too far down, we need to wait for rotation to finish before dropping more
            if (topPosition.value < LiftInternals.Position.MIDDLE.value) delay(600);

            // wait until lift is done before finishing
            System.out.println("go to final position");
            liftInternals.goToPositionBlocking(bottomPosition.get(), 1);

            stage = Stage.COMPLETE;
        });
    }

    public boolean isBusy() {
        return !(stage == Stage.WAITING || stage == Stage.WAITING_FOR_TEST || stage == Stage.TEST_WAITING || stage == Stage.COMPLETE);
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
        WAITING, GRABBING, GRABBED,
        WAITING_FOR_TEST,
        TEST_DROP, TEST_WAITING, TEST_REVERTING,
        DROPPING, DROPPED, COMPLETE
    }
}
