package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.hardware.LiftInternals
import org.firstinspires.ftc.teamcode.AsyncLift
import org.firstinspires.ftc.teamcode.ManualLift
import org.firstinspires.ftc.teamcode.LiftSubsystem
import org.firstinspires.ftc.teamcode.hardware.Drivetrain
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Controls:
 * Left stick X/Y - drivetrain translation
 * Right stick X - drivetrain rotation
 * Logitech button - swap between async and manual lift controls (it is async at start)
 * Right bumper + left bumper - reset lift encoder
 * Dpad left/right - move back/forward at half speed
 *
 * Manual lift control:
 * Dpad up/down - raise/lower lift
 * Left trigger - turn claw
 * TODO: bind claw to left and right dpad to preserve state and allow micro-adjustments
 * Right trigger - open/close claw
 *
 * Async lift control:
 * Left/right trigger - change bottom position (left is down, right is up); default is the lowest setting
 * B/Y/X - set top position to high/medium/low (respectively); default is high
 * A - start/continue cycle
 */
@TeleOp
open class ControlledOpMode : OpMode(), CycleUsingOpMode<ControlledOpMode> {
    // TODO merge this with auto if needed
    override val cycleExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    protected lateinit var liftInternals: LiftInternals
    protected lateinit var drivetrain: Drivetrain
    protected lateinit var currentLiftSubsystem: LiftSubsystem
    protected lateinit var asyncLift: AsyncLift
    protected lateinit var manualLift: ManualLift

    protected var manualControl = false
    private var switchButtonCache = false
    private fun attemptSwitch() {
        val button = gamepad1.guide
        if (button) {
            if (!switchButtonCache && currentLiftSubsystem.canSwitch) {
                manualControl = !manualControl
                currentLiftSubsystem.prepareForSwitch()
                currentLiftSubsystem = if (manualControl) manualLift else asyncLift
            }
        }
        switchButtonCache = button
    }

    override fun init() {
        liftInternals = LiftInternals(this)
        asyncLift = AsyncLift(liftInternals, this)
        manualLift = ManualLift(liftInternals, this)
        drivetrain = Drivetrain(this)
        currentLiftSubsystem = asyncLift
    }

    override fun loop() {
        drivetrain.loop()
        currentLiftSubsystem.loop()
        attemptSwitch()
        telemetry.addData("lift encoder", liftInternals.motor.currentPosition)
    }
}