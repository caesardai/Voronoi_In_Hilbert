package geometry;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.LinkedList;

/*
 * Class to handle operations in a Hilbert Geometry.
 */

public class HilbertGeometry {
  final static Point2D.Double ZERO = new Point2D.Double(0, 0);
  final static double step = 0.001;
  /* Convex defining the domain of the geometry */ 
  public Convex convex;
  /* Center points of Hilbert Balls */
  public Point2D.Double[] centerPoints = new Point2D.Double[0];
  /* Radius for the Hilbert Balls */
  protected double[] radiuses = new double[0];
  /* Coordinates of a square encompassing the domain */
  protected int max_X = Integer.MIN_VALUE, max_Y = Integer.MIN_VALUE, min_X = Integer.MAX_VALUE, min_Y = Integer.MAX_VALUE;

  /*
   * Default constructor
   */
  public HilbertGeometry() {}
  
  /* Creates a Hilbert Geometry with a domain encoded in a file. */
  public HilbertGeometry(String filename) {
    if (filename == null) {
      this.convex = new Convex();
    } else {
      this.convex = new Convex(filename); 
    }
  }
  
  /* Resets the definition of the domain and the geometry */ 
  public void reset() {
    this.centerPoints = new Point2D.Double[0];
    this.convex = new Convex();
    max_X = Integer.MIN_VALUE;
    max_Y = Integer.MIN_VALUE;
    min_X = Integer.MAX_VALUE;
    min_Y = Integer.MAX_VALUE;
  }

  /* Gives the distance between points p and q */ 
  public double distance(Point2D.Double p, Point2D.Double q) {
    if (p.x == q.x && p.y == q.y) {
      return 0;
    }
    Point2D.Double[] intersectionPoints = intersectionPoints(p, q);
    Point2D.Double P = intersectionPoints[0];
    Point2D.Double Q = intersectionPoints[1];
    Point2D.Double pP = new Point2D.Double(p.x - P.x, p.y - P.y);
    Point2D.Double qQ = new Point2D.Double(q.x - Q.x, q.y - Q.y);
    Point2D.Double pQ = new Point2D.Double(p.x - Q.x, p.y - Q.y);
    Point2D.Double qP = new Point2D.Double(q.x - P.x, q.y - P.y);
    return Math.abs(0.5 * Math.log(
        pP.distanceSq(ZERO) * qQ.distanceSq(ZERO) /
        (pQ.distanceSq(ZERO) * qP.distanceSq(ZERO))));
  }
  
  /* Returns true if point p is in the domain */
  public boolean isInConvex(Point2D.Double p) {
    return convex.isInConvex(p);
  }
  
  /*
   * Finds control points for the convex domain.
   */
  public int findPoint(Point2D.Double p) {
    return this.convex.findPoint(p);
  }
  
  /* Gives the index of point p in the array centerPoints */
  public int findCenterPoint(Point2D.Double p) {
    for (int i = 0; i < this.centerPoints.length; i++) {
      if (Util.closePoints(p, centerPoints[i])) {
        return i;
      }
    }
    return -1;
  }
  
  /* returns point of index i */
  public Point2D.Double getPoint(int index) {
    return this.convex.getPoint(index);
  }
  
  /* return center point of index index */
  public Point2D.Double getCenterPoint(int index) {
    return this.centerPoints[index];
  }
  
  /* returns the number of balls drawn */
  public int ballCount() {
	  return this.centerPoints.length;
  }
  
  /* modifies point of index i */
  public void movePoint(int index, Point2D.Double q) {
    this.convex.movePoint(index, q);
  }
  
  /* modifies center point of given index */
  public void moveCenterPoint(int index, Point2D.Double p) {
    this.centerPoints[index] = p;
  }
  
  /* removes a ball at index i */
  public void removeBall(int index) {
	  Point2D.Double[] newBalls = new Point2D.Double[this.centerPoints.length - 1];
	  for(int i = 0; i < this.centerPoints.length; i++) {
		  if(i != index)
			  newBalls[i] = this.centerPoints[i];
	  }
	  this.centerPoints = newBalls; 
  }
  
  /* adds a center point */
  public void addCenterPoint(Point2D.Double p, double radius) {
    Point2D.Double[] oldCenterPoints = this.centerPoints.clone();
    double[] oldRadiuses = this.radiuses.clone();
    this.centerPoints = new Point2D.Double[oldCenterPoints.length + 1];
    this.radiuses = new double[oldCenterPoints.length + 1];
    for (int i = 0; i < oldCenterPoints.length; i++) {
      this.centerPoints[i] = oldCenterPoints[i];
      this.radiuses[i] = oldRadiuses[i];
    }
    this.centerPoints[oldCenterPoints.length] = p;
    this.radiuses[oldCenterPoints.length] = radius; 
  }
  
