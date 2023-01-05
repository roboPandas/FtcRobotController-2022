package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import static org.firstinspires.ftc.teamcode.Utils.delay;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LiftInternals {
    public static final ExecutorService liftExecutor = Executors.newSingleThreadExecutor();
    public static final ExecutorService clawExecutor = Executors.newSingleThreadExecutor();
    public static final double MOTOR_SCALE_FACTOR = 0.8;
    public static final double MOTOR_UNLOCK_POWER = 0.1;
    private final OpMode opMode;
    public final DcMotor motor;
    public final Servo rotationServo;
    public final Servo clawServo;
    public final Servo lockServo;
    /** @see #setMode(DcMotor.RunMode) */
    private DcMotor.RunMode mode = DcMotor.RunMode.RUN_TO_POSITION;

    public LiftInternals(OpMode opMode) {
        this.opMode = opMode;
        HardwareMap hardwareMap = opMode.hardwareMap;
        motor = hardwareMap.get(DcMotor.class, "liftMotor");
        rotationServo = hardwareMap.get(Servo.class, "rotationServo");
        clawServo = hardwareMap.get(Servo.class, "clawServo");
        lockServo = hardwareMap.get(Servo.class, "lockServo");

        motor.setTargetPosition(Position.STACK_1.value);
        resetEncoder(); // TODO do we need this?
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Set an auto-clamp for the servo
        // These all assume that the position scaling is linear, and that we are using the center of the servo's range
        rotationServo.scaleRange(0.17, 0.845);
        clawServo.scaleRange(0.08, 0.195);
        lockServo.scaleRange(0, 0.12);
    }

    // TODO for kotlin refactor grab, drop, lock, unlock, rotate into boolean vals (locked, grabbed, etc.)
    // TODO also remove the reverse functions during said refactor
    // Claw
    // Grab is 0; drop is 1
    public static final int GRAB_DELAY_MS = 600;
    public static final int DROP_DELAY_MS = 750;
    public void grab() {
        internalSetClaw(0, GRAB_DELAY_MS); // TODO does 550 work??
        System.out.println("grab finished");
    }

    public void drop() {
        internalSetClaw(1, DROP_DELAY_MS);
    }

    private void internalSetClaw(int pos, long delayMillis) {
        int currentPos = (int) clawServo.getPosition();
        if (currentPos == pos) return;
        Utils.setPwm(clawServo, true);
        clawServo.setPosition(pos);
        System.out.println("position set " + clawServo.getPosition());
        clawExecutor.submit(() -> {
            Utils.delay(delayMillis);
            Utils.setPwm(clawServo, false);
        });
    }

    // Rotation TODO test these numbers
    public void rotateToDrop(boolean reversed) {
        rotationServo.setPosition(reversed ? 1 : 0);
    }

    public void rotateToGrab(boolean reversed) {
        rotationServo.setPosition(reversed ? 0 : 1);
    }

    // Lock
    // Lock is 0; unlock is 1
    public void lock() {
        lockServo.setPosition(0);
    }

    public void unlock() {
        lockServo.setPosition(1);
    }

    // motor
    // Prevents constantly setting a new mode
    public void setMode(DcMotor.RunMode newMode) {
        if (mode == newMode) return;
        mode = newMode;
        motor.setMode(mode);
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
        liftExecutor.submit(() -> {
            if (goToPositionInternal(targetPosition, power)) {
                while (motor.isBusy()) delay(50); // TODO i changed this from a manual position check to isBusy - should I change it back?
                lock();
            }
        });
    }

    private boolean goToPositionInternal(int targetPosition, double power) {
        // positive is up
        boolean needsLock = targetPosition < motor.getCurrentPosition();
        if (needsLock) {
            setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor.setPower(MOTOR_UNLOCK_POWER);
            delay(100);
            unlock();
        }
        delay(100);
        motor.setTargetPosition(targetPosition);
        setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(power * MOTOR_SCALE_FACTOR);
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
        STACK_1(0), STACK_2(300), STACK_3(300), STACK_4(450), STACK_5(450),
        // the lowest position that allows rotation
        CAN_ROTATE(1600),
        // junction heights
        LOW(1600), MIDDLE(2200), HIGH(2900);

        public static final Position GROUND = STACK_1;

        public final int value;
        Position(int value) {
            this.value = value;
        }

        public static Position fromStackHeight(int height) {
            return valueOf("STACK_" + height);
        }
    }
}
