package org.firstinspires.ftc.teamcode.opmodes.tests

import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotorEx
import org.firstinspires.ftc.teamcode.get

@TeleOp
@Disabled
class ZeroPowerBehaviorTest : OpMode() {
    private lateinit var motors: ArrayList<DcMotorEx>

    override fun init() {
        motors = arrayListOf(
            hardwareMap.get<DcMotorEx>("frontLeft"),
            hardwareMap.get<DcMotorEx>("frontRight"),
            hardwareMap.get<DcMotorEx>("backLeft"),
            hardwareMap.get<DcMotorEx>("backRight")
        )
    }

    override fun loop() {
        motors.forEach { telemetry.addData(hardwareMap.getNamesOf(it).toString(), it.zeroPowerBehavior) }
    }
}