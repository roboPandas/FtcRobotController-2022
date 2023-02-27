package org.firstinspires.ftc.teamcode.roadrunner

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import org.firstinspires.ftc.teamcode.roadrunner.drive.SampleMecanumDrive
import org.firstinspires.ftc.teamcode.roadrunner.trajectorysequence.TrajectorySequenceBuilder
import org.firstinspires.ftc.teamcode.trajectories.CommonTrajectorySequence
import org.firstinspires.ftc.teamcode.trajectories.Operation
import org.firstinspires.ftc.teamcode.trajectories.Operation.BooleanOperation
import org.firstinspires.ftc.teamcode.trajectories.Operation.ScalarOperation
import org.firstinspires.ftc.teamcode.trajectories.Operation.VecOperation
import org.firstinspires.ftc.teamcode.trajectories.Operation.PoseOperation
import org.firstinspires.ftc.teamcode.trajectories.Operation.PoseAndScalarOperation
import org.firstinspires.ftc.teamcode.trajectories.Vec
import org.firstinspires.ftc.teamcode.trajectories.Pose

object RoadRunnerTrajectories {
    fun SampleMecanumDrive.apply(
        vararg sequences: CommonTrajectorySequence
    ): TrajectorySequenceBuilder {
        val builder = trajectorySequenceBuilder(pose(sequences[0].start))
        for (sequence in sequences) {
            builder.apply(sequence)
        }
        return builder
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun TrajectorySequenceBuilder.apply(sequence: CommonTrajectorySequence) {
        sequence.operations.forEach { apply(it) }
    }

    private fun TrajectorySequenceBuilder.apply(op: Operation) {
        when (op) {
            is BooleanOperation -> when (op.type) {
                BooleanOperation.Type.SET_REVERSED -> setReversed(op.value)
            }
            is ScalarOperation -> when (op.type) {
                ScalarOperation.Type.FORWARD -> forward(op.value)
                ScalarOperation.Type.STRAFE_LEFT -> strafeLeft(op.value)
            }
            is VecOperation -> {
                val vec = vec(op.vec)
                when (op.type) {
                    VecOperation.Type.LINE_TO -> lineTo(vec)
                    VecOperation.Type.LINE_TO_CONSTANT_HEADING -> lineToConstantHeading(vec)
                    VecOperation.Type.STRAFE_TO -> strafeTo(vec)
                }
            }
            is PoseOperation -> {
                val pose = pose(op.pose)
                when (op.type) {
                    PoseOperation.Type.LINE_TO_LINEAR_HEADING -> lineToLinearHeading(pose)
                    PoseOperation.Type.LINE_TO_SPLINE_HEADING -> lineToSplineHeading(pose)
                    PoseOperation.Type.SPLINE_TO -> splineTo(pose.vec(), pose.heading)
                    PoseOperation.Type.SPLINE_TO_CONSTANT_HEADING -> splineToConstantHeading(
                        pose.vec(),
                        pose.heading
                    )
                }
            }
            is PoseAndScalarOperation -> {
                val pose = pose(op.pose)
                when (op.type) {
                    PoseAndScalarOperation.Type.SPLINE_TO_LINEAR_HEADING -> splineToLinearHeading(
                        pose,
                        op.value
                    )
                    PoseAndScalarOperation.Type.SPLINE_TO_SPLINE_HEADING -> splineToSplineHeading(
                        pose,
                        op.value
                    )
                }
            }
        }
    }

    private fun vec(vec: Vec) = Vector2d(vec.x, vec.y)

    private fun pose(pose: Pose) = Pose2d(pose.x, pose.y, pose.heading)
}