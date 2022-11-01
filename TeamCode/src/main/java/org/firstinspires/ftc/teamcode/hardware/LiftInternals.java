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
    /** @see #setMode(DcMotor.RunMode) */
    private DcMotor.RunMode mode = DcMotor.RunMode.RUN_TO_POSITION;

    public LiftInternals(HardwareMap hardwareMap) {
        motor = hardwareMap.get(DcMotor.class, "liftMotor");
        rotationServo = hardwareMap.get(Servo.class, "rotationServo");
        clawServo = hardwareMap.get(Servo.class, "clawServo");

        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // Set an auto-clamp for the servo
        rotationServo.scaleRange(0.1, 0.9); // TODO test these numbers
        clawServo.scaleRange(0.1, 0.9); // TODO test these numbers
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
        motor.setPower(power);
    }

    public void resetEncoder() {
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(mode);
    }

    public enum Position {
        // TODO do we need two bottom positions or more?
        GROUND(0), STACK(0), // GROUND is for a low stack or ground; STACK is for a higher stack
        LOW(0), MIDDLE(0), HIGH(0);

        public final int value;
        Position(int value) {
            this.value = value;
        }
    }
}