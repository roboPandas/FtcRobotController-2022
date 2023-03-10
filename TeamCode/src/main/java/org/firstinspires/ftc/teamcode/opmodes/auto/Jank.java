package org.firstinspires.ftc.teamcode.opmodes.auto;

import android.util.Pair;

import org.firstinspires.ftc.teamcode.trajectories.CommonTrajectorySequence;
import org.firstinspires.ftc.teamcode.trajectories.Trajectories.FainterLight;
import org.firstinspires.ftc.teamcode.trajectories.Vec;

public class Jank {
    public static Pair<CommonTrajectorySequence[], CommonTrajectorySequence[]> buildTrajectories(int cycles, CommonTrajectorySequence preload) {
        cycles++; // account for preload
        CommonTrajectorySequence[] toStacks = new CommonTrajectorySequence[cycles];
        CommonTrajectorySequence[] toJunctions = new CommonTrajectorySequence[cycles];
        System.out.println("before");
        for (int i = 0; i < cycles; i++) {
            toJunctions[i] = i == 0 ? preload : FainterLight.INSTANCE.buildToJunction(
                    new Vec(i * 0.25, i * -0.35),
                    toStacks[i - 1].getEnd()
            );
            System.out.println("junction " + i);
            toStacks[i] = FainterLight.INSTANCE.buildToStack(
                    new Vec(i * 0.25, i * -0.35),
                    toJunctions[i].getEnd()
            );
            System.out.println("stack " + i);
        }
        return new Pair<>(toStacks, toJunctions);
    }
}
