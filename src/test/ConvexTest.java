package test;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

import geometry.Convex;
import geometry.KdTree;
import geometry.Util;

import trapmap.Segment;

public class ConvexTest {
	public static void testOnBoundary() {
		Convex c = new Convex();
		
		// add points
		c.addPoint(new Point2D.Double(1d, 0d));
		c.addPoint(new Point2D.Double(2d, 1d));
		c.addPoint(new Point2D.Double(1d, 2d));
		c.addPoint(new Point2D.Double(0d, 1d));
		
		// query points
		Point2D.Double p1 = new Point2D.Double(0.5, 1.5);
		Point2D.Double p2 = new Point2D.Double(0.5, 0.5);
		Point2D.Double p3 = new Point2D.Double(1d, 1d);
		Point2D.Double p4 = new Point2D.Double(3.5, 2.5);
		Point2D.Double p5 = new Point2D.Double(0.5, 2.5);
		Point2D.Double p6 = new Point2D.Double(1.5, 1.5);
		
		// determine if points are on the boundary of the convex body
		System.out.println(c.isOnConvexBoundary(p1));
		System.out.println(c.isOnConvexBoundary(p2));
		System.out.println(c.isOnConvexBoundary(p3));
		System.out.println(c.isOnConvexBoundary(p4));
		System.out.println(c.isOnConvexBoundary(p5));
		System.out.println(c.isOnConvexBoundary(p6));
	}
	
	public static void testConstructingSpokesSegments() {
		Convex c = new Convex();
		c.addPoint( new Point2D.Double(3.1, 18.8) );
		c.addPoint( new Point2D.Double(20.6, 28.7) );
		c.addPoint( new Point2D.Double(30.9, 1.6) );
		c.addPoint( new Point2D.Double(11.4, 0.2) );
		
		Point2D.Double site1 = new Point2D.Double(13.8, 14.8);
		Point2D.Double site2 = new Point2D.Double(20.4, 18.1);
		
		c.siteSegment(Arrays.copyOfRange(c.convexHull, 0, c.convexHull.length-1), site2);
		Map<Point2D.Double, List<Segment>> mapping = c.siteSegment(Arrays.copyOfRange(c.convexHull, 0, c.convexHull.length-1), site1);
		
		List<Segment> results = mapping.get(site1);
		
		System.out.println("finished");
	}

	public static void testSpokeIntersects() {
		Convex c = new Convex();
		c.addPoint( new Point2D.Double(3.1, 18.8) );
		c.addPoint( new Point2D.Double(20.6, 28.7) );
		c.addPoint( new Point2D.Double(30.9, 1.6) );
		c.addPoint( new Point2D.Double(11.4, 0.2) );
		
		Point2D.Double site1 = new Point2D.Double(13.8, 14.8);
		Point2D.Double site2 = new Point2D.Double(20.4, 18.1);
		
		List<Segment> results = c.spokeIntersects(Arrays.copyOfRange(c.convexHull, 0, c.convexHull.length-1), new Point2D.Double[] {site1}, site2);
		
		System.out.println("finished");
	}
	
	public static void testSpokeHullIntersect() {
		Convex c = new Convex();
		c.addPoint(new Point2D.Double(6.4, 5.3));
		c.addPoint(new Point2D.Double(16d, 50d));
		c.addPoint(new Point2D.Double(60d, 18d));
		c.addPoint(new Point2D.Double(54d, 3d));

		Point2D.Double site = new Point2D.Double(24d, 14d);
		Point2D.Double s1 = new Point2D.Double(27.6, 27.4);

		Point2D.Double[] hullVertices = Arrays.copyOfRange(c.convexHull, 0, c.convexHull.length - 1);
		Point2D.Double[] siteVertices = new Point2D.Double[] {site, s1};

		List<Segment> intersectionPoints = c.spokeHullIntersection(hullVertices, siteVertices);

		System.out.println("finished");
	}
	
