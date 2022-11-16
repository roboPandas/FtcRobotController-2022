package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.teamcode.hardware.Lift;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Cycle {
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Lift lift;

    public Cycle(Lift lift) {
        this.lift = lift;
    }

    public void start() {
        executor.submit(() -> {
            lift.closeClaw();
            delay(100);

            lift.turnToDrop();
            lift.goToPosition(1, 1);
            while (Math.abs(lift.motor.getCurrentPosition() - lift.motor.getTargetPosition()) > 20) delay(50);
        });
    }

    public void finish() {
        executor.submit(() -> {
            lift.openClaw();
            delay(100);

            lift.turnToGrab();
            lift.goToPosition(0, 1);
            while (Math.abs(lift.motor.getCurrentPosition() - lift.motor.getTargetPosition()) > 20) delay(50);
        });
    }

    private void delay(int millis) {
        long initialTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - initialTime < millis);
    }
}
