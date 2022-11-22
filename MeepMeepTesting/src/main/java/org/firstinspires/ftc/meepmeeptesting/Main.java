package org.firstinspires.ftc.meepmeeptesting;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class Main {
    public static final double DEG_90 = Math.toRadians(90);

    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(600);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 13)
                .setDimensions(15, 17)
                .followTrajectorySequence(drive ->
                        drive.trajectorySequenceBuilder(startPose()) // TODO is this 72-based or 70-based?
                                // FIXME if splines don't work use linear
                                // Preload
                                .forward(53.5) // 2 tiles + 5.5 inches to reach the center
                                .turn(negateIfReversed(-Math.PI / 4))
                                .forward(5) // takes us to (-32.46, -8.46)
                                // Intake
                                .setReversed(true) // splines always start by moving forward, so we need to call backward forward
                                .splineToLinearHeading(new Pose2d(negateIfReversed(-46), -12, reversed() ? Math.PI : 0), reversed() ? 0 : Math.PI)
                                .setReversed(false)
                                .back(10)
                                // Delivery
                                .forward(15)
                                .splineToLinearHeading(new Pose2d(negateIfReversed(-32.46), -8.46, reversed() ? 3 * Math.PI / 4 : Math.PI / 4), Math.PI / 2)
                                // Intake
                                .setReversed(true) // splines always start by moving forward, so we need to call backward forward
                                .splineToLinearHeading(new Pose2d(negateIfReversed(-46), -12, reversed() ? Math.PI : 0), reversed() ? 0 : Math.PI)
                                .setReversed(false)
                                .back(10)
                                // Delivery
                                .forward(15)
                                .splineToLinearHeading(new Pose2d(negateIfReversed(-32.46), -8.46, reversed() ? 3 * Math.PI / 4 : Math.PI / 4), Math.PI / 2)
                                // Parking
                                .splineToLinearHeading(new Pose2d(negateIfReversed(-36), -12, reversed() ? Math.PI : 0), -Math.PI / 2)
                                .strafeRight(negateIfReversed(24))
                                .build()
                );

        meepMeep.setBackground(MeepMeep.Background.FIELD_POWERPLAY_OFFICIAL)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }

    private static Pose2d startPose() {
        return new Pose2d(negateIfReversed(-36), -65.5, Math.PI / 2);
    }
    private static boolean reversed() { return false; }
    private static double negateIfReversed(double a) { return reversed() ? -a : a; }
}