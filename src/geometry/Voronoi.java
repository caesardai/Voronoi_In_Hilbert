package geometry;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import drawing.DrawingApplet;
import drawing.DrawUtil;

import micycle.trapmap.Segment;

public class Voronoi {
	/* HG where we compute voronoi diagram */
	private HilbertGeometry geometry;
	/* List of center points for the Voronoi diagram */
	public LinkedList<Point2D.Double> centerPoints = new LinkedList<Point2D.Double>();
	/*
	 * for each point inside the domain, give the index of the center points that is
	 * closest to it.
	 */
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
		this.centerPoints.add(p);
		;
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
		this.centerPoints.add(index, p);
	}

	/*
	 * Gives the index of the center point nearest to p.
	 */
	public int nearestPoint(Point2D.Double p) {
		if (centerPoints.size() == 0) {
			return -1;
		}
		if (centerPoints.size() == 1) {
			return 0;
		}
		int nearestPoint = 0;
		double nearestDistance = geometry.distance(p, centerPoints.get(0));
		for (int i = 1; i < centerPoints.size(); i++) {
			double tempDist = geometry.distance(p, centerPoints.get(i));
			/*
			 * if (Double.isInfinite(tempDist)) continue;
			 */
			if (tempDist < nearestDistance) {
				nearestDistance = tempDist;
				nearestPoint = i;
			}

//			else if (tempDist == nearestDistance) {
//				// if p is equidistant from two voronoi sites (may not be shortest
//				// distance between all sites in the diagram
//				System.out.println(
//						"Distance from p from site 1: " + geometry.distance(p, this.centerPoints.get(nearestPoint))); //
//				System.out.println("Distance from p from site 2: " + geometry.distance(p, //
//						this.centerPoints.get(i)) + "\n");
//				nearestPoint = this.centerPoints.size();
//			}
		}
		return nearestPoint;
	}

	public void computeVoronoi() {
		this.voronoiPoints.clear();
		this.geometry.extremePoints();
		for (int x = this.geometry.min_X; x < this.geometry.max_X; x += 2) {
			for (int y = this.geometry.min_Y; y < this.geometry.max_Y; y += 2) {
				Point2D.Double p = new Point2D.Double(x, y);
				if (this.geometry.convex.isInConvex(p)) {
					this.voronoiPoints.put(p, nearestPoint(p));
				}
			}
		}
	}

	/*
	 * Calculate spoke angle
	 */
	public static double spokeAngle(Point2D.Double site, Point2D.Double hull) {
		double deltaX = site.getX() - hull.getX();
		double deltaY = site.getY() - hull.getY();
		double angle = Math.tan(Math.abs(deltaY) / Math.abs(deltaX));

		// positive horizontal line
		if (hull.y == site.y && hull.x > site.x)
			return 0;
		// negative horizontal line
		else if (hull.y == site.y && hull.x < site.x)
			return Math.PI;
		// positive vertical line
		else if (hull.y > site.y && hull.x == site.x)
			return Math.PI / 2;
		// negative vertical line
		else if (hull.y < site.y && hull.x == site.x)
			return 3 * Math.PI / 2;
		// quadrant 1
		else if (hull.x > site.x && hull.y > site.y)
			return angle;
		// quadtant 2
		else if (hull.x < site.x && hull.y > site.y)
			return Math.PI - angle;
		// quadrant 3
		else if (hull.x < site.x && hull.y < site.y)
			return Math.PI + angle;
		// quadrant 4
		else
			return 2 * Math.PI - angle; // (hull.x > site.x && hull.y < site.y)
	}

	/*
	 * Rotates point p by some angle theta
	 */
	private static Point2D.Double rotationMatrix(Point2D.Double p, double theta) { // deleted static
		Double[][] R = new Double[2][2];
		R[0][0] = Math.cos(theta);
		R[1][1] = R[0][0];
		R[1][0] = Math.sin(theta);
		R[0][1] = -R[1][0];

		return new Point2D.Double(R[0][0] * p.x + R[0][1] * p.y, R[1][0] * p.x + R[1][1] * p.y);
	}

	/*
	 * Return coefficients of all lines that intersect the Voronoi site and rotated
	 * point for site s
	 */
	public static Double[][] thetaRays(Point2D.Double s, int n) {
		// System.out.println("thetaRays starting.");
		// ensure that angular division is a valid number
		if (n < 1)
			return null;

		Point2D.Double p = new Point2D.Double(1, 0);
		Double[][] lines = new Double[n][3];
		for (int k = 0; k < n; k++) {
			// determine rotated point
			Point2D.Double r = rotationMatrix(p, k * Math.toRadians(360 / n));
			r.x += s.x;
			r.y += s.y;
			if(k == 69)
				System.out.println(Util.printCoordinate(r));

			// convert points to homogeneous coordinates and compute line between the two
			// lines
			Point3d hr = HilbertGeometry.toHomogeneous(r);
			Point3d hs = HilbertGeometry.toHomogeneous(s);
			Point3d rsLine = hr.crossProduct(hs);
			lines[k][0] = rsLine.x;
			lines[k][1] = rsLine.y;
			lines[k][2] = rsLine.z;
			// System.out.println("Line between (" + r.x + ", " + r.y + ") and (" + s.x + ",
			// " + s.y + "): " + rsLine.x + "x + " + rsLine.y + "y + " + rsLine.z + " = 0");
		}

		return lines;
	}

	/*
	 * Find the closest point near the traced line end point
	 */
	private Point2D.Double closestPoint(Point2D.Double p) {
		// check if given point is already in the hashmap
		if (this.voronoiPoints.containsKey(p)) {
			// System.out.println("valid point: " + Util.printCoordinate(p) + "\n");
			return p;
		}

		// otherwise, look at surrounding eight points
		Point2D.Double testPoint = new Point2D.Double(p.x - 1, p.y - 1);
		for (int i = 0; i < 9; i++) {
			// don't check center point again
			if (i == 4) {
				testPoint.x += 1;
				continue;
			}

			// check if given point is in hashmap
			if (voronoiPoints.containsKey(testPoint)) {
				// System.out.println("valid point: " + Util.printCoordinate(testPoint) + "\n");
				return testPoint;
			}

			// System.out.println("tested point " + Util.printCoordinate(testPoint));

			// otherwise, change point to the next point
			if (i % 3 != 2)
				testPoint.x += 1;
			else {
				testPoint.x -= 2;
				testPoint.y += 1;
			}
		}
		System.out.println("no points were found");
		return null;
	}

	/*
	 * Detecting bisectors by keeping track of the color
	 */
	public LinkedList<Point2D.Double> thetaRayTrace(DrawingApplet frame, Double[] line, Point2D.Double site) {
		LinkedList<Point2D.Double> bisectorPoints = new LinkedList<Point2D.Double>();
		Point2D.Double p1 = new Point2D.Double((int) site.x, (int) site.y);
		Point2D.Double p2 = new Point2D.Double((int) site.x, (int) site.y);
		boolean traversePositive = true;
		boolean loop = true;

		// find bisector point if it exists
		while (loop) {
			// determine if the line given is a vertical line
			if (line[1] == 0) {
				if (traversePositive)
					p2.y += 1;
				else
					p2.y -= 1;
			}
			// otherwise, line is not a vertical line
			else {
				// determine position p2. check traversePosition to determine which
				// direction to travel across the line
				if (traversePositive)
					p2.x += 1;
				else
					p2.x -= 1;
				p2.y = (int) (-(line[0] * p2.x + line[2]) / line[1]);
			}

			/*
			 * Check if p2 is in the convex body If so, find and compare the color of the
			 * point
			 */
			if (this.geometry.convex.isInConvex(p1) && this.geometry.convex.isInConvex(p2)) {
				// find the closest points p1 and p2
				Point2D.Double closestP1 = this.closestPoint(p1);
				// System.out.println(Util.printCoordinate(p1));
				Point2D.Double closestP2 = this.closestPoint(p2);
				// System.out.println(Util.printCoordinate(p2));

//				// DEBUGGING: display information about missing points
//				if (closestP1 == null || closestP2 == null) {
//					if (closestP1 == null)
//						System.out.println("closestP1: null");
//					if (closestP2 == null)
//						System.out.println("closestP2: null");
//
//					System.out.println("site: " + Util.printCoordinate(site));
//					System.out.println("p1: " + Util.printCoordinate(p1));
//					System.out.println("p2: " + Util.printCoordinate(p2));
//					System.out.println("Line: " + line[0] + "x + " + line[1] + "y + " + line[2] + " = 0");
//					System.out.print("hull points: ");
//					for (Point2D.Double hull : this.geometry.convex.convexHull)
//						System.out.print(Util.printCoordinate(hull) + ", ");
//					System.out.println();
//					this.geometry.extremePoints();
//					System.out.print("minX: " + this.geometry.min_X);
//					System.out.print(", maxX: " + this.geometry.max_X);
//					System.out.print(", minY: " + this.geometry.min_Y);
//					System.out.println(", maxY: " + this.geometry.max_Y);
//				
//					continue;
//				}

				/*
				 * Compare the colors between two closest point; if they are different, then p1
				 * is on the bisector (roughly)
				 */
				if (this.voronoiPoints.get(closestP1) != this.voronoiPoints.get(closestP2)) {
					bisectorPoints.add((Point2D.Double) p1.clone());
					// System.out.println("added point: " + Util.printCoordinate(p1));
					if (traversePositive) {
						traversePositive = false;
						p1.setLocation((int) site.x, (int) site.y);
						p2.setLocation((int) site.x, (int) site.y);
						// Check negative direction
					} else
						loop = false;
				}
			}
			// otherwise, update boolean correctly
			else {
				if (traversePositive) {
					traversePositive = false;
					p1.setLocation((int) site.x, (int) site.y);
					p2.setLocation((int) site.x, (int) site.y);
					// Check negative direction
				} else
					loop = false;
			}

			// update p1
			p1 = (Point2D.Double) p2.clone();
		}
		return bisectorPoints;
	}
	
	/*
	 * Compute discriminant for x-solution for a ugly, but valid intersection point between a conic and a line
	 */
	private static Double computeDiscriminantX(Double A, Double B, Double C, Double D, Double E, Double F, Double a, Double b, Double c) {
		// System.out.println("A: " + A);
		// System.out.println("B: " + B);
		// System.out.println("C: " + C);
		// System.out.println("D: " + D);
		// System.out.println("E: " + E);
		// System.out.println("F: " + F);
		// System.out.println("line: " + Util.printLineEq(new Double[] {a, b, c}));
		Double first = Math.pow(- C * c * b + 2 * B * a * c +  D * Math.pow(b, 2) - E * a * b, 2);
		Double second = A * Math.pow(b, 2) - C * a * b + B * Math.pow(a, 2);
		Double third = B * Math.pow(c, 2) - E * c * b + F * Math.pow(b, 2);
		return first - 4 * second * third; 
	}

	/*
	 * Compute discriminant for x-solution for a ugly, but valid intersection point between a conic and a line
	 */
	private static Double computeDiscriminantY(Double A, Double B, Double C, Double D, Double E, Double F, Double a, Double b, Double c) {
		Double first = Math.pow(2 * A * b * c - C * c * a - D * b * a + E * Math.pow(a, 2), 2);
		Double second = A * Math.pow(b, 2) - C * b * a + B * Math.pow(a, 2);
		Double third = A * Math.pow(c, 2) - D * c * a + F * Math.pow(a, 2);
		return first - 4 * second * third; 
	}
	
	/*
	 * Compute x solution for a ugly, but valid intersection point between a conic and a line
	 */
	private static Double[] computeSolutionX(Double A, Double B, Double C, Double D, Double E, Double F, Double a, Double b, Double c) {
		Double[] solutions = new Double[2];
		Double firstTerm = -(2 * B * a * c + D * Math.pow(b, 2) - E * a * b - C * c * b);
		Double discriminant = Voronoi.computeDiscriminantX(A, B, C, D, E, F, a, b, c);
		Double denominator = 2 * (A * Math.pow(b, 2) - C * a * b + B * Math.pow(a, 2));
		
		solutions[0] = (firstTerm + Math.sqrt(discriminant)) / denominator;
		solutions[1] = (firstTerm - Math.sqrt(discriminant)) / denominator;
		
		System.out.println("x+: " + solutions[0]);
		System.out.println("x-: " + solutions[1]);
		
		return solutions;
	}
	
	/*
	 * Compute x solution for a ugly, but valid intersection point between a conic and a line
	 */
	private static Double[] computeSolutionY(Double A, Double B, Double C, Double D, Double E, Double F, Double a, Double b, Double c) {
		Double[] solutions = new Double[2];
		Double firstTerm = -1 * (2 * A * b * c - C * c * a - D * b * a + E * Math.pow(a, 2));
		Double discriminant = Voronoi.computeDiscriminantY(A, B, C, D, E, F, a, b, c);
		Double denominator = 2 * (A * Math.pow(b, 2) - C * b * a + B * Math.pow(a, 2));
		
		solutions[0] = (firstTerm + Math.sqrt(discriminant)) / denominator;
		solutions[1] = (firstTerm - Math.sqrt(discriminant)) / denominator;
		
		System.out.println("y+: " + solutions[0]);
		System.out.println("y-: " + solutions[1]);

		return solutions;
	}

	/*
	 * Detecting bisectors by keeping track of the color
	 * Source: https://stemandmusic.in/maths/coordinate-geometry/conicLI.php
	 */
	public LinkedList<Point2D.Double> newthetaRayTrace(DrawingApplet frame, Double[] line, Point2D.Double site1, Point2D.Double site2, 
			Segment edge1, Segment edge2, Segment edge3, Segment edge4) {
		
		// list of bisector points to return
		LinkedList<Point2D.Double> bisectorPoints = new LinkedList<Point2D.Double>();
		
		// compute line equations for Segments
		// get endpoints
		Point2D.Double leftPoint1 = new Point2D.Double(edge1.getLeftPoint().x, edge1.getLeftPoint().y);
		Point2D.Double rightPoint1 = new Point2D.Double(edge1.getRightPoint().x, edge1.getRightPoint().y);
		Point2D.Double leftPoint2 = new Point2D.Double(edge2.getLeftPoint().x, edge2.getLeftPoint().y);
		Point2D.Double rightPoint2 = new Point2D.Double(edge2.getRightPoint().x, edge2.getRightPoint().y);
		Point2D.Double leftPoint3 = new Point2D.Double(edge3.getLeftPoint().x, edge3.getLeftPoint().y);
		Point2D.Double rightPoint3 = new Point2D.Double(edge3.getRightPoint().x, edge3.getRightPoint().y);
		Point2D.Double leftPoint4 = new Point2D.Double(edge4.getLeftPoint().x, edge4.getLeftPoint().y);
		Point2D.Double rightPoint4 = new Point2D.Double(edge4.getRightPoint().x, edge4.getRightPoint().y);
		
		// convert to homogenous points
		Point3d lp1 = HilbertGeometry.toHomogeneous(leftPoint1);
		Point3d rp1 = HilbertGeometry.toHomogeneous(rightPoint1);
		Point3d lp2 = HilbertGeometry.toHomogeneous(leftPoint2);
		Point3d rp2 = HilbertGeometry.toHomogeneous(rightPoint2);
		Point3d lp3 = HilbertGeometry.toHomogeneous(leftPoint3);
		Point3d rp3 = HilbertGeometry.toHomogeneous(rightPoint3);
		Point3d lp4 = HilbertGeometry.toHomogeneous(leftPoint4);
		Point3d rp4 = HilbertGeometry.toHomogeneous(rightPoint4);
		
		// get line equations
		Point3d line1 = lp1.crossProduct(rp1); // a_1 x + a_2 y + a_3 = 0
		Point3d line2 = lp2.crossProduct(rp2); // b_1 x + b_2 y + b_3 = 0
		Point3d line3 = lp3.crossProduct(rp3); // c_1 x + c_2 y + c_3 = 0
		Point3d line4 = rp4.crossProduct(lp4); // d_1 x + d_2 y + d_3 = 0
		
		// System.out.println(Util.printLineEq(new Double[] {line1.x, line1.y, line1.z}));
		// System.out.println(Util.printLineEq(new Double[] {line2.x, line2.y, line2.z}));
		// System.out.println(Util.printLineEq(new Double[] {line3.x, line3.y, line3.z}));
		// System.out.println(Util.printLineEq(new Double[] {line4.x, line4.y, line4.z}));
		
		// compute constants
		Double K = (Math.abs(line4.x * site1.x + line4.y * site1.y + line4.z) / Math.abs(line2.x * site1.x + line2.y * site1.y + line2.z))
				* (Math.abs(line3.x * site2.x + line3.y * site2.y + line3.z) / Math.abs(line1.x * site2.x + line1.y * site2.y + line1.z));
		Double s = (double) 1;
		// System.out.println("K: " + K);
		
		// compute coefficients of bisector curve
		Double A = line3.x * line4.x - K * s * (line1.x * line2.x); 
		Double B = line3.y * line4.y - K * s * line1.y * line2.y; 
		Double C = line3.y * line4.x + line3.x * line4.y - K * s * (line1.y * line2.x + line1.x * line2.y); 
		Double D = line3.z * line4.x + line3.x * line4.z - K * s * (line1.z * line2.x + line1.x * line2.z); 
		Double E = line3.z * line4.y + line3.y * line4.z - K * s * (line1.z * line2.y + line1.y * line2.z); 
		Double F = line3.z * line4.z - K * s * (line1.z * line2.z); 
		
		System.out.println("line: " + Util.printLineEq(line));

		// determine intersection point
		Double discriminantX = Voronoi.computeDiscriminantX(A, B, C, D, E, F, line[0], line[1], line[2]);
		Double discriminantY = Voronoi.computeDiscriminantY(A, B, C, D, E, F, line[0], line[1], line[2]);
		
		if(discriminantX >= 0) {
			System.out.println("discriminantX: " + discriminantX);
			Double[] solutionX = Voronoi.computeSolutionX(A, B, C, D, E, F, line[0], line[1], line[2]);
			for(int index = 0; index < solutionX.length; index++) {
				Double y = -(line[0] / line[1]) * solutionX[index] - line[2] / line[1];
				Point2D.Double newPoint = new Point2D.Double(solutionX[index], y);
				if(!bisectorPoints.contains(newPoint))
					bisectorPoints.add(newPoint);
			}
		}
		if(discriminantY >= 0) {
			System.out.println("discriminantY: " + discriminantY);
			Double[] solutionY = Voronoi.computeSolutionY(A, B, C, D, E, F, line[0], line[1], line[2]);
			for(int index = 0; index < solutionY.length; index++) {
				Double x = -(line[1] / line[0]) * solutionY[index] - line[2] / line[0];
				Point2D.Double newPoint = new Point2D.Double(x, solutionY[index]);
				if(!bisectorPoints.contains(newPoint))
					bisectorPoints.add(newPoint);
			}
			
		}

		// return all bisectors found
		return bisectorPoints;
	}

	/*
	 * Spokes stop at Voronoi bisector to visualize effects of multiple spokes on
	 * bisectors
	 */
	public LinkedList<Point2D.Double> augustAlgoWeak(Point2D.Double hull, Point2D.Double site) {
		Double[] line = new Double[3];
		Point3d pProj = HilbertGeometry.toHomogeneous(hull); // hull point
		Point3d qProj = HilbertGeometry.toHomogeneous(site); // site point
		Point3d pqLine = pProj.crossProduct(qProj);

		line[0] = pqLine.x;
		line[1] = pqLine.y;
		line[2] = pqLine.z;

		return thetaRayTrace(null, line, site);
	}

	public static void main(String[] argv) {
		Point2D.Double s1 = new Point2D.Double(23, 20);
		Point2D.Double s2 = new Point2D.Double(28, 12);
		HilbertGeometry g = new HilbertGeometry();
		g.convex = new Convex();
		Voronoi v = new Voronoi(g);
		Point2D.Double p1 = new Point2D.Double(0, 0);
		Point2D.Double p2 = new Point2D.Double(10, 30);
		Point2D.Double p3 = new Point2D.Double(35, 20);
		Point2D.Double p4 = new Point2D.Double(40, 5);
		Segment e1 = new Segment((float) p1.x, (float) p1.y, (float) p2.x, (float) p2.y);
		Segment e2 = new Segment((float) p2.x, (float) p2.y, (float) p3.x, (float) p3.y);
		Segment e3 = new Segment((float) p3.x, (float) p3.y, (float) p4.x, (float) p4.y);
		Segment e4 = new Segment((float) p4.x, (float) p4.y, (float) p1.x, (float) p1.y);

		int n = 200;
		Double[][] lines = Voronoi.thetaRays(s1, n);
		// System.out.println(Util.printLineEq(lines[1]));
		LinkedList<Point2D.Double> intersectionPoints = v.newthetaRayTrace(null, lines[69], s1, s2, e1, e2, e3, e4);
		for(Point2D.Double p : intersectionPoints)
			System.out.println("point: " + Util.printCoordinate(p));
	}	
}
