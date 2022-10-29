package org.firstinspires.ftc.teamcode;

/** Functions used by more than one class */
public class Utils {
    @SuppressWarnings("StatementWithEmptyBody")
    public static void delay(long millis) {
        long endTime = System.currentTimeMillis() + millis;
        while (System.currentTimeMillis() < endTime);
    }
}
