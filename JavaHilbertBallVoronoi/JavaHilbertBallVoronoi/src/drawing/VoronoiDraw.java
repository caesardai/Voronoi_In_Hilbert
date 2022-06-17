package drawing;

import geometry.Point3d;
import geometry.Voronoi;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;


public class VoronoiDraw {
  private DrawingApplet frame;
  private Voronoi voronoi;
  private LinkedList<Point3d> colors = new LinkedList<Point3d>();
  private final static Random RANDOM_INT = new Random();
  protected boolean hasChanged = true;
  
  public VoronoiDraw(HilbertGeometryDraw g, DrawingApplet frame) {
    this.voronoi = new Voronoi(g);
    this.frame = frame;
  }
  
  public void addPoint(Point2D.Double p) {
    this.voronoi.addPoint(p);
    this.colors.add(new Point3d(RANDOM_INT.nextInt(255), RANDOM_INT.nextInt(255), RANDOM_INT.nextInt(255)));
    this.hasChanged = true;
  }
  
  public int findPoint(Point2D.Double p) {
    return this.voronoi.findPoint(p);
  }

  public Point2D.Double getPoint(int index) {
    return this.voronoi.getPoint(index);
  }
  
  public void removePoint(Point2D.Double p) {
    int i = this.findPoint(p);
    if (i >= 0) {
      this.voronoi.removePoint(p);
      this.colors.remove(i);
    }
    this.hasChanged = true;
  }

  public void movePoint(int index, Point2D.Double p) {
    this.voronoi.movePoint(index, p);
    Point3d colors = this.colors.get(index);
    this.colors.remove(index);
    this.colors.add(colors);
    this.hasChanged = true;
    this.computeVoronoi();
  }
  
  public void reset() {
    this.voronoi.reset();
    this.colors = new LinkedList<Point3d>();
    this.hasChanged = true;
  }
  
  public void computeVoronoi() {
    this.voronoi.computeVoronoi();
  }
  
  public void drawPoints() {
    synchronized(this) {
      int N = this.voronoi.centerPoints.size();
      for (Point2D.Double p : this.voronoi.voronoiPoints.keySet()) {
        Point3d color = this.colors.get(this.voronoi.voronoiPoints.get(p));
        this.frame.fill((float)color.x, (float)color.y, (float)color.z);
        this.frame.stroke((float)color.x, (float)color.y, (float)color.z);
        this.frame.ellipse((float)p.x, (float)p.y, 1, 1);
      }
      
      for (int i = 0; i < N; i++) {
        Point3d color = this.colors.get(i);
        Point2D.Double p = this.voronoi.centerPoints.get(i);
        this.frame.fill((float)color.x, (float)color.y, (float)color.z);
        this.frame.stroke(255, 255, 255);
        this.frame.ellipse((float)p.x, (float)p.y, 10, 10);
      }
    }
    this.frame.fill(0, 0, 0);
    this.frame.stroke(255, 255, 255);
  }
  
  public void colorPoint(Point2D.Double p) {
    int nearestPoint = this.voronoi.nearestPoint(p);
    this.voronoi.voronoiPoints.put(p, nearestPoint);
  }
  
}
