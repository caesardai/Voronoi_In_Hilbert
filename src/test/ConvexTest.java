package test;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Map;
import java.util.List;

import geometry.Convex;

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
		
		Point2D.Double site = new Point2D.Double(13.8, 14.8);
		
		Map<Point2D.Double, List<Segment>> mapping = c.siteSegment(Arrays.copyOfRange(c.convexHull, 0, c.convexHull.length-1), site);
		
		List<Segment> results = mapping.get(site);
		
		System.out.println("finished");
	}
}
