package geometry;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import trapmap.Segment;

public class VoronoiCell {
	Point2D.Double site;
	// ArrayList<Segment> cellWall = new ArrayList<Segment>();
	// Store the vertices as a list of cells
	ArrayList<Point2D.Double> cellVertices = new ArrayList<Point2D.Double>();

	// constructor
	public VoronoiCell(Point2D.Double site, ArrayList<Point2D.Double> cellVertices) {
		this.site = site;
		// this.cellWall = cellWall;
		this.cellVertices = cellVertices;

		// sort them by angle to the site
		Collections.sort(cellVertices, Comparator.comparingDouble(a -> calculateAnglePointSite(a)));

		// Remove duplicates
		for (int i = 1; i < cellVertices.size(); i++) {
			if (cellVertices.get(i).x == cellVertices.get(i - 1).x
					&& cellVertices.get(i).y == cellVertices.get(i - 1).y) {
				cellVertices.remove(i);
			}

		}

	}

//	public Segment getWallIndex(int index) {
//		return this.cellWall.get(index);
//	}
	public Void Printer() {
		System.out.print("[");
		for (int i = 0; i < cellVertices.size(); i++) {
			Point2D.Double vertice = cellVertices.get(i);
			if (i == cellVertices.size() - 1) {
				System.out.print("(" + vertice.x + "," + vertice.y + ")");
			} else {
				System.out.print("(" + vertice.x + "," + vertice.y + "),");
			}
		}
		System.out.println("]");
		return null;
	}
	
	public ArrayList<Point2D.Double> getVertices() {
		return this.cellVertices;
	}

	public Point2D.Double getVertexIndex(int index) {
		return this.cellVertices.get(index);
	}

	public Point2D.Double getSite() {
		return this.site;
	}

	private double calculateAnglePointSite(Point2D.Double point) {
		double angle = Math.toDegrees(Math.atan2(point.x - site.x, point.y - site.y));
		// Keep angle between 0 and 360
		angle = angle + Math.ceil(-angle / 360) * 360;
		return angle;
	}
//	public VoronoiCell BisectorBetweenVoronoiCells(VoronoiCell OtherCell) {
//		
//	}
}
