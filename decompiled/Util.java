import processing.core.PApplet;
import java.awt.Color;


public class Util
{
    static final Color DEFAULT;
    static final Color RED;
    static final Color BLUE;
    static final Color GREEN;
    static final Color PURPLE;
    static final double epsilon = 10.0;
    
    static {
        DEFAULT = new Color(255, 255, 255);
        RED = new Color(255, 0, 0);
        BLUE = new Color(0, 0, 255);
        GREEN = new Color(0, 155, 0);
        PURPLE = new Color(250, 12, 255);
    }
    
    public static void changeColor(final PApplet frame, final Color c) {
        frame.fill(c.getRGB());
        frame.stroke(c.getRGB());
    }
    
    public static boolean closePoints(final Point_2 p1, final Point_2 p2) {
        return p1.squareDistance(p2) < 10.0;
    }
    
    public static boolean samePoints(final Point_2 p1, final Point_2 p2) {
        return p1 != null && p1.squareDistance(p2) == 0.0;
    }
    
    public static void drawPoint(final Point_2 p, final PApplet frame) {
        frame.ellipse((float)p.getX(), (float)p.getY(), 5.0f, 5.0f);
    }
    
    public static void drawSegment(final Point_2 p, final Point_2 q, final PApplet frame) {
        frame.line((float)p.getX(), (float)p.getY(), (float)q.getX(), (float)q.getY());
    }
    
    public static boolean contains(final Point_2[] array, final Point_2 point) {
        for (int i = 0; i < array.length; ++i) {
            if (samePoints(array[i], point)) {
                return true;
            }
        }
        return false;
    }
}