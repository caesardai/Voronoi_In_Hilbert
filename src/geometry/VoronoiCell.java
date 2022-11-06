package geometry;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import trapmap.Segment;

public class VoronoiCell {
	Point2D.Double site;
	ArrayList<Segment> cellWall = new ArrayList<Segment>();
	// Store the vertices as a list of cells
	ArrayList<Point2D.Double> cellVertices = new ArrayList<Point2D.Double>();
	
	// constructor
	public VoronoiCell(Point2D.Double site, ArrayList<Segment> cellWall,ArrayList<Point2D.Double> cellVertices) {
		this.site = site;
		this.cellWall = cellWall;
		this.cellVertices = cellVertices;
			
	}
	
	public Segment getWallIndex(int index) {
		return this.cellWall.get(index);
	}
	
	public Point2D.Double getVetexIndex(int index) {
		return this.cellVertices.get(index);
	}
	
	public Point2D.Double getSite() {
		return this.site;
	}
	
}
