package test;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import geometry.Bisector;
import geometry.Convex;
import geometry.KdTree;
import geometry.HilbertGeometry;
import geometry.Point3d;
import geometry.Sector;
import geometry.Voronoi;
import geometry.VoronoiCell;
import geometry.Util;

import trapmap.Segment;
import trapmap.TrapMap;
import trapmap.Trapezoid;

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
		for (Point2D.Double p : points)
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
		for (Double[] l : lines)
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

		Point3d spoke = HilbertGeometry.toHomogeneous(site1)
				.crossProduct(HilbertGeometry.toHomogeneous(c.convexHull[2]));

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

		Point2D.Double[] hullVertices = Arrays.copyOfRange(c.convexHull, 0, c.convexHull.length - 1);
		Point2D.Double[] siteVertices = new Point2D.Double[] { site1, site2 };

		// construct segments
		List<Segment> edgeSegments = c.spokeHullIntersection(hullVertices, siteVertices);
		List<Segment> site1Segments = c.spokeIntersects(hullVertices, new Point2D.Double[] { site2 }, site1);
		List<Segment> site2Segments = c.spokeIntersects(hullVertices, new Point2D.Double[] { site1 }, site2);

		// combine lists
		List<Segment> allSegments = new ArrayList<Segment>();
		allSegments.addAll(edgeSegments);
		allSegments.addAll(site1Segments);
		allSegments.addAll(site2Segments);

		// construct graph
		KdTree<KdTree.XYZPoint> tree = new KdTree<KdTree.XYZPoint>(null, 2);

		// insert segments into graph
		for (Segment s : allSegments) {
			Point2D.Double left = Util.toPoint2D(s.getLeftPoint());
			Point2D.Double right = Util.toPoint2D(s.getRightPoint());

			KdTree.KdNode node = KdTree.getNode(tree, Util.toXYZPoint(left));
			if (node == null) {
				tree.add(Util.toXYZPoint(left));
				node = KdTree.getNode(tree, Util.toXYZPoint(left));
			}

			KdTree.XYZPoint point = node.getID();
			point.addNeighbor(right, s.getSite1(), s.getEdge());

			node = KdTree.getNode(tree, Util.toXYZPoint(right));
			if (node == null) {
				tree.add(Util.toXYZPoint(right));
				node = KdTree.getNode(tree, Util.toXYZPoint(right));
			}

			point = node.getID();
			point.addNeighbor(left, s.getSite1(), s.getEdge());
		}

		// construct sectors
