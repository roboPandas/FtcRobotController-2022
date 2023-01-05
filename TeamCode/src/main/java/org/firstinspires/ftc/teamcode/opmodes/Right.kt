package org.firstinspires.ftc.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous

@Autonomous
class Right : Left() {
    override val reversed = true
}