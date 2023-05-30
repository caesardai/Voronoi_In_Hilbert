package geometry;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import Jama.Matrix;

import drawing.DrawingApplet;
import drawing.DrawUtil;

import trapmap.TrapMap;
import trapmap.Trapezoid;
import trapmap.Segment;
import processing.core.PShape;
import processing.core.PVector;

public class Voronoi {
	/* HG where we compute voronoi diagram */
	protected HilbertGeometry geometry;
	protected int max_X = Integer.MIN_VALUE, max_Y = Integer.MIN_VALUE, min_X = Integer.MAX_VALUE, min_Y = Integer.MAX_VALUE;
	/* List of center points for the Voronoi diagram */
	public LinkedList<Point2D.Double> centerPoints = new LinkedList<Point2D.Double>();
	/*
	 * for each point inside the domain, give the index of the center points that is
	 * closest to it.
	 */
	public HashMap<Point2D.Double, Integer> voronoiPoints = new HashMap<Point2D.Double, Integer>();
//	public HashMap<Point2D.Double, Integer> hilbertVoronoiPoints = new HashMap<Point2D.Double, Integer>();

	/* Variable storing all the segment that's inserted */
	public ArrayList<Segment> allSegments = new ArrayList<Segment>();
	/* Trapezoidal map of all Voronoi cells */
	public TrapMap voronoiCells = null;

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
	
	public Set<Point2D.Double> computeHilbertVoronoi(Point2D.Double p1, Point2D.Double p2) {
		this.voronoiPoints.clear();
		Set<Point2D.Double> pts = new HashSet<Point2D.Double>();
		ArrayList<VoronoiCell> cells = realAugusteAlgo(p1, p2);
		for (VoronoiCell c : cells) {
			for (Point2D.Double v : c.getVertices()) {
				if (this.geometry.convex.isOnConvexBoundary(v)) {
					continue;
				} else {
					pts.add(v);
				}
			}
		}
		return pts;
	}
//	ArrayList<Segment> cellSegs = new ArrayList<Segment>();
	
//	Point2D.Double endPoint;
//	Point2D.Double beginPoint = c.getVertexIndex(0);
//	for (int i = 1; i < c.getVertices().size(); i++) {
//		endPoint = c.getVertexIndex(i);
//		cellSegs.add(Util.pointsToSeg(beginPoint, endPoint));
//	}
//	Convex con = new Convex();
//	con.spokeSegments = cellSegs;
//	
//	this.hilbertExtremePoints(con);
//	for (int x = this.geometry.min_X; x < this.geometry.max_X; x += 2) {
//		for (int y = this.geometry.min_Y; y < this.geometry.max_Y; y += 2) {
//			Point2D.Double p = new Point2D.Double(x, y);
//			if (this.geometry.convex.isInConvex(p)) {
//				this.voronoiPoints.put(p, nearestPoint(p));
//			}
//		}
//	}
	
	protected void hilbertExtremePoints(Convex c) {
		int N = c.convexHull.length;
	    for (int i = 0; i < N; i++) {
	      if (max_X < c.convexHull[i].x) {
	        max_X = (int)c.convexHull[i].x;
	      }
	      if (max_Y < c.convexHull[i].y) {
	        max_Y = (int)c.convexHull[i].y;
	      }
	      if (min_X > c.convexHull[i].x) {
	        min_X = (int)c.convexHull[i].x;
	      }
	      if (min_Y > c.convexHull[i].y) {
	        min_Y = (int)c.convexHull[i].y;
	      }
	    }
	}

	/*
	 * Calculate spoke angle
	 */
	public static double spokeAngle(Point2D.Double site, Point2D.Double hull) {
		double deltaX = hull.getX() - site.getX();
		double deltaY = hull.getY() - site.getY();
		double angle = Math.atan(deltaY / deltaX);

		if (deltaX < 0) {
			angle += Math.PI;
		} else if (deltaY < 0) {
			angle = 2 * Math.PI + angle;
		}

		return angle;

//		// positive horizontal line
//		if (hull.y == site.y && hull.x > site.x)
//			return 0;
//		// negative horizontal line
//		else if (hull.y == site.y && hull.x < site.x)
//			return Math.PI;
//		// positive vertical line
//		else if (hull.y > site.y && hull.x == site.x)
//			return Math.PI / 2;
//		// negative vertical line
//		else if (hull.y < site.y && hull.x == site.x)
//			return 3 * Math.PI / 2;
//		// quadrant 1
//		else if (hull.x > site.x && hull.y > site.y)
//			return angle;
//		// Quadrant 2
//		else if (hull.x < site.x && hull.y > site.y)
//			return Math.PI - angle;
//		// quadrant 3
//		else if (hull.x < site.x && hull.y < site.y)
//			return Math.PI + angle;
//		// quadrant 4
//		else
//			return 2 * Math.PI - angle; // (hull.x > site.x && hull.y < site.y)
	}

	/*
	 * Rotates point p by some angle theta
	 */
	public static Point2D.Double rotationMatrix(Point2D.Double p, double theta) { // deleted static
		Double[][] R = new Double[2][2];
		R[0][0] = Math.cos(theta);
		R[1][1] = R[0][0];
		R[1][0] = Math.sin(theta);
		R[0][1] = -R[1][0];

		return new Point2D.Double(R[0][0] * p.x + R[0][1] * p.y, R[1][0] * p.x + R[1][1] * p.y);
	}

