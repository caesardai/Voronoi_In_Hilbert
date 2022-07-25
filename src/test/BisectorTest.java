package test;

import java.awt.geom.Point2D;
import java.util.LinkedList;

import geometry.Bisector;
import geometry.Convex;
import geometry.Voronoi;
import geometry.Util;

import micycle.trapmap.Segment;

public class BisectorTest {
	public static void testBisectorLineIntersectionPoints() {
		Point2D.Double s1 = new Point2D.Double(23, 20);
		Point2D.Double s2 = new Point2D.Double(28, 12);
		Point2D.Double p1 = new Point2D.Double(0, 0);
		Point2D.Double p2 = new Point2D.Double(10, 30);
		Point2D.Double p3 = new Point2D.Double(35, 20);
		Point2D.Double p4 = new Point2D.Double(40, 5);
		Convex c = new Convex();
		c.addPoint(p1);
		c.addPoint(p2);
		c.addPoint(p3);
		c.addPoint(p4);
		Segment e1 = new Segment((float) p1.x, (float) p1.y, (float) p2.x, (float) p2.y);
		Segment e2 = new Segment((float) p2.x, (float) p2.y, (float) p3.x, (float) p3.y);
		Segment e3 = new Segment((float) p3.x, (float) p3.y, (float) p4.x, (float) p4.y);
		Segment e4 = new Segment((float) p4.x, (float) p4.y, (float) p1.x, (float) p1.y);
		Bisector b = new Bisector(s1, s2, e1, e2, e3, e4);

		int n = 253;
		Double[][] lines = Voronoi.thetaRays(s1, n);
		// System.out.println(Util.printLineEq(lines[1]));
		LinkedList<Point2D.Double> intersectionPoints = b.intersectionPointsWithLine(c, lines[56]);
		for(Point2D.Double p : intersectionPoints)
			System.out.println("solution: " + Util.printCoordinate(p));
	}
	
	public static void main(String[] argv) {
		testBisectorLineIntersectionPoints();
	}
}
