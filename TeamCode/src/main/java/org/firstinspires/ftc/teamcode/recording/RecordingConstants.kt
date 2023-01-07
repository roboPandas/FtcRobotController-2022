package org.firstinspires.ftc.teamcode.recording

import com.qualcomm.robotcore.hardware.Gamepad
import java.lang.reflect.Field

internal class RecordingConstants {
    companion object {
        /**
         * The default file name of the exported recording.
         */
        const val DEFAULT_OUTPUT_FILE_NAME = "recorded_inputs.txt"

        /**
         * The fields of controllers which are tracked for changes
         */
        val RECORDED_FIELDS = listOf(
            "left_stick_x",
            "left_stick_y",
            "right_stick_x",
            "right_stick_y",
            "dpad_up",
            "dpad_down",
            "dpad_left",
            "dpad_right",
            "a",
            "b",
            "x",
            "y",
            "guide",
            "start",
            "back",
            "left_bumper",
            "right_bumper",
            "left_stick_button",
            "right_stick_button",
            "left_trigger",
            "right_trigger",
            "circle",
            "cross",
            "triangle",
            "square",
            "share",
            "options",
            "touchpad",
            "touchpad_finger_1",
            "touchpad_finger_2",
            "touchpad_finger_1_x",
            "touchpad_finger_1_y",
            "touchpad_finger_2_x",
            "touchpad_finger_2_y",
            "ps"
        ).map {
            val original: Field = Gamepad::class.java.getDeclaredField(it)
            val last: Field = InputRecordingController::class.java.getDeclaredField("last_$it")
            Pair(original, last)
        }
    }
}