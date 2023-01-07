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
    
    constructor(opMode: OpMode, file: File) : this(opMode, FileInputStream(file))
    constructor(opMode: OpMode, fileName: String) : this(opMode, File(fileName))
    constructor(opMode: OpMode) : this(opMode, RecordingConstants.DEFAULT_OUTPUT_FILE_NAME)

    /**
     * Update the state of this controller based on the recorded inputs.
     * Should be called from an OpMode loop.
     */
    fun loop() {
        sequence.update(this, opMode.runtime)
    }
}