package geometry;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import trapmap.Segment;

/**
 * Object to store the boundary of the Sector and any important metadata about the boundary 
 * 
 * @author sam
 */
public class SectorBoundary {
	// stores the vertices of the sector in a counter-clockwise manner
	private Point2D.Double[] vertices;
	
	// for asking point query questions
	public Convex c;
	
	// for each segment that makes up the sector, store whether that segment comes a site's spoke or null if it is on an edge 
	// MAYBE THIS SHOULD BE A SEGMENT TO STORE CONVEX HULL EDGE INFO
	private Point2D.Double[] segmentOrigin;
	

	// assume that for element at index i, elements (i-1)%N and (i+1)%N share distinct end points with element i
	// we can assume this since another algorithm constructs segments passed in to construct these segments
	/**
	 * Constructor for SegmentBoundary. Initializes the vertices of the sector. Also associates an edge with the site that edge is associated with
	 * 
	 * @param segs the segments that make up the sector. assume that the segments are ordered corrected
	 */
	public SectorBoundary(ArrayList<Segment> segs) {
		if(segs.size() < 1) {
			this.vertices = null;
			this.segmentOrigin = null;
			return;
		}
		
		this.vertices = new Point2D.Double[segs.size()];
		this.segmentOrigin = new Point2D.Double[segs.size()];
		this.c = new Convex();
		
		for(int index = 0; index < segs.size(); index++) {
			Point2D.Double vertex = Util.toPoint2D(segs.get(index).getLeftPoint());
			this.vertices[index] = vertex;
			this.segmentOrigin[index] = segs.get(index).getSite1();
			this.c.addPoint(vertex);
		}
		
	}
	
	/**
	 * @param index the index to query from the vertex array
	 * @return Returns the i-th vertex of the sector
	 */
	public Point2D.Double getVertex(int index) {
		if(index < 0 || index >= this.vertices.length)
			return null;
		
		return this.vertices[index];
	}
	
	/**
	 * @param index the index of the i-th vertex
	 * @return returns the i-th and (i+1)%N-th line segment
	 */
	public Segment getEdge(int index) {
		return new Segment(
					(float) this.vertices[index].x,
					(float) this.vertices[index].y,
					(float) this.vertices[(index+1) % this.vertices.length].x,
					(float) this.vertices[(index+1) % this.vertices.length].y
				);
	}
	
	/**
	 * @return The number of edges that make up the sector
	 */
	public int getNumEdges() {
		return this.vertices.length;
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
}
