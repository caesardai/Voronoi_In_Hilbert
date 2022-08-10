package geometry;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import geometry.Convex;
import trapmap.Segment;

public class Sector {
	// Convex object
	Convex sector = null;
	// sites
	Point2D.Double site1, site2;
	// Edges
	private Segment edge1;
	private Segment edge2;
	private Segment edge3;
	private Segment edge4;

	public Sector(Point2D.Double site1, Point2D.Double site2, Segment edge1, Segment edge2, Segment edge3,
			Segment edge4, ArrayList<Point2D.Double> points) {
		this.site1 = site1;
		this.site2 = site2;
		this.edge1 = edge1;
		this.edge2 = edge2;
		this.edge3 = edge3;
		this.edge4 = edge4;
		
		sector = new Convex();
		for(Point2D.Double p : points)
			sector.addPoint(p);
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
}




