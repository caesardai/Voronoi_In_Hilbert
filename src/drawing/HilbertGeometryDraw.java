package drawing;

import geometry.Convex;
import geometry.HilbertGeometry;
import geometry.Util;
import geometry.Voronoi;
import geometry.VoronoiCell;
import geometry.Point3d;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;

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

	/*
	 * Resets the definition of the domain and the geometry
	 */
	public void reset() {
		super.reset();
		this.convexDraw = new ConvexDraw(frame, this.convex);
	}

	/*
	 * Drawing methods.
	 */
	public void draw(boolean mode, int selectedPoint) {
		/* this.frame.stroke(0, 0, 0); */
		this.frame.stroke(255, 0, 0);
		drawConvexHull();
		if (mode) {
			this.frame.stroke(0, 0, 0);
			drawCenterPoints(selectedPoint);
			this.frame.stroke(255, 255, 0);
			// drawHilbertBalls(radius);
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
		if (this.centerPoints.length < 1)
			return;
		this.extremePoints();
		for (int i = 0; i < this.centerPoints.length; i++) {
			Point2D.Double Q = this.centerPoints[i];
			this.drawHilbertBall(Q, radius);
			this.drawHilbertBallPoints(Q, radius);
		}
	}

	private void drawHilbertBallsLinear(int selectedPoint) {
		if (this.centerPoints.length < 1)
			return;
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
		for (int x = min_X; x < max_X; x += 1) {
			for (int y = min_Y; y < max_Y; y += 1) {
				Point2D.Double q = new Point2D.Double(x, y);
				if (this.convex.isInConvex(q)) {
					if (this.distance(p, q) < radius) {
						this.frame.ellipse((float) q.x, (float) q.y, 1, 1);
					}
				}
			}
		}
	}
	
	public void drawHilbertVoronoi(Set<Point2D.Double> pts) {
		DrawUtil.changeColor(this.frame, DrawUtil.DEFAULT);
//		Point2D.Double beginPoint = pts.get(0);
//		Point2D.Double endPoint;
		LinkedList<Point2D.Double> points = new LinkedList<Point2D.Double>();
		for (Point2D.Double p : pts) {
			points.add(p);
		}
		ConvexDraw hilbertBall = new ConvexDraw(this.frame, new Convex(points));
		hilbertBall.drawControlPolygon();
		
		Point2D.Double beginPoint = points.get(0);
		Point2D.Double endPoint;
		for (int i = 1; i < pts.size(); i++) {
			endPoint = points.get(i);
//			System.out.println("line1" + Util.printCoordinate(beginPoint));
			DrawUtil.drawPoint(beginPoint, this.frame);
//			DrawUtil.drawSegment(beginPoint, endPoint, this.frame);
//			System.out.println("line2" + Util.printCoordinate(endPoint));
			beginPoint = endPoint;
			
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
	 * Draws the ray from a centerPoint and stops at a bisector
	 */
	private void drawRays(Point2D.Double p) {
		DrawUtil.changeColor(this.frame, DrawUtil.GREY);
		for (int index = 0; index < this.convex.convexHull.length - 1; index++) {
			Point2D.Double q = this.convex.convexHull[index];
			// LinkedList<Point2D.Double> r = this.frame.voronoi.augustAlgoWeak(q, p);
			DrawUtil.changeColor(this.frame, DrawUtil.WHITE);

			// Draw spoke from hull to hull
			Point2D.Double[] edgePoints = intersectionPoints(p, q);
			DrawUtil.drawSegment(edgePoints[0], edgePoints[1], this.frame);
//			if (Util.samePoints(edgePoints[0], edgePoints[1])) {
//				System.out.println("are the same points");
//			}
//
//			// If the spoke end point is not on bisector
//			if (r.size() == 0) {
//				DrawUtil.drawSegment(edgePoints[0], edgePoints[1], this.frame);
//			}
//
//			// Draw spoke from hull vertex to bisector
//			if (r.size() == 1) {
//				Point2D.Double leftPoint, rightPoint;
//				if (edgePoints[0].x < edgePoints[1].x) {
//					leftPoint = edgePoints[0];
//					rightPoint = edgePoints[1];
//				} else {
//					leftPoint = edgePoints[1];
//					rightPoint = edgePoints[0];
//				}
//
//				// hull vertex - bisector
//				if (p.x < r.get(0).x) {
//					DrawUtil.drawSegment(leftPoint, r.get(0), this.frame);
//				}
//				// hull - bisector
//				else {
//					DrawUtil.drawSegment(rightPoint, r.get(0), this.frame);
////					// Print statement for testing
////					System.out.print("left: " + Util.printCoordinate(leftPoint) + "; ");
////					System.out.print("bisector: " + Util.printCoordinate(r.get(0)) + "; ");
////					System.out.print("site: " + Util.printCoordinate(p) + "; ");
////					System.out.println("right: " + Util.printCoordinate(rightPoint));
//				}
//			}
//			
//			// Draw spoke from bisector to bisector
//			if (r.size() == 2 && r.get(0) != null && r.get(1) != null) {
//				DrawUtil.drawSegment(r.get(0), r.get(1), this.frame);
//			}
//		}

//		DrawUtil.changeColor(this.frame, DrawUtil.DEFAULT);
		}
	}
}

