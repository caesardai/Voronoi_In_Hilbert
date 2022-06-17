package drawing;

import java.awt.Color;
import java.awt.geom.Point2D;

import processing.core.PApplet;

public class DrawUtil {
  final static Color DEFAULT = new Color(255, 255, 255);
  final static Color RED = new Color(255, 0, 0);
  final static Color BLUE = new Color(0, 0, 255);
  final static Color GREEN = new Color(0, 155, 0);
  final static Color PURPLE = new Color(250, 12, 255);
  final static Color BLACK = new Color(0, 0, 0);
  final static Color GREY = new Color(10, 10, 10);
  

  public static void changeColor(PApplet frame, Color c) {
    frame.fill(c.getRGB());
    frame.stroke(c.getRGB());
  }
  
  public static void drawPoint(Point2D.Double p, PApplet frame) {
    frame.ellipse((float)p.getX(), (float)p.getY(), 4, 4);
  }
  
  public static void drawSegment(Point2D.Double p, Point2D.Double q, PApplet frame) {
    frame.line((float)p.getX(), (float)p.getY(), (float)q.getX(), (float)q.getY());
  }
  
}
