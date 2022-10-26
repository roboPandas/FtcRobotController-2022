package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Subsystem;

// TODO perhaps get this off sticks so controller 2 can also have manual control over the drivetrain if needed
public class Lift implements Subsystem {
    private static final double SCALE_FACTOR = 0.8;
    private final DcMotor motor;
    private final Servo bottomServo;
    private final Servo clawServo; // TODO this assumes one claw servo, which may not be accurate.
    private final Gamepad manualGamepad;
    /** true if controlled using the manual gamepad, and false if controlled using async cycles */
    public volatile boolean manualControl = true; // TODO this should probably be a field in Cycle

    public Lift(HardwareMap hardwareMap, Gamepad manualGamepad) {
        motor = hardwareMap.get(DcMotor.class, "liftMotor");
        bottomServo = hardwareMap.get(Servo.class, "bottomServo");
        clawServo = hardwareMap.get(Servo.class, "clawServo");
        this.manualGamepad = manualGamepad;

        // Set an auto-clamp for the servo
        bottomServo.scaleRange(0.1, 0.9); // TODO test these numbers
        clawServo.scaleRange(0.1, 0.9); // TODO test these numbers
    }

    public void loop() {
        if (manualGamepad == null || !manualControl) return;

        // rotation
        bottomServo.setPosition((manualGamepad.right_stick_x + 1) / 2);

        // slide
        motor.setPower(manualGamepad.left_stick_y * SCALE_FACTOR);

        // claw (open by default)
        clawServo.setPosition(manualGamepad.right_trigger);
    }
}