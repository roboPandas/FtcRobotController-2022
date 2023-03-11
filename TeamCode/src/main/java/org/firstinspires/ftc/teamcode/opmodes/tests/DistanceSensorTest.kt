package org.firstinspires.ftc.teamcode.opmodes.tests

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.DistanceSensor
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit

@Autonomous(group = "tests")
class DistanceSensorTest : LinearOpMode() {
    override fun runOpMode() {
        val distanceSensor = hardwareMap["distanceSensor"] as DistanceSensor
        waitForStart()
//        val start = System.currentTimeMillis()
//        val list = ArrayList<Double>(40)
//        while (System.currentTimeMillis() - start < 5000) {
//            val distance = distanceSensor.getDistance(DistanceUnit.INCH)
//            telemetry.addData("distance", distance)
//            telemetry.update()
//            list += distance
//        }
//        telemetry.addData("average", list.sum() / list.size)
//        telemetry.update()
//        @Suppress("ControlFlowWithEmptyBody")
//        while (opModeIsActive());
        while (opModeIsActive()) {
            telemetry.addData("distance", distanceSensor.getDistance(DistanceUnit.INCH))
            telemetry.update()
        }
    }
}