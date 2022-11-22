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
                        drive.trajectorySequenceBuilder(new Pose2d(-36, -65.5, DEG_90)) // TODO is this 72-based or 70-based?
                                // FIXME if splines don't work use linear
                                // Preload
                                .forward(53.5) // 2 tiles + 5.5 inches to reach the center
                                .turn(Math.toRadians(-45))
                                .forward(5) // takes us to (-32.46, -8.46)
                                // Intake
                                .splineToLinearHeading(new Pose2d(-46, -12, 0), Math.toRadians(180))
                                .back(10)
                                // Delivery
                                .forward(10)
                                .splineToLinearHeading(new Pose2d(-32.46, -8.46, Math.toRadians(45)), Math.toRadians(90))
                                // Parking
                                .splineToLinearHeading(new Pose2d(-36, -12, 0), Math.toRadians(270))
                                .strafeRight(24)
                                // Either move forward 24, -24, or 0 from vision
                                .build()
                );

        meepMeep.setBackground(MeepMeep.Background.FIELD_POWERPLAY_OFFICIAL)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}