//		List<Sector> sectors = Convex.constructSector(site1, site2, tree, v);

		System.out.println("got here");

		System.out.println("look at points");
	}

	// method to test the construction of a given sector
	public static void testConstructSector() {
		HilbertGeometry g = new HilbertGeometry();

		g.convex = new Convex();
		Convex c = g.convex;
		c.addPoint(new Point2D.Double(200d, 200d));
		c.addPoint(new Point2D.Double(700d, 200d));
		c.addPoint(new Point2D.Double(700d, 700d));
		c.addPoint(new Point2D.Double(200d, 700d));
		Point2D.Double site1 = new Point2D.Double(504d, 281d);
		Point2D.Double site2 = new Point2D.Double(382d, 584d);

		// construct graph of sectors
		Voronoi v = new Voronoi(g);
		KdTree<KdTree.XYZPoint> tree = v.constructGraph(site1, site2);

		// construct segment to search from
		KdTree.KdNode node = KdTree.getNode(tree, Util.toXYZPoint(site1));
		KdTree.XYZPoint site1XYZ = node.getID();
		node = KdTree.getNode(tree, Util.toXYZPoint(site1XYZ.getNeighbor(5).otherNode));
		KdTree.XYZPoint n1XYZ = node.getID();
		Point2D.Double s1 = n1XYZ.getNeighbor(1).otherNode;
		Point2D.Double s2 = site2;
		Segment segToSearch = new Segment(Util.toPVector(s1), Util.toPVector(s2));

		// construct sectors
		List<Sector> sectors = c.constructSector(segToSearch, site1, site2, tree);

		System.out.println("Insert breakpoint here!");
	}

	public static void soulCrusher(String convexFile, String siteFile) {
		HilbertGeometry g = new HilbertGeometry();
		g.convex = new Convex();
		Convex c = g.convex;

		// read any files to determine hull of the set
		if (convexFile != null) {
			Point2D.Double[] hullVertices = c.load(convexFile);
			for (int index = 0; index < hullVertices.length; index++)
				c.addPoint(hullVertices[index]);
		} else {
			c.addPoint(new Point2D.Double(200d, 200d));
			c.addPoint(new Point2D.Double(700d, 200d));
			c.addPoint(new Point2D.Double(800d, 700d));
			c.addPoint(new Point2D.Double(300d, 700d));
		}

		// read any files to determine the sites for this diagram
		Point2D.Double site1 = null;
		Point2D.Double site2 = null;
		;
		boolean defaultSites = true;
		Point2D.Double[] siteVertices = null;
		if (siteFile != null) {
			siteVertices = c.load(siteFile);
			if (siteVertices.length != 2)
				defaultSites = false;
			else {
				site1 = new Point2D.Double(599d, 393d);
				site2 = new Point2D.Double(398d, 530d);
			}
		}
		if (defaultSites) {
			site1 = siteVertices[0];
			site2 = siteVertices[1];
		} else {
			site1 = new Point2D.Double(598d, 585d);
			site2 = new Point2D.Double(504d, 281d);
		}

		// print hull vertices
		System.out.print("[");
		for (Point2D.Double p : c.convexHull)
			System.out.print(Util.printCoordinate(p) + ", ");
		System.out.println("]");

		// print site vertices
		System.out.println("[" + Util.printCoordinate(site1) + ", " + Util.printCoordinate(site2) + "]");

		Voronoi v12 = new Voronoi(g);
        ArrayList<VoronoiCell> cells12 = new ArrayList<VoronoiCell>();
        cells12 = v12.realAugusteAlgo(site1, site2);
        System.out.println("---------------------------");
        cells12.get(0).Printer();
        cells12.get(1).Printer();
        System.out.println("Break Point Here!");
//		ArrayList<TrapMap> trapMaps = v.realAugusteAlgo(site1, site2);
//		for (Trapezoid Trap : trapMaps.get(0).findFaceTrapezoids(site1.x,site1.y)) {
//			System.out.println("{"+Trap.toString()+"}");
//		}
//		ArrayList<Bisector> bisectorList = v.realAugusteAlgo(site1, site2);

//		System.out.println("We have " + bisectorList.size() + " bisectors.");
//
//		Pattern p = Pattern.compile("E[0-9]+");
//		// Print Statement
//		// ************************************************************************************
//		int index = 0;
//		// ************************************************************************************
//		for (Bisector b : bisectorList) {
//			String printStatement = b.toString();
//			Matcher m = p.matcher(printStatement);
//			while (m.find()) {
//				String pattern = m.group(0);
//				String replacement = " * 10^{" + pattern.substring(1, pattern.length()) + "}";
//				printStatement = printStatement.replaceAll(pattern, replacement);
//			}
//
//			System.out.println(printStatement + "\\left\\{" + b.getLeftEndPoint().x + " \\le x \\le "
//					+ b.getRightEndPoint().x + "\\right\\}");
//		}
//		System.out.println("Insert breakpoint in here!");
	}

	public static void main(String[] argv) {
//		VoronoiTest.testConstructSector();

//		"src/convexes/hull0";
//		"src/convexes/site0"
		String convexFile = "src/convexes/LoadHull";
		String siteFile = "src/convexes/LoadSites";
		VoronoiTest.soulCrusher(convexFile, siteFile);
	}
}