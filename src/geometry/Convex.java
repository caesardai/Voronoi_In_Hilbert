package geometry;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.*;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import trapmap.Segment;
import processing.core.PVector;

/*
 * Class to handle operations on 2D Convex shapes.
 */
public class Convex {
	// User defined points
	public Point2D.Double[] points;
	// Actual points defining the convex hull.
	public Point2D.Double[] convexHull;
	// Spoke segments
	public List<Segment> spokeSegments;

	// Default constructor
	public Convex() {
		points = new Point2D.Double[0];
		convexHull = new Point2D.Double[0];
		spokeSegments = new ArrayList<Segment>();
	}

	// Constructs convex from file.
	public Convex(String filename) {
		points = load(filename);
		this.computeConvexHull();
	}

	/*
	 * Inserting a site site and find the segments around it
	 */
	public Map<Point2D.Double, List<Segment>> siteSegment(Point2D.Double[] hullVertex, Point2D.Double site) {
		int numHullVertex = hullVertex.length;
		// Segments with site as endpoint
		Map<Point2D.Double, List<Segment>> siteSegments = new HashMap<Point2D.Double, List<Segment>>();

		// Find equations for hull edges
		Point3d[] hullEdges = new Point3d[(int) Math.ceil(numHullVertex / 2)]; // stores hull edges as Point3d object
		for (int h = 0; h < numHullVertex - 1; h++) {
			Point3d v_1 = HilbertGeometry.toHomogeneous(hullVertex[h]);
			Point3d v_2 = HilbertGeometry.toHomogeneous(hullVertex[h + 1]);
			hullEdges[h] = v_1.crossProduct(v_2);
		}
		// number of hull edges
		int numHullEdges = hullEdges.length;

		// Find spoke segments
		Point3d sproj = HilbertGeometry.toHomogeneous(site);
		for (int h = 0; h < numHullVertex; h++) {
			Point3d hproj = HilbertGeometry.toHomogeneous(hullVertex[h]);
			Point3d spoke = sproj.crossProduct(hproj);

			for (int hVertex = 0; hVertex < numHullEdges; hVertex++) {
				// Construct segments from current hull vertex to site

				// Convert 2d points to PVector object
				Segment spokeHullVertexSegments = constructSegment(site, hullVertex[hVertex]); // site to vertex
				spokeSegments.add(spokeHullVertexSegments);

				// Construct segments from this hull intersect to site
				Point3d spokeHullEdgeIntersect = spoke.crossProduct(hullEdges[hVertex]);
				Point2D.Double spokeHullEdgeIntersectPoint = HilbertGeometry.toCartesian(spokeHullEdgeIntersect);
				Segment spokeHullEdgeSegments = constructSegment(site, spokeHullEdgeIntersectPoint); // site to edge
				spokeSegments.add(spokeHullEdgeSegments);
			}
		}

		// Constructing hashmap with site as the key and list of site-containing
		// segments as value
		siteSegments.put(site, spokeSegments);
		return siteSegments;
	}

	/**
	 * Find spoke intersections when more than one spoke is inserted
	 * 
	 * @param hullVertex
	 * @param newSite
	 * @param segments
	 * @return
	 */
	public List<Point2D.Double> spokeIntersects(Point2D.Double[] hullVertex, Point2D.Double newSite, List<Segment> segments) {
		double i_x, i_y;
		double s, t, s1_x, s1_y, s2_x, s2_y;
		Point2D.Double seg1LeftPoint = null;
		Point2D.Double seg1RightPoint = null;
		Point2D.Double seg2LeftPoint = null;
		Point2D.Double seg2RightPoint = null;
		List<Point2D.Double> spokeIntersects = new ArrayList<Point2D.Double>();
		Map<Point2D.Double, List<Segment>> newSegments = new HashMap<Point2D.Double, List<Segment>>();
		newSegments = siteSegment(hullVertex, newSite);

		// Looping through each element in the hashmap
		for (Map.Entry<Point2D.Double, List<Segment>> segList : newSegments.entrySet()) {

			// Access the list of segments associated with each site
			List<Segment> segs = segList.getValue();
			for (int i = 0; i < segs.size(); i++) {
				Segment s1 = segs.get(i); // current segment
				Segment s2 = segs.get(i); // next segment
				Point2D.Double spokeIntersectionPoint = null;

				// Segment 1 left and right end Point2D
				PVector s1l = s1.getLeftPoint();
				double s1lx = s1l.x;
				double s1ly = s1l.y;
//				seg1LeftPoint.setLocation(s1l.x, s1l.y);
				PVector s1r = s1.getRightPoint();
				float s1rx = s1r.x;
				float s1ry = s1r.y;
//				seg1RightPoint.setLocation(s1r.x, s1r.y);
				// Segment 1 left and right end Point2D
				PVector s2l = s2.getLeftPoint();
				float s2lx = s2l.x;
				float s2ly = s2l.y;
//				seg2LeftPoint.setLocation(s2l.x, s2l.y);
				PVector s2r = s2.getRightPoint();
				float s2rx = s2r.x;
				float s2ry = s2r.y;
//				seg2RightPoint.setLocation(s2r.x, s2r.y);

				// Intersection points stored in i_x and i_y
				s1_x = s1rx - s1lx;
				s1_y = s1ry - s1ly;
				s2_x = s2rx - s2lx;
				s2_y = s2ry - s2ly;

				s = (-s1_y * (s1lx - s2lx) + s1_x * (s1ly - s2ly)) / (-s2_x * s1_y + s1_x * s2_y);
				t = (s2_x * (s1ly - s2ly) - s2_y * (s1lx - s2lx)) / (-s2_x * s1_y + s1_x * s2_y);
				
				// Collision detected
				if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
					i_x = s1lx + (t * s1_x);
					i_y = s1ly + (t * s1_y);
					spokeIntersectionPoint.setLocation(i_x, i_y);
				}
				spokeIntersects.add(spokeIntersectionPoint);
			}
		}
		return spokeIntersects;
	}

