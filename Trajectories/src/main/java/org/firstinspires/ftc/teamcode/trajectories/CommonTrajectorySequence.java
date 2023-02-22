package org.firstinspires.ftc.teamcode.trajectories;

import org.firstinspires.ftc.teamcode.trajectories.Operation.BooleanOperation;
import org.firstinspires.ftc.teamcode.trajectories.Operation.BooleanOperation.Type;
import org.firstinspires.ftc.teamcode.trajectories.Operation.PoseAndScalarOperation;
import org.firstinspires.ftc.teamcode.trajectories.Operation.PoseOperation;
import org.firstinspires.ftc.teamcode.trajectories.Operation.ScalarOperation;
import org.firstinspires.ftc.teamcode.trajectories.Operation.VecOperation;

import java.util.ArrayList;
import java.util.List;

public class CommonTrajectorySequence {
    public final Pose start;
    public final Pose end;
    public final List<Operation> operations;

    protected CommonTrajectorySequence(Pose start, Pose end, List<Operation> operations) {
        this.start = start;
        this.end = end;
        this.operations = operations;
    }

    public static Builder builder(Pose start) {
        return new Builder(start);
    }
    
    public static class Builder {
        private final Pose start;
        private Pose current;
        private final List<Operation> operations = new ArrayList<>();

        protected Builder(Pose start) {
            this.start = start;
            this.current = start;
        }

        private Builder bool(BooleanOperation.Type type, boolean value) {
            operations.add(new BooleanOperation(type, value));
            return this;
        }

        private Builder scalar(ScalarOperation.Type type, double value) {
            operations.add(new ScalarOperation(type, value));
            return this;
        }

        private Builder vec(VecOperation.Type type, Vec vec) {
            operations.add(new VecOperation(type, vec));
            return this;
        }

        private Builder pose(PoseOperation.Type type, Pose pose) {
            operations.add(new PoseOperation(type, pose));
            return this;
        }

        private Builder poseScalar(PoseAndScalarOperation.Type type, Pose pose, double value) {
            operations.add(new PoseAndScalarOperation(type, pose, value));
            return this;
        }

        public Builder setReversed(boolean reversed) {
            return bool(Type.SET_REVERSED, reversed);
        }

        public Builder lineTo(Vec endPosition) {
            current = current.withPos(endPosition);
            return vec(VecOperation.Type.LINE_TO, endPosition);
        }

        public Builder lineToConstantHeading(Vec endPosition) {
            current = current.withPos(endPosition);
            return vec(VecOperation.Type.LINE_TO_CONSTANT_HEADING, endPosition);
        }

        public Builder lineToLinearHeading(Pose endPose) {
            current = endPose;
            return pose(PoseOperation.Type.LINE_TO_LINEAR_HEADING, endPose);
        }

        public Builder lineToSplineHeading(Pose endPose) {
            current = endPose;
            return pose(PoseOperation.Type.LINE_TO_SPLINE_HEADING, endPose);
        }

        public Builder strafeTo(Vec endPosition) {
            current = current.withPos(endPosition);
            return vec(VecOperation.Type.STRAFE_TO, endPosition);
        }

        public Builder forward(double distance) {
            offsetCurrent(current.heading, distance);
            return scalar(ScalarOperation.Type.FORWARD, distance);
        }

        public Builder back(double distance) {
            return forward(-distance);
        }

        public Builder strafeLeft(double distance) {
            offsetCurrent(current.heading + (Math.PI / 2), distance);
            return scalar(ScalarOperation.Type.STRAFE_LEFT, distance);
        }

        private void offsetCurrent(double angle, double distance) {
            double dX = distance * Math.cos(angle);
            double dY = distance * Math.sin(angle);
            current = current.withPos(current.x + dX, current.y + dY);
        }

        public Builder strafeRight(double distance) {
            return strafeLeft(-distance);
        }

        public Builder splineTo(Vec endPosition, double endHeading) {
            current = new Pose(endPosition, endHeading);
            return pose(PoseOperation.Type.SPLINE_TO, new Pose(endPosition, endHeading));
        }

        public Builder splineToConstantHeading(Vec endPosition, double endHeading) {
            current = new Pose(endPosition, endHeading);
            return pose(PoseOperation.Type.SPLINE_TO_CONSTANT_HEADING, new Pose(endPosition, endHeading));
        }

        public Builder splineToLinearHeading(Pose endPose, double endHeading) {
            current = endPose;
            return poseScalar(PoseAndScalarOperation.Type.SPLINE_TO_LINEAR_HEADING, endPose, endHeading);
        }

        public Builder splineToSplineHeading(Pose endPose, double endHeading) {
            current = endPose;
            return poseScalar(PoseAndScalarOperation.Type.SPLINE_TO_SPLINE_HEADING, endPose, endHeading);
        }

        public CommonTrajectorySequence build() {
            return new CommonTrajectorySequence(start, current, operations);
        }
    }
}
