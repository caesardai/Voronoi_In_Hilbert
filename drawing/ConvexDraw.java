package drawing;

import geometry.Convex;

import java.awt.geom.Point2D;


/*
 * Class to handle operations on 2D Convex shapes.
 */

public class ConvexDraw {
  public DrawingApplet frame;
  protected Convex convex;
  
  public ConvexDraw(DrawingApplet frame, Convex convex) {
    this.frame = frame;
    this.convex = convex;
  }
  
  /**
   * Draw the control points
   */
  public void drawControlPoints() {
      for(int i = 0;i < this.convex.points.length;i++)
        DrawUtil.drawPoint(this.convex.points[i], this.frame);
  }
  
  /**
   * Draw the control polygon 
   */
  public void drawControlPolygon() {
  for(Point2D.Double p : this.convex.points) {
    this.frame.ellipse((float)p.getX(), (float)p.getY(), 1, 1);
  }
  }
  
  /*
   * Draws the centerPoints
   */
  public void drawCenterPoints(Point2D.Double[] centerPoints, int selectedPoint) {
    for(int i = 0; i < centerPoints.length; i++) {
      if (i == selectedPoint) {
        DrawUtil.changeColor(this.frame, DrawUtil.RED);
      }
      DrawUtil.drawPoint(centerPoints[i], this.frame);
      if (i == selectedPoint) {
        DrawUtil.changeColor(this.frame, DrawUtil.BLACK);
      }
    }
  }
  
  public void drawConvexHull() {
    if (convex.convexHull.length < 3) return;
    Point2D.Double beginPoint = convex.convexHull[0];
    Point2D.Double endPoint;
    for (int i = 1; i < this.convex.convexHull.length; i++) {
      endPoint = convex.convexHull[i];
      DrawUtil.drawPoint(beginPoint, this.frame);
      DrawUtil.drawSegment(beginPoint, endPoint, this.frame);
      beginPoint = endPoint;
    }
  }
}
