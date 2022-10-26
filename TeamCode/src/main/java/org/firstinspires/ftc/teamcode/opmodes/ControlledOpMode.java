package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.Drivetrain;
import org.firstinspires.ftc.teamcode.hardware.Lift;
import org.firstinspires.ftc.teamcode.Subsystem;

/**
 * Controls:
 * Left stick X/Y - drivetrain translation
 * Right stick X - drivetrain rotation
 *
 * Manual lift control (controller 2) TODO perhaps use a button on controller 1 to switch from auto to manual?
 * Left stick Y - raise/lower lift at given speed
 * Right stick X - turn bottom servo
 * Right trigger - open/close claw
 */
@TeleOp
public class ControlledOpMode extends OpMode { // TODO merge this with auto if needed
    private Subsystem[] all;

    @Override
    public void init() {
        all = new Subsystem[] {
                new Drivetrain(hardwareMap, gamepad1),
                new Lift(hardwareMap, gamepad2)
        };
    }

    @Override
    public void loop() {
        for (Subsystem subsystem : all) subsystem.loop();
    }
}
