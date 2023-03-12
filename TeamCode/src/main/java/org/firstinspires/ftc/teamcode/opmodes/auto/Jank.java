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
        Vec[] stackOffsets = {
                new Vec(0, -1), // preload
                new Vec(-1.5, -1), // cycle 1
                new Vec(1, -1), // cycle 2
                new Vec(0, 0), // ...
                new Vec(0, 0),
                new Vec(0, 0)
        };
        Vec[] junctionOffsets = {
                null, // preload, unused
                new Vec(2, 0), // cycle 1
                new Vec(1, 1), // cycle 2
                new Vec(0, 0), // ...
                new Vec(0, 0),
                new Vec(0, 0)
        };
        System.out.println("before");
        for (int i = 0; i < cycles; i++) {
            toJunctions[i] = i == 0 ? preload : FainterLight.INSTANCE.buildToJunction(
                    junctionOffsets[i],
                    toStacks[i - 1].getEnd()
            );
            System.out.println("junction " + i);
            toStacks[i] = FainterLight.INSTANCE.buildToStack(
                    stackOffsets[i],
                    toJunctions[i].getEnd()
            );
        }
        return new Pair<>(toStacks, toJunctions);
    }
}
