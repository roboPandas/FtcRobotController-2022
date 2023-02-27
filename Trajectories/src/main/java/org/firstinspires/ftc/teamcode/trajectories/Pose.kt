package org.firstinspires.ftc.teamcode.trajectories;

public class Pose {
    public final double x, y;
    public final double heading;

    public Pose(double x, double y, double heading) {
        this.x = x;
        this.y = y;
        this.heading = heading;
    }

    public Pose(Vec vec, double heading) {
        this(vec.x, vec.y, heading);
    }

    public Pose withPos(Vec vec) {
        return withPos(vec.x, vec.y);
    }

    public Pose withPos(double x, double y) {
        return new Pose(x, y, heading);
    }

    public Pose withHeading(double heading) {
        return new Pose(x, y, heading);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")" + " @ " + Math.toDegrees(heading);
    }
}
