package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import java.util.concurrent.ExecutorService

interface CycleContainer<out T> where T : CycleContainer<T>, T : OpMode {
    val cycleExecutor: ExecutorService
}