package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import kotlinx.coroutines.runBlocking
import org.firstinspires.ftc.teamcode.AsyncLift
import org.firstinspires.ftc.teamcode.LiftSubsystem
import org.firstinspires.ftc.teamcode.ManualLift
import org.firstinspires.ftc.teamcode.Subsystem
import org.firstinspires.ftc.teamcode.hardware.Drivetrain
import org.firstinspires.ftc.teamcode.hardware.LiftInternals

/**
 * Controls:
 * Left stick X/Y - drivetrain translation
 * Right stick X - drivetrain rotation
 * Logitech button - swap between async and manual lift controls (it is async at start(
 *
 * Manual lift control
 * Dpad up/down - raise/lower lift
 * Left trigger - turn claw
 * Right trigger - open/close claw
 *
 * Async lift control
 * Dpad up/down - change bottom position; default is the lowest setting
 * B/Y/X/A - set top position to high/medium/low/ground (respectively); default is high
 * Right bumper - start/continue cycle
 */
@TeleOp
class ControlledOpMode : LinearOpMode() {
    private lateinit var all: Array<Subsystem>
    private lateinit var asyncLift: AsyncLift
    private lateinit var manualLift: ManualLift
    private var manualControl = false
    private var switchButtonCache = false

    private fun attemptSwitch() {
        val button = gamepad1.guide
        if (button) {
            val currentLiftSubsystem = all[1] as LiftSubsystem
            if (!switchButtonCache && currentLiftSubsystem.canSwitch) {
                manualControl = !manualControl
                currentLiftSubsystem.prepareForSwitch()
                all[1] = if (manualControl) manualLift else asyncLift
            }
        }
        switchButtonCache = button
    }

    override fun runOpMode() {
        val liftInternals = LiftInternals(hardwareMap)
        asyncLift = AsyncLift(liftInternals, gamepad1)
        manualLift = ManualLift(liftInternals, gamepad1)
        all = arrayOf(
                Drivetrain(hardwareMap, gamepad1),
                asyncLift
        )

        waitForStart()
        runBlocking {
            while (opModeIsActive()) {
                for (subsystem in all) subsystem.loop()
                attemptSwitch()
            }
        }
    }
}