  /*
   * Updating the radius of one Hilbert Ball
   */
  public void updateRadius(int selectedCenterPoint, double newRadius) {
    this.radiuses[selectedCenterPoint] += newRadius;
    this.radiuses[selectedCenterPoint] = Math.max(0, this.radiuses[selectedCenterPoint]);
    System.out.println("Radius updated to: "+ this.radiuses[selectedCenterPoint]);
  }
  
  
  /* Gets the intersection Points of the line defined by point p and q with the convex */
  protected Point2D.Double[] intersectionPoints(Point2D.Double p, Point2D.Double q) {
    Point3d pProj = toHomogeneous(p);
    Point3d qProj = toHomogeneous(q);
    //convex.drawSegment(pProj, qProj);
    Point3d pqLine = pProj.crossProduct(qProj);
    Point3d beginPoint = toHomogeneous(convex.convexHull[0]);
    Point2D.Double[] intersectionPoints = new Point2D.Double[2];
    int count = 0;
    for (int i = 1; i < convex.convexHull.length; i++) {
      Point3d endPoint = toHomogeneous(convex.convexHull[i]);
      Point2D.Double intersect = toCartesian(intersection(pqLine, beginPoint, endPoint));
      if (intersect != null && count < 2 && !Util.contains(intersectionPoints, intersect)) {
        intersectionPoints[count] = intersect;
        count++;
      }
      beginPoint = endPoint;
    }
    return intersectionPoints;
  }
  
  /* Computes intersection point in homogeneous coordinates between line and segment [PQ] */ 
  protected static Point3d intersection(Point3d line, Point3d p, Point3d q) {
    Point3d vectorPQ = new Point3d(p.x - q.x, p.y - q.y, p.z - q.z);
    double denom = line.scalarProduct(vectorPQ);
    if (denom == 0) return new Point3d();
    double t = - line.scalarProduct(q) / denom;
    
    // handle floating point error
    if( t < 0 && t > -1e-15 )
    	t = 0d;
    
    if (t >= 0 && t <= 1) {
      double[] scalarCoeff = {t, 1-t};
      Point3d[] pointCoeff = {p, q};
      return Point3d.linearCombination(pointCoeff, scalarCoeff);
    }
    return new Point3d();
  }
  
  /*
   * Gives a list of points whose convex hull is the hilbert ball of radius r and of center p
   */

  public LinkedList<Point2D.Double> getHilbertBall(Point2D.Double p, double radius) {
    LinkedList<Point2D.Double> points = new LinkedList<Point2D.Double>();
    for (double theta = 0; theta < Math.PI; theta+=step) {
      Point2D.Double p2 = new Point2D.Double(p.x + Math.cos(theta), p.y + Math.sin(theta));
      Point2D.Double[] intersectionPoints = intersectionPoints(p, p2);
      if (intersectionPoints[0] == null || intersectionPoints[1] == null) continue;
      double alpha = (p.x - intersectionPoints[1].x) / (intersectionPoints[0].x - intersectionPoints[1].x);
      double A = alpha / (1 - alpha);
      double beta1 = A / (A + Math.exp(radius));
      double beta2 = A/(A + Math.exp(-radius));
      double[] coeff1 = {beta1, 1 - beta1};
      double[] coeff2 = {beta2, 1 - beta2};
      points.add(linearCombination(intersectionPoints, coeff1));
      points.add(linearCombination(intersectionPoints, coeff2));
    }
    return points;
  }
  
  /*
   * Gives a list of extreme points that belong to the hilbert ball of radius r and center p
   */
  
  public LinkedList<Point2D.Double> getHilbertBallExtremePoints(Point2D.Double p, double radius) {
    LinkedList<Point2D.Double> points = new LinkedList<Point2D.Double>();
    HashSet<Point2D.Double> pointsSet = new HashSet<Point2D.Double>();
    for (int i = 0; i < this.convex.convexHull.length; i ++) {
      Point2D.Double q = this.convex.convexHull[i];
      Point2D.Double[] intersectionPoints = intersectionPoints(p, q);
      if (intersectionPoints[0] == null || intersectionPoints[1] == null) continue;
      double alpha = (p.x - intersectionPoints[1].x) / (intersectionPoints[0].x - intersectionPoints[1].x);
      double A = alpha / (1 - alpha);
      double beta1 = A / (A + Math.exp(radius));
      double beta2 = A/(A + Math.exp(-radius));
      double[] coeff1 = {beta1, 1 - beta1};
      double[] coeff2 = {beta2, 1 - beta2};
      Point2D.Double pA = linearCombination(intersectionPoints, coeff1);
      Point2D.Double pB = linearCombination(intersectionPoints, coeff2);
      pointsSet.add(pA);
      pointsSet.add(pB);
    }
    for (Point2D.Double q : pointsSet) {
      points.add(q);
    }
    return points;
  }
  
  /*
   * Computes the encompassing rectangle of the domain. 
   */
  
  protected void extremePoints() {
    int N = convex.convexHull.length;
    for (int i = 0; i < N; i++) {
      if (max_X < convex.convexHull[i].x) {
        max_X = (int)convex.convexHull[i].x;
      }
      if (max_Y < convex.convexHull[i].y) {
        max_Y = (int)convex.convexHull[i].y;
      }
      if (min_X > convex.convexHull[i].x) {
        min_X = (int)convex.convexHull[i].x;
      }
      if (min_Y > convex.convexHull[i].y) {
        min_Y = (int)convex.convexHull[i].y;
      }
    }
  }
  
  /*
   * Operations on 3D points and 2D points
   */
  
  private static Point2D.Double linearCombination(Point2D.Double[] points, double[] coeff) {
    Point2D.Double result = new Point2D.Double(0, 0);
    for (int i = 0; i < coeff.length; i++) {
      result.x += (points[i].x * coeff[i]);
      result.y += (points[i].y * coeff[i]);
    }
    return result;
  }
  
  public static Point3d toHomogeneous(Point2D.Double p) {
    return new Point3d(p.x, p.y, 1);
  }
  
  public static Point2D.Double toCartesian(Point3d p) {
    if (p.z == 0) return null;
    return new Point2D.Double(p.x / p.z, p.y / p.z);
  }
}
