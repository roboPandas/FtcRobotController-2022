package org.firstinspires.ftc.teamcode;

public interface Subsystem {
    // TODO store constants here?
    void loop();

    default void stop() {}
}
