package geometry;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import geometry.Convex;
import trapmap.Segment;

public class Sector {
	// Convex object
	SectorBoundary sector = null;

	// sites
	Point2D.Double site1, site2;

	// Edges
	private Segment edge1;
	private Segment edge2;
	private Segment edge3;
	private Segment edge4;

	public Sector(Point2D.Double site1, Point2D.Double site2, Segment edge1, Segment edge2, Segment edge3,
			Segment edge4, ArrayList<Segment> segs) {
		this.site1 = site1;
		this.site2 = site2;
		this.edge1 = edge1;
		this.edge2 = edge2;
		this.edge3 = edge3;
		this.edge4 = edge4;
		
		sector = new SectorBoundary(segs);
	}
	
	/*
	 * Return site1 value
	 */
	public Point2D.Double getSite1() {
		return this.site1;
	}
	
	/*
	 * Return site2 value
	 */
	public Point2D.Double getSite2() {
		return this.site2;
	}

	/*
	 * Return edge1 value
	 */
	public Segment getEdge1() {
		return this.edge1;
	}
	
	/*
	 * Return edge2 value
	 */
	public Segment getEdge2() {
		return this.edge2;
	}
	
	/*
	 * Return edge3 value
	 */
	public Segment getEdge3() {
		return this.edge3;
	}
	
	/*
	 * Return edge4 value
	 */
	public Segment getEdge4() {
		return this.edge4;
	}
	
	/*
	 * Check if given query point is in the convex object
	 */
	public boolean isInSector(Point2D.Double p) {
		return this.sector.isInConvex(p);
	}
	
	/**
	 * Sorts the edges of the sector to satisfy the assumptions of the bisector
	 * 
	 * @param v Voronoi object used to compute the equidistant point
	 */
	/* MAY NOT BE WELL PLACED HERE */
	public void sortEdges(Voronoi v) {
		// find a spoke on the sector that is associated with a site
		int spokeIndex = 0;
		while(this.sector.getEdgeSite(spokeIndex) == null || spokeIndex >= this.sector.getNumEdges())
			spokeIndex++;
		
		// if an error occurs (all sector should contain one edge 
		if(spokeIndex == this.sector.getNumEdges()) {
			System.out.println("Error: sector is only composed of edges from the convex hull's boundary.");
			return;
		}
		
		// compute spoke
		Segment edge = this.sector.getEdge(spokeIndex);
		Point3d spoke = HilbertGeometry.toHomogeneous(Util.toPoint2D(edge.getLeftPoint())).crossProduct( 
					HilbertGeometry.toHomogeneous(Util.toPoint2D(edge.getRightPoint()))
			);
		
		// compute the bisector
		// we assume that the spoke above comes site1 or site2. anything else is an error
		Point2D.Double s1 = this.sector.getEdgeSite(spokeIndex);
		Point2D.Double s2 = this.site1;
		if(Util.samePoints(s1, this.site1))
			s2 = this.site2;
		Point2D.Double bisector = v.findEquiDistancePoint(s1, s2, spoke);
		
		// determine which edge these points lay on
		// line equations to the four edges associated with sector
		Point3d[] edges = new Point3d[4];
		edges[0] = HilbertGeometry.toHomogeneous(Util.toPoint2D(this.edge1.getLeftPoint())).crossProduct( 
					HilbertGeometry.toHomogeneous(Util.toPoint2D(this.edge1.getRightPoint()))
			);
		edges[1] = HilbertGeometry.toHomogeneous(Util.toPoint2D(this.edge2.getLeftPoint())).crossProduct( 
					HilbertGeometry.toHomogeneous(Util.toPoint2D(this.edge2.getRightPoint()))
			);
		edges[2] = HilbertGeometry.toHomogeneous(Util.toPoint2D(this.edge3.getLeftPoint())).crossProduct( 
					HilbertGeometry.toHomogeneous(Util.toPoint2D(this.edge3.getRightPoint()))
			);
		edges[3] = HilbertGeometry.toHomogeneous(Util.toPoint2D(this.edge4.getLeftPoint())).crossProduct( 
					HilbertGeometry.toHomogeneous(Util.toPoint2D(this.edge4.getRightPoint()))
			);
		Segment[] s = new Segment[] {this.edge1, this.edge2, this.edge3, this.edge4};
		
		// find boundary points
		ArrayList<Point2D.Double> points1 = (ArrayList<Point2D.Double>) Arrays.asList(v.geometry.intersectionPoints(this.site1, bisector));
		ArrayList<Point2D.Double> points2 = (ArrayList<Point2D.Double>) Arrays.asList(v.geometry.intersectionPoints(this.site2, bisector));
		
		// create mapping for the two intersection points. to keep track what edges the intersection points lay on
		Map<Point2D.Double, Segment> mapping1 = new HashMap<Point2D.Double, Segment>();
		for(int i = 0; i <  points1.size(); i++) {
			for(int j = 0; j < edges.length; j++) {
				if( Math.abs(edges[j].scalarProduct(HilbertGeometry.toHomogeneous(points1.get(i)))) < 1e-14 ) {
					mapping1.put(points1.get(i), s[i]);
					break;
				}
			}
		}
		Map<Point2D.Double, Segment> mapping2 = new HashMap<Point2D.Double, Segment>();
		for(int i = 0; i <  points2.size(); i++) {
			for(int j = 0; j < edges.length; j++) {
				if( Math.abs(edges[j].scalarProduct(HilbertGeometry.toHomogeneous(points2.get(i)))) < 1e-14 ) {
					mapping2.put(points2.get(i), s[i]);
					break;
				}
			}
		}
		
		// add all colinear points
		points1.add(this.site1);
		points1.add(bisector);
		points2.add(this.site2);
		points2.add(bisector);

		// sort colinear points; sort x-values if line is not vertical. sort y-values otherwise
		ArrayList<Double> compare1 = new ArrayList<Double>();
		ArrayList<Double> compare2 = new ArrayList<Double>();
		if(this.site1.x - bisector.x == 0) {
			for(Point2D.Double p : points1)
				compare1.add(p.y);
		} else {
			for(Point2D.Double p : points1)
				compare1.add(p.x);
		}
		if(this.site2.x - bisector.x == 0) {
			for(Point2D.Double p : points2)
				compare2.add(p.y);
		} else {
			for(Point2D.Double p : points2)
				compare2.add(p.x);
		}
		Convex.quickSort(points1, compare1, 0, points1.size() - 1);
		Convex.quickSort(points2, compare2, 0, points2.size() - 1);
		
		// check if edge guess are correct
		if( Util.samePoints(points1.get(1), this.site1) ) {
			if(!this.edge2.equals(mapping1.get(points1.get(0)))) {
				Segment temp = this.edge2;
				this.edge2 = this.edge4;
				this.edge4 = temp;
			}
		} else {
			if(!this.edge4.equals(mapping1.get(points1.get(0)))) {
				Segment temp = this.edge2;
				this.edge2 = this.edge4;
				this.edge4 = temp;
			}
		}
		if( Util.samePoints(points2.get(1), this.site2) ) {
			if(!this.edge3.equals(mapping2.get(points2.get(0)))) {
				Segment temp = this.edge3;
				this.edge3 = this.edge1;
				this.edge1 = temp;
			}
		} else {
			if(!this.edge1.equals(mapping2.get(points2.get(0)))) {
				Segment temp = this.edge3;
				this.edge3 = this.edge1;
				this.edge1 = temp;
			}
		}
	}
}




