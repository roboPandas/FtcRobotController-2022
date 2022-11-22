package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous
public class Right extends Left {
    @Override
    protected boolean reversed() {
        return true;
    }
}
