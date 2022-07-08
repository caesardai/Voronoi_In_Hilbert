package geometry;
import java.awt.geom.Point2D;

public class Util {
  final static double epsilon = 10d;
  
  public static boolean closePoints(Point2D.Double p1, Point2D.Double p2) {
    return ((Double) p1.distanceSq(p2) < epsilon);
  }
  
  public static boolean samePoints(Point2D.Double p1, Point2D.Double p2) {
    if (p1 == null) return false;
    return ((Double) p1.distanceSq(p2) < 1);
  }
  
  public static boolean contains(Point2D.Double[] array, Point2D.Double point) {
    for (int i = 0; i < array.length; i++) {
      if (samePoints(array[i], point)) {
        return true;
      }
    }
    return false;
  }
  
  public static String printCoordinate(Point2D.Double p) {
	  return "(" + p.x + ", " + p.y + ")";
  }
}
