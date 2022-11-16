package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Subsystem;

// TODO perhaps get this off sticks so controller 2 can also have manual control over the drivetrain if needed
public class Lift implements Subsystem {
    private static final double SCALE_FACTOR = 0.8;
    public final DcMotor motor;
    private final Servo rotationServo;
    private final Servo clawServo; // TODO this assumes one claw servo, which may not be accurate.
    private final Gamepad manualGamepad;
    /** true if controlled using the manual gamepad, and false if controlled using async cycles */
    public volatile boolean manualControl = true;
    private DcMotor.RunMode mode = DcMotor.RunMode.RUN_USING_ENCODER;

    public Lift(HardwareMap hardwareMap, Gamepad manualGamepad) {
        motor = hardwareMap.get(DcMotor.class, "liftMotor");
        rotationServo = hardwareMap.get(Servo.class, "rotationServo");
        clawServo = hardwareMap.get(Servo.class, "clawServo");
        this.manualGamepad = manualGamepad;

        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Set an auto-clamp for the servo
        rotationServo.scaleRange(0.1, 0.9); // TODO test these numbers
        clawServo.scaleRange(0.1, 0.9); // TODO test these numbers
    }

    public void loop() {
        if (manualGamepad == null || !manualControl) return;

        setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // rotation
        rotationServo.setPosition((manualGamepad.right_stick_x + 1) / 2);

        // slide
        motor.setPower(manualGamepad.left_stick_y * SCALE_FACTOR);

        // claw (open by default)
        clawServo.setPosition(manualGamepad.right_trigger);
    }

    public void closeClaw() {
        clawServo.setPosition(1);
    }

    public void openClaw() {
        clawServo.setPosition(0);
    }

    public void turnToDrop() {}

    public void turnToGrab() {}

    public void goToPosition(int position, double power) {
        setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setTargetPosition(position);
        motor.setPower(power);
    }

    private void setMode(DcMotor.RunMode mode) {
        if (this.mode == mode) return;
        motor.setMode(mode);
        this.mode = mode;
    }
}