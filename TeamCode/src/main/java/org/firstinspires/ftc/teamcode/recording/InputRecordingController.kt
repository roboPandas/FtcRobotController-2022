package org.firstinspires.ftc.teamcode.recording

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.Gamepad
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/**
 * A replacement for a default controller which will record all inputs, making them available
 * for replay at another time through a file export.
 * @param opMode the OpMode using this controller
 * @param gamepadToReplace the default controller this is replacing, likely either gamepad1 or gamepad2
 */
@Suppress("unused", "PrivatePropertyName")
class InputRecordingController(private val opMode: OpMode, gamepadToReplace: Gamepad) : Gamepad() {
    init {
        copy(gamepadToReplace)
    }

    private val inputs = InputSequence()
    
    @Volatile private var last_left_stick_x = 0f
    @Volatile private var last_left_stick_y = 0f
    @Volatile private var last_right_stick_x = 0f
    @Volatile private var last_right_stick_y = 0f
    @Volatile private var last_dpad_up = false
    @Volatile private var last_dpad_down = false
    @Volatile private var last_dpad_left = false
    @Volatile private var last_dpad_right = false
    @Volatile private var last_a = false
    @Volatile private var last_b = false
    @Volatile private var last_x = false
    @Volatile private var last_y = false
    @Volatile private var last_guide = false
    @Volatile private var last_start = false
    @Volatile private var last_back = false
    @Volatile private var last_left_bumper = false
    @Volatile private var last_right_bumper = false
    @Volatile private var last_left_stick_button = false
    @Volatile private var last_right_stick_button = false
    @Volatile private var last_left_trigger = 0f
    @Volatile private var last_right_trigger = 0f
    @Volatile private var last_circle = false
    @Volatile private var last_cross = false
    @Volatile private var last_triangle = false
    @Volatile private var last_square = false
    @Volatile private var last_share = false
    @Volatile private var last_options = false
    @Volatile private var last_touchpad = false
    @Volatile private var last_touchpad_finger_1 = false
    @Volatile private var last_touchpad_finger_2 = false
    @Volatile private var last_touchpad_finger_1_x = 0f
    @Volatile private var last_touchpad_finger_1_y = 0f
    @Volatile private var last_touchpad_finger_2_x = 0f
    @Volatile private var last_touchpad_finger_2_y = 0f
    @Volatile private var last_ps = false

    /**
     * Copy the state of the given controller.
     */
    override fun copy(gamepad: Gamepad) {
        super.copy(gamepad)
        saveState()
    }

    /**
     * Clears the gamepad and any recordings.
     */
    override fun reset() {
        super.reset()
        inputs.clear()
    }

    /**
     * Export the recorded inputs to the file of the given name,
     * or to the default file if none is provided.
     */
    @JvmOverloads
    fun export(fileName: String = RecordingConstants.DEFAULT_OUTPUT_FILE_NAME) {
        val file = File(fileName)
        file.delete()
        file.createNewFile()
        export(file)
    }

    /**
     * Export the recorded inputs to the given file.
     */
    fun export(file: File) {
        export(FileOutputStream(file))
    }

    /**
     * Export the recorded inputs to the given output stream.
     */
    fun export(out: OutputStream) {
        inputs.write(out)
    }

    /**
     * When the state of inputs changes, those changes are recorded into the input sequence.
     */
    private fun saveState() {
        for (fields in RecordingConstants.RECORDED_FIELDS) {
            val original = fields.first
            val last = fields.second
            val originalVal = original.get(this)
            val lastVal = last.get(this)
            if (originalVal != lastVal) {
                // something changed, save it
                val input = ControllerInput(original, originalVal)
                inputs.add(opMode.runtime, input)
            }
        }
    }
}