package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.lynx.LynxServoController;
import com.qualcomm.robotcore.hardware.Servo;

/** Functions used by more than one class */
public class Utils {
    @SuppressWarnings("StatementWithEmptyBody")
    public static void delay(long millis) {
//        long endTime = System.currentTimeMillis() + millis;
//        while (System.currentTimeMillis() < endTime);
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException("L");
        }
    }

    public static void pwmEnable(Servo servo, boolean enabled) {
        if (enabled) ((LynxServoController) servo.getController()).setServoPwmEnable(servo.getPortNumber());
        else ((LynxServoController) servo.getController()).setServoPwmDisable(servo.getPortNumber());
    }
}
