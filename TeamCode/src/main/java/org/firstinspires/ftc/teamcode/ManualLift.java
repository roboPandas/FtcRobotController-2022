package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.hardware.LiftInternals;
import org.firstinspires.ftc.teamcode.opmodes.CycleUsingOpMode;

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
            power = (liftInternals.motor.getCurrentPosition() < LiftInternals.Position.HIGH.value + 50) ? 0 : LiftInternals.MOTOR_SCALE_FACTOR;
        } else if (gamepad.dpad_down) {
            needsUnlock = true;
            power = -LiftInternals.MOTOR_SCALE_FACTOR;
        } else {
            needsUnlock = false;
            power = 0;
        }

        if (lastPower != power) {
            liftInternals.liftExecutor.submit(() -> { // prevent float errors TODO is this comparison for float purposes needed
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
        return liftInternals.motor.getCurrentPosition() > LiftInternals.Position.CAN_ROTATE.value;
    }

    @Override
    public void prepareForSwitch() {
        new Cycle((CycleUsingOpMode<?>) opMode, liftInternals, LiftInternals.Position.LOW, LiftInternals.Position.STACK_1).finish();
    }
}
