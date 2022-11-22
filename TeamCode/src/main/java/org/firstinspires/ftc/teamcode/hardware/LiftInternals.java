package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import static org.firstinspires.ftc.teamcode.Utils.delay;

public class LiftInternals {
    public static final double SCALE_FACTOR = 0.8;
    public final DcMotor motor;
    public final Servo rotationServo;
    public final Servo clawServo; // TODO this assumes one claw servo, which may not be accurate.
    public final Servo lockServo;
    /** @see #setMode(DcMotor.RunMode) */
    private DcMotor.RunMode mode = DcMotor.RunMode.RUN_TO_POSITION;

    public LiftInternals(HardwareMap hardwareMap) {
        motor = hardwareMap.get(DcMotor.class, "liftMotor");
        rotationServo = hardwareMap.get(Servo.class, "rotationServo");
        clawServo = hardwareMap.get(Servo.class, "clawServo");
        lockServo = hardwareMap.get(Servo.class, "lockServo");

        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // Set an auto-clamp for the servo TODO test these numbers
        rotationServo.scaleRange(0.1, 0.9);
        clawServo.scaleRange(0.1, 0.9);
        lockServo.scaleRange(0.1, 0.9);
    }


    // Claw TODO test these numbers
    public void grab() {
        clawServo.setPosition(1);
    }

    public void drop() {
        clawServo.setPosition(0);
    }

    // Rotation TODO test these numbers
    public void rotateToDrop() {
        rotationServo.setPosition(1);
    }

    public void rotateToGrab() {
        rotationServo.setPosition(0);
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

    public void goToPositionBlocking(LiftInternals.Position position, double power) {
        goToPosition(position, power);
        while (Math.abs(motor.getCurrentPosition() - position.value) > 20) delay(50); // TODO test the tolerance
    }

    /** Power MUST be positive. */
    public void goToPosition(Position targetPosition, double power) {
        goToPosition(targetPosition.value, power);
    }

    /** Power MUST be positive. */
    private void goToPosition(int targetPosition, double power) { // just in case
        setMode(DcMotor.RunMode.RUN_TO_POSITION);
        unlock();
        motor.setPower(power);
        lock();
    }

    public void resetEncoder() {
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(mode);
    }

    public enum Position {
        // TODO do we need two bottom positions or more?
        // GROUND is for a low stack or ground; STACK is for a higher stack
        // CAN_ROTATE is the lowest position where we can start rotating the lift.
        // TODO test if we need to explicitly disable locking for the GROUND position
        GROUND(0), STACK(0), CAN_ROTATE(0),
        LOW(0), MIDDLE(0), HIGH(0);

        public final int value;
        Position(int value) {
            this.value = value;
        }
    }
}