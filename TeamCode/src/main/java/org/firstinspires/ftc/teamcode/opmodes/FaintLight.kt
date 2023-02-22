package org.firstinspires.ftc.teamcode.opmodes

import android.os.Build
import androidx.annotation.RequiresApi
import com.qualcomm.robotcore.eventloop.opmode.Autonomous

@RequiresApi(Build.VERSION_CODES.N)
@Autonomous
open class FaintLightLeft : FainterLightLeft() {

}

@RequiresApi(Build.VERSION_CODES.N)
class FaintLightRight : FaintLightLeft() {
    override val reversed = true
}
