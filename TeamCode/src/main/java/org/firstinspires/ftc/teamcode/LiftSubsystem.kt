package org.firstinspires.ftc.teamcode

interface LiftSubsystem : Subsystem {
    val canSwitch: Boolean
    fun prepareForSwitch()
}