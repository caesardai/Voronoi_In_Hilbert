package geometry;

import java.awt.geom.Point2D;
import processing.core.PVector;
import trapmap.Segment;
import Jama.Matrix;

public class Util {
	final static double epsilon = 10d;

	public static boolean closePoints(Point2D.Double p1, Point2D.Double p2) {
		return ((Double) p1.distanceSq(p2) < epsilon);
	}

	public static boolean samePoints(Point2D.Double p1, Point2D.Double p2) {
		if (p1 == null)
			return false;
		return ((Double) p1.distanceSq(p2) < 1);
	}

	public static boolean roughlySamePoints(Point2D.Double p1, Point2D.Double p2, Double error) {
		if (p1 == null || p2 == null)
			return false;
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

	public static Point3d computeLineEquation(Point2D.Double p1, Point2D.Double p2) {
		Point3d lp = HilbertGeometry.toHomogeneous(p1);
		Point3d rp = HilbertGeometry.toHomogeneous(p2);
		return lp.crossProduct(rp);
	}

	public static Point2D.Double[] intersectionPoints(Point2D.Double p, Point2D.Double q, Convex convex) {
		Point3d pProj = HilbertGeometry.toHomogeneous(p);
		Point3d qProj = HilbertGeometry.toHomogeneous(q);
		// convex.drawSegment(pProj, qProj);
		Point3d pqLine = pProj.crossProduct(qProj);
		Point3d beginPoint = HilbertGeometry.toHomogeneous(convex.convexHull[0]);
		Point2D.Double[] intersectionPoints = new Point2D.Double[2];
		int count = 0;
		for (int i = 1; i < convex.convexHull.length; i++) {
			Point3d endPoint = HilbertGeometry.toHomogeneous(convex.convexHull[i]);
			Point2D.Double intersect = HilbertGeometry
					.toCartesian(HilbertGeometry.intersection(pqLine, beginPoint, endPoint));
			if (intersect != null && count < 2 && !Util.contains(intersectionPoints, intersect)) {
				intersectionPoints[count] = intersect;
				count++;
			}
			beginPoint = endPoint;
		}
		return intersectionPoints;
	}

	public static Point2D.Double[] intersectionPoints(Point3d line, Convex convex) {
		Point3d pqLine = line;
		Point3d beginPoint = HilbertGeometry.toHomogeneous(convex.convexHull[0]);
		Point2D.Double[] intersectionPoints = new Point2D.Double[2];
		int count = 0;
		for (int i = 1; i < convex.convexHull.length; i++) {
			Point3d endPoint = HilbertGeometry.toHomogeneous(convex.convexHull[i]);
			Point2D.Double intersect = HilbertGeometry
					.toCartesian(HilbertGeometry.intersection(pqLine, beginPoint, endPoint));
			if (intersect != null && count < 2 && !Util.contains(intersectionPoints, intersect)) {
				intersectionPoints[count] = intersect;
				count++;
			}
			beginPoint = endPoint;
		}
		return intersectionPoints;
	}

	public static Point2D.Double lineIntersection(Point3d l1, Point3d l2) {
		Point3d intersect = l1.crossProduct(l2);
		if (intersect.z == 0)
			return null;
		else
			return HilbertGeometry.toCartesian(intersect);
	}

	public static Double distanceXYToSegment(Double x3, Double y3, Segment seg) {
		PVector lPoint = seg.getLeftPoint();
		PVector rPoint = seg.getRightPoint();
		Point2D.Double l1 = toPoint2D(lPoint);
		Point2D.Double l2 = toPoint2D(rPoint);
		Double x1 = l1.x;
		Double y1 = l1.y;
		Double x2 = l2.x;
		Double y2 = l2.y;

		Double px = x2 - x1;
		Double py = y2 - y1;
		Double temp = (px * px) + (py * py);
		Double u = ((x3 - x1) * px + (y3 - y1) * py) / (temp);
		if (u > 1) {
			u = 1d;
		}
		if (u < 0) {
			u = 0d;
		}
		Double x = x1 + u * px;
		Double y = y1 + u * py;

		Double dx = x - x3;
		Double dy = y - y3;
		Double dist = Math.sqrt(dx * dx + dy * dy);
		
		return dist;
	}

	public Double distancePointToSegment(Point2D.Double pt, Segment seg) {
		PVector lPoint = seg.getLeftPoint();
		PVector rPoint = seg.getRightPoint();
		Point2D.Double l1 = toPoint2D(lPoint);
		Point2D.Double l2 = toPoint2D(rPoint);

		return Math.abs(
				(l2.getX() - l1.getX()) * (l1.getY() - pt.getY()) - (l1.getX() - pt.getX()) * (l2.getY() - l1.getY()))
				/ Math.sqrt(Math.pow(l2.getX() - l1.getX(), 2) + Math.pow(l2.getY() - l1.getY(), 2));
	}

	public Double distancePointToLine(Point2D.Double pt, Point3d line) {
		Double x = pt.x;
		Double y = pt.y;
		Double a = line.x;
		Double b = line.y;
		Double c = line.z;

		return Math.abs(((a * x + b * y + c)) / (Math.sqrt(a * a + b * b)));
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

	public static Segment pointsToSeg(Point2D.Double p1, Point2D.Double p2) {
		PVector pv1 = toPVector(p1);
		PVector pv2 = toPVector(p2);
		Segment s = new Segment(pv1, pv2);
		return s;
	}
	
	/**
	 * Converts matrix m that corresponds the homogeneous coordinate p back into an
	 * Point object
	 * 
	 * @param m the matrix m intended to be converted into homogeneous coordinate
	 * @return the homogeneous coordinate p that corresponds to m
	 */
	public static Point2D.Double homogenousToPoint2D(Matrix m) {
		if (m.getRowDimension() != 3 && m.getColumnDimension() != 1)
			return null;

		if (m.get(2, 0) == 0)
			return new Point2D.Double(Integer.MAX_VALUE, Integer.MAX_VALUE);
		else
			return new Point2D.Double(m.get(0, 0) / m.get(2, 0), m.get(1, 0) / m.get(2, 0));
	}

	/**
	 * Converts matrix m that corresponds the coordinate p back into an Point object
	 * 
	 * @param m the matrix m intended to be converted into standard coordinate
	 * @return the coordinate p that corresponds to m
	 */
	public static Point2D.Double toPoint2D(Matrix m) {
		if (m.getRowDimension() != 2 && m.getColumnDimension() != 1)
			return null;
		else
			return new Point2D.Double(m.get(0, 0), m.get(1, 0));
	}

	/**
	 * Converts point p into a matrix that corresponds the homogeneous coordinate
	 * corresponds to p
	 * 
	 * @param p, the intended Point2D.Double point to being converted
	 * @return a (n+1) x 1 matrix that corresponds the homogeneous coordinates of p
	 */
	public static Matrix toMatrix(Point2D.Double p) {
		return new Matrix(new double[][] { { p.x }, { p.y }, { 1 } });
	}

	public static void addToPoint(Point2D.Double returnPoint, Point2D.Double addVector) {
		returnPoint.x += addVector.x;
		returnPoint.y += addVector.y;
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
