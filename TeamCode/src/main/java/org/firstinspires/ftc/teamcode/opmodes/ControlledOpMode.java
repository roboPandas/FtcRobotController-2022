package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.AsyncLift;
import org.firstinspires.ftc.teamcode.LiftSubsystem;
import org.firstinspires.ftc.teamcode.ManualLift;
import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.LiftInternals;
import org.firstinspires.ftc.teamcode.Subsystem;

/**
 * Controls:
 * Left stick X/Y - drivetrain translation
 * Right stick X - drivetrain rotation
 * Logitech button - swap between async and manual lift controls (it is async at start)
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
public class ControlledOpMode extends OpMode { // TODO merge this with auto if needed
    private Subsystem[] all;
    private AsyncLift asyncLift;
    private ManualLift manualLift;
    private boolean manualControl = false;

    private boolean switchButtonCache = false;

    private void attemptSwitch() {
        boolean button = gamepad1.guide;
        if (button) {
            LiftSubsystem currentLiftSubsystem = (LiftSubsystem) all[1];
            if (!switchButtonCache && currentLiftSubsystem.canSwitch()) {
                manualControl = !manualControl;
                currentLiftSubsystem.prepareForSwitch();
                all[1] = manualControl ? manualLift : asyncLift;
            }
        }
        switchButtonCache = button;
    }

    @Override
    public void init() {
        LiftInternals liftInternals = new LiftInternals(hardwareMap);
        asyncLift = new AsyncLift(liftInternals, gamepad1);
        manualLift = new ManualLift(liftInternals, gamepad1);
        all = new Subsystem[] {
                new Drivetrain(hardwareMap, gamepad1),
                asyncLift
        };
    }

    @Override
    public void loop() {
        for (Subsystem subsystem : all) subsystem.loop();
        attemptSwitch();
    }
}
