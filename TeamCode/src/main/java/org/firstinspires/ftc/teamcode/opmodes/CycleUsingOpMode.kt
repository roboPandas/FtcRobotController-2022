package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import java.util.concurrent.ExecutorService

interface CycleUsingOpMode<out T> where T : CycleUsingOpMode<T>, T : OpMode {
    val self: OpMode
        get() = this as OpMode
    val cycleExecutor: ExecutorService
}