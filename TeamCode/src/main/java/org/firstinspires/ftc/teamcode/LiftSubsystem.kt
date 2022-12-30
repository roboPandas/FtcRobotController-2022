package org.firstinspires.ftc.teamcode

import org.firstinspires.ftc.teamcode.hardware.LiftInternals

interface LiftSubsystem {
    fun loop(): Boolean
    fun prepareForSwitch()

    fun stop() {
        LiftInternals.liftExecutor.shutdownNow()
        LiftInternals.clawExecutor.shutdownNow()
    }
}