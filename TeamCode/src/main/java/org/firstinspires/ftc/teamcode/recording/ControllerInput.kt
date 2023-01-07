package org.firstinspires.ftc.teamcode.recording

import com.qualcomm.robotcore.hardware.Gamepad
import java.lang.reflect.Field

/**
 * Represents a change in a controller's input.
 */
internal class ControllerInput<T>(val field: Field, val value: T) {
    /**
     * Apply this change to the given controller.
     */
    fun apply(controller: Gamepad) {
        field.set(controller, value)
    }

    override fun toString(): String {
        return "ControllerInput(${field.name} set to $value)"
    }
}