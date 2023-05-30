package geometry;

import java.awt.geom.Point2D;
import trapmap.Segment;

public class EdgeData {
	public Point2D.Double otherNode;
	public Point2D.Double site;
	public Segment edge;
	
	public EdgeData(Point2D.Double p, Point2D.Double site, Segment s) {
		this.otherNode = p;
		this.site = site;
		this.edge = s;
	}
	
	public EdgeData clone() {
		return new EdgeData(this.otherNode, this.site, this.edge);
	}
	
	public boolean isOnEdge(Point2D.Double p) {
		Double x = p.x;
		Double y = p.y;
		Double ptToSegDistance = Util.distanceXYToSegment(x, y, edge);
		
		if (ptToSegDistance < 0.01) {
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() {
		return "[" + Util.printCoordinate(otherNode) + ", " + Util.printCoordinate(site) + "]";
	}
}
