package geometry;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import trapmap.Segment;

public class Sector {
	// Convex object
	public Convex sector = null;

	// sites
	Point2D.Double site1, site2;

	// for each segment that makes up the sector, store whether that segment comes a
	// site's spoke or null if it is on an edge
	// MAYBE THIS SHOULD BE A SEGMENT TO STORE CONVEX HULL EDGE INFO
	private Point2D.Double[] segmentOrigin;

	// Edges
	private Segment edge1;
	private Segment edge2;
	private Segment edge3;
	private Segment edge4;

	public Sector(Point2D.Double site1, Point2D.Double site2, Segment edge1, Segment edge2, Segment edge3,
			Segment edge4, ArrayList<Point2D.Double> vertices, ArrayList<Point2D.Double> sites) {

		this.site1 = site1;
		this.site2 = site2;
		this.edge1 = edge1;
		this.edge2 = edge2;
		this.edge3 = edge3;
		this.edge4 = edge4;

		sector = new Convex();
		if(sites != null)
			this.segmentOrigin = new Point2D.Double[vertices.size()];
		else
			this.segmentOrigin = null;
		
		System.out.println("Sector vertices: ");
        for (int i = 0; i < vertices.size(); i++) {
            sector.addPoint(vertices.get(i));
            System.out.println("("+vertices.get(i).x +"," + vertices.get(i).y + ")");
            if(sites != null)
                this.segmentOrigin[i] = sites.get(i);
        }
		
//		for (int i = 0; i < vertices.size(); i++) {
//			sector.addPoint(vertices.get(i));
//			if(sites != null)
//				this.segmentOrigin[i] = sites.get(i);
//		}
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
	
	/**
	 * Method to set the value of site1. Only can be applied if site1 equals null
	 * @param site1 Value to assign field site1
	 */
	public void setSite1(Point2D.Double site1) {
		if(this.site1 == null) this.site1 = site1;
	}

	/**
	 * Method to set the value of site1. Only can be applied if site1 equals null
	 * @param site1 Value to assign field site1
	 */
	public void setSite2(Point2D.Double site2) {
		if(this.site2 == null) this.site2 = site2;
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
	
	/**
	 * @return The number of edges that make up the sector
	 */
	public int getNumEdges() {
		return this.sector.convexHull.length - 1;
	}
	
	/**
	 * @param index the index of the i-th vertex
	 * @return returns the i-th and (i+1)%N-th line segment
	 */
	public Segment getEdge(int index) {
		Point2D.Double[] vertices = this.sector.convexHull;
		return new Segment((float) vertices[index].x, 
				(float) vertices[index].y,
				(float) vertices[(index + 1) % vertices.length].x,
				(float) vertices[(index + 1) % vertices.length].y);
	}
	
	/**
	 * Method to set the values of all edges. Only can be applied if all edges equals null
	 * @param edges Four edges to assign the field edges
	 */
	public void setEdges(Segment[] edges) {
		if(this.edge1 == null && this.edge2 == null && this.edge3 == null && this.edge4 == null) {
			if(edges.length == 4) {
				this.edge1 = edges[0];
				this.edge2 = edges[1];
				this.edge3 = edges[2];
				this.edge4 = edges[3];
			}
		}
	}
	
	/**
	 * Method to set the value of segOrigin. Only can be applied if segOrigin equals null
	 * @param sites the ArrayList that store the sites of the segments of the sector's boundary
	 */
	public void setSegSites(ArrayList<Point2D.Double> sites) {
		if(this.segmentOrigin == null) {
			this.segmentOrigin = new Point2D.Double[sites.size()];
			for(int index = 0; index < sites.size(); index++)
				this.segmentOrigin[index] = sites.get(index);
		}
	}
	
	/*
	 * Check if given query point is in the convex object
	 */
	public boolean isInSector(Point2D.Double p) {
		boolean inInterior = this.sector.isInConvex(p);
		boolean onBoundary = this.sector.isOnConvexBoundary(p);
		return (inInterior || onBoundary);
	}
	
	/**
	 * @param index the index to query from the vertex array
	 * @return Returns the i-th vertex of the sector
	 */
	public Point2D.Double getEdgeSite(int index) {
		if(index < 0 || index >= this.segmentOrigin.length)
			return null;

		return this.segmentOrigin[index];
	}
	
	/**
	 * Method that determines if the vertices of sector object and argument sector s are the same
	 * @param The sector to compare against 
	 * @return True if the two sectors have exactly the same vertices. Otherwise, false
	 */
	public boolean isEqual(Sector s) {
		// array to store vertices of sector s
		List<Point2D.Double> sVertices = Arrays.asList(s.sector.convexHull);
		
		// checks if every vertex of this object is contained in vertex set of s
		for(Point2D.Double v : this.sector.convexHull) {
			if(!sVertices.contains(v))
				return false;
		}
		
		// all vertices are contained in the vertex set of s
		return true;
	}

}
