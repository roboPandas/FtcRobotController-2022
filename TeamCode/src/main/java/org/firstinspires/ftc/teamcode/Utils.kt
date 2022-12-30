package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.ServoControllerEx

/** Functions used by more than one class  */
object Utils {
    fun delay(millis: Long) {
//        long endTime = System.currentTimeMillis() + millis;
//        while (System.currentTimeMillis() < endTime);
        try {
            Thread.sleep(millis)
        } catch (e: InterruptedException) {
            throw RuntimeException("L")
        }
    }

    var Servo.pwmEnabled: Boolean
        set(enabled) = (controller as ServoControllerEx).run {
            if (enabled) setServoPwmEnable(portNumber) else setServoPwmDisable(portNumber)
        }
        get() = (controller as ServoControllerEx).isServoPwmEnabled(portNumber)

}