package org.firstinspires.ftc.teamcode.roadrunner;

import android.os.Build.VERSION_CODES;

import androidx.annotation.RequiresApi;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;

import org.firstinspires.ftc.teamcode.roadrunner.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.roadrunner.trajectorysequence.TrajectorySequenceBuilder;
import org.firstinspires.ftc.teamcode.trajectories.CommonTrajectorySequence;
import org.firstinspires.ftc.teamcode.trajectories.Operation;
import org.firstinspires.ftc.teamcode.trajectories.Operation.BooleanOperation;
import org.firstinspires.ftc.teamcode.trajectories.Operation.PoseAndScalarOperation;
import org.firstinspires.ftc.teamcode.trajectories.Operation.PoseOperation;
import org.firstinspires.ftc.teamcode.trajectories.Operation.ScalarOperation;
import org.firstinspires.ftc.teamcode.trajectories.Operation.VecOperation;
import org.firstinspires.ftc.teamcode.trajectories.Pose;
import org.firstinspires.ftc.teamcode.trajectories.Vec;

@RequiresApi(api = VERSION_CODES.N)
public class RoadRunnerTrajectories {
    public static TrajectorySequenceBuilder apply(SampleMecanumDrive drive, CommonTrajectorySequence... sequences) {
        TrajectorySequenceBuilder builder = drive.trajectorySequenceBuilder(pose(sequences[0].start));
        for (CommonTrajectorySequence sequence : sequences) {
            apply(builder, sequence);
        }
        return builder;
    }

    public static void apply(TrajectorySequenceBuilder builder, CommonTrajectorySequence sequence) {
        sequence.operations.forEach(op -> apply(op, builder));
    }

    private static void apply(Operation op, TrajectorySequenceBuilder builder) {
        if (op instanceof BooleanOperation) {
            BooleanOperation b = (BooleanOperation) op;
            switch (b.type) {
                case SET_REVERSED: builder.setReversed(b.value); break;
            }
        } else if (op instanceof ScalarOperation) {
            ScalarOperation s = (ScalarOperation) op;
            switch (s.type) {
                case FORWARD: builder.forward(s.value); break;
                case STRAFE_LEFT: builder.strafeLeft(s.value); break;
            }
        } else if (op instanceof VecOperation) {
            VecOperation v = (VecOperation) op;
            Vector2d vec = vec(v.vec);
            switch (v.type) {
                case LINE_TO: builder.lineTo(vec); break;
                case LINE_TO_CONSTANT_HEADING: builder.lineToConstantHeading(vec); break;
                case STRAFE_TO: builder.strafeTo(vec); break;
            }
        } else if (op instanceof PoseOperation) {
            PoseOperation p = (PoseOperation) op;
            Pose2d pose = pose(p.pose);
            switch (p.type) {
                case LINE_TO_LINEAR_HEADING: builder.lineToLinearHeading(pose); break;
                case LINE_TO_SPLINE_HEADING: builder.lineToSplineHeading(pose); break;
                case SPLINE_TO: builder.splineTo(pose.vec(), pose.getHeading()); break;
                case SPLINE_TO_CONSTANT_HEADING: builder.splineToConstantHeading(pose.vec(), pose.getHeading()); break;
            }
        } else if (op instanceof PoseAndScalarOperation) {
            PoseAndScalarOperation p = (PoseAndScalarOperation) op;
            Pose2d pose = pose(p.pose);
            switch (p.type) {
                case SPLINE_TO_LINEAR_HEADING: builder.splineToLinearHeading(pose, p.value); break;
                case SPLINE_TO_SPLINE_HEADING: builder.splineToSplineHeading(pose, p.value); break;
            }
        }
    }

    private static Vector2d vec(Vec vec) {
        return new Vector2d(vec.x, vec.y);
    }

    private static Pose2d pose(Pose pose) {
        return new Pose2d(pose.x, pose.y, pose.heading);
    }
}
