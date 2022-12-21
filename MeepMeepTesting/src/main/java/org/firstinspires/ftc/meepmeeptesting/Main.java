package org.firstinspires.ftc.meepmeeptesting;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

/** DESPERATION PROCEDURES:
 * 1. Change max vel to 36, using distance sensors as needed for consistency.
 * 2. Make trajectories go too far and cancel them.
 * 3. If our distance varies and we need on-the-fly trajectory building, have a massive array of pre-built trajectories instead.
 * 4. If we can, set RIDICULOUSLY high accel and vel constraints right before a distance sensor.
 */
public class Main {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(700);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(36, 36, Math.PI, Math.PI, 13)
                .setDimensions(15, 17)
                .followTrajectorySequence(drive ->
                        drive.trajectorySequenceBuilder(startPose()) // TODO is this 72-based or 70-based?
                                // FIXME if splines don't work use linear
                                // Preload
                                .setReversed(true)
                                .back(7)
                                .splineTo(new Vector2d(negateIfReversed(-42), -34), reversed() ? 0 : Math.PI)
                                .setReversed(false)
                                .splineTo(new Vector2d(negateIfReversed(-36), -22), Math.PI / 2)
                                .splineTo(new Vector2d(negateIfReversed(-28.6), -4.6), reversed() ? 3 * Math.PI / 4 : Math.PI / 4)
                                // Intake
                                .setReversed(true) // splines always start by moving forward, so we need to call backward forward
                                .splineTo(new Vector2d(negateIfReversed(-46), -12), reversed() ? 0 : Math.PI)
                                .back(17.5)
                                // Delivery
                                .setReversed(false)
                                .forward(22.5)
                                .splineTo(new Vector2d(negateIfReversed(-28.6), -4.6), reversed() ? 3 * Math.PI / 4 : Math.PI / 4)
//                                // Intake
//                                .setReversed(true) // splines always start by moving forward, so we need to call backward forward
//                                .splineTo(new Vector2d(negateIfReversed(-46), -12), reversed() ? 0 : Math.PI)
//                                .back(17.5)
//                                // Delivery
//                                .setReversed(false)
//                                .forward(22.5)
//                                .splineTo(new Vector2d(negateIfReversed(-28.6), -4.6), reversed() ? 3 * Math.PI / 4 : Math.PI / 4)
//                                // Intake
//                                .setReversed(true) // splines always start by moving forward, so we need to call backward forward
//                                .splineTo(new Vector2d(negateIfReversed(-46), -12), reversed() ? 0 : Math.PI)
//                                .back(17.5)
//                                // Delivery
//                                .setReversed(false)
//                                .forward(22.5)
//                                .splineTo(new Vector2d(negateIfReversed(-28.6), -4.6), reversed() ? 3 * Math.PI / 4 : Math.PI / 4)
//                                // Intake
//                                .setReversed(true) // splines always start by moving forward, so we need to call backward forward
//                                .splineTo(new Vector2d(negateIfReversed(-46), -12), reversed() ? 0 : Math.PI)
//                                .back(17.5)
//                                // Delivery
//                                .setReversed(false)
//                                .forward(22.5)
//                                .splineTo(new Vector2d(negateIfReversed(-28.6), -4.6), reversed() ? 3 * Math.PI / 4 : Math.PI / 4)
//                                // Intake
//                                .setReversed(true) // splines always start by moving forward, so we need to call backward forward
//                                .splineTo(new Vector2d(negateIfReversed(-46), -12), reversed() ? 0 : Math.PI)
//                                .back(17.5)
//                                // Delivery
//                                .setReversed(false)
//                                .forward(22.5)
//                                .splineTo(new Vector2d(negateIfReversed(-28.6), -4.6), reversed() ? 3 * Math.PI / 4 : Math.PI / 4)
                                // Parking
                                .setReversed(true) // splines always start by moving forward, so we need to call backward forward
                                .back(4) // TODO make this longer as needed
                                // slower but MAY be more reliable
//                                .splineToLinearHeading(new Pose2d(negateIfReversed(-36), -12, reversed() ? Math.PI : 0), -Math.PI / 2)
//                                .setReversed(false)
//                                .strafeRight(negateIfReversed(24))
//                                .forward(negateIfReversed(24))
                                // faster but MAY be less reliable
                                .splineTo(new Vector2d(negateIfReversed(-36), -22), -Math.PI / 2) // TODO does this work in real life?
                                .splineTo(new Vector2d(negateIfReversed(-42), -34), reversed() ? 0 : Math.PI)
                                .back(18)
                                .setReversed(false)
                                .build()
                );

        meepMeep.setBackground(MeepMeep.Background.FIELD_POWERPLAY_OFFICIAL)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }

    private static Pose2d startPose() {
        return new Pose2d(negateIfReversed(-36), -65.5, -Math.PI / 2);
    }
    private static boolean reversed() { return true; }
    private static double negateIfReversed(double a) { return reversed() ? -a : a; }
}