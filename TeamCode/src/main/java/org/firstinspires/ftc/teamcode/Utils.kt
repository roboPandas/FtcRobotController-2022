/** Functions used by more than one class  */
@file:JvmName("Utils")

package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.PwmControl
import java.lang.RuntimeException

@Suppress("ControlFlowWithEmptyBody")
fun delay(millis: Long) {
    try {
        Thread.sleep(millis)
    } catch (e: InterruptedException) {
        throw RuntimeException("L")
    }
}

var Servo.pwm: Boolean
    set(enabled) = (this as PwmControl).run {
        if (enabled) setPwmEnable() else setPwmDisable()
    }
    get() = (this as PwmControl).isPwmEnabled
