package org.firstinspires.ftc.teamcode.recording

import com.qualcomm.robotcore.hardware.Gamepad
import java.io.OutputStream

/**
 * A sequence of recorded inputs.
 */
internal class InputSequence {
    private val inputs = arrayListOf<Pair<Double, ControllerInput<*>>>()

    /**
     * Update the given controller using the stored inputs. Removes all used inputs.
     */
    fun update(controller: Gamepad, runtime: Double) {
        // go through the inputs and apply (and remove) any that have occurred
        while (inputs.isNotEmpty() && inputs[0].first <= runtime) {
            val input = inputs.removeAt(0)
            input.second.apply(controller)
        }
    }

    /**
     * Add a new input to the sequence.
     */
    fun add(time: Double, input: ControllerInput<*>) {
        inputs.add(Pair(time, input))
    }

    /**
     * Empty this sequence.
     */
    fun clear() {
        inputs.clear()
    }

    /**
     * Write this sequence to the given OutputStream.
     */
    fun write(out: OutputStream) {
        val output = StringBuilder()
        for ((time, input) in inputs) {
            output.append("$time, ${input.field.name}, ${input.value}\n")
        }
        out.write(output.toString().toByteArray())
    }

    /**
     * Read data for this sequence from the given string of inputs.
     */
    fun read(data: String) {
        val inputs = data.split("\n")
        for (inputStr in inputs) {
            val split = inputStr.split(", ")
            if (split.size != 3)
                throw IllegalArgumentException("Expected 3 elements in input entry, got ${split.size}")
            val time = split[0].toDouble()
            val field = Gamepad::class.java.getDeclaredField(split[1])
            val valueStr = split[2]
            val input = ControllerInput(field,
                    when (val type = field.type) {
                        Boolean::class.java -> valueStr.toBooleanStrict()
                        Float::class.java -> valueStr.toFloat()
                        else -> throw IllegalArgumentException("$type is not a readable input; must be boolean or float")
                    }
            )
            this.inputs += Pair(time, input)
        }
    }
}
