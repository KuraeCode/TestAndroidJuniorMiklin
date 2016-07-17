package com.runemaster.testjavajuniormiklin;

/**
 * Created by RuneMaster on 17.07.2016.
 */

public class Point {
    private double x;
    private double y;

    @Override
    public String toString() {
        return "Point{" +
                "x="  + x +
                ", y= " + y +
                '}';
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

}
