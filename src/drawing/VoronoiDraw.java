package drawing;

import geometry.Point3d;
import geometry.Voronoi;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.FileReader;


public class VoronoiDraw {
  private DrawingApplet frame;
  private Voronoi voronoi;
  private LinkedList<Point3d> colors = new LinkedList<Point3d>();
  private final static Random RANDOM_INT = new Random();
  protected boolean hasChanged = false;
  
  public VoronoiDraw(HilbertGeometryDraw g, DrawingApplet frame) {
    this.voronoi = new Voronoi(g);
    this.frame = frame;
  }
  /*
   * Constructs convex from file.
   */
  public VoronoiDraw(HilbertGeometryDraw g, String filename, DrawingApplet frame) {
    this.voronoi = new Voronoi(g);
    this.frame = frame;

    Point2D.Double[] newPoints = load(filename);
    for(int index = 0; index < newPoints.length; index++) {
    	this.addPoint(newPoints[index]);
    	g.addCenterPoint(newPoints[index], 1);
    }
    this.voronoi.computeVoronoi();
  }
  
  public void addPoint(Point2D.Double p) {
    this.voronoi.addPoint(p);
    this.colors.add(new Point3d(RANDOM_INT.nextInt(255), RANDOM_INT.nextInt(255), RANDOM_INT.nextInt(255)));
    this.hasChanged = true;
  }
  
  public int findPoint(Point2D.Double p) {
    return this.voronoi.findPoint(p);
  }
  
  public int numPoints() {
	  return this.voronoi.centerPoints.size();
  }
  
  public LinkedList<Point2D.Double> thetaRayTrace(DrawingApplet frame, Double[][] lines, Point2D.Double site) {
	  return this.voronoi.thetaRayTrace(this.frame, lines, site);
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
      if(N == 0) return;
      for (Point2D.Double p : this.voronoi.voronoiPoints.keySet()) {
    	int siteIndex = this.voronoi.voronoiPoints.get(p);
    	Point3d color;
        // if our selected point is equidistant between two sites
    	if(siteIndex == this.voronoi.centerPoints.size()) {
    		color = new Point3d(0, 0, 0);
    		// System.out.println("Found equidistant points");
			this.frame.fill((float)color.x, (float)color.y, (float)color.z);
			this.frame.stroke((float)color.x, (float)color.y, (float)color.z);
			this.frame.ellipse((float)p.x, (float)p.y, 1, 1);
    	} else {
    		color = this.colors.get(siteIndex);
			this.frame.fill((float)color.x, (float)color.y, (float)color.z);
			this.frame.stroke((float)color.x, (float)color.y, (float)color.z);
			this.frame.ellipse((float)p.x, (float)p.y, 1, 1);
    	}
    	  
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
  
  public void drawRays(Point2D.Double p) {
	  
  }

  
  /* Loads Voronoi points from input file */ 
  public Point2D.Double[] load(String filename) {
    Scanner in;
    try {
      in = new Scanner(new FileReader(filename));
    } catch (FileNotFoundException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
      return null;
    }
    // Retrieving number of control points 
    int N = 0;
    try {N = in.nextInt();} catch(Exception e) {
      System.out.println(e.getCause());
    }
    Point2D.Double[] controlPoints = new Point2D.Double[N];
    
    //Retrieving control points coordinates.
    double X, Y;
    for (int i = 0; i < N; i++) {
      X = in.nextDouble();
      Y = in.nextDouble();
      controlPoints[i] = new Point2D.Double(X, Y);
      }
    
    in.close();
    return controlPoints;
  }
}

