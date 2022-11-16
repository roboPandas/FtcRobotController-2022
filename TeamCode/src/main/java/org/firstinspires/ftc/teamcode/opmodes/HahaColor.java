package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;

@TeleOp
public class HahaColor extends OpMode {
    private ColorSensor colorSensor;

    @Override
    public void init() {
        colorSensor = hardwareMap.get(ColorSensor.class, "emo");
    }

    @Override
    public void loop() {
        long color = colorSensor.argb();

        long alpha = (color >> 24) & 0xFF;
        long red = (color >> 16) & 0xFF;
        long green = (color >> 8) & 0xFF;
        long blue = color & 0xFF;

        telemetry.addData("bitstring", Long.toString(color, 2));

        telemetry.addData("R", red);
        telemetry.addData("G", green);
        telemetry.addData("B", blue);
        telemetry.addData("A", alpha);

        telemetry.addData("Ru", colorSensor.red());
        telemetry.addData("Gu", colorSensor.green());
        telemetry.addData("Bu", colorSensor.blue());
        telemetry.addData("Au", colorSensor.alpha());

//        telemetry.addData("RP", colorSensor.red() / red);
//        telemetry.addData("GP", colorSensor.green() / green);
//        telemetry.addData("BP", colorSensor.blue() / blue);
//        telemetry.addData("AP", colorSensor.alpha() / alpha);
    }
}
