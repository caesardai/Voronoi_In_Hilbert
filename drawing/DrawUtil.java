package drawing;

import java.awt.Color;
import java.awt.geom.Point2D;

import processing.core.PApplet;

public class DrawUtil {
  final static Color DEFAULT;
  final static Color RED;
  final static Color BLUE;
  final static Color GREEN;
  final static Color PURPLE;
  final static Color BLACK;
  final static Color GREY;
  final static Color WHITE;
  
  static {
	 DEFAULT = new Color(255, 255, 255); 
	 RED = new Color(255, 0, 0); 
	 BLUE = new Color(0, 0, 255); 
	 GREEN = new Color(0, 155, 0); 
	 PURPLE = new Color(250, 12, 255); 
	 BLACK = new Color(0, 0, 0); 
	 GREY = new Color(0, 0, 0, 75); 
	 WHITE = new Color(255, 255, 255); 
  }

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
