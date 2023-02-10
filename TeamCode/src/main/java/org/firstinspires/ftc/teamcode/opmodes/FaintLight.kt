package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous

@Autonomous
open class FaintLightLeft : FainterLightLeft() {

}

class FaintLightRight : FaintLightLeft() {
    override val reversed = true
}
