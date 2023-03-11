package org.firstinspires.ftc.teamcode.recording

import android.os.Environment
import com.qualcomm.robotcore.hardware.Gamepad
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.function.DoubleSupplier

/**
 * Replays inputs saved via an InputRecordingController.
 */
class InputReplayingController private constructor(private val gamepad: Gamepad, input: InputStream, needsClose: Boolean, private val runtime: DoubleSupplier) : Gamepad() {
    private val sequence = InputSequence()
    
    init {
        sequence.read(String(input.readBytes()))
        if (needsClose) input.close()
    }

    /**
     * @param opMode the OpMode using this controller
     * @param input an InputStream providing the controller input to replay
     */
    constructor(gamepad: Gamepad, input: InputStream, runtime: DoubleSupplier) : this(gamepad, input, false, runtime)

    /**
     * Replay inputs saved via an InputRecordingController.
     * @param opMode the OpMode using this controller
     * @param file a File providing the controller input to replay
     */
    constructor(gamepad: Gamepad, file: File, runtime: DoubleSupplier) : this(gamepad, FileInputStream(file), true, runtime)

    /**
     * Replay inputs saved via an InputRecordingController.
     * @param opMode the OpMode using this controller
     * @param fileName the name of the file providing the controller input to replay
     */
    @JvmOverloads
    constructor(gamepad: Gamepad, fileName: String = RecordingConstants.DEFAULT_OUTPUT_FILE_NAME, runtime: DoubleSupplier)
            : this(gamepad, Environment.getExternalStorageDirectory().resolve("FIRST/data/$fileName"), runtime)

    /**
     * Update the state of this controller based on the recorded inputs.
     * Should be called from an OpMode loop.
     */
    fun update() {
        sequence.update(gamepad, runtime.asDouble)
    }

    fun printSequence() {
        sequence.write(System.out)
    }
}