	/**
	 * 
	 * @param center
	 * @param theta
	 * @return
	 */
	public static Point2D.Double rotatePoint(Point2D.Double center, double theta) {
		Matrix v = new Matrix(new double[] { 1, 0 }, 2);
		Matrix rotation = new Matrix(
				new double[] { Math.cos(theta), Math.sin(theta), -Math.sin(theta), Math.cos(theta) }, 2);
		Matrix vRotated = rotation.times(v);
		return new Point2D.Double(vRotated.get(0, 0) + center.x, vRotated.get(1, 0) + center.y);
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
			Double theta = k * (2 * Math.PI / n);
			Point2D.Double r = rotationMatrix(p, theta);
			r.x += s.x;
			r.y += s.y;
			/*
			 * if(k == 1) { System.out.println(Util.printCoordinate(r));
			 * System.out.println(Util.printCoordinate(new Point2D.Double(s.x +
			 * Math.cos(theta), s.y + Math.sin(theta)))); System.out.println(theta); }
			 */

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
	 * Return coefficients of all lines that intersect the Voronoi site and rotated
	 * point for site s
	 */
	public static Double[][] thetaRays(Point2D.Double s, Point2D.Double startPoint, Point2D.Double endPoint, int n) {
		// ensure that angular division is a valid number
		if (n < 1)
			return null;

		// compute the starting and ending angle
		Double theta1, theta2;
		if (s.y == startPoint.y)
			theta1 = (s.y < startPoint.y) ? Math.PI / 2 : 3 * Math.PI / 2;
		if (s.y == endPoint.y)
			theta2 = (s.y < endPoint.y) ? Math.PI / 2 : 3 * Math.PI / 2;
		theta1 = Voronoi.spokeAngle(s, startPoint);
		theta2 = Voronoi.spokeAngle(s, endPoint);

		// ensure that angles are ordered correctly
		if (theta2 < theta1) {
			Double temp = theta1;
			theta1 = theta2;
			theta2 = temp;
		}

		Point2D.Double p = new Point2D.Double(1, 0);
		Double[][] lines = new Double[n + 1][3];
		for (int k = 0; k <= n; k++) {
			// determine rotated point
			Double theta = theta1 + k * ((theta2 - theta1) / n);
			Point2D.Double r = rotationMatrix(p, theta);
			r.x += s.x;
			r.y += s.y;

			// convert points to homogeneous coordinates and compute line between the two
			// lines
			Point3d hr = HilbertGeometry.toHomogeneous(r);
			Point3d hs = HilbertGeometry.toHomogeneous(s);
			Point3d rsLine = hr.crossProduct(hs);
			lines[k][0] = rsLine.x;
			lines[k][1] = rsLine.y;
			lines[k][2] = rsLine.z;
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
		// System.out.println("no points were found");
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
	 * Determines all intersection points between the bisector and all lines passed
	 * through the method
	 */
	public static LinkedList<Point2D.Double> newthetaRayTrace(Bisector b, Convex c, Double[][] lines) {
		// list of intersection points
		LinkedList<Point2D.Double> intersectionPoints = new LinkedList<Point2D.Double>();

		// for each line in the list, compute the intersection points
//		for(Double[] l : lines) {
//			intersectionPoints.addAll(b.intersectionPointsWithLine(c, l));
//		}

		return intersectionPoints;
	}

	/**
	 * Determines the equidistance point between site1 and site2 with respect to the
	 * Hilbert Metric
	 * 
	 * @param site1 the first site
	 * @param site2 the second site
	 * @param spoke the spoke to trace and find the equidistant point. assume that
	 *              this spoke is constructed from site1 and convex hull vertex
	 * @return if an equidistant point is found, this returns the equidistant point.
	 *         otherwise, this returns null
	 */
	public Point2D.Double findEquiDistancePoint(Point2D.Double site1, Point2D.Double site2, Point3d spoke) {
		Convex c = this.geometry.convex;

		// determine which edge the spoke intersects with
		Point2D.Double intersectionPoint = null;
		Point2D.Double[] hullVertices = c.convexHull;
		List<Point2D.Double> hvList = Arrays.asList(hullVertices);
		for (int index = 0; index < hullVertices.length; index++) {
			// compute line
			Point3d e1 = HilbertGeometry.toHomogeneous(hullVertices[index]);
			Point3d e2 = HilbertGeometry.toHomogeneous(hullVertices[(index + 1) % hullVertices.length]);
			Point3d edge = e1.crossProduct(e2);
			Point2D.Double intersection = HilbertGeometry.toCartesian(spoke.crossProduct(edge));
			if (c.isOnConvexBoundary(intersection)) {
				// if spoke is not vertical
				if (Math.abs(spoke.y) > 1e-8) {
					if (site1.x < site2.x && intersection.x > site1.x) {
						intersectionPoint = intersection;
						break;
					} else if (site1.x > site2.x && intersection.x < site1.x) {
						intersectionPoint = intersection;
						break;
					}
				} else {
					if (site1.y < site2.y && intersection.y > site1.y) {
						intersectionPoint = intersection;
						break;
					} else if (site1.y > site2.y && intersection.y > site1.y) {
						intersectionPoint = intersection;
						break;
					}
				}
			}
		}

		// break up the spoke into very small intervals by looking at the segment
		// determine if the spoke is vertical or not
		Double jump = 0d;
		int divisions = 1000;
		Point2D.Double first = site1;
		Point2D.Double second = intersectionPoint;
		boolean useX = true;
		if (Math.abs(spoke.y) > 1e-8) { // horizontal
			if (first.x > second.x) {
				first = intersectionPoint;
				second = site1;
			}
			jump = (second.x - first.x) / divisions;
		} else { // vertical
			if (first.y > second.y) {
				first = intersectionPoint;
				second = site1;
			}
			jump = (second.y - first.y) / divisions;
			useX = false;
		}

		// begin looking for an equidistant point
		ArrayList<Point2D.Double> allPoints = new ArrayList<Point2D.Double>();
		ArrayList<Double> differences = new ArrayList<Double>();
		for (int intervalCount = 0; intervalCount < divisions; intervalCount++) {
			double x = 0d;
			double y = 0d;
			if (useX) {
				x = first.x + jump * intervalCount;
				y = (-spoke.x / spoke.y) * x - spoke.z / spoke.y;
			} else {
				y = first.y + jump * intervalCount;
				x = (-spoke.y / spoke.x) * y - spoke.z / spoke.x;
			}

			Point2D.Double curr = new Point2D.Double(x, y);
			allPoints.add(curr);

			// look at the distance between site1 and site2 with curr
			// if their distance is within epsilon, then the point is equidistant to the two
			// sites. return that point
			double epsilon = 1e-3;
			double diff = Math.abs(this.geometry.distance(site1, curr) - this.geometry.distance(site2, curr));
			differences.add(diff);
			if (diff <= epsilon)
				return curr;
		}

		// if no point was found, then return null
		Double smallest = Collections.min(differences);
		Point2D.Double point = null;
		int count = differences.indexOf(smallest);
		return null;
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

	/*
	 * REAL Auguste Algorithm Pseudo Code
	 */

	/*
	 * realAugusteAlgo(site1, site2)
	 * 
	 * 
	 * 
	 * findEquidistantPoint(site1, site2) {
	 * 
	 * equidistancePoint[] = new[];
	 * 
	 * find line L(h_[0], site1) trace line {
	 * 
	 * equidistancePoint = Bisector(site1, site2, edge1, edge2, edge3, edge4,
	 * leftEndPoint, rightEndPoint); } find centerPoint sectors = constructSectors()
	 * 
	 * for each sector { check if the equidistancePoint is in sector }
	 * 
	 * determineEdges(); draw line between h[0] and equiDistancePoint;
	 * 
	 * for each hullVertex in h[1] to h[n] { equidistancePoint[i] = Bisector(site1,
	 * site2, edge1, edge2, edge3, edge4, leftEndPoint, rightEndPoint); }
	 *
	 * find the segment on the bisector/sector boundary intersection lays on in the
	 * sector boundary find the sectors that share the previous segments as one of
	 * their edges
	 * 
	 * return equiDistancePoint[] }
	 */

	private Bisector computeBisectorInSector(Sector sec) {
		// finding sector that contains equidistant point
		Segment e1 = sec.getEdge1();
		Segment e2 = sec.getEdge2();
		Segment e3 = sec.getEdge3();
		Segment e4 = sec.getEdge4();

		// compute Bisector
		Bisector b = new Bisector(sec.site1, sec.site2, e1, e2, e3, e4);

		if (b.returnA() == 0.0) {
			b.setAValue(0.0000001);
		}
		return b;
	}

	private Segment[] bisectorSectorIntersection(Bisector bi, Sector sec, Point2D.Double entryPoint) {
		Segment[] intersectSeg = new Segment[2];

		// find all possible intersection points and the segments they lay on
		LinkedList<Point2D.Double> allIntersectionPoints = new LinkedList<Point2D.Double>();
		LinkedList<Segment> allSegments = new LinkedList<Segment>();
		// array of Voronoi boundary segments that intersects bisector
		Point2D.Double[] secVertices = sec.sector.convexHull;
		LinkedList<Point2D.Double> secBisecIntersect = null;
		for (int i = 0; i < secVertices.length - 1; i++) {
			Point2D.Double v1 = secVertices[i];
			Point2D.Double v2 = secVertices[i + 1];
			Point3d linev1v2 = Util.computeLineEquation(v1, v2);
			secBisecIntersect = bi.intersectionPointsWithLine(this.geometry.convex, linev1v2);
			for (Point2D.Double p : secBisecIntersect) {
				// check if the point is in the sector
				if (sec.isInSector(p)) {
					// check if the point is not the entryPoint
					if (!Util.roughlySamePoints(p, entryPoint, 1e-4)) {
						// segments that conic intersect with
						allIntersectionPoints.add(p);
						allSegments.add(Util.pointsToSeg(v1, v2));
					} else { // found the entryPoint, store this segment
						intersectSeg[0] = Util.pointsToSeg(v1, v2);
					}
				}
			}
		}

		// if this the first bisector computed, then do the following
		if (entryPoint == null) {
			// assume that the first two intersection points are the only two intersection
			// points on this sector
			bi.setEndPoints(allIntersectionPoints.get(0));
			bi.setEndPoints(allIntersectionPoints.get(1));
			intersectSeg[0] = allSegments.get(0);
			intersectSeg[1] = allSegments.get(1);
			return intersectSeg;
		} else {
			// loop through all intersection points and find the point closest to entryPoint
			ArrayList<Double> distances = new ArrayList<Double>(allIntersectionPoints.size());
			for (int index = 0; index < allIntersectionPoints.size(); index++)
				distances.add(entryPoint.distance(allIntersectionPoints.get(index)));

			// get arg min of distances
			double minDistances = Collections.min(distances);
			int minDistPointIndex = distances.indexOf(minDistances);
			bi.setEndPoints(allIntersectionPoints.get(minDistPointIndex));

			// return results
			intersectSeg[1] = allSegments.get(minDistPointIndex);
			return intersectSeg;
		}
	}

	public ArrayList<Segment> approximateBisector(Bisector b) {
		ArrayList<Point2D.Double> voronoiCellVertices = new ArrayList<Point2D.Double>();
		ArrayList<Segment> voronoiCellSegment = new ArrayList<Segment>();
		Point2D.Double leftPoint = b.getLeftEndPoint();
		Point2D.Double rightPoint = b.getRightEndPoint();
		Double leftX = leftPoint.getX();
		Double rightX = rightPoint.getX();
		Segment line = Util.pointsToSeg(leftPoint, rightPoint);

		Double incrementX;
		if (leftX < rightX) {
			incrementX = (rightX - leftX) / 5;
		} else {
			incrementX = (leftX - rightX) / 5;
		}

		Double increment = leftX;
		// Keep approximating bisector until we're out of range
		while (increment <= rightX + .0000001) {
			if (Math.abs(increment - rightX) <= .0000001) {
				increment = rightX;
			}
			// assign value
			Double[] potentialYVal = b.computeX(increment);
			Double[] pointToLineDistance = new Double[potentialYVal.length];
			for (int i = 0; i < potentialYVal.length; i++) {
				pointToLineDistance[i] = Util.distanceXYToSegment(increment, potentialYVal[i], line);
			}
			// if there is only one Y value
			if (pointToLineDistance.length == 1) {
				Point2D.Double pt = new Point2D.Double(increment, potentialYVal[0]);
				voronoiCellVertices.add(pt);
			} else if (pointToLineDistance[0] < pointToLineDistance[1]) {
				Point2D.Double pt = new Point2D.Double(increment, potentialYVal[0]);
				voronoiCellVertices.add(pt);
			} else if (pointToLineDistance[1] < pointToLineDistance[0]) {
				Point2D.Double pt = new Point2D.Double(increment, potentialYVal[1]);
				voronoiCellVertices.add(pt);
			} else {
				Point2D.Double pt = new Point2D.Double(increment, potentialYVal[0]);
				voronoiCellVertices.add(pt);
			}
			if (increment >= rightX) {
				break;
			}
			increment += incrementX;
		}

		// Construct segment with voronoi cell vertices
		for (int i = 0; i < voronoiCellVertices.size() - 1; i++) {
			Point2D.Double p1 = voronoiCellVertices.get(i);
			Point2D.Double p2 = voronoiCellVertices.get(i + 1);
			Segment newSeg = Util.pointsToSeg(p1, p2);
			voronoiCellSegment.add(newSeg);
		}

		return voronoiCellSegment;
	}

	public ArrayList<VoronoiCell> realAugusteAlgo(Point2D.Double site1, Point2D.Double site2) {
		// constants
		Convex c = this.geometry.convex;

		// construct sectors
		KdTree<KdTree.XYZPoint> graph = this.constructGraph(site1, site2);

		// get site1 neighbor - arraylist
		KdTree.XYZPoint s1XYZ = KdTree.getNode(graph, Util.toXYZPoint(site1)).getID();
		ArrayList<EdgeData> s1Neighbors = s1XYZ.getNeighbors();

		// sort neighbors base on angular coordinates
		ArrayList<Double> s1Angles = Convex.computeAngles(s1Neighbors, site1);
		Convex.quickSort(s1Neighbors, s1Angles, 0, s1Neighbors.size() - 1);

		// find site1, site2 segment angular coordinates
		Double toAngle = Voronoi.spokeAngle(site1, site2);
		
		// find the immediate left spokes
		int counter = s1Neighbors.size() - 1;
		for (int i = 0; i < s1Neighbors.size(); i++) {
			if (toAngle < s1Angles.get(i)) {
				counter = i;
				break;
			}
		}
		Point2D.Double v = s1Neighbors.get(counter).otherNode;
		Segment sharedEdge = Util.pointsToSeg(site1, v);

		// construct sector
		List<Sector> sectors = c.constructSector(sharedEdge, site1, site2, graph);

		// check which sector to use
		int indexOfCenterSector = 0;
		for (int index = 0; index < sectors.get(1).sector.convexHull.length; index++) {
			List<Point2D.Double> listOfVertices = Arrays.asList(sectors.get(1).sector.convexHull);
			if (listOfVertices.contains(site2)) {
				indexOfCenterSector = 1;
				break;
			}
		}

		// Print statement
		// ************************************************************************************
//		if (indexOfCenterSector == 0) {
//			System.out.println("We are printing out sector[" + indexOfCenterSector + "]\n");
//		} else {
//			System.out.println("We are printing out sector[" + indexOfCenterSector + "]\n");
//		}
		// ************************************************************************************

		// finding sector that contains equidistant point
		Sector firstSector = sectors.get(indexOfCenterSector);
		Sector currSector = firstSector;

		// calculate bisector object
		Bisector b = computeBisectorInSector(currSector);

		// find and return a list of segments that intersect with the bisector
		Segment[] intersectingSegments = bisectorSectorIntersection(b, currSector, null);

		// tracking some segment
		int currSegIndex = 0;
		Segment currSeg = intersectingSegments[currSegIndex];

		// arraylist to store bisectors
		ArrayList<Bisector> bisectors = new ArrayList<Bisector>();
		bisectors.add(b.clone());

		// Print statement
		// ************************************************************************************
		Point2D.Double biLeftPt = b.getLeftEndPoint();
		Point2D.Double biRightPt = b.getRightEndPoint();
//		System.out.println("Bisector in the Current Sector");
//		System.out.println("Left Endpoint: " + Util.printCoordinate(biLeftPt));
//		System.out.println("Right Endpoint: " + Util.printCoordinate(biRightPt) + "\n");
		// ************************************************************************************

		// while it isn't the convex hull edge keep going
		Point2D.Double[] bisectHullIntersect = new Point2D.Double[2];
		Segment[] newIntersectingSegments = null;
		boolean completedBisector = false;
		boolean switchedSides = false;
		while (!completedBisector) {
			// construct new sector with the shared segment
			List<Sector> neighborSectors = c.constructSector(currSeg, site1, site2, graph);

			for (Sector neighborSec : neighborSectors) {

				// check if the constructed sector is the sector that we already have
				if (neighborSec.isEqual(currSector)) { // fix later
					continue;
				}
				// if it's not, then it's a new neighbor sector, then calculate new bisector
				else {
					currSector = neighborSec;
					b = computeBisectorInSector(currSector);

					// find one the bisector intersection points
					Point2D.Double endPoint = null;
					Bisector bist = null;
					if (!switchedSides) {
						bist = bisectors.get(bisectors.size() - 1);
					} else {
						bist = bisectors.get(0);
//						if (bist.returnA() == 0.0) {
//							bist.setAValue(0.0000001);
//						}
						switchedSides = false;
					}
					endPoint = bist.getLeftEndPoint();
					Point3d currSegLine = Util.computeLineEquation(Util.toPoint2D(currSeg.getLeftPoint()),
							Util.toPoint2D(currSeg.getRightPoint()));
					if (Math.abs(currSegLine.scalarProduct(HilbertGeometry.toHomogeneous(endPoint))) > 1e-8) {
						endPoint = bist.getRightEndPoint();
					}
					b.setEndPoints(endPoint);

					// returns two sector segments that the bisector intersect with
					newIntersectingSegments = bisectorSectorIntersection(b, currSector, endPoint);
					bisectors.add(b.clone());

					// compare segments in this list to currSeg; only return the segment that is not
					// currSeg
					for (int index = 0; index < newIntersectingSegments.length; index++) {
						if (!currSeg.equals(newIntersectingSegments[index])) {
							currSeg = newIntersectingSegments[index];
							break;
						}
					}

					// stop searching bisector
					break;
				}
			}

			// if the boundary loops back to to the original segment, then break the loop
			if (currSeg.equals(intersectingSegments[1])) {
				completedBisector = true;
			}
			if (c.isOnConvexBoundary(b.getLeftEndPoint()) || c.isOnConvexBoundary(b.getRightEndPoint())) {
				if (c.isOnConvexBoundary(b.getLeftEndPoint())) {
					bisectHullIntersect[0] = b.getLeftEndPoint();
				} else {
					bisectHullIntersect[1] = b.getRightEndPoint();
				}

				if (++currSegIndex > 1)
					completedBisector = true;
				else {
					currSeg = intersectingSegments[currSegIndex];
					currSector = firstSector;
					switchedSides = true;
				}
			}
		}

		// if we are updating to a segment on the convex hull; stop move in current
		// direction and
		// move in the opposite direction
//		System.out.print("++++++++++++++++++++++++++++++\n");
		if (c.isOnConvexBoundary(b.getLeftEndPoint()) || c.isOnConvexBoundary(b.getRightEndPoint())) {
			// store convex hull intersection points
			if (++currSegIndex > 1) {
//				 System.out.println("bk2: " + Util.printCoordinate(b.getLeftEndPoint()) + ", " + 
//						 Util.printCoordinate(b.getRightEndPoint()));
				if (c.isOnConvexBoundary(b.getLeftEndPoint())) {
					if (bisectHullIntersect[0]==null) {
						bisectHullIntersect[0] = b.getLeftEndPoint();
					}
					else {
						bisectHullIntersect[1] = b.getLeftEndPoint();
					}
				} 
				else {
					if (bisectHullIntersect[1]==null) {
						bisectHullIntersect[1] = b.getRightEndPoint();
						}
					else {
						bisectHullIntersect[0] = b.getRightEndPoint();
					}
				}
				completedBisector = true;
			} else {
				currSeg = intersectingSegments[currSegIndex];
				currSector = firstSector;
				switchedSides = true;
			}
		}

		for (Bisector bisec : bisectors) {
			// pass all segments into AllSegments
			ArrayList<Segment> aprox = approximateBisector(bisec);
			for (int i = 0; i < aprox.size(); i++) {
				allSegments.add(aprox.get(i));
				PVector lpt = aprox.get(i).getLeftPoint();
				PVector rpt = aprox.get(i).getRightPoint();
				if (aprox.get(i).getRightPoint().y <= aprox.get(i).getLeftPoint().y) {
//					System.out.println("y=" + "M(" + aprox.get(i).getLeftPoint().x + "," + aprox.get(i).getLeftPoint().y
//							+ "," + aprox.get(i).getRightPoint().x + "," + aprox.get(i).getRightPoint().y + ",x)"
//							+ "\\left\\{ " + aprox.get(i).getLeftPoint().x + " \\le x \\le "
//							+ aprox.get(i).getRightPoint().x + " \\right\\} " + " \\left\\{ "
//							+ aprox.get(i).getRightPoint().y + " \\le y \\le " + aprox.get(i).getLeftPoint().y
//							+ " \\right\\} ");
				} else {
//					System.out.println("y=" + "M(" + aprox.get(i).getLeftPoint().x + "," + aprox.get(i).getLeftPoint().y
//							+ "," + aprox.get(i).getRightPoint().x + "," + aprox.get(i).getRightPoint().y + ",x)"
//							+ "\\left\\{ " + aprox.get(i).getLeftPoint().x + " \\le x \\le "
//							+ aprox.get(i).getRightPoint().x + " \\right\\} " + " \\left\\{ "
//							+ aprox.get(i).getLeftPoint().y + " \\le y \\le " + aprox.get(i).getRightPoint().y
//							+ " \\right\\} ");
				}
				// System.out.println(Util.printCoordinate(Util.toPoint2D(lpt)) + ", " +
				// Util.printCoordinate(Util.toPoint2D(rpt)));
				// System.out.println("Bisector " + i + ": " + aprox.get(i) + "\n");
//				System.out.println("y="+"M("+aprox.get(i).getLeftPoint().x+","+aprox.get(i).getLeftPoint().y+
//						","+aprox.get(i).getRightPoint().x+","+aprox.get(i).getRightPoint().y+",x)"+ "\left\{ " + 
//						aprox.get(i).getLeftPoint().x+" \le x \le "+aprox.get(i).getRightPoint().x+" \right\} "+ 
//						" \left\{ "+aprox.get(i).getLeftPoint().y+" \le y \le "+aprox.get(i).getRightPoint().y+" \right\} ");
//				M\left(q,r,s,t,x\right)=\frac{\left(t-r\right)}{s-q}\left(x-q\right)+r
			}

			// constrcut VoronoiCell Object

		}
//		System.out.println(c.isBisectOnConvexBoundary(bisectHullIntersect[0]));
//		System.out.println(c.isBisectOnConvexBoundary(bisectHullIntersect[1]));
//		System.out.println("hull inter pt:" + Util.printCoordinate(bisectHullIntersect[0]) + "," + Util.printCoordinate(bisectHullIntersect[1]));

		// NOW WE MAE THE VORONOI CELLS FOR THE TWO SITES

		// We find where the bisector hits the hull
		Segment hullSeg1 = c.isBisectOnConvexBoundary(bisectHullIntersect[0]);
		Segment hullSeg2 = c.isBisectOnConvexBoundary(bisectHullIntersect[1]);

		ArrayList<Point2D.Double> cellWallSite1 = new ArrayList<Point2D.Double>();
		ArrayList<Point2D.Double> cellWallSite2 = new ArrayList<Point2D.Double>();

		// we get which side of the bisector our sites are on
		GrahamScan.Turn turnSite1 = GrahamScan.getTurn(bisectHullIntersect[0], bisectHullIntersect[1], site1);
		GrahamScan.Turn turnSite2 = GrahamScan.getTurn(bisectHullIntersect[0], bisectHullIntersect[1], site2);
		// VoronoiCell vorCell = new VoronoiCell(site1, allSegments);

		// We find where we need to start searching the boundary of the hull
		int startingIndex = 0;
		for (int i = 0; i < c.convexHull.length; i++)
			if (c.convexHull[i].x == hullSeg1.getRightPoint().x && c.convexHull[i].y == hullSeg1.getRightPoint().y) {
				startingIndex = i;
			}

		// BUILD CELL WALL WITH POINTS FOR SITE 1 OF BOUNDARY
		// Do we need to switch the bisectHullIntersect[...]'s insertion
		Boolean switcher = false;
		if (site1.x > site2.x) {
			switcher = true;
		}
		if (switcher) {
			cellWallSite1.add(bisectHullIntersect[1]);
		} else {
			cellWallSite1.add(bisectHullIntersect[0]);
		}
		// Add the Hull walls
		for (int i = startingIndex; i < c.convexHull.length + startingIndex; i++) {
			if (GrahamScan.getTurn(bisectHullIntersect[0], bisectHullIntersect[1],
					c.convexHull[i % c.convexHull.length]) == turnSite1) {
				cellWallSite1.add(c.convexHull[i % c.convexHull.length]);
			}
		}
		if (switcher) {
			cellWallSite1.add(bisectHullIntersect[0]);
		} else {
			cellWallSite1.add(bisectHullIntersect[1]);
		}

		// BUILD CELL WALL WITH POINTS FOR SITE 2 OF BOUNDARY
		if (switcher) {
			cellWallSite2.add(bisectHullIntersect[0]);
		} else {
			cellWallSite2.add(bisectHullIntersect[1]);
		}
		// Add the hull walls
		for (int i = startingIndex; i < c.convexHull.length + startingIndex; i++) {
			if (GrahamScan.getTurn(bisectHullIntersect[0], bisectHullIntersect[1],
					c.convexHull[i % c.convexHull.length]) == turnSite2) {
				cellWallSite2.add(c.convexHull[i % c.convexHull.length]);
			}
		}
		if (switcher) {
			cellWallSite2.add(bisectHullIntersect[1]);
		} else {
			cellWallSite2.add(bisectHullIntersect[0]);
		}

		// THE BISECTOR IS IN ALLSEGMENT
		// construct PShape polygons
		ArrayList<Segment> cell1 = new ArrayList<Segment>();
		ArrayList<Segment> cell2 = new ArrayList<Segment>();
		for (int i = 0; i < cellWallSite1.size() - 1; i++) {
			if (cellWallSite1.get(i) != cellWallSite1.get(i + 1)) {
				cell1.add(Util.pointsToSeg(cellWallSite1.get(i), cellWallSite1.get(i + 1)));
			}
		}

		for (int i = 0; i < cellWallSite2.size() - 1; i++) {
			if (cellWallSite2.get(i) != cellWallSite2.get(i + 1)) {
				cell2.add(Util.pointsToSeg(cellWallSite2.get(i), cellWallSite2.get(i + 1)));
			}
		}

		for (Segment seg : allSegments) {
			cell1.add(seg);
			cell2.add(seg);
		}
		System.out.println("-------------------------------------------------");
		for (Segment seg : cell2) {
			if (seg.getRightPoint().y <= seg.getLeftPoint().y) {
//				System.out.println(
//						"y=" + "M(" + seg.getLeftPoint().x + "," + seg.getLeftPoint().y + "," + seg.getRightPoint().x
//								+ "," + seg.getRightPoint().y + ",x)" + "\\left\\{ " + seg.getLeftPoint().x
//								+ " \\le x \\le " + seg.getRightPoint().x + " \\right\\} " + " \\left\\{ "
//								+ seg.getRightPoint().y + " \\le y \\le " + seg.getLeftPoint().y + " \\right\\} ");
			} else {
//				System.out.println(
//						"y=" + "M(" + seg.getLeftPoint().x + "," + seg.getLeftPoint().y + "," + seg.getRightPoint().x
//								+ "," + seg.getRightPoint().y + ",x)" + "\\left\\{ " + seg.getLeftPoint().x
//								+ " \\le x \\le " + seg.getRightPoint().x + " \\right\\} " + " \\left\\{ "
//								+ seg.getLeftPoint().y + " \\le y \\le " + seg.getRightPoint().y + " \\right\\} ");
			}
		}
//		System.out.println("-------------------------------------------------");
		for (Segment seg : cell1) {
			if (seg.getRightPoint().y <= seg.getLeftPoint().y) {
//				System.out.println(
//						"y=" + "M(" + seg.getLeftPoint().x + "," + seg.getLeftPoint().y + "," + seg.getRightPoint().x
//								+ "," + seg.getRightPoint().y + ",x)" + "\\left\\{ " + seg.getLeftPoint().x
//								+ " \\le x \\le " + seg.getRightPoint().x + " \\right\\} " + " \\left\\{ "
//								+ seg.getRightPoint().y + " \\le y \\le " + seg.getLeftPoint().y + " \\right\\} ");
			} else {
//				System.out.println(
//						"y=" + "M(" + seg.getLeftPoint().x + "," + seg.getLeftPoint().y + "," + seg.getRightPoint().x
//								+ "," + seg.getRightPoint().y + ",x)" + "\\left\\{ " + seg.getLeftPoint().x
//								+ " \\le x \\le " + seg.getRightPoint().x + " \\right\\} " + " \\left\\{ "
//								+ seg.getLeftPoint().y + " \\le y \\le " + seg.getRightPoint().y + " \\right\\} ");
			}
		}
//		System.out.println("-------------------------------------------------");
		// construct trapmap
//		ArrayList<Point2D> vertices = new ArrayList<Point2D>();
//		for (Segment seg : cell1){
//			Point2D.Double additionL = new Point2D.Double(seg.getLeftPoint().x,seg.getLeftPoint().y);
//			Point2D.Double additionR = new Point2D.Double(seg.getRightPoint().x,seg.getRightPoint().y);
//			vertices.add(additionL);
//			vertices.add(additionR);
//			}
//		PShape shape = new PShape(vertices);
//		
//		TrapMap site1TP = new TrapMap(cell1);
//		TrapMap site2TP = new TrapMap(cell2);
//		site1TP.findFaceTrapezoids(site1.x,site1.y);

//		ArrayList<TrapMap> trapMaps= new ArrayList<TrapMap>();
//		trapMaps.add(site1TP);
//		trapMaps.add(site2TP);

		// TURN THE SEGMENT LISTS INTO VORONOI CELLS
		ArrayList<Point2D.Double> vertices1 = new ArrayList<Point2D.Double>();
		for (int i = 0; i < cell1.size(); i++) {
			Segment seg = cell1.get(i);
			Point2D.Double newPoint1 = new Point2D.Double(seg.getRightPoint().x, seg.getRightPoint().y);
			vertices1.add(newPoint1);
			Point2D.Double newPoint2 = new Point2D.Double(seg.getLeftPoint().x, seg.getLeftPoint().y);
			vertices1.add(newPoint2);
		}
		VoronoiCell cellSite1 = new VoronoiCell(site1, vertices1);
		ArrayList<Point2D.Double> vertices2 = new ArrayList<Point2D.Double>();
		for (int i = 0; i < cell2.size(); i++) {
			Segment seg = cell2.get(i);
			Point2D.Double newPoint1 = new Point2D.Double(seg.getRightPoint().x, seg.getRightPoint().y);
			vertices2.add(newPoint1);
			Point2D.Double newPoint2 = new Point2D.Double(seg.getLeftPoint().x, seg.getLeftPoint().y);
			vertices2.add(newPoint2);
		}
		VoronoiCell cellSite2 = new VoronoiCell(site2, vertices2);
		// return Bisector
		// return bisectors;
		ArrayList<VoronoiCell> cells = new ArrayList<VoronoiCell>();
		cells.add(cellSite1);
		cells.add(cellSite2);
		return cells;
		
	}
	

	/**
	 * Given a convex hull and two sites, this method a construct the graph whose
	 * nodes are either the sites or intersection points between a spoke and another
	 * spoke or edge. Two points are connected in the graph if the line segment
	 * between the two points is contained in either spoke or edge. The line segment
	 * cannot contain another intersection point from another spoke/edge
	 * 
	 * @param s1 first site
	 * @param s2 second site
	 * @return returns the graph of the sites and all intersection points between
	 *         any pair of spokes or edges
	 */
	public KdTree<KdTree.XYZPoint> constructGraph(Point2D.Double s1, Point2D.Double s2) {
		// get arrays of hull and site vertices
		Convex c = this.geometry.convex;
		Point2D.Double[] hullVertices = Arrays.copyOfRange(c.convexHull, 0, c.convexHull.length - 1);
		Point2D.Double[] siteVertices = new Point2D.Double[] { s1, s2 };

		List<Segment> edgeSegments = c.spokeHullIntersection(hullVertices, siteVertices);
		List<Segment> site1Segments = c.spokeIntersects(hullVertices, new Point2D.Double[] { s2 }, s1);
		List<Segment> site2Segments = c.spokeIntersects(hullVertices, new Point2D.Double[] { s1 }, s2);

		// combine lists
		List<Segment> allSegments = new ArrayList<Segment>();
		allSegments.addAll(edgeSegments);
		allSegments.addAll(site1Segments);
		allSegments.addAll(site2Segments);

		// construct graph
		KdTree<KdTree.XYZPoint> tree = new KdTree<KdTree.XYZPoint>(null, 2);

		// insert segments into graph
		for (Segment s : allSegments) {
			Point2D.Double left = Util.toPoint2D(s.getLeftPoint());
			Point2D.Double right = Util.toPoint2D(s.getRightPoint());

			KdTree.KdNode node = KdTree.getNode(tree, Util.toXYZPoint(left));
			if (node == null) {
				tree.add(Util.toXYZPoint(left));
				node = KdTree.getNode(tree, Util.toXYZPoint(left));
			}

			KdTree.XYZPoint point = node.getID();
			point.addNeighbor(right /* , s.getSite1(), s.getEdge() */);

			node = KdTree.getNode(tree, Util.toXYZPoint(right));
			if (node == null) {
				tree.add(Util.toXYZPoint(right));
				node = KdTree.getNode(tree, Util.toXYZPoint(right));
			}

			point = node.getID();
			point.addNeighbor(left /* , s.getSite1(), s.getEdge() */);
		}

		// return the graph
		return tree;
	}

	/*
	 * Construct the Trapezoidal map for all Voronoi cells
	 */
	public void constructVoronoiCellTrapMap() {
		// if there are no Voronoi cells, then return
		if (this.centerPoints.size() < 1)
			return;

		// if there is one Voronoi cells, make a trapmap using only the sides of the
		// convex body
//		else if(this.centerPoints.size() == 1) {
//			LinkedList<Segment> edges = new LinkedList();
//			for(Segment s : this.geometry.convex.lineSegments)
//				edges.add(s);
//			this.voronoiCells = new TrapMap(edges);
//		}

		// for cases where there are more than one site
		else {
			return; // to implemented later
		}
	}

//	public static void main(String[] argv) {
//		Point2D.Double s1 = new Point2D.Double(23, 20);
//		Point2D.Double s2 = new Point2D.Double(28, 12);
//		HilbertGeometry g = new HilbertGeometry();
//		g.convex = new Convex();
//		Voronoi v = new Voronoi(g);
//		Point2D.Double p1 = new Point2D.Double(0, 0);
//		Point2D.Double p2 = new Point2D.Double(10, 30);
//		Point2D.Double p3 = new Point2D.Double(35, 20);
//		Point2D.Double p4 = new Point2D.Double(40, 5);
//		g.convex.addPoint(p1);
//		g.convex.addPoint(p2);
//		g.convex.addPoint(p3);
//		g.convex.addPoint(p4);
//		// g.convex.pointsToSegment(g.convex.convexHull);
//		Segment e1 = new Segment((float) p1.x, (float) p1.y, (float) p2.x, (float) p2.y);
//		Segment e2 = new Segment((float) p2.x, (float) p2.y, (float) p3.x, (float) p3.y);
//		Segment e3 = new Segment((float) p3.x, (float) p3.y, (float) p4.x, (float) p4.y);
//		Segment e4 = new Segment((float) p4.x, (float) p4.y, (float) p1.x, (float) p1.y);
//		Bisector b = new Bisector(s1, s2, e1, e2, e3, e4, p1, p3);
//
//		v.addPoint(s1);
//		v.constructVoronoiCellTrapMap();
//		List<Trapezoid> allTrap = v.voronoiCells.getAllTrapezoids();
//	}
}
