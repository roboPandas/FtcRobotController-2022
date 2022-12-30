package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.teamcode.hardware.LiftInternals;

public interface LiftSubsystem extends Subsystem {
    boolean canSwitch();
    void prepareForSwitch();

    @Override
    default void stop() {
        LiftInternals.liftExecutor.shutdownNow();
        LiftInternals.clawExecutor.shutdownNow();
    }
}
