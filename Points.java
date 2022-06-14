/*
 * REU-CAAR: Hilbert Geometry
 * 06/14/2022
 * Class to store points and any of its assoicated features/labels
 */

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class Points {
    // fields of the Points class; can add more fields if needed
    private double x;
    private double y;
    private Color color;

    // constructors
    public Points(double x, double y) {
        this.x = x;
        this.y = y;
        this.color = null;
    }

    public Points(double x, double y, Color c) {
        this.x = x;
        this.y = y;
        this.color = c;
    }

    // getter and setter methods
    public double getX() { return this.x; }
    public double getY() { return this.y; }
    public Color getColor() { return this.color; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setColor(Color c) { this.color = c; }
}
