package geometry;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

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
			if (Double.isInfinite(tempDist))
				continue;
			if (tempDist < nearestDistance) {
				nearestDistance = tempDist;
				nearestPoint = i;
			} else if (tempDist == nearestDistance) {
				// if p is equidistant from two voronoi sites (may not be shortest
				// distance between all sites in the diagram
				// System.out.println("Distance from p from site 1: " + geometry.distance(p,
				// this.centerPoints.get(nearestPoint)));
				// System.out.println("Distance from p from site 2: " + geometry.distance(p,
				// this.centerPoints.get(i)) + "\n");
				nearestPoint = this.centerPoints.size();
			}
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

	// rotates point p by some angle theta
	private static Point2D.Double rotationMatrix(Point2D.Double p, double theta) { // deleted static
		Double[][] R = new Double[2][2];
		R[0][0] = Math.cos(theta);
		R[1][1] = R[0][0];
		R[1][0] = Math.sin(theta);
		R[0][1] = -R[1][0];

		return new Point2D.Double(R[0][0] * p.x + R[0][1] * p.y, R[1][0] * p.x + R[1][1] * p.y);
	}

	// return coefficients of all lines that intersect the Voronoi site and rotated
	// point for site s
	public static Double[][] thetaRays(Point2D.Double s, int n) {
		System.out.println("thetaRays starting.");
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
			// System.out.println( k + ": " + (rsLine.x * s.x + rsLine.y * s.y + rsLine.z) +
			// " : " + (rsLine.x * r.x + rsLine.y * r.y + rsLine.z) );
		}

		return lines;
	}

	private Point2D.Double closestPoint(Point2D.Double p) {
		// check if given point is already in the hashmap
		if(this.voronoiPoints.containsKey(p)) return p;
		
		// otherwise, look at surrounding eight points
		Point2D.Double testPoint = new Point2D.Double(p.x-1, p.y-1);
		for (int i = 0; i < 9; i++) {
			// don't check center point again
			if(i == 4) {
				testPoint.x += 1;
				continue;
			}
			
			// check if given point is in hashmap
			if (voronoiPoints.containsKey(testPoint))
				return testPoint;
			
			System.out.println("tested point (" + testPoint.x + ", " + testPoint.y + ")");

			// otherwise, change point to the next point
			if(i % 3 != 2) 
				testPoint.x += 1;
			else {
				testPoint.x -= 2;
				testPoint.y += 1;
			}
		}
		return null;
	}
	
	public Double[][] thetaRayTrace(Double[][] lines, Point2D.Double site) {
		int rayIndex = 0;
		int x0 = (int) site.x;
		double y0 = (int) site.y;
		double y;
		double y_ver; // vertical
		Double[][] bisectorPoints = new Double[lines.length][2];
		System.out.println("thetaRayTracing starting.");

		int currentColor;
		int nextColor;
		Point2D.Double currentPoint = new Point2D.Double(x0, y0);
		Point2D.Double nextPoint = new Point2D.Double(0, 0);

		// Initialize 1st point ray tracing
		// y0 = -(lines[rayIndex][0] + lines[rayIndex][2]) / lines[rayIndex][1];
		// currentPoint.setLocation(x0, y0);

		for (int x = x0 + 1; x < x0 + 300; x++) {
			rayIndex++;
			nextPoint.setLocation(currentPoint);

			// If the point is vertical then increment y
			if (lines[rayIndex][1] == 0) {
				for (int x_ver = 0; x_ver < 300; x++) {
					rayIndex++;
					nextPoint = currentPoint;

					y_ver = lines[x_ver][0] + lines[x_ver][2];

					currentPoint.setLocation(x_ver, y_ver);
					currentColor = voronoiPoints.get(currentPoint);
					nextColor = voronoiPoints.get(nextPoint);

					if (currentColor != nextColor) {
						System.out.println("The intersection point of thetaRay" + rayIndex + ": " + nextPoint);
						bisectorPoints[rayIndex][0] = nextPoint.x;
						bisectorPoints[rayIndex][1] = nextPoint.y;

					}
				}
			}

			else {
//				Set<Point2D.Double> keys = voronoiPoints.keySet();
//				System.out.println("key set size: " + keys.size());

				// trace until Voronoi points color change
				y = -(lines[rayIndex][0] * x + lines[rayIndex][2]) / lines[rayIndex][1];
				System.out.println("site location: " + site.x + ", " + site.y);
				System.out.println("Point - x: " + (int) x + " y: " + (int) y);
				System.out.println("Line: " + lines[rayIndex][0] + "x + " + lines[rayIndex][1] + "y + " + lines[rayIndex][2] + " = 0");
				currentPoint.setLocation((int) x, (int) y);


				Point2D cc = closestPoint(currentPoint);
				Point2D nc = closestPoint(nextPoint);
				currentColor = voronoiPoints.get(cc);
				nextColor = voronoiPoints.get(nc);
				System.out.println("closest point to currentPoint: " + cc);
				System.out.println("closest point to nextPoint: " + nc);
				
				

				if (currentColor != nextColor) {
					System.out.println("The intersection point of thetaRay" + rayIndex + ": " + nextPoint);
					bisectorPoints[rayIndex][0] = nextPoint.x;
					bisectorPoints[rayIndex][1] = nextPoint.y;
				}
			}
		}

		return bisectorPoints;
	}
	
}
