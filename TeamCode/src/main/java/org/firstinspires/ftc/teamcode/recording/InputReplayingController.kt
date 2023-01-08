package org.firstinspires.ftc.teamcode.recording

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.Gamepad
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

/**
 * Replay inputs saved via an InputRecordingController.
 * @param opMode the OpMode using this controller
 * @param input an InputStream providing the controller input to replay
 */
class InputReplayingController(val opMode: OpMode, input: InputStream) : Gamepad() {
    private val sequence = InputSequence()
    
    init {
        sequence.read(String(input.readBytes()))
    }

    /**
     * Replay inputs saved via an InputRecordingController.
     * @param opMode the OpMode using this controller
     * @param file a File providing the controller input to replay
     */
    constructor(opMode: OpMode, file: File) : this(opMode, FileInputStream(file))
    /**
     * Replay inputs saved via an InputRecordingController.
     * @param opMode the OpMode using this controller
     * @param fileName the name of the file providing the controller input to replay
     */
    @JvmOverloads
    constructor(opMode: OpMode, fileName: String = RecordingConstants.DEFAULT_OUTPUT_FILE_NAME)
            : this(opMode, File(fileName))

    /**
     * Update the state of this controller based on the recorded inputs.
     * Should be called from an OpMode loop.
     */
    fun update() {
        sequence.update(this, opMode.runtime)
    }
}