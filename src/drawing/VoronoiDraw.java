package drawing;

import geometry.KdTree;
import geometry.Point3d;
import geometry.Voronoi;
import geometry.VoronoiCell;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class VoronoiDraw {
	private DrawingApplet frame;
	private Voronoi voronoi;
	private LinkedList<Point3d> colors = new LinkedList<Point3d>();
	private final static Random RANDOM_INT = new Random();
	protected boolean hasChanged = false;

	public VoronoiDraw(HilbertGeometryDraw g, DrawingApplet frame) {
		this.voronoi = new Voronoi(g);
		this.frame = frame;
	}

	/*
	 * Constructs convex from file.
	 */
	public VoronoiDraw(HilbertGeometryDraw g, String filename, DrawingApplet frame) {
		this.voronoi = new Voronoi(g);
		this.frame = frame;

		Point2D.Double[] newPoints = load(filename);
		for (int index = 0; index < newPoints.length; index++) {
			this.addPoint(newPoints[index]);
			g.addCenterPoint(newPoints[index], 1);
		}
		this.voronoi.computeVoronoi();
	}

	public void addPoint(Point2D.Double p) {
		this.voronoi.addPoint(p);
		this.colors.add(new Point3d(RANDOM_INT.nextInt(255), RANDOM_INT.nextInt(255), RANDOM_INT.nextInt(255)));
		this.hasChanged = true;
	}

	public int findPoint(Point2D.Double p) {
		return this.voronoi.findPoint(p);
	}

	public int numPoints() {
		return this.voronoi.centerPoints.size();
	}

	public Point2D.Double getPoint(int index) {
		return this.voronoi.getPoint(index);
	}

	public void removePoint(Point2D.Double p) {
		int i = this.findPoint(p);
		if (i >= 0) {
			this.voronoi.removePoint(p);
			this.colors.remove(i);
		}
		this.hasChanged = true;
	}

	public void movePoint(int index, Point2D.Double p) {
		this.voronoi.movePoint(index, p);
		Point3d colors = this.colors.get(index);
		this.colors.remove(index);
		this.colors.add(colors);
		this.hasChanged = true;
		this.computeVoronoi();
	}

	public void reset() {
		this.voronoi.reset();
		this.colors = new LinkedList<Point3d>();
		this.hasChanged = true;
	}

	public void computeVoronoi() {
		this.voronoi.computeVoronoi();
	}
	
	public Set<Point2D.Double> computeHilbertVoronoi(Point2D.Double p1, Point2D.Double p2) {
		return this.voronoi.computeHilbertVoronoi(p1, p2);
	}
	
	public ArrayList<VoronoiCell> realAugusteAlgo(Point2D.Double p1, Point2D.Double p2) {
		return this.voronoi.realAugusteAlgo(p1, p2);
	}
	
	public void drawRays(Point2D.Double p) {

	}

	public LinkedList<Point2D.Double> thetaRayTrace(DrawingApplet frame, Double[] lines, Point2D.Double site) {
		return this.voronoi.thetaRayTrace(this.frame, lines, site);
	}

	public LinkedList<Point2D.Double> augustAlgoWeak(Point2D.Double hull, Point2D.Double site) {
		LinkedList<Point2D.Double> bisectorPoint = new LinkedList<Point2D.Double>();
		bisectorPoint = this.voronoi.augustAlgoWeak(hull, site);
		return bisectorPoint;
	}

	public void drawPoints() {
		synchronized (this) {
			int N = this.voronoi.centerPoints.size();
			if (N == 0)
				return;
			for (Point2D.Double p : this.voronoi.voronoiPoints.keySet()) {
				int siteIndex = this.voronoi.voronoiPoints.get(p);
				Point3d color;
				// if our selected point is equidistant between two sites
				if (siteIndex == this.voronoi.centerPoints.size()) {
					color = new Point3d(0, 0, 0);
					// System.out.println("Found equidistant points");
					this.frame.fill((float) color.x, (float) color.y, (float) color.z);
					this.frame.stroke((float) color.x, (float) color.y, (float) color.z);
					this.frame.ellipse((float) p.x, (float) p.y, 1, 1);
				} else {
					color = this.colors.get(siteIndex);
					this.frame.fill((float) color.x, (float) color.y, (float) color.z);
					this.frame.stroke((float) color.x, (float) color.y, (float) color.z);
					this.frame.ellipse((float) p.x, (float) p.y, 1, 1);
				}

			}

			for (int i = 0; i < N; i++) {
				Point3d color = this.colors.get(i);
				Point2D.Double p = this.voronoi.centerPoints.get(i);
				this.frame.fill((float) color.x, (float) color.y, (float) color.z);
				this.frame.stroke(255, 255, 255);
				this.frame.ellipse((float) p.x, (float) p.y, 10, 10);
			}
		}
		this.frame.fill(0, 0, 0);
		this.frame.stroke(255, 255, 255);
	}

	public void colorPoint(Point2D.Double p) {
		int nearestPoint = this.voronoi.nearestPoint(p);
		this.voronoi.voronoiPoints.put(p, nearestPoint);
	}

	
	
	/**
	 * Given a convex hull and two sites, this method a construct the graph whose nodes are either the sites or intersection points between a spoke and another spoke or edge. Two points are connected in the graph if the line segment between the two points is contained in either spoke or edge. The line segment cannot contain another intersection point from another spoke/edge
	 * 
	 * @param s1 first site
	 * @param s2 second site
	 * @return returns the graph of the sites and all intersection points between any pair of spokes or edges
	 */
	public KdTree<KdTree.XYZPoint> constructGraph(Point2D.Double s1, Point2D.Double s2) {
		return this.voronoi.constructGraph(s1, s2);
	}

	/* Loads Voronoi points from input file */
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
