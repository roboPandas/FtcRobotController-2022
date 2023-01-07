package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import java.util.concurrent.ExecutorService

interface CycleUsingOpMode<out T> where T : CycleUsingOpMode<T>, T : OpMode {
    @Suppress("UNCHECKED_CAST")
    val self: T
        get() = this as T
    val cycleExecutor: ExecutorService
}