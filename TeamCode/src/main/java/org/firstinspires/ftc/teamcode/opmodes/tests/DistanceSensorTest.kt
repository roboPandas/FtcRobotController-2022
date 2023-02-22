package org.firstinspires.ftc.teamcode.opmodes.tests

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DistanceSensor
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit

@TeleOp
class DistanceSensorTest : OpMode() {
    private lateinit var distanceJunction: DistanceSensor
    private lateinit var distanceClaw: DistanceSensor

    override fun init() {
        distanceClaw = hardwareMap["distanceClaw"] as DistanceSensor
        distanceJunction = hardwareMap["distanceJunction"] as DistanceSensor
    }

    override fun loop() {
        telemetry.addData("distance claw", distanceClaw.getDistance(DistanceUnit.CM))
        telemetry.addData("distance junction", distanceJunction.getDistance(DistanceUnit.CM))
    }
}