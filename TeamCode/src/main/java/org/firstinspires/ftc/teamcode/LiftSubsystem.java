package org.firstinspires.ftc.teamcode;

public interface LiftSubsystem extends Subsystem {
    boolean canSwitch();
    void prepareForSwitch();
    void stop();
}
