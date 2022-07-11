package geometry;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import drawing.DrawingApplet;
import drawing.DrawUtil;

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
			/*
			 * else if (tempDist == nearestDistance) { // if p is equidistant from two
			 * voronoi sites (may not be shortest // distance between all sites in the
			 * diagram // System.out.println("Distance from p from site 1: " +
			 * geometry.distance(p, // this.centerPoints.get(nearestPoint))); //
			 * System.out.println("Distance from p from site 2: " + geometry.distance(p, //
			 * this.centerPoints.get(i)) + "\n"); nearestPoint = this.centerPoints.size(); }
			 */
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
	 * rotates point p by some angle theta
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
	 * return coefficients of all lines that intersect the Voronoi site and rotated
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

		// System.out.println("site: " + Util.printCoordinate(site) + "; line: " +
		// Util.printLineEq(line));
		// System.out.println("moving positive");

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
				// determine position p2. check traversePosition to determine which direction to
				// travel across the line
				if (traversePositive)
					p2.x += 1;
				else
					p2.x -= 1;
				p2.y = (int) (-(line[0] * p2.x + line[2]) / line[1]);
			}
			// check if p2 is in the convex body; if so, find color of point and compare
			// color
			if (this.geometry.convex.isInConvex(p1) && this.geometry.convex.isInConvex(p2)) {
				// DEBUG: Draw these points
				// DrawUtil.changeColor(frame, DrawUtil.BLACK);
				// DrawUtil.drawPoint(p1, frame);
				// DrawUtil.drawPoint(p2, frame);

				// find the closest points p1 and p2
				Point2D.Double closestP1 = this.closestPoint(p1);
				// System.out.println(Util.printCoordinate(p1));
				Point2D.Double closestP2 = this.closestPoint(p2);
				// System.out.println(Util.printCoordinate(p2));

				// DEBUGGING: display information about missing points
				if (closestP1 == null || closestP2 == null) {
					if (closestP1 == null)
						System.out.println("closestP1: null");
					if (closestP2 == null)
						System.out.println("closestP2: null");

					System.out.println("site: " + Util.printCoordinate(site));
					System.out.println("p1: " + Util.printCoordinate(p1));
					System.out.println("p2: " + Util.printCoordinate(p2));
					System.out.println("Line: " + line[0] + "x + " + line[1] + "y + " + line[2] + " = 0");
					System.out.print("hull points: ");
					for (Point2D.Double hull : this.geometry.convex.convexHull)
						System.out.print(Util.printCoordinate(hull) + ", ");
					System.out.println();
					this.geometry.extremePoints();
					System.out.print("minX: " + this.geometry.min_X);
					System.out.print(", maxX: " + this.geometry.max_X);
					System.out.print(", minY: " + this.geometry.min_Y);
					System.out.println(", maxY: " + this.geometry.max_Y);
				}

				// compare the colors between two closest point; if they are different, then p1
				// is on the bisector (roughly)
				if (this.voronoiPoints.get(closestP1) != this.voronoiPoints.get(closestP2)) {
					bisectorPoints.add((Point2D.Double) p1.clone());
					// System.out.println("added point: " + Util.printCoordinate(p1));
					if (traversePositive) {
						traversePositive = false;
						p1.setLocation((int) site.x, (int) site.y);
						p2.setLocation((int) site.x, (int) site.y);
						// System.out.println("moving negative");
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
					// System.out.println("moving negative");
				} else
					loop = false;
			}

			// update p1
			p1 = (Point2D.Double) p2.clone();
		}

		// System.out.println("finished determining bisectors");
		return bisectorPoints;
	}
	
	/*
	 * Spokes stop at Voronoi bisector to visualize effects of multiple spokes on bisectors
	 */
	public LinkedList<Point2D.Double> augustAlgoWeak(Point2D.Double hull, Point2D.Double site) {
		// System.out.println("August Algo start");
//		double x = 1;
//		double y = 0;

		int currentColor;
		int nextColor;
		boolean traversePositive = true;
		boolean bisectorDetected = true;
		
		Double[] line = new Double[3];
		LinkedList<Point2D.Double> bisectorPoints = new LinkedList<Point2D.Double>();
		// Point2D.Double[] bisectorPoints = new Point2D.Double[2];
		Point2D.Double currentPoint = new Point2D.Double((int) site.x, (int) site.y); // set begin point as site
		Point2D.Double nextPoint = new Point2D.Double((int) site.x, (int) site.y);
		

		Point3d pProj = HilbertGeometry.toHomogeneous(hull); // hull point
		Point3d qProj = HilbertGeometry.toHomogeneous(site); // site point
		Point3d pqLine = pProj.crossProduct(qProj);
		System.out.println("pqLine:" + pqLine);
		
		line[0] = pqLine.x;
		line[1] = pqLine.y;
		line[2] = pqLine.z;

//		while (!bisectorDetected) {
//			
//		}
		
		while (bisectorDetected) { // while bisector is not detected, repeat
			// vertical
			if (line[1] == 0) {
				if (traversePositive) {
					nextPoint.y += 1;
				} else {
					nextPoint.y -= 1;
				}
			} else {
				if (traversePositive) {
					nextPoint.x += 1;
				} else {
					nextPoint.x -= 1; 
				}
				nextPoint.y = (int) (-line[0] * nextPoint.x + line[2] / line[1]);
			}
			
			// point in convex body
			if (this.geometry.convex.isInConvex(currentPoint) && this.geometry.convex.isInConvex(nextPoint)) {
				System.out.println("August Algo starts");
//				nextPoint.setLocation(currentPoint);
//				y = -(a * x + c) / b; // calculate 
//				currentPoint.setLocation(x, y);

				Point2D.Double closestPoint1 = this.closestPoint(currentPoint);
				Point2D.Double closestPoint2 = this.closestPoint(nextPoint);
				currentColor = this.voronoiPoints.get(closestPoint1);
				nextColor = this.voronoiPoints.get(closestPoint2);
				
				if (currentColor != nextColor) {
					bisectorPoints.add((Point2D.Double) currentPoint.clone());
					if(traversePositive) {
						traversePositive = false;
						currentPoint.setLocation((int) site.x, (int) site.y);
						nextPoint.setLocation((int) site.x, (int) site.y);
					}
				} else {
					bisectorDetected = false;
				}
			}
			else {
				if (traversePositive) {
					traversePositive = false;
					currentPoint.setLocation((int) site.x, (int) site.y);
					nextPoint.setLocation((int) site.x, (int) site.y);
				} else {
					bisectorDetected = false;
				}
			}
			currentPoint = (Point2D.Double) nextPoint.clone();
		} 
		return bisectorPoints;
	}
	
	/*
	 * main
	 */
	public static void main(String[] argv) {
		Point2D.Double s1 = new Point2D.Double(150, 200);
		Point2D.Double s2 = new Point2D.Double(300, 150);
		HilbertGeometry g = new HilbertGeometry();
		g.convex = new Convex();
		Voronoi v = new Voronoi(g);
		v.geometry.convex.addPoint(new Point2D.Double(100, 100));
		v.geometry.convex.addPoint(new Point2D.Double(350, 700));
		v.geometry.convex.addPoint(new Point2D.Double(430, 50));
		v.addPoint(s1);
		v.addPoint(s2);
		v.computeVoronoi();

		int n = 4;
		Double[][] lines = Voronoi.thetaRays(s1, n);
		LinkedList<Point2D.Double> allBisectors = new LinkedList<Point2D.Double>();
		for (int index = 0; index < lines.length; index++) {
			LinkedList<Point2D.Double> l = v.thetaRayTrace(null, lines[index], s1);
			/*
			 * if(l.size() > 2) { System.out.println("too many bisectors");
			 * System.out.println("site: " + Util.printCoordinate(s1));
			 * System.out.println("line " + (index+1) + " : " + lines[index][0] + "x + " +
			 * lines[index][1] + "y + " + lines[index][2] + " = 0"); }
			 */
			System.out.println("size: " + l.size());
			for (Point2D.Double p : l) {
				if (!allBisectors.contains(p))
					allBisectors.add((Point2D.Double) p.clone());
			}
		}

		lines = Voronoi.thetaRays(s2, n);
		for (int index = 0; index < lines.length; index++) {
			LinkedList<Point2D.Double> l = v.thetaRayTrace(null, lines[index], s2);
			/*
			 * if(l.size() > 2) { System.out.println("too many bisectors");
			 * System.out.println("site: " + Util.printCoordinate(s1));
			 * System.out.println("line " + (index+1) + " : " + lines[index][0] + "x + " +
			 * lines[index][1] + "y + " + lines[index][2] + " = 0"); }
			 */
			System.out.println("size: " + l.size());
			for (Point2D.Double p : l) {
				if (!allBisectors.contains(p))
					allBisectors.add((Point2D.Double) p.clone());
			}
		}

		for (Point2D.Double p : allBisectors)
			System.out.print(Util.printCoordinate(p));
		System.out.println("\nSize: " + allBisectors.size());
	}
}
