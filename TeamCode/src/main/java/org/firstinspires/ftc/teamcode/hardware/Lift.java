package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Subsystem;
import org.jetbrains.annotations.Nullable;

// TODO perhaps get this off sticks so controller 2 can also have manual control over the drivetrain if needed
public class Lift implements Subsystem {
    private static final double SCALE_FACTOR = 0.8;
    public final DcMotor motor;
    private final Servo rotationServo;
    private final Servo clawServo; // TODO this assumes one claw servo, which may not be accurate.
    @Nullable private final Gamepad manualGamepad;
    /** true if controlled using the manual gamepad, and false if controlled using async cycles */
    public volatile boolean manualControl;
    /** @see #setMode(DcMotor.RunMode) */
    private DcMotor.RunMode mode = DcMotor.RunMode.RUN_USING_ENCODER;

    public Lift(HardwareMap hardwareMap, @Nullable Gamepad manualGamepad) {
        motor = hardwareMap.get(DcMotor.class, "liftMotor");
        rotationServo = hardwareMap.get(Servo.class, "rotationServo");
        clawServo = hardwareMap.get(Servo.class, "clawServo");
        this.manualGamepad = manualGamepad;
        manualControl = manualGamepad != null;

        // Enable encoder use for this motor
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Set an auto-clamp for the servo
        rotationServo.scaleRange(0.1, 0.9); // TODO test these numbers
        clawServo.scaleRange(0.1, 0.9); // TODO test these numbers
    }

    public void loop() {
        if (!manualControl) return; // auto/semi-auto

        // manual
        // rotation
        rotationServo.setPosition((manualGamepad.right_stick_x + 1) / 2);

        // slide
        setMode(DcMotor.RunMode.RUN_USING_ENCODER); // RUN_TO_POSITION behaves differently!
        motor.setPower(manualGamepad.left_stick_y * SCALE_FACTOR);

        // claw (open by default)
        clawServo.setPosition(manualGamepad.right_trigger);

        // reset encoder
        if (manualGamepad.left_bumper && manualGamepad.right_bumper) resetEncoder();
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
    private void setMode(DcMotor.RunMode newMode) {
        if (mode == newMode) return;
        motor.setMode(mode);
        mode = newMode;
    }

    /** Power MUST be positive. */
    public void goToPosition(Position targetPosition, double power) {
        goToPosition(targetPosition.value, power);
    }

    /** Power MUST be positive. */
    private void goToPosition(int targetPosition, double power) {
        setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(power);
    }

    public void resetEncoder() {
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(mode);
    }

    public enum Position {
        GROUND(0), LOW(0), MIDDLE(0), HIGH(0), BEGIN_PROBING(0); // TODO test encoder values

        public final int value;
        Position(int value) {
            this.value = value;
        }
    }
}