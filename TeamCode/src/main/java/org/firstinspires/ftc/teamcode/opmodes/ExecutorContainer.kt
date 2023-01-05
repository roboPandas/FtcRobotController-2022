package org.firstinspires.ftc.teamcode.opmodes

import java.util.concurrent.ExecutorService

interface ExecutorContainer {
    var liftExecutor: ExecutorService
    var clawExecutor: ExecutorService
    var cycleExecutor: ExecutorService
}