//				Point3d s1lh = HilbertGeometry.toHomogeneous(seg1LeftPoint);
//				Point3d s1rh = HilbertGeometry.toHomogeneous(seg1RightPoint);
//				Point3d s2lh = HilbertGeometry.toHomogeneous(seg2LeftPoint);
//				Point3d s2rh = HilbertGeometry.toHomogeneous(seg2RightPoint);
//				Point3d line1 = s1lh.crossProduct(s1rh);
//				Point3d line2 = s2lh.crossProduct(s2rh);
//				
//				double denom = line1.scalarProduct(line2);
//			    if (denom == 0) return new Point3d();
//			    double t = - line1.scalarProduct(q) / denom;
//			    if (t >= 0 && t <= 1) {
//			      double[] scalarCoeff = {t, 1-t};
//			      Point3d[] pointCoeff = {p, q};
//			      return Point3d.linearCombination(pointCoeff, scalarCoeff);
//			    }
//			    // return new Point3d();


	/*
	 * Converting 2d points to PVectors
	 */
	public Segment constructSegment(Point2D.Double p1, Point2D.Double p2) {
		double p1x = p1.x;
		double p1y = p1.y;
		double p2x = p2.x;
		double p2y = p2.y;
		PVector v1 = new PVector();
		PVector v2 = new PVector();
		v1.set((float) p1x, (float) p1y);
		v2.set((float) p2x, (float) p2y);
		Segment segment = new Segment(v1, v2);
		return segment;
	}

	/*
	 * Check 3 points orientation
	 */
	public int threePointsOrientation(Point2D.Double p1, Point2D.Double p2, Point2D.Double p3) {
		int orientation;
		int val = (int) ((p2.y - p1.y) * (p3.x - p2.x) - (p2.x - p1.x) * (p3.y - p2.y));

		if (val == 0) {
			orientation = 0; // Collinear
		} else if (val == 1) {
			orientation = 1; // Clockwise
		} else {
			orientation = 2; // Counter Clockwise
		}
		return orientation;
	}

	/*
	 * Find where the spokes intersects the convex hull
	 */
	public List<Point3d> spokeHullIntersection(Point2D.Double[] hullVertex, Point2D.Double[] allPoints) {
		// Separating out the hull points from site points
		List<Point2D.Double> allPoint = new ArrayList<Point2D.Double>(Arrays.asList(allPoints));
		List<Point2D.Double> hullPoint = new ArrayList<Point2D.Double>(Arrays.asList(hullVertex));
		List<Point2D.Double> sites = new ArrayList<Point2D.Double>();
		allPoint.removeAll(hullPoint);
		Collections.copy(sites, allPoint);
		Point2D.Double[] allSites = new Point2D.Double[sites.size()];
		allSites = (Double[]) sites.toArray();

		int hullVertexLength = hullVertex.length;
		int sitesArrayLength = allSites.length;

		// Find equations for hull edges
		Point3d[] hullEdges = new Point3d[(int) Math.ceil(hullVertexLength / 2)];
		for (int h = 0; h < hullVertexLength - 1; h++) {
			Point3d v_1 = HilbertGeometry.toHomogeneous(hullVertex[h]);
			Point3d v_2 = HilbertGeometry.toHomogeneous(hullVertex[h + 1]);
			hullEdges[h] = v_1.crossProduct(v_2);
		}
		int hullEdgesArrayLength = hullEdges.length;

		// Find hull edge intersects
		List<Point3d> spokeHullIntersects = new ArrayList<Point3d>();
		// Point3d[] spokeHullIntersects = new Point3d[sitesArrayLength * 2];
		for (int s = 0; s < sitesArrayLength; s++) {
			Point3d sproj = HilbertGeometry.toHomogeneous(allSites[s]);

			for (int h = 0; h < hullVertexLength; h++) {
				Point3d hproj = HilbertGeometry.toHomogeneous(hullVertex[h]);
				Point3d shLine = hproj.crossProduct(sproj); // spoke

				for (int i = 0; i < hullEdgesArrayLength; i++) {
					spokeHullIntersects.add(hullEdges[i].crossProduct(shLine));
				}
			}
		}
		return spokeHullIntersects;
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


