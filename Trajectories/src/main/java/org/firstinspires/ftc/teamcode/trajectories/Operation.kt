package org.firstinspires.ftc.teamcode.trajectories

sealed interface Operation {
    class BooleanOperation(val type: Type, val value: Boolean) : Operation {
        enum class Type {
            SET_REVERSED
        }
    }

    class ScalarOperation(val type: Type, val value: Double) : Operation {
        enum class Type {
            FORWARD, STRAFE_LEFT
        }
    }

    class VecOperation(val type: Type, val vec: Vec) : Operation {
        enum class Type {
            LINE_TO, LINE_TO_CONSTANT_HEADING, STRAFE_TO
        }
    }

    class PoseOperation(val type: Type, val pose: Pose) : Operation {
        enum class Type {
            LINE_TO_LINEAR_HEADING, LINE_TO_SPLINE_HEADING, SPLINE_TO, SPLINE_TO_CONSTANT_HEADING
        }
    }

    class PoseAndScalarOperation(val type: Type, val pose: Pose, val value: Double) : Operation {
        enum class Type {
            SPLINE_TO_LINEAR_HEADING, SPLINE_TO_SPLINE_HEADING
        }
    }
}