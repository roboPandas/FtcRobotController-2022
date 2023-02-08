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

@JvmOverloads
inline fun waitUntil(delayMillis: Long = 10, block: () -> Boolean) {
    while (!block()) delay(delayMillis)
}

@JvmOverloads
inline fun whileWatingFor(time: Long, delayMillis: Long = 10, block: () -> Unit) {
    val startTime = System.currentTimeMillis()
    while (System.currentTimeMillis() - startTime < time) {
        block()
        delay(delayMillis)
    }
}
