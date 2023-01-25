/** Functions used by more than one class  */
@file:JvmName("Utils")

package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.PwmControl
import java.lang.RuntimeException
import kotlin.math.pow
import kotlin.math.roundToInt

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

fun Double.round(nthPlace: Int): Double {
    val factor = (10.0).pow(nthPlace)
    return ((this * factor).roundToInt()) / factor
}

fun Float.round(nthPlace: Int): Float {
    val factor = (10.0f).pow(nthPlace)
    return ((this * factor).roundToInt()) / factor
}

inline operator fun <reified T> HardwareMap.get(name: String): T = this[T::class.java, name]
