package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.hardware.LiftInternals;

public class ManualLift implements LiftSubsystem {
    private final LiftInternals liftInternals;
    private final Gamepad gamepad;
    private double power;

    public ManualLift(LiftInternals liftInternals, Gamepad gamepad) {
        this.liftInternals = liftInternals;
        this.gamepad = gamepad;
    }

    @Override
    public void loop() {
        // reset encoder
        if (gamepad.left_bumper && gamepad.right_bumper) liftInternals.resetEncoder();

        // rotation
        liftInternals.rotationServo.setPosition(1 - gamepad.left_trigger);

        // slide
        if (gamepad.dpad_up) {
            liftInternals.lock();
            power = LiftInternals.SCALE_FACTOR;
        } else if (gamepad.dpad_down) {
            liftInternals.unlock();
            power = -LiftInternals.SCALE_FACTOR;
        } else {
            liftInternals.lock();
            power = 0;
        }

        if (liftInternals.motor.getPower() != power) LiftInternals.liftExecutor.submit(() -> {
            Utils.delay(100);
            liftInternals.motor.setPower(power);
        });

        // claw (closed by default)
        if (gamepad.right_trigger > 0.5) liftInternals.drop();
        else liftInternals.grab();
    }

    @Override
    public boolean canSwitch() {
        return true;
    }

    @Override
    public void prepareForSwitch() { // FIXME fix this to be more elegant and to not break things. as of now this function is NOT safe to call.
        liftInternals.motor.setTargetPosition(LiftInternals.Position.STACK_1.value);
        liftInternals.motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        liftInternals.drop();
        liftInternals.rotateToGrab(false);
        liftInternals.goToPosition(LiftInternals.Position.STACK_1, 1);
    }
}
