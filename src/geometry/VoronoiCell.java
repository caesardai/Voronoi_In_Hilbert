package geometry;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class VoronoiCell {
	Point2D.Double site;
	ArrayList<Bisector> cellWall = new ArrayList<Bisector>();
	// Store the vertices as a list of cells
	ArrayList<Point2D.Double> cellVertices = new ArrayList<Point2D.Double>();
	
	// constructor
	public VoronoiCell(Point2D.Double site, ArrayList<Bisector> cellWall,ArrayList<Point2D.Double> cellVertices) {
		this.site = site;
		this.cellWall = cellWall;
		this.cellVertices = cellVertices;
		
		// constructing the bisector approximation
		for (Bisector wall : cellWall) {
			Point2D.Double leftPoint = wall.getLeftEndPoint();
			Point2D.Double rightPoint = wall.getRightEndPoint();
			
		}
		
	}
	
	
}
