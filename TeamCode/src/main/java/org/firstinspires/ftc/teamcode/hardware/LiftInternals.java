package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import static org.firstinspires.ftc.teamcode.Utils.delay;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LiftInternals {
    public static final ExecutorService executor = Executors.newSingleThreadExecutor(); // TODO can we find a way to do this without multithreading? are interrupts a thing?
    public static final double SCALE_FACTOR = 0.8;
    public final DcMotor motor;
    public final Servo rotationServo;
    public final Servo clawServo;
    public final Servo lockServo;
    /** @see #setMode(DcMotor.RunMode) */
    private DcMotor.RunMode mode = DcMotor.RunMode.RUN_TO_POSITION;

    public LiftInternals(HardwareMap hardwareMap) {
        motor = hardwareMap.get(DcMotor.class, "liftMotor");
        rotationServo = hardwareMap.get(Servo.class, "rotationServo");
        clawServo = hardwareMap.get(Servo.class, "clawServo");
        lockServo = hardwareMap.get(Servo.class, "lockServo");

        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Set an auto-clamp for the servo TODO test these numbers
        // These all assume that the position scaling is linear, and that we are using the center of the servo's range
        rotationServo.scaleRange(0.2, 0.8); // 180° out of 300°
        clawServo.scaleRange(0.425, 0.575); // 45° out of 300°
        lockServo.scaleRange(0.25, 0.75); // 90° out of 180°
    }


    // Claw TODO test these numbers
    public void grab() {
        clawServo.setPosition(1);
    }

    public void drop() {
        clawServo.setPosition(0);
    }

    // Rotation TODO test these numbers
    public void rotateToDrop(boolean reversed) {
        rotationServo.setPosition(reversed ? 0 : 1);
    }

    public void rotateToGrab(boolean reversed) {
        rotationServo.setPosition(reversed ? 1 : 0);
    }

    // Lock TODO test these numbers
    public void lock() {
        lockServo.setPosition(1);
    }

    public void unlock() {
        lockServo.setPosition(0);
    }

    // motor
    // Prevents constantly setting a new mode
    public void setMode(DcMotor.RunMode newMode) {
        if (mode == newMode) return;
        motor.setMode(mode);
        mode = newMode;
    }

    public void goToPositionBlocking(LiftInternals.Position targetPosition, double power) {
        // I didn't reuse as much code as I could have since I want to avoid multithreading unless needed
        boolean needsLock = goToPositionInternal(targetPosition.value, power);
        while (motor.isBusy()) delay(50); // TODO i changed this from a manual position check to isBusy - should I change it back?
        if (needsLock) lock();
    }

    /** Power MUST be positive. */
    public void goToPosition(Position targetPosition, double power) {
        goToPosition(targetPosition.value, power);
    }

    /** Power MUST be positive. */
    private void goToPosition(int targetPosition, double power) { // just in case
        if (goToPositionInternal(targetPosition, power)) executor.submit(() -> {
            while (motor.isBusy()) delay(50); // TODO i changed this from a manual position check to isBusy - should I change it back?
            lock();
        });
    }

    private boolean goToPositionInternal(int targetPosition, double power) {
        setMode(DcMotor.RunMode.RUN_TO_POSITION);
        // this assumes positive = up; flip the sign if not true
        boolean needsLock = targetPosition < motor.getCurrentPosition();
        if (needsLock) unlock();
        motor.setPower(power);
        motor.setTargetPosition(targetPosition);
        return needsLock;
    }

    public void resetEncoder() {
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(mode);
    }

    public enum Position {
        // TODO document the math for why we need 5 separate stack positions
        // TODO test if we need to explicitly disable locking for the GROUND position
        // STACK_N is a stack containing N cones
        // STACK_1 is for a single cone, and should be the default bottom position
        STACK_1(0), STACK_2(0), STACK_3(0), STACK_4(0), STACK_5(0),
        // the lowest position that allows rotation
        CAN_ROTATE(0),
        // junction heights
        LOW(0), MIDDLE(0), HIGH(0);

        public static final Position GROUND = STACK_2;

        public final int value;
        Position(int value) {
            this.value = value;
        }

        public static Position fromStackHeight(int height) {
            return valueOf("STACK_" + height);
        }
    }
}
