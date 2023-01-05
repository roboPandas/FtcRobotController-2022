package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.LiftInternals;

public class ManualLift implements LiftSubsystem {
    private final LiftInternals liftInternals;
    private final OpMode opMode;
    private final Gamepad gamepad;
    // i have NO IDEA why these can't be local variables, but this WILL break if they aren't fields
    private volatile double power;
    private volatile double lastPower;
    private volatile boolean needsUnlock;

    public ManualLift(LiftInternals liftInternals, OpMode opMode) {
        this.liftInternals = liftInternals;
        this.opMode = opMode;
        this.gamepad = opMode.gamepad1;
    }

    @Override
    public void loop() {
        // reset encoder
        if (gamepad.left_bumper && gamepad.right_bumper) liftInternals.resetEncoder();

        // rotation
        liftInternals.rotationServo.setPosition(1 - gamepad.left_trigger);

        // slide
        // TODO in kotlin this will be a Pair<Int, Boolean> and an if expression
        if (gamepad.dpad_up) {
            needsUnlock = false;
            power = LiftInternals.MOTOR_SCALE_FACTOR;
        } else if (gamepad.dpad_down) {
            needsUnlock = true;
            power = -LiftInternals.MOTOR_SCALE_FACTOR;
        } else {
            needsUnlock = false;
            power = 0;
        }

        if (lastPower != power) {
            LiftInternals.liftExecutor.submit(() -> { // prevent float errors TODO is this comparison for float purposes needed
                if (needsUnlock) {
                    liftInternals.motor.setPower(LiftInternals.MOTOR_UNLOCK_POWER);
                    Utils.delay(100);
                    liftInternals.unlock();
                } else {
                    liftInternals.lock();
                }

                Utils.delay(100);
                liftInternals.motor.setPower(power);
            });
        }

        lastPower = power;

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
