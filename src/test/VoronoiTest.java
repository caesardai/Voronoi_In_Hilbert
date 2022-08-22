package test;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;

import geometry.Bisector;
import geometry.Convex;
import geometry.KdTree;
import geometry.HilbertGeometry;
import geometry.Point3d;
import geometry.Sector;
import geometry.Voronoi;
import geometry.Util;

import trapmap.Segment;

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
		g.convex.addPoint(p1);
		g.convex.addPoint(p2);
		g.convex.addPoint(p3);
		g.convex.addPoint(p4);
		Segment e1 = new Segment((float) p1.x, (float) p1.y, (float) p2.x, (float) p2.y);
		Segment e2 = new Segment((float) p2.x, (float) p2.y, (float) p3.x, (float) p3.y);
		Segment e3 = new Segment((float) p3.x, (float) p3.y, (float) p4.x, (float) p4.y);
		Segment e4 = new Segment((float) p4.x, (float) p4.y, (float) p1.x, (float) p1.y);
		Bisector b = new Bisector(s1, s2, e1, e2, e3, e4, p1, p3);

		int n = 20;
		Double[][] lines = Voronoi.thetaRays(s1, p1, p3, n);
		LinkedList<Point2D.Double> points = Voronoi.newthetaRayTrace(b, g.convex, lines);
		for(Point2D.Double p : points)
			System.out.println("points on bisector: " + Util.printCoordinate(p));
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
	
	public static void testFindEquiDistantPoint() {
		HilbertGeometry g = new HilbertGeometry();
		g.convex = new Convex();
		Convex c = g.convex;
		
		c.addPoint(new Point2D.Double(0d, 0d));
		c.addPoint(new Point2D.Double(100d, 300d));
		c.addPoint(new Point2D.Double(350d, 200d));
		c.addPoint(new Point2D.Double(400d, 50d));
		
		Point2D.Double site1 = new Point2D.Double(82.4d, 85d);
		Point2D.Double site2 = new Point2D.Double(197d, 139.6d);
		
//		Point2D.Double test = new Point2D.Double(223.42520000000002d, 145.60500000000002d);
//		
//		double d = g.distance(site1, test);
		
		Point3d spoke = HilbertGeometry.toHomogeneous(site1).crossProduct(HilbertGeometry.toHomogeneous(c.convexHull[2]));
		
		Voronoi v = new Voronoi(g);
		
		Point2D.Double equidistant = v.findEquiDistancePoint(site1, site2, spoke);
		
		System.out.println("breakpoint here");
	}
	
	public static void testEdgeSort() {
		HilbertGeometry g = new HilbertGeometry();
		g.convex = new Convex();
		Convex c = g.convex;
		Voronoi v = new Voronoi(g);
		
		c.addPoint(new Point2D.Double(154d, 620d));
		c.addPoint(new Point2D.Double(67d, 190d));
		c.addPoint(new Point2D.Double(406d, 20d));
		c.addPoint(new Point2D.Double(488d, 500d));
		
		Point2D.Double site1 = new Point2D.Double(253d, 170d);
		Point2D.Double site2 = new Point2D.Double(343d, 500d);
		
		Point2D.Double[] hullVertices = Arrays.copyOfRange(c.convexHull, 0, c.convexHull.length -1);
		Point2D.Double[] siteVertices = new Point2D.Double[] {site1, site2};

		// construct segments
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
		
		// construct sectors
		List<Sector> sectors = Convex.constructSector(site1, site2, tree, v);

		System.out.println("got here");
		
		System.out.println("look at points");
	}
	
	public static void main(String[] argv) {
		VoronoiTest.testEdgeSort();
	}
}