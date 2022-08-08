package geometry;

import java.awt.geom.Point2D;
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

	/**
	 * Inserting a site site and find the segments around it
	 * 
	 * @param hullVertex get information
	 * @param site
	 * @return
	 */
	public Map<Point2D.Double, List<Segment>> siteSegment(Point2D.Double[] hullVertex, Point2D.Double site) {
		int numHullVertex = hullVertex.length;
		// Segments with site as end point
		Map<Point2D.Double, List<Segment>> siteSegments = new HashMap<Point2D.Double, List<Segment>>();

		// Find equations for hull edges
		Point3d[] hullEdges = new Point3d[numHullVertex]; // stores hull edges as Point3d object
		for (int h = 0; h < numHullVertex; h++) {
			Point3d v_1 = HilbertGeometry.toHomogeneous(hullVertex[h]);
			Point3d v_2 = HilbertGeometry.toHomogeneous(hullVertex[(h + 1) % numHullVertex]);
			hullEdges[h] = v_1.crossProduct(v_2);
		}
		// number of hull edges
		int numHullEdges = hullEdges.length;

		// Find spoke segments
		Point3d sproj = HilbertGeometry.toHomogeneous(site);
		for (int h = 0; h < numHullVertex; h++) {
			Point3d hproj = HilbertGeometry.toHomogeneous(hullVertex[h]);
			Point3d spoke = sproj.crossProduct(hproj);

			// Construct segments from current hull vertex to site
			// Convert 2d points to PVector object
			Segment spokeHullVertexSegments = constructSegment(site, hullVertex[h]); // site to vertex
			spokeSegments.add(spokeHullVertexSegments);

			for (int hVertex = 0; hVertex < numHullEdges; hVertex++) {
				// Construct segments from this hull intersect to site
				Point3d spokeHullEdgeIntersect = spoke.crossProduct(hullEdges[hVertex]);
				Point2D.Double spokeHullEdgeIntersectPoint = HilbertGeometry.toCartesian(spokeHullEdgeIntersect);

				Segment spokeHullEdgeSegments = constructSegment(site, spokeHullEdgeIntersectPoint); // site to edge

				// check if intersection point is one of the convex body vertices
				if (spokeSegments.contains(spokeHullEdgeSegments))
					continue;

				// check if intersection point is in the closure of the convex body
				else if (this.isOnConvexBoundary(spokeHullEdgeIntersectPoint)) {
					spokeSegments.add(spokeHullEdgeSegments);
				}
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
	public List<Segment> spokeIntersects(Point2D.Double[] hullVertex, Point2D.Double newSite, List<Segment> segments) {
		double i_x, i_y;
		double s, t, s1_x, s1_y, s2_x, s2_y;
//		Point2D.Double seg1LeftPoint = null;
//		Point2D.Double seg1RightPoint = null;
//		Point2D.Double seg2LeftPoint = null;
//		Point2D.Double seg2RightPoint = null;
		List<Point2D.Double> spokeIntersects = new ArrayList<Point2D.Double>();
		Map<Point2D.Double, List<Segment>> newSegments = new HashMap<Point2D.Double, List<Segment>>();
		newSegments = siteSegment(hullVertex, newSite);

		// grab arraylist for new sites
		List<Segment> newSpokes = newSegments.get(newSite);
		List<Segment> innerSegments = new ArrayList<Segment>();

		// Looping through each element in the hashmap
		for (Map.Entry<Point2D.Double, List<Segment>> segList : newSegments.entrySet()) {

			// do not look at arraylist of new site
			if (segList.getKey().equals(newSite))
				continue;

			// Access the list of segments associated with each site
			Point2D.Double site = segList.getKey();
			List<Segment> segs = segList.getValue();
			double s0l = site.x;
			double s0r = site.y;

			// loop through all new spokes
			for (Segment seg1 : newSpokes) {
				// loop through all old spokes
				for (Segment seg2 : segs) {
					Segment s1 = seg1; // new spoke from new site
					Segment s2 = seg2; // old spoke from old site
					Point2D.Double spokeIntersectionPoint = null;

					// Segment 1 left and right end Point2D
					PVector s1l = s1.getLeftPoint();
					double s1lx = s1l.x;
					double s1ly = s1l.y;
					// seg1LeftPoint.setLocation(s1l.x, s1l.y);
					PVector s1r = s1.getRightPoint();
					double s1rx = s1r.x;
					double s1ry = s1r.y;
					// seg1RightPoint.setLocation(s1r.x, s1r.y);
					// Segment 1 left and right end Point2D
					PVector s2l = s2.getLeftPoint();
					double s2lx = s2l.x;
					double s2ly = s2l.y;
					// seg2LeftPoint.setLocation(s2l.x, s2l.y);
					PVector s2r = s2.getRightPoint();
					double s2rx = s2r.x;
					double s2ry = s2r.y;
					// seg2RightPoint.setLocation(s2r.x, s2r.y);

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
						spokeIntersectionPoint = new Point2D.Double();
						spokeIntersectionPoint.setLocation(i_x, i_y);
						Segment newInnerSegment = new Segment((float) s0l, (float) s0r, (float) i_x, (float) i_y);
						innerSegments.add(newInnerSegment);
					}
					spokeIntersects.add(spokeIntersectionPoint);
				}
			}
		}
		return innerSegments;
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

	/**
	 * Find where the spokes intersects the convex hull
	 * 
	 * @param hullVertex
	 * @param sitePoints
	 * @return
	 */
	public List<Point2D.Double> spokeHullIntersection(Point2D.Double[] hullVertex, Point2D.Double[] sitePoints) {
		// Separating out the hull points from site points
		int numHullVertex = hullVertex.length;
		int numSites = sitePoints.length;

		// Find hull edges segments
		List<Segment> hullSegments = new ArrayList<Segment>((int) Math.ceil(numHullVertex / 2));
		for (int hv = 0; hv < numHullVertex; hv++) {
			float hullVertex1EndPt1 = (float) hullVertex[hv].x;
			float hullVertex1EndPt2 = (float) hullVertex[hv].y;
			float hullVertex2EndPt1 = (float) hullVertex[(hv + 1) % numHullVertex].x;
			float hullVertex2EndPt2 = (float) hullVertex[(hv + 1) % numHullVertex].y;
			Segment newHullEdgeSegment = new Segment(hullVertex1EndPt1, hullVertex1EndPt2, hullVertex2EndPt1,
					hullVertex2EndPt2);
			hullSegments.add(newHullEdgeSegment);
		}
		int numHullSegments = hullSegments.size();

		// Find site-edge spoke segments
		List<Segment> siteHullEdgeSpoke = new ArrayList<Segment>(numHullVertex * numSites);
		// Check intersection points between hull edges segments and site-edge spoke
		List<Point2D.Double> spokeHullIntersects = new ArrayList<Point2D.Double>(numHullVertex * numSites);

		for (int s = 0; s < numSites; s++) {
			float sitePoint1 = (float) sitePoints[s].x;
			float sitePoint2 = (float) sitePoints[s].y;
			Point2D.Double site_i = sitePoints[numSites];

			for (int h = 0; h < numHullVertex; h++) {
				float hullVertexEndPoint1 = (float) hullVertex[h].x;
				float hullVertexEndPoint2 = (float) hullVertex[h].y;
				Point2D.Double hullVertex_i = hullVertex[numSites];

				// Construct hull vertex site segments
				Segment newSegment = new Segment(sitePoint1, sitePoint2, hullVertexEndPoint1,
						hullVertexEndPoint2);
				siteHullEdgeSpoke.add(newSegment);

				// Find spoke hull edge intersection and construct segment
				Point3d s_i = HilbertGeometry.toHomogeneous(site_i);
				Point3d hv_i = HilbertGeometry.toHomogeneous(hullVertex_i);
				Point3d siteHullVertexLine = s_i.crossProduct(hv_i);

				Point2D.Double segLeftPoint = null;
				Point2D.Double segRightPoint = null;
				for (Segment seg : hullSegments) {
					PVector hullEdgeEndPoint1 = seg.getLeftPoint();
					PVector hullEdgeEndPoint2 = seg.getRightPoint();
					double heep1x = hullEdgeEndPoint1.x;
					double heep1y = hullEdgeEndPoint1.y;
					double heep2x = hullEdgeEndPoint2.x;
					double heep2y = hullEdgeEndPoint2.y;
					segLeftPoint.setLocation(heep1x, heep1y);
					segRightPoint.setLocation(heep2x, heep2y);
					Point3d heepl = HilbertGeometry.toHomogeneous(segLeftPoint);
					Point3d heepr = HilbertGeometry.toHomogeneous(segRightPoint);
					Point3d hullEdgeLine = heepl.crossProduct(heepr);

					Point3d spokeHullEdgeIntersection3d = hullEdgeLine.crossProduct(siteHullVertexLine);
					Point2D.Double spokeHullEdgeIntersection2d = HilbertGeometry
							.toCartesian(spokeHullEdgeIntersection3d);

					if (isOnConvexBoundary(spokeHullEdgeIntersection2d)) {
						PVector v1 = new PVector();
						PVector v2 = new PVector();
						v1.set((float) sitePoint1, (float) sitePoint2);
						v2.set((float) spokeHullEdgeIntersection2d.x, (float) spokeHullEdgeIntersection2d.y);
						Segment spokeHullEdgeSegment = new Segment(v1, v2);
					}
					// segLeftPoint.setLocation(s2l.x, s2l.y);
				}
			}
		}
		int numSiteEdgeSegments = siteHullEdgeSpoke.size();

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
	 * Decides whether point p lies in the interior convex.
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

	/**
	 * Determines if the query point lays on the boundary of the convex body
	 * 
	 * @param p query point
	 * @return true if the query point is on the convex body. otherwise, return
	 *         false
	 */
	public boolean isOnConvexBoundary(Point2D.Double p) {
		if (p == null)
			return false;

		int N = convexHull.length;
		Point2D.Double a, b;
		boolean onBoundary = false;

		for (int i = 0; i < N - 1; i++) {
			a = convexHull[i];
			b = convexHull[i + 1];
			Point2D.Double ab = new Point2D.Double(b.x - a.x, b.y - a.y);
			Point2D.Double ap = new Point2D.Double(p.x - a.x, p.y - a.y);
			double crossProduct = ab.x * ap.y - ab.y * ap.x;
			if (Math.abs(crossProduct) <= 1e-8) {
				// check if the point is within the correct bounds
				double smallX = a.x;
				double largeX = b.x;
				double smallY = a.y;
				double largeY = b.y;

				// swap values if not correct
				if (smallX > largeX) {
					double tmp = smallX;
					smallX = largeX;
					largeX = tmp;
				}
				if (smallY > largeY) {
					double tmp = smallY;
					smallY = largeY;
					largeY = tmp;
				}

				if ((p.x >= smallX && p.x <= largeX) && (p.y >= smallY && p.y <= largeY))
					onBoundary = true;
			}
		}
		return onBoundary;
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

	/**
	 * Determines if there exists a point in the list that is approximately the same
	 * as the query point
	 * 
	 * @param list list of points
	 * @param p    query point
	 * @return returns a boolean that corresponds to if there is an approximately
	 *         equal query point in the list
	 */
	private static boolean almostContainsElement(List<Point2D.Double> list, Point2D.Double p) {
		for (Point2D.Double q : list) {
			if (p.distance(q) <= 1e-12)
				return true;
		}
		return false;
	}
	
	/**
	 * Given a set of points that are all colinear with each other, sort the points from left to right on this line
	 * 
	 * @param points the set of colinear points
	 */
	private static void sortColinearPoints(ArrayList<Point2D.Double> points) {
		// only sort if there are more than 1 point in our list
		if(points.size() < 2)
			return;
		
		// determine if the set of points are colinear with a vertical line
		if(Math.abs( points.get(0).y - points.get(1).y ) <= 1e-8)
			Convex.quickSort(points, 0, points.size() - 1, false);
		
		// otherwise, sort points by the x-coordinate
		// we are ignoring any float-point errors that may occur here
		Convex.quickSort(points, 0, points.size() - 1, true);
		
	}
	private static int partition(ArrayList<Point2D.Double> array, int begin, int end, boolean useX) {
        int pivot = end;

        int counter = begin;
		for (int i = begin; i < end; i++) {
			// compare either x or y
			double compare1, compare2;
			if(useX) {
				compare1 = array.get(i).x;
				compare2 = array.get(pivot).x;
			} else {
				compare1 = array.get(i).y;
				compare2 = array.get(pivot).y;
			}
			
			if (compare1 < compare2) {
				Point2D.Double temp = array.get(counter);
				array.set(counter, array.get(i));
				array.set(i, temp);
				counter++;
			}
		}
		Point2D.Double temp = array.get(pivot);
		array.set(pivot, array.get(counter));
		array.set(counter, temp);

		return counter;
    }

    private static void quickSort(ArrayList<Point2D.Double> array, int begin, int end, boolean useX) {
        if (end <= begin) return;
        int pivot = partition(array, begin, end, useX);
        quickSort(array, begin, pivot-1, useX);
        quickSort(array, pivot+1, end, useX);
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

	public static void main(String[] argv) {
		ArrayList<Point2D.Double> colinear = new ArrayList<Point2D.Double>();
//		colinear.add(new Point2D.Double(57.671, 19.282));
//		colinear.add(new Point2D.Double(-11.746, -1.521));
//		colinear.add(new Point2D.Double(-54.158, -14.231));
//		colinear.add(new Point2D.Double(16.7, 7.004));
//		colinear.add(new Point2D.Double(-28.002, -6.392));
//		colinear.add(new Point2D.Double(58.951, 19.666));
//		colinear.add(new Point2D.Double(51.668, 17.484));
//		colinear.add(new Point2D.Double(-52.655, -13.78));
//		colinear.add(new Point2D.Double(40.823, 14.234));
//		colinear.add(new Point2D.Double(-9.526, -0.855));
		
//		for(int i = 0; i < 10; i++) 
//			colinear.add(new Point2D.Double(1d, Math.random() * 200 - 100));
//		
//		System.out.println("before");
//		
//		Convex.quickSort(colinear, 0, colinear.size()-1, false);
//		
//		System.out.println("after");
		
		HashMap<Segment, Boolean> map = new HashMap<Segment, Boolean>();
		Segment k = new Segment(1f, 1f, 1f, 1f);
		
		for(int index = 0; index < 10; index++) {
			float f1 = (float) Math.random() * 200 - 100;
			float f2 = (float) Math.random() * 200 - 100;
			float f3 = (float) Math.random() * 200 - 100;
			float f4 = (float) Math.random() * 200 - 100;
			map.put(new Segment(f1, f2, f3, f4), true);
			if(index == 3)
				k = new Segment(f1, f2, f3, f4);
		}
		
		boolean[] contains = new boolean[1000000000];
		
		if(contains[918143])
			System.out.println("true");
		else
			System.out.println("false");
		
//		Segment[] segs = map.keySet().toArray(new Segment[map.keySet().size()]);
//		boolean containsCollision = false;
//		for(int index = 0; index < segs.length; index++) {
//			for(int curr = 0; curr < segs.length; curr++) {
//				if(curr != index) {
//					if(segs[index].hashCode() == segs[curr].hashCode()) {
//						containsCollision = true;
//						break;
//					}
//				}
//			}
//		}
//		System.out.println(containsCollision);
		
//		float f1 = (float) Math.random() * 200 - 100;
//		float f2 = (float) Math.random() * 200 - 100;
//		float f3 = (float) Math.random() * 200 - 100;
//		float f4 = (float) Math.random() * 200 - 100;
//		System.out.println(map.containsKey(new Segment(f1, f2, f3, f4)));
//		System.out.println(map.containsKey(k));
	}
}

/*
 * Under spokeHullIntersection
 */

//double i_x, i_y;
//double s, t, s1_x, s1_y, s2_x, s2_y;

//for (int i = 0; i < numHullSegments; i++) {
//	Segment s1 = hullSegments.get(i);
//	PVector s1l = s1.getLeftPoint();
//	double s1lx = s1l.x;
//	double s1ly = s1l.y;
//	PVector s1r = s1.getRightPoint();
//	double s1rx = s1r.x;
//	double s1ry = s1r.y;
//
//	for (int j = 0; j < numSiteEdgeSegments; j++) {
//		Point2D.Double spokeHullIntersectionPoint = null;
//		Segment s2 = hullSegments.get(j);
//		PVector s2l = s2.getLeftPoint();
//		double s2lx = s2l.x;
//		double s2ly = s2l.y;
//		PVector s2r = s2.getRightPoint();
//		double s2rx = s2r.x;
//		double s2ry = s2r.y;
//
//		s1_x = s1rx - s1lx;
//		s1_y = s1ry - s1ly;
//		s2_x = s2rx - s2lx;
//		s2_y = s2ry - s2ly;
//
//		s = (-s1_y * (s1lx - s2lx) + s1_x * (s1ly - s2ly)) / (-s2_x * s1_y + s1_x * s2_y);
//		t = (s2_x * (s1ly - s2ly) - s2_y * (s1lx - s2lx)) / (-s2_x * s1_y + s1_x * s2_y);
//
//		// Collision detected
//		if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
//			i_x = s1lx + (t * s1_x);
//			i_y = s1ly + (t * s1_y);
//			spokeHullIntersectionPoint = new Point2D.Double();
//			spokeHullIntersectionPoint.setLocation(i_x, i_y);
//		}
//		spokeHullIntersects.add(spokeHullIntersectionPoint);
//	}
//}

//List<Point2D.Double> allPoint = new ArrayList<Point2D.Double>(Arrays.asList(allPoints));
//List<Point2D.Double> hullPoint = new ArrayList<Point2D.Double>(Arrays.asList(hullVertex));
//List<Point2D.Double> sites = new ArrayList<Point2D.Double>();
//allPoint.removeAll(hullPoint);
//sites.addAll(allPoint);
//Point2D.Double[] allSites = new Point2D.Double[sites.size()];
//allSites = sites.toArray(new Point2D.Double[sites.size()]);

//	// Find hull edge intersects
//	List<Point3d> spokeHullIntersects = new ArrayList<Point3d>();
//	List<Point2D.Double> spokeHullIntersectsDouble = new ArrayList<Point2D.Double>();
//	// Point3d[] spokeHullIntersects = new Point3d[sitesArrayLength * 2];
//	for (int s = 0; s < numSites; s++) {
//		Point3d sproj = HilbertGeometry.toHomogeneous(sitePoints[s]);
//
//		for (int h = 0; h < numHullVertex; h++) {
//			Point3d hproj = HilbertGeometry.toHomogeneous(hullVertex[h]);
//			Point3d shLine = hproj.crossProduct(sproj); // spoke
//
//			for (int i = 0; i < hullEdgesArrayLength; i++) {
//				Point3d intersection = hullEdges[i].crossProduct(shLine);
//				Point2D.Double intersectionPoint = HilbertGeometry.toCartesian(intersection);
//				
//				// check if the intersection is already in intersection point list
//				if(  Convex.almostContainsElement(spokeHullIntersectsDouble, intersectionPoint) )
//					continue;
//				
//				if( this.isOnConvexBoundary( intersectionPoint ) ) {
//					spokeHullIntersects.add(intersection);
//					spokeHullIntersectsDouble.add(intersectionPoint);
//				}
//			}
//		}
//	}
//	return spokeHullIntersects;

