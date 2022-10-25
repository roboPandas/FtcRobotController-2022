package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Drivetrain {
    private static final double SCALE_FACTOR = -0.8;
    private static final int[][] MULTIPLIERS = {
            {+1, +1},
            {+1, -1},
            {-1, +1},
            {-1, -1}
    }; // z doesn't need a multiplier since everything is +1
    private final DcMotor[] all;
    private final Gamepad gamepad;

    public Drivetrain(HardwareMap hardwareMap, Gamepad gamepad) {
        this.gamepad = gamepad;
        all = new DcMotor[] {
                hardwareMap.get(DcMotor.class, "frontLeft"),
                hardwareMap.get(DcMotor.class, "frontRight"),
                hardwareMap.get(DcMotor.class, "backLeft"),
                hardwareMap.get(DcMotor.class, "backRight")
        };
    }

    public void tick() {
        double x = -gamepad.left_stick_x;
        double y = gamepad.left_stick_y;
        double z = -gamepad.right_stick_x;

        double total = Math.abs(x) + Math.abs(y) + Math.abs(z);

        if (total == 0) { // prevent division by 0
            for (DcMotor motor : all) motor.setPower(0);
            return;
        }

        // Adjust input to never exceed 1
        double factor = Math.max(Math.hypot(x, y), Math.abs(z)) * SCALE_FACTOR / total;
        for (int i = 0; i < 4; i++) all[i].setPower(
                ((MULTIPLIERS[i][0] * x) + (MULTIPLIERS[i][1] * y) + z) * factor
        );
    }
}
