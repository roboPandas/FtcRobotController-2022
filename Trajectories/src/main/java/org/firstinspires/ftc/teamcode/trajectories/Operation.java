package org.firstinspires.ftc.teamcode.trajectories;

public interface Operation {
    class BooleanOperation implements Operation {
        public final Type type;
        public final boolean value;

        public BooleanOperation(Type type, boolean value) {
            this.type = type;
            this.value = value;
        }

        public enum Type {
            SET_REVERSED
        }
    }
    class ScalarOperation implements Operation {
        public final Type type;
        public final double value;

        public ScalarOperation(Type type, double value) {
            this.type = type;
            this.value = value;
        }

        public enum Type {
            FORWARD,
            STRAFE_LEFT
        }
    }

    class VecOperation implements Operation {
        public final Type type;
        public final Vec vec;

        public VecOperation(Type type, Vec vec) {
            this.type = type;
            this.vec = vec;
        }

        public enum Type {
            LINE_TO,
            LINE_TO_CONSTANT_HEADING,
            STRAFE_TO
        }
    }

    public static class PoseOperation implements Operation {
        public final Type type;
        public final Pose pose;

        public PoseOperation(Type type, Pose pose) {
            this.type = type;
            this.pose = pose;
        }

        public enum Type {
            LINE_TO_LINEAR_HEADING,
            LINE_TO_SPLINE_HEADING,

            SPLINE_TO,
            SPLINE_TO_CONSTANT_HEADING
        }
    }

    public static class PoseAndScalarOperation implements Operation {
        public final Type type;
        public final Pose pose;
        public final double value;

        public PoseAndScalarOperation(Type type, Pose pose, double value) {
            this.type = type;
            this.pose = pose;
            this.value = value;
        }

        public enum Type {
            SPLINE_TO_LINEAR_HEADING,
            SPLINE_TO_SPLINE_HEADING
        }
    }
}
