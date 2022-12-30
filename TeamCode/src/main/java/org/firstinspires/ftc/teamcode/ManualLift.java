package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.hardware.LiftInternals;

public class ManualLift implements LiftSubsystem {
    public/*private*/ final LiftInternals liftInternals; // FIXME debug
    private final Gamepad gamepad;

    public ManualLift(LiftInternals liftInternals, Gamepad gamepad) {
        this.liftInternals = liftInternals;
        this.gamepad = gamepad;
    }

    @Override
    public void loop() {
        // rotation
        liftInternals.rotationServo.setPosition(gamepad.left_trigger);

        // slide
        liftInternals.motor.setPower(
                gamepad.dpad_up ? LiftInternals.SCALE_FACTOR :
                        gamepad.dpad_down ? -LiftInternals.SCALE_FACTOR : 0
        ); // TODO perhaps make a method to automatically scale the motor power

        // claw (closed by default)
        if (gamepad.right_trigger > 0.5) liftInternals.drop();
        else liftInternals.grab();

        // reset encoder
        if (gamepad.left_bumper && gamepad.right_bumper) liftInternals.resetEncoder();    }

    @Override
    public boolean canSwitch() {
        return true;
    }

    @Override
    public void prepareForSwitch() {
        liftInternals.motor.setTargetPosition(LiftInternals.Position.STACK_1.value);
        liftInternals.motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        liftInternals.drop();
        liftInternals.rotateToGrab(false);
        liftInternals.goToPosition(LiftInternals.Position.STACK_1, 1);
    }
}