	public static void createSectorGraph() {
		Convex c = new Convex();
		c.addPoint(new Point2D.Double(100d, 100d));
		c.addPoint(new Point2D.Double(540d, 200d));
		c.addPoint(new Point2D.Double(360d, 600));

		Point2D.Double site1 = new Point2D.Double(404d, 253d);
		Point2D.Double site2 = new Point2D.Double(349.5, 406d);
		
		Point2D.Double[] hullVertices = Arrays.copyOfRange(c.convexHull, 0, c.convexHull.length -1);
		Point2D.Double[] siteVertices = new Point2D.Double[] {site1, site2};
		
		List<Segment> edgeSegments = c.spokeHullIntersection(hullVertices, siteVertices);
		List<Segment> site1Segments = c.spokeIntersects(hullVertices, new Point2D.Double[] {site2}, site1);
		List<Segment> site2Segments = c.spokeIntersects(hullVertices, new Point2D.Double[] {site1}, site2);
		
		// combine lists
		List<Segment> allSegments = new ArrayList<Segment>();
		allSegments.addAll(edgeSegments);
		allSegments.addAll(site1Segments);
		allSegments.addAll(site2Segments);
		
		// construct graph
		KdTree<KdTree.XYZPoint> tree = new KdTree<KdTree.XYZPoint>(null, 2);
		
		// insert segments into graph
		for(Segment s : allSegments) {
			Point2D.Double left = Util.toPoint2D(s.getLeftPoint());
			Point2D.Double right = Util.toPoint2D(s.getRightPoint());

			KdTree.KdNode node = KdTree.getNode(tree, Util.toXYZPoint(left));
			if(node == null) {
				tree.add(Util.toXYZPoint(left));
				node = KdTree.getNode(tree, Util.toXYZPoint(left));
			}

			KdTree.XYZPoint point = node.getID();
			point.addNeighbor(right, s.getSite1(), s.getEdge());

			node = KdTree.getNode(tree, Util.toXYZPoint(right));
			if(node == null) {
				tree.add(Util.toXYZPoint(right));
				node = KdTree.getNode(tree, Util.toXYZPoint(right));
			}

			point = node.getID();
			point.addNeighbor(left, s.getSite1(), s.getEdge());
		}
		
		// visualize results
	}
	
	public static void testQuickSort() {
        ArrayList<Point2D.Double> colinear = new ArrayList<Point2D.Double>();
        colinear.add(new Point2D.Double(57.671, 19.282));
        colinear.add(new Point2D.Double(-11.746, -1.521));
        colinear.add(new Point2D.Double(-54.158, -14.231));
        colinear.add(new Point2D.Double(16.7, 7.004));
        colinear.add(new Point2D.Double(-28.002, -6.392));
        colinear.add(new Point2D.Double(58.951, 19.666));
        colinear.add(new Point2D.Double(51.668, 17.484));
        colinear.add(new Point2D.Double(-52.655, -13.78));
        colinear.add(new Point2D.Double(40.823, 14.234));
        colinear.add(new Point2D.Double(-9.526, -0.855));

        ArrayList<Double> compare = new ArrayList<Double>();
        for(Point2D.Double p : colinear)
            compare.add(p.x);

        System.out.println("before");
        System.out.println(compare);

        Convex.quickSort(colinear, compare, 0, colinear.size()-1);

        System.out.println("after");
        System.out.println(compare);

        colinear.clear();
        compare.clear();

        for(int i = 0; i < 10; i++) 
            colinear.add(new Point2D.Double(1d, Math.random() * 200 - 100));
        for(Point2D.Double p : colinear)
            compare.add(p.y);

        System.out.println("before");
        System.out.println(compare);

        Convex.quickSort(colinear, compare, 0, colinear.size()-1);

        System.out.println("after");
        System.out.println(compare);

    }

	
	public static void main(String[] argv) {
		ConvexTest.testQuickSort();
	}
}

/*
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
			PPVector					double s1lx = s1l.x;
			double s1ly = s1l.y;
			// seg1LeftPoint.setLocation(s1l.x, s1l.y);
			PVector s1r = s1.getRightPoint();
			double s1rx = s1r.x;
			double s1ry = s1r.y;P
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
*/