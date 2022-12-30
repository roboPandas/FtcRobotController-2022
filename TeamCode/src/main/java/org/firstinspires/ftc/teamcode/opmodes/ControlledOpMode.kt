package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.hardware.Drivetrain
import org.firstinspires.ftc.teamcode.AsyncLift
import org.firstinspires.ftc.teamcode.ManualLift
import org.firstinspires.ftc.teamcode.LiftSubsystem
import org.firstinspires.ftc.teamcode.hardware.LiftInternals

/**
 * Controls:
 * Left stick X/Y - drivetrain translation
 * Right stick X - drivetrain rotation
 * Logitech button - swap between async and manual lift controls (it is async at start)
 * RB + LB -> Reset lift encoders
 *
 * Manual lift control
 * Dpad up/down - raise/lower lift
 * Left trigger - turn claw
 * TODO: bind claw to left and right dpad to preserve state and allow micro-adjustments
 * Right trigger - open/close claw
 *
 * Async lift control
 * Dpad up/down - change bottom position; default is the lowest setting
 * B/Y/X - set top position to high/medium/low (respectively); default is high
 * A - start/continue cycle
 */
@TeleOp
class ControlledOpMode : OpMode() {
    // TODO merge this with auto if needed
    private lateinit var drivetrain: Drivetrain
    private lateinit var currentLiftSubsystem: LiftSubsystem
    private lateinit var asyncLift: AsyncLift
    private lateinit var manualLift: ManualLift

    private var switchButtonCache = false
    private var manualControl = false

    private fun attemptSwitch() {
        val button = gamepad1.guide

        if (button && !switchButtonCache && currentLiftSubsystem.loop()) {
            manualControl = !manualControl
            currentLiftSubsystem.prepareForSwitch()
            currentLiftSubsystem = if (manualControl) manualLift else asyncLift
        }

        switchButtonCache = button
    }

    override fun init() {
        drivetrain = Drivetrain(hardwareMap, gamepad1)

        val liftInternals = LiftInternals(hardwareMap)
        asyncLift = AsyncLift(liftInternals, gamepad1)
        manualLift = ManualLift(liftInternals, gamepad1)

        currentLiftSubsystem = asyncLift
    }

    override fun loop() {
        drivetrain.loop()
        attemptSwitch()
    }

    override fun stop() = currentLiftSubsystem.stop()
}