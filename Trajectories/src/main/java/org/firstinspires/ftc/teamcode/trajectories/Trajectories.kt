package org.firstinspires.ftc.teamcode.trajectories;

import static java.lang.Math.PI;

public class Trajectories {
    public static class FainterLight {
        public static final CommonTrajectorySequence PRELOAD = CommonTrajectorySequence.builder(pose(-36, -65.5, -PI / 2))
                .setReversed(true) // robot starts backwards
                .back(2.0) // back up from wall to not hit when rotating
                .splineToSplineHeading(pose(-36, -53.5, PI), PI / 2) // rotate and move up a bit
                .splineToSplineHeading(pose(-38, -23.5, 11 * PI / 8), PI / 2)
                .splineToSplineHeading(pose(-31.0, -4.0, 5 * PI / 4), PI / 4) // finish and spline to pole
                .build();

        public static final CommonTrajectorySequence TO_STACK = CommonTrajectorySequence.builder(PRELOAD.end)
                .setReversed(false)
                .splineTo(vec(-38, -16.0), PI) // spline towards cones
                .forward(22.0) // drive into cones
                .build();

        public static final CommonTrajectorySequence TO_JUNCTION = CommonTrajectorySequence.builder(TO_STACK.end)
                .setReversed(true)
                .back(20.5) // back up from cones
                .splineToSplineHeading(pose(-25.5, -5, 5 * PI / 4), PI / 4)
                .build();

        public static final CommonTrajectorySequence PARK = CommonTrajectorySequence.builder(TO_JUNCTION.end)
                .setReversed(false)
                .splineTo(vec(-38, -12), PI) // spline back to cones
                .build();
    }

    public static Vec vec(double x, double y) {
        return new Vec(x, y);
    }

    public static Pose pose(double x, double y, double heading) {
        return new Pose(x, y, heading);
    }

    public static double rad(double deg) {
        return Math.toRadians(deg);
    }
}
