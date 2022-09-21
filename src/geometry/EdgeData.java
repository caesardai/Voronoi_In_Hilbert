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
	
	public String toString() {
		return "[" + Util.printCoordinate(otherNode) + ", " + Util.printCoordinate(site) + "]";
	}
}
