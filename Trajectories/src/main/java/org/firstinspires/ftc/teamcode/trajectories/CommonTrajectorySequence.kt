package org.firstinspires.ftc.teamcode.trajectories

import org.firstinspires.ftc.teamcode.trajectories.Operation.BooleanOperation
import org.firstinspires.ftc.teamcode.trajectories.Operation.ScalarOperation
import org.firstinspires.ftc.teamcode.trajectories.Operation.VecOperation
import org.firstinspires.ftc.teamcode.trajectories.Operation.PoseOperation
import org.firstinspires.ftc.teamcode.trajectories.Operation.PoseAndScalarOperation

class CommonTrajectorySequence private constructor(
    val start: Pose,
    val end: Pose,
    val operations: List<Operation>
) {
    class Builder(private val start: Pose) {
        private var current: Pose
        private val operations: MutableList<Operation> = ArrayList()

        init {
            current = start
        }

        private fun bool(type: BooleanOperation.Type, value: Boolean) = apply {
            operations.add(BooleanOperation(type, value))
        }

        private fun scalar(type: ScalarOperation.Type, value: Double) = apply {
            operations.add(ScalarOperation(type, value))
        }

        private fun vec(type: VecOperation.Type, vec: Vec) = apply {
            operations.add(VecOperation(type, vec))
        }

        private fun pose(type: PoseOperation.Type, pose: Pose) = apply {
            operations.add(PoseOperation(type, pose))
        }

        private fun poseScalar(
            type: PoseAndScalarOperation.Type,
            pose: Pose,
            value: Double
        ) = apply {
            operations.add(PoseAndScalarOperation(type, pose, value))
        }

        fun setReversed(reversed: Boolean): Builder {
            return bool(BooleanOperation.Type.SET_REVERSED, reversed)
        }

        fun lineTo(endPosition: Vec): Builder {
            current = current.withPos(endPosition)
            return vec(VecOperation.Type.LINE_TO, endPosition)
        }

        fun lineToConstantHeading(endPosition: Vec): Builder {
            current = current.withPos(endPosition)
            return vec(VecOperation.Type.LINE_TO_CONSTANT_HEADING, endPosition)
        }

        fun lineToLinearHeading(endPose: Pose): Builder {
            current = endPose
            return pose(PoseOperation.Type.LINE_TO_LINEAR_HEADING, endPose)
        }

        fun lineToSplineHeading(endPose: Pose): Builder {
            current = endPose
            return pose(PoseOperation.Type.LINE_TO_SPLINE_HEADING, endPose)
        }

        fun strafeTo(endPosition: Vec): Builder {
            current = current.withPos(endPosition)
            return vec(VecOperation.Type.STRAFE_TO, endPosition)
        }

        fun forward(distance: Double): Builder {
            offsetCurrent(current.heading, distance)
            return scalar(ScalarOperation.Type.FORWARD, distance)
        }

        fun back(distance: Double): Builder {
            return forward(-distance)
        }

        fun strafeLeft(distance: Double): Builder {
            offsetCurrent(current.heading + Math.PI / 2, distance)
            return scalar(ScalarOperation.Type.STRAFE_LEFT, distance)
        }

        private fun offsetCurrent(angle: Double, distance: Double) {
            val dX = distance * Math.cos(angle)
            val dY = distance * Math.sin(angle)
            current = current.withPos(current.x + dX, current.y + dY)
        }

        fun strafeRight(distance: Double): Builder {
            return strafeLeft(-distance)
        }

        fun splineTo(endPosition: Vec, endHeading: Double): Builder {
            current = Pose(endPosition, endHeading)
            return pose(PoseOperation.Type.SPLINE_TO, Pose(endPosition, endHeading))
        }

        fun splineToConstantHeading(endPosition: Vec, endHeading: Double): Builder {
            current = Pose(endPosition, endHeading)
            return pose(
                PoseOperation.Type.SPLINE_TO_CONSTANT_HEADING,
                Pose(endPosition, endHeading)
            )
        }

        fun splineToLinearHeading(endPose: Pose, endHeading: Double): Builder {
            current = endPose
            return poseScalar(
                PoseAndScalarOperation.Type.SPLINE_TO_LINEAR_HEADING,
                endPose,
                endHeading
            )
        }

        fun splineToSplineHeading(endPose: Pose, endHeading: Double): Builder {
            current = endPose
            return poseScalar(
                PoseAndScalarOperation.Type.SPLINE_TO_SPLINE_HEADING,
                endPose,
                endHeading
            )
        }

        fun build(): CommonTrajectorySequence {
            return CommonTrajectorySequence(start, current, operations)
        }
    }

    companion object {
        fun builder(start: Pose) = Builder(start)
    }
}