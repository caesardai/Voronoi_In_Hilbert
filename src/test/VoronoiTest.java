package test;

import java.awt.geom.Point2D;
import java.util.LinkedList;

import geometry.Convex;
import geometry.HilbertGeometry;
import geometry.Voronoi;
import geometry.Util;

import micycle.trapmap.Segment;

public class VoronoiTest {
	public static void testNewThetaRayTrace() {
		Point2D.Double s1 = new Point2D.Double(23, 20);
		Point2D.Double s2 = new Point2D.Double(28, 12);
		HilbertGeometry g = new HilbertGeometry();
		g.convex = new Convex();
		Voronoi v = new Voronoi(g);
		Point2D.Double p1 = new Point2D.Double(0, 0);
		Point2D.Double p2 = new Point2D.Double(10, 30);
		Point2D.Double p3 = new Point2D.Double(35, 20);
		Point2D.Double p4 = new Point2D.Double(40, 5);
		Segment e1 = new Segment((float) p1.x, (float) p1.y, (float) p2.x, (float) p2.y);
		Segment e2 = new Segment((float) p2.x, (float) p2.y, (float) p3.x, (float) p3.y);
		Segment e3 = new Segment((float) p3.x, (float) p3.y, (float) p4.x, (float) p4.y);
		Segment e4 = new Segment((float) p4.x, (float) p4.y, (float) p1.x, (float) p1.y);

		int n = 400;
		Double[][] lines = Voronoi.thetaRays(s1, n);
		// System.out.println(Util.printLineEq(lines[1]));
		// LinkedList<Point2D.Double> intersectionPoints = v.newthetaRayTrace(null, lines[198], s1, s2, e1, e2, e3, e4);
//		for(Point2D.Double p : intersectionPoints)
//			System.out.println("point: " + Util.printCoordinate(p));
	}
	
	public static void testSpokeAngle() {
		Point2D.Double t1 = new Point2D.Double(0, 0);
		int n = 12;
		int k = 11;
		Double theta = k * 2 * Math.PI / n; 
		Point2D.Double t2 = new Point2D.Double(Math.cos(theta), Math.sin(theta));
		System.out.println("angle: " + Voronoi.spokeAngle(t1, t2) * 180 / Math.PI);
	}
	
	public static void testNewThetaRay() {
		Point2D.Double p1 = new Point2D.Double(0, 0);
		Point2D.Double end1 = new Point2D.Double(1, 0);
		Point2D.Double end2 = new Point2D.Double(-1, 0);
		int n = 4;
		Double[][] lines = Voronoi.thetaRays(p1, end1, end2, n);
		for(Double[] l : lines)
			System.out.println(Util.printLineEq(l));
	}
	
	public static void main(String[] argv) {
		testNewThetaRay();
	}
}