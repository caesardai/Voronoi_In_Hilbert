package geometry;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import Jama.Matrix;

import drawing.DrawingApplet;
import drawing.DrawUtil;

import trapmap.TrapMap;
import trapmap.Trapezoid;
import trapmap.Segment;

import processing.core.PVector;

public class Voronoi {
	/* HG where we compute voronoi diagram */
	protected HilbertGeometry geometry;
	/* List of center points for the Voronoi diagram */
	public LinkedList<Point2D.Double> centerPoints = new LinkedList<Point2D.Double>();
	/*
	 * for each point inside the domain, give the index of the center points that is
	 * closest to it.
	 */
	public HashMap<Point2D.Double, Integer> voronoiPoints = new HashMap<Point2D.Double, Integer>();

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

	public void realAugusteAlgo(Point2D.Double site1, Point2D.Double site2) {
		// constants
		Convex c = this.geometry.convex;

		// find equidistance on some spoke
		Point3d spoke = Util.computeLineEquation(site1, c.convexHull[0]);
		Point2D.Double equidistantPoint = this.findEquiDistancePoint(site1, site2, spoke);

		// Saving a list of intersection points
		ArrayList<Point2D.Double> intersectionPoints = new ArrayList<Point2D.Double>();
		intersectionPoints.add(equidistantPoint);
		for (int i = 0; i < c.convexHull.length; i++) {
			Point3d otherSpoke = Util.computeLineEquation(site2, c.convexHull[i]);
			Point2D.Double intersect = Util.lineIntersection(spoke, otherSpoke);
			if (intersect != null)
				intersectionPoints.add(intersect);
		}
		Convex.sortColinearPoints(intersectionPoints);
		Point2D.Double centerPoint = intersectionPoints.get(intersectionPoints.indexOf(equidistantPoint) - 1);

		// construct sectors
		KdTree<KdTree.XYZPoint> graph = this.constructGraph(site1, site2);
		List<Sector> sectors = c.constructSector(centerPoint, site1, site2, graph);

		// finding sector that contains equidistant point
		Sector currSector = null;
		for (Sector sec : sectors) {
			if (sec.isInSector(equidistantPoint)) {
				currSector = sec;
				break;
			}
		}

		Segment e1 = currSector.getEdge1();
		Segment e2 = currSector.getEdge2();
		Segment e3 = currSector.getEdge3();
		Segment e4 = currSector.getEdge4();

		// compute Bisector
		Bisector b = new Bisector(site1, site2, e1, e2, e3, e4);

		int segCount = 0;
		// array of Voronoi boundary segments that intersects bisector
		Segment[] intersectSeg = new Segment[2];
		Point2D.Double[] secVertices = currSector.sector.convexHull;
		for (int i = 0; i < secVertices.length; i++) {
			Point2D.Double v1 = secVertices[i];
			Point2D.Double v2 = secVertices[i + 1];
			Point3d linev1v2 = Util.computeLineEquation(v1, v2);
			LinkedList<Point2D.Double> secBisecIntersect = b.intersectionPointsWithLine(linev1v2);
			if (secBisecIntersect == null) {
				continue;
			} else {
				for (Point2D.Double pt : secBisecIntersect) {
					if (segCount == 2)
						break;
					if (currSector.isInSector(pt)) {
						intersectSeg[segCount++] = Util.pointsToSeg(v1, v2);
					}
				}
			}
			if (segCount == 2)
				break;
		}
		
		
		
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
			point.addNeighbor(right, s.getSite1(), s.getEdge());

			node = KdTree.getNode(tree, Util.toXYZPoint(right));
			if (node == null) {
				tree.add(Util.toXYZPoint(right));
				node = KdTree.getNode(tree, Util.toXYZPoint(right));
			}

			point = node.getID();
			point.addNeighbor(left, s.getSite1(), s.getEdge());
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
		g.convex.addPoint(p1);
		g.convex.addPoint(p2);
		g.convex.addPoint(p3);
		g.convex.addPoint(p4);
		// g.convex.pointsToSegment(g.convex.convexHull);
		Segment e1 = new Segment((float) p1.x, (float) p1.y, (float) p2.x, (float) p2.y);
		Segment e2 = new Segment((float) p2.x, (float) p2.y, (float) p3.x, (float) p3.y);
		Segment e3 = new Segment((float) p3.x, (float) p3.y, (float) p4.x, (float) p4.y);
		Segment e4 = new Segment((float) p4.x, (float) p4.y, (float) p1.x, (float) p1.y);
		Bisector b = new Bisector(s1, s2, e1, e2, e3, e4, p1, p3);

		v.addPoint(s1);
		v.constructVoronoiCellTrapMap();
		List<Trapezoid> allTrap = v.voronoiCells.getAllTrapezoids();
	}
}
