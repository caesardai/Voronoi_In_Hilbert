package geometry;
import java.awt.geom.Point2D;
import processing.core.PVector;

public class Util {
  final static double epsilon = 10d;
  
  public static boolean closePoints(Point2D.Double p1, Point2D.Double p2) {
    return ((Double) p1.distanceSq(p2) < epsilon);
  }
  
  public static boolean samePoints(Point2D.Double p1, Point2D.Double p2) {
    if (p1 == null) return false;
    return ((Double) p1.distanceSq(p2) < 1);
  }

  public static boolean roughlySamePoints(Point2D.Double p1, Point2D.Double p2, Double error) {
    if (p1 == null || p2 == null) return false;
    return ((Double) p1.distanceSq(p2) <= error);
  }
  
  public static boolean contains(Point2D.Double[] array, Point2D.Double point) {
    for (int i = 0; i < array.length; i++) {
      if (samePoints(array[i], point)) {
        return true;
      }
    }
    return false;
  }
  
  public static PVector toPVector(Point2D.Double p) {
	  return new PVector((float) p.x, (float) p.y);
  }

  public static Point2D.Double toPoint2D(PVector p) {
	  return new Point2D.Double((double) p.x, (double) p.y);
  }

  public static Point2D.Double toPoint2D(KdTree.XYZPoint p) {
	  return new Point2D.Double(p.x, p.y);
  }
  
  public static KdTree.XYZPoint toXYZPoint(Point2D.Double p) {
	  return new KdTree.XYZPoint(p.x, p.y);
  }
  
  public static String printCoordinate(Point2D.Double p) {
	  return "(" + p.x + ", " + p.y + ")";
  }

  public static String printLineEq(Double[] line) {
	  return line[0] + "x + " + line[1] + "y + " + line[2] + " = 0";
  }
}
