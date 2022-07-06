package drawing;

import geometry.Convex;
import geometry.HilbertGeometry;
import geometry.Util;
import geometry.Voronoi;
import geometry.Point3d;

import java.awt.geom.Point2D;
import java.util.LinkedList;
/*
 * Class to handle operations in a Hilbert Geometry.
 */

public class HilbertGeometryDraw extends HilbertGeometry {
  private DrawingApplet frame;
  protected ConvexDraw convexDraw;
  
  public HilbertGeometryDraw(DrawingApplet frame, String filename) {
    super(filename);
    this.frame = frame;
    this.convexDraw = new ConvexDraw(frame, this.convex);
  }
  
  /* Resets the definition of the domain and the geometry */ 
  public void reset() {
    super.reset();
    this.convexDraw = new ConvexDraw(frame, this.convex);
  }
  
  /*
   * Drawing methods.
   */
  
  public void draw(boolean mode, int selectedPoint) {
   /* this.frame.stroke(0, 0, 0);*/
    this.frame.stroke(255,0, 0);
    drawConvexHull();
    if (mode) {
      this.frame.stroke(0, 0, 0);
      drawCenterPoints(selectedPoint);
      this.frame.stroke(255, 255, 0);
      //drawHilbertBalls(radius);
      drawHilbertBallsLinear(selectedPoint);
    }
    this.frame.stroke(0, 0, 0);
  }
  
  private void drawConvexHull() {
    this.convexDraw.drawConvexHull();
  }
  
  private void drawCenterPoints(int selectedPoint) {
    this.convexDraw.drawCenterPoints(this.centerPoints, selectedPoint);
  }
  
  private void drawHilbertBalls(double radius) {
    if (this.centerPoints.length < 1) return;
    this.extremePoints();
    for (int i = 0; i < this.centerPoints.length; i++) {
      Point2D.Double Q = this.centerPoints[i];
      this.drawHilbertBall(Q, radius);
      this.drawHilbertBallPoints(Q, radius);
    }
  }
  
  private void drawHilbertBallsLinear(int selectedPoint) {
    if (this.centerPoints.length < 1) return;
    this.extremePoints();
    for (int i = 0; i < this.centerPoints.length; i++) {
      Point2D.Double Q = this.centerPoints[i];
      // this.drawHilbertBallLinear(Q, this.radiuses[i]);
      // this.drawHilbertBallPoints(Q, this.radiuses[i]);
      if (i == selectedPoint) {
        this.drawRays(Q);
      }
    }
  }
  
  /*
   * Draws the HilberBall of center p with brute force.
   */
  
  private void bruteForceHilbertBall(Point2D.Double p, double radius) {
    for (int x = min_X; x < max_X; x+=1) {
      for (int y = min_Y; y < max_Y; y+=1) {
        Point2D.Double q = new Point2D.Double(x, y);
        if (this.convex.isInConvex(q)) {
          if (this.distance(p, q) < radius) {
            this.frame.ellipse((float)q.x, (float)q.y, 1, 1);
          }
        } 
      }
    }
  }
  
  /*
   * Draws the HilbertBall of center p.
   */
  private void drawHilbertBall(Point2D.Double p, double radius) {
    DrawUtil.changeColor(this.frame, DrawUtil.DEFAULT);
    LinkedList<Point2D.Double> points = this.getHilbertBall(p, radius);
    ConvexDraw hilbertBall = new ConvexDraw(this.frame, new Convex(points));
    hilbertBall.drawControlPolygon();
  }
  
  /*
   * Draws the HilbertBall of center p in linear time.
   */
  private void drawHilbertBallLinear(Point2D.Double p, double radius) {
    DrawUtil.changeColor(this.frame, DrawUtil.DEFAULT);
    LinkedList<Point2D.Double> points = this.getHilbertBallExtremePoints(p, radius);
    ConvexDraw hilbertBall = new ConvexDraw(this.frame, new Convex(points));
    hilbertBall.drawConvexHull();
  }
  
  /*
   * Draws the extreme points of the Hilbert ball
   */
  private void drawHilbertBallPoints(Point2D.Double p, double radius) {
    DrawUtil.changeColor(this.frame, DrawUtil.PURPLE);
    LinkedList<Point2D.Double> extremePoints = this.getHilbertBallExtremePoints(p, radius);
    for (Point2D.Double q : extremePoints) {
      DrawUtil.drawPoint(q, this.frame);
    }
    DrawUtil.changeColor(this.frame, DrawUtil.DEFAULT);
  }
  
  /*
   * Draws the ray from a centerPoint
   */
  private void drawRays(Point2D.Double p) {
		DrawUtil.changeColor(this.frame, DrawUtil.GREY);
		for (Point2D.Double q : this.convex.convexHull) {
			
			int n = 10;
			Double[][] results = Voronoi.thetaRays(q, n);
			if(this.frame.voronoi == null)
				System.out.println("voronoi draw is null");
			// loop through all voronoi sites
			Double[][] bisectorPoints = this.frame.voronoi.thetaRayTrace(results);
			
			Double theta = Voronoi.spokeAngle(p, q);
			int unit = (int) (theta / (2 * Math.PI / n));
			
			// find intersection between lines PQ and bp[unit]-bp[unit+1]
			Point3d pqLine = this.toHomogeneous(p).crossProduct(this.toHomogeneous(q));
			Point2D.Double bp1 = new Point2D.Double(bisectorPoints[unit][0], bisectorPoints[unit][1]);
			Point2D.Double bp2 = new Point2D.Double(bisectorPoints[unit+1][0], bisectorPoints[unit+1][1]);
			Point3d bisectorLine = this.toHomogeneous(bp1).crossProduct(this.toHomogeneous(bp2));
			
			Point2D.Double intersection = this.toCartesian(pqLine.crossProduct(bisectorLine));
			
			// draw line between hull and intersection point
			DrawUtil.drawSegment(q, intersection, this.frame);
			
			
			
			/*
			Point2D.Double[] r = this.intersectionPoints(p, q);
			if (r.length == 2 && r[0] != null && r[1] != null) {
				if (Util.samePoints(r[0], q)) {
					DrawUtil.drawSegment(q, r[1], this.frame);
				} else {
					DrawUtil.drawSegment(q, r[0], this.frame);
				}
			}
			*/
		}
		DrawUtil.changeColor(this.frame, DrawUtil.DEFAULT);
	}
}
