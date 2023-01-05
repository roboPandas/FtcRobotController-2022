package org.firstinspires.ftc.teamcode

interface LiftSubsystem : Subsystem {
    fun canSwitch(): Boolean
    fun prepareForSwitch()
}