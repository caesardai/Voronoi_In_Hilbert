package geometry;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class Voronoi {
  /* HG where we compute voronoi diagram */
  private HilbertGeometry geometry;
  /* List of center points for the Voronoi diagram */
  public LinkedList<Point2D.Double> centerPoints = new LinkedList<Point2D.Double>();
  /* for each point inside the domain, give the index of the center points that is closest to it. */
  public HashMap<Point2D.Double, Integer> voronoiPoints = new HashMap<Point2D.Double, Integer>(); 
  
  public Voronoi(HilbertGeometry g) {
    this.geometry = g;
  }
  
  public void reset() {
    this.centerPoints = new LinkedList<Point2D.Double>();
    this.voronoiPoints.clear();
  }
  
  /*
   * Adds a new center point.
   */
  public void addPoint(Point2D.Double p) {
    this.centerPoints.add(p);;
  }
  
  /*
   * Methods that finds p in the list of center Points.
   */
  public int findPoint(Point2D.Double p) {
    for (int i = 0; i < centerPoints.size(); i++) {
      if (Util.closePoints(p, centerPoints.get(i))) {
        return i;
      }
    }
    return -1;
  }
  
  /*
   * Remove point
   */
  public void removePoint(Point2D.Double p) {
    int i = findPoint(p);
    if (i >= 0) {
      this.centerPoints.remove(p);
    }
  }
  
  /*
   * Retrieves center point of index i.
   */
  public Point2D.Double getPoint(int index) {
    return this.centerPoints.get(index);
  }
  
  /*
   * Moves center point of index i.
   */
  public void movePoint(int index, Point2D.Double p) {
    this.centerPoints.remove(index);
    this.centerPoints.add(index,p);
  }
  
  /*
   * Gives the index of the center point nearest to p. 
   */
  public int nearestPoint(Point2D.Double p) {
	if (centerPoints.size() == 0) {return -1;}
    if (centerPoints.size() == 1) {return 0;}
    int nearestPoint = 0;
    double nearestDistance = geometry.distance(p, centerPoints.get(0));
    for (int i = 1; i < centerPoints.size(); i++) {
      double tempDist = geometry.distance(p, centerPoints.get(i));
      if(Double.isInfinite(tempDist)) continue;
      if (tempDist < nearestDistance) {
        nearestDistance = tempDist; 
        nearestPoint = i;
      } else if(tempDist == nearestDistance) { // if p is equidistant from two voronoi sites (may not be shortest distance between all sites in the diagram
    	  // System.out.println("Distance from p from site 1: " + geometry.distance(p, this.centerPoints.get(nearestPoint)));
    	  // System.out.println("Distance from p from site 2: " + geometry.distance(p, this.centerPoints.get(i)) + "\n");
    	  nearestPoint = this.centerPoints.size();
      }
    }
    return nearestPoint;
  }
  
  public void computeVoronoi() {
    this.voronoiPoints.clear();
    this.geometry.extremePoints();
      for (int x = this.geometry.min_X; x < this.geometry.max_X; x+=2) {
        for (int y = this.geometry.min_Y; y < this.geometry.max_Y; y+=2) {
          Point2D.Double p = new Point2D.Double(x, y);
          if (this.geometry.convex.isInConvex(p)) {
            this.voronoiPoints.put(p, nearestPoint(p));
          } 
        }
      }
  }
  
}
