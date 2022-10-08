package test;

import java.awt.geom.Point2D;
import java.util.LinkedList;

import geometry.Bisector;
import geometry.Convex;
import geometry.Point3d;
import geometry.Voronoi;
import geometry.Util;

import trapmap.Segment;

public class BisectorTest {
	public static void testBisectorLineIntersectionPoints() {
		// construct the convex hull
		Point2D.Double p1 = new Point2D.Double(50d, 50d);
		Point2D.Double p2 = new Point2D.Double(150d, 50d);
		Point2D.Double p3 = new Point2D.Double(150d, 150d);
		Point2D.Double p4 = new Point2D.Double(50d, 150d);
		Convex c = new Convex();
		c.addPoint(p1);
		c.addPoint(p2);
		c.addPoint(p3);
		c.addPoint(p4);
		
		// construct corresponding segments of the edge of the convex hull
		Segment e1 = new Segment((float) p1.x, (float) p1.y, (float) p2.x, (float) p2.y);
		Segment e2 = new Segment((float) p2.x, (float) p2.y, (float) p3.x, (float) p3.y);
		Segment e3 = new Segment((float) p3.x, (float) p3.y, (float) p4.x, (float) p4.y);
		Segment e4 = new Segment((float) p4.x, (float) p4.y, (float) p1.x, (float) p1.y);
		
		// define sites
		Point2D.Double s1 = new Point2D.Double(130d, 69d);
		Point2D.Double s2 = new Point2D.Double(82d, 124d);
		
		// compute bisector
		Bisector b = new Bisector(s1, s2, e1, e3, e2, e4);
		
		// define some line
		Point3d line = new Point3d(-1d, -1d, 180d);

		LinkedList<Point2D.Double> intersectionPoints = b.intersectionPointsWithLine(c, line);
		for(Point2D.Double p : intersectionPoints)
			System.out.println("solution: " + Util.printCoordinate(p));
	}
	
	public static void testProjectiveMatrices() {
		Segment s1 = new Segment(0, 0, 10, 30);
		Segment s2 = new Segment(10, 30, 35, 20);
		Segment s3 = new Segment(35, 20, 40, 5);
		Segment s4 = new Segment(40, 5, 0, 0);
		
		
		Bisector b1 = new Bisector(new Point2D.Double(1, 1), new Point2D.Double(2, 2), s1, s2, s3, s4);
	}
	
	public static void testBisectorComputation() {
		// construct convex hull
		Point2D.Double h1 = new Point2D.Double(50d, 50d);
		Point2D.Double h2 = new Point2D.Double(150d, 50d);
		Point2D.Double h3 = new Point2D.Double(150d, 150d);
		Point2D.Double h4 = new Point2D.Double(50d, 150d);
		
		Convex c = new Convex();
		c.addPoint(h1);
		c.addPoint(h2);
		c.addPoint(h3);
		c.addPoint(h4);
		
		// set sites
		Point2D.Double site1 = new Point2D.Double(130d, 69d);
		Point2D.Double site2 = new Point2D.Double(82d, 124d);
		
		// set edges
		Segment edge1 = new Segment(Util.toPVector(h4), Util.toPVector(h3));
		Segment edge2 = new Segment(Util.toPVector(h1), Util.toPVector(h2));
		Segment edge3 = new Segment(Util.toPVector(h2), Util.toPVector(h3));
		Segment edge4 = new Segment(Util.toPVector(h4), Util.toPVector(h3));
		
		// compute bisector
		Bisector b = new Bisector(site1, site2, edge1, edge2, edge3, edge4);
		System.out.println(b);
	}
	
	public static void main(String[] argv) {
		testBisectorComputation();
	}
}
