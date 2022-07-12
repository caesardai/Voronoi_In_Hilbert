package geometry;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/*
 * Class to handle operations on 2D Convex shapes.
 */
public class Convex {
	// User defined points
	public Point2D.Double[] points;
	// Actual points defining the convex hull.
	public Point2D.Double[] convexHull;

	// Default constructor
	public Convex() {
		points = new Point2D.Double[0];
		convexHull = new Point2D.Double[0];
	}

	// Constructs convex from file.
	public Convex(String filename) {
		points = load(filename);
		this.computeConvexHull();
	}

	/*
	 * Constructs convex from a list of control points.
	 */
	public Convex(LinkedList<Point2D.Double> controlPoints) {
		controlPoints = this.deleteDoubles(controlPoints);
		this.points = new Point2D.Double[controlPoints.size()];
		int i = 0;
		for (Point2D.Double p : controlPoints) {
			this.points[i] = new Point2D.Double(p.x, p.y);
			i++;
		}
		this.convexHull = new Point2D.Double[0];
		this.computeConvexHull();
	}
	
	private LinkedList<Point2D.Double> deleteDoubles(LinkedList<Point2D.Double> controlPoints) {
		LinkedList<Point2D.Double> newControlPoints = new LinkedList<Point2D.Double>();
		for (Point2D.Double p : controlPoints) {
			boolean isAlreadyInside = false;
			for (Point2D.Double q : newControlPoints) {
				if (Util.samePoints(p, q)) {
					isAlreadyInside = true;
				}
				if (isAlreadyInside) {
					break;
				}
			}
			if (!isAlreadyInside) {
				newControlPoints.add(p);
			}
		}
		return newControlPoints;
	}

	/*
	 * Finds point P in convexHull and gives the index.
	 */
	public int findPoint(Point2D.Double p) {
		for (int i = 0; i < this.convexHull.length; i++) {
			Point2D.Double testPoint = this.convexHull[i];
			if (Util.closePoints(testPoint, p)) {
				return i;
			}
		}
		return -1;
	}

	/*
	 * Finds point of index i in convex hull.
	 */
	public Point2D.Double getPoint(int index) {
		if (index >= 0 && index < this.convexHull.length) {
			return this.convexHull[index];
		}
		return null;
	}

	/*
	 * Changes the point of index i.
	 */
	public void movePoint(int index, Point2D.Double q) {
		this.convexHull[index] = q;
		this.points = this.convexHull;
		this.computeConvexHull();
	}

	/*
	 * Adds a control points to change the definition of the convex.
	 */
	public void addPoint(Point2D.Double p) {
		Point2D.Double[] oldControlPoints = this.points.clone();
		this.points = new Point2D.Double[oldControlPoints.length + 1];
		for (int i = 0; i < oldControlPoints.length; i++) {
			this.points[i] = oldControlPoints[i];
		}
		this.points[oldControlPoints.length] = p;
		this.computeConvexHull();
	}

	/*
	 * Removes a control point to change the definition of the convex.
	 */
	public void removePoint(Point2D.Double p) {
		int index = -1;
		for (int i = 0; i < this.convexHull.length; i++) {
			if (Util.closePoints(p, convexHull[i])) {
				index = i;
				break;
			}
		}
		if (index != -1) {
			this.points = new Point2D.Double[convexHull.length - 1];
			int count = 0;
			for (int j = 0; j < this.convexHull.length; j++) {
				if (j != index) {
					this.points[count] = this.convexHull[j];
					count++;
				}
			}
			this.computeConvexHull();
		}
	}

	/*
	 * Decides whether point p lies in the convex.
	 */
	public boolean isInConvex(Point2D.Double p) {
		int N = convexHull.length;
		Point2D.Double a, b;
		int out = 0, in = 0;
		for (int i = 0; i < N; i++) {
			a = convexHull[i % N];
			b = convexHull[(i + 1) % N];
			Point2D.Double ab = new Point2D.Double(b.x - a.x, b.y - a.y);
			Point2D.Double ap = new Point2D.Double(p.x - a.x, p.y - a.y);
			double crossProduct = ab.x * ap.y - ab.y * ap.x;
			if (crossProduct > 0) {
				out++;
			} else if (crossProduct < 0) {
				in++;
			}
			if (out > 0 && in > 0)
				return false;
		}
		return true;
	}

	/*
	 * Computes the convex hull of the set of control points defined by the user.
	 */
	private void computeConvexHull() {
		if (this.points.length < 3)
			return;
		List<Point2D.Double> initialPoints = new LinkedList<Point2D.Double>();
		for (Point2D.Double p : this.points) {
			initialPoints.add(p);
		}
		List<Point2D.Double> convexList = null;
		try {
			convexList = GrahamScan.getConvexHull(initialPoints);
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {

		}
		if (convexList == null) {
			Set<Point2D.Double> sortedPoints = GrahamScan.getSortedPointSet(initialPoints);
			convexList = new LinkedList<Point2D.Double>();
			for (Point2D.Double p : sortedPoints) {
				convexList.add(p);
			}
		}
		this.convexHull = new Point2D.Double[convexList.size()];
		for (int i = 0; i < convexList.size(); i++) {
			convexHull[i] = convexList.get(i);
		}
	}

	/* Loads control points from input file */
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
		try {
			N = in.nextInt();
		} catch (Exception e) {
			System.out.println(e.getCause());
		}
		Point2D.Double[] controlPoints = new Point2D.Double[N];

		// Retrieving control points coordinates.
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

