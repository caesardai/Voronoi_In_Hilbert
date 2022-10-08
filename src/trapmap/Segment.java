package trapmap;

import processing.core.PShape;
import processing.core.PVector;

// modification to original source code
import java.awt.geom.Point2D;

import geometry.Util;

/**
 * Represents a line segment by its endpoints. Endpoints are stored in order as
 * given by the compareTo function of the Point class.
 *
 * @author Tyler Chenhall
 */
public class Segment {

	private PVector lPoint;
	private PVector rPoint;

	PShape faceA; // segment will always have one face
	PShape faceB; // possible (such as mesh)
	
	// for TrapMap
//	private Point2D.Double site1;
//	private Point2D.Double site2;
	
	// for sector graph
//	private Segment edge;

	public Segment(PVector one, PVector two) {
		// we store the left, lower point as lpoint
		// the other point is stored as rpoint
		if (compareTo(one, two) <= 0) {
			lPoint = one;
			rPoint = two;
		} else {
			lPoint = two;
			rPoint = one;
		}
	}
	
	/**
	 * Creates a line segment that preserves the orientation in which p1 and p2 are presented (i.e. p1 -> p2 is the parameterization of this line)
	 * @param p1 End point of line segment.
	 * @param p2 End point of line segment
	 */
	public Segment(Point2D.Double p1, Point2D.Double p2) {
		this.lPoint = Util.toPVector(p1);
		this.rPoint = Util.toPVector(p2);
	}

	/**
	 * Constructs a segment with reference to the polygonal face whose member is the
	 * segment.
	 * 
	 * @param one
	 * @param two
	 * @param face
	 */
	Segment(PVector one, PVector two, PShape face) {
		// we store the left, lower point as lpoint
		// the other point is stored as rpoint
		if (compareTo(one, two) <= 0) {
			lPoint = one;
			rPoint = two;
		} else {
			lPoint = two;
			rPoint = one;
		}
		faceA = face;
	}

	public Segment(float p1X, float p1Y, float p2X, float p2Y) {
		this(new PVector(p1X, p1Y), new PVector(p2X, p2Y));
	}

	/**
	 * Construct Segment with labels of one site
	 */
//	public Segment(float p1X, float p1Y, float p2X, float p2Y, Point2D.Double s1) {
//		this(new PVector(p1X, p1Y), new PVector(p2X, p2Y));
//		this.site1 = s1;
//		this.site2 = null;
//		this.edge = null;
//	}

	/**
	 * Construct Segment with labels of one site
	 */
//	public Segment(float p1X, float p1Y, float p2X, float p2Y, Point2D.Double s1, Point2D.Double s2) {
//		this(new PVector(p1X, p1Y), new PVector(p2X, p2Y));
//		this.site1 = s1;
//		this.site2 = s2;
//		this.edge = null;
//	}

	/**
	 * Get the left segment endpoint (as ordered by the compareTo function of the
	 * Point class).
	 *
	 * @return The left segment endpoint
	 */
	public PVector getLeftPoint() {
		return lPoint;
	}

	/**
	 * Get the right segment endpoint (as ordered by the compareTo function of the
	 * Point class).
	 *
	 * @return The right segment endpoint
	 */
	public PVector getRightPoint() {
		return rPoint;
	}

	/**
	 * Get the minimum x value for a point on the segment. Since the endpoints are
	 * ordered horizontally, this is easy
	 *
	 * @return The minimum x value
	 */
	float getMinX() {
		return lPoint.x;
	}

	/**
	 * Get the maximum x value for a point on the segment.
	 *
	 * @return The maximum x value
	 */
	float getMaxX() {
		return rPoint.x;
	}

	/**
	 * Get the minimum y value for a point on the segment.
	 *
	 * @return The minimum y value
	 */
	float getMinY() {
		return Math.min(lPoint.y, rPoint.y);
	}

	/**
	 * Get the maximum y value for a point on the segment.
	 *
	 * @return The maximum y value
	 */
	float getMaxY() {
		return Math.max(lPoint.y, rPoint.y);
	}
	
	/**
	 * Get the site1 label of the Segment
	 * 
	 * @return site1 label
	 */
//	public Point2D.Double getSite1() {
//		return this.site1;
//	}

	/**
	 * Get the site2 label of the Segment
	 * 
	 * @return site2 label
	 */
//	public Point2D.Double getSite2() {
//		return this.site2;
//	}

	/**
	 * Updates the site1 label to the new site p
	 * 
	 * @param p1, the new site to update site1
	 */
//	public void setSites(Point2D.Double p1) {
//		this.site1 = p1;
//	}

	/**
	 * Updates the site1 label to the new site p1, and updates the site2 label to the new site p2
	 * 
	 * @param p1, the new site to update site1
	 * @param p2, the new site to update site2
	 */
//	public void setSites(Point2D.Double p1, Point2D.Double p2) {
//		this.site1 = p1;
//		this.site2 = p2;
//	}
	
//	public Segment getEdge() {
//		return this.edge;
//	}
	
//	public void setEdge(Segment s) {
//		this.edge = s;
//	}


	/**
	 * Returns the point on the segment at the given x value or the lower endpoint
	 * if the segment is vertical. The behavior for vertical segments may change
	 * later
	 *
	 * @param x The x-value to intersect the line at
	 * @return The point on the line (segment) at the given x-value
	 */
	public PVector intersect(float x) {
		if (lPoint.x != rPoint.x) {
			float ysum = (x - lPoint.x) * (rPoint.y) + (rPoint.x - x) * (lPoint.y);
			float yval = ysum / (rPoint.x - lPoint.x);
			return new PVector(x, yval);
		} else {
			return new PVector(lPoint.x, lPoint.y);
		}
	}

	/**
	 * Calculates the slope of a non vertical segment. If the segment might be
	 * vertical, isVertical should be checked first
	 *
	 * @return the slope (if not vertical) or 0 if it is vertical
	 */
	private double getSlope() {
		if (isVertical()) {
			return 0;
		}
		return (rPoint.y - lPoint.y) / (rPoint.x - lPoint.x);
	}

	/**
	 * Checks if this segment is vertical
	 *
	 * @return True if the segment is vertical
	 */
	private boolean isVertical() {
		return (rPoint.x == lPoint.x);
	}

	/**
	 * Checks to see if this segment object crosses another properly (not a shared
	 * endpoint)
	 *
	 * @param other The other segment to check against
	 * @return True if the segments intersect at a point which is not a common
	 *         vertex
	 */
	boolean crosses(Segment other) {
		// check if x-ranges overlap
		if ((other.lPoint.x > this.rPoint.x) || (other.rPoint.x < this.lPoint.x)) {
			return false;
		}

		// at this point, the x-ranges overlap
		if (this.isVertical() && other.isVertical()) {// they must lie vertically aligned
			if (this.getMaxY() <= other.getMinY() || this.getMinY() >= other.getMaxY()) {
				return false;
			}
			return true;
		} else if (this.isVertical()) {
			PVector p = other.intersect(this.lPoint.x);
			return (p.y > this.getMinY()) && (p.y < this.getMaxY());
		} else { // neither segment is a vertical line
			/*
			 * We use a bounding box technique instead of directly computing the
			 * intersection. It is quite possible we aren't saving any time with this
			 * strategy.
			 */

			// must find the intersection points
			double slope1 = this.getSlope();
			double slope2 = other.getSlope();
			// use slope1 to calculate 3 b's, same for slope2
			double b00 = this.lPoint.y - this.lPoint.x * slope1;
			double b01 = other.lPoint.y - other.lPoint.x * slope1;
			double b02 = other.rPoint.y - other.rPoint.x * slope1;

			double b10 = other.lPoint.y - other.lPoint.x * slope2;
			double b11 = this.lPoint.y - this.lPoint.x * slope2;
			double b12 = this.rPoint.y - this.rPoint.x * slope2;
			if (((b01 <= b00 && b00 <= b02) || (b01 >= b00 && b00 >= b02)) && ((b11 <= b10 && b10 <= b12) || b11 >= b10 && b10 >= b12)) {
				return this.equals(other) || !(this.lPoint.equals(other.lPoint) || this.lPoint.equals(other.rPoint)
						|| this.rPoint.equals(other.lPoint) || this.rPoint.equals(other.rPoint));

			}
		}
		return false;
	}

	@Override
	public boolean equals(Object s) {
		if (!(s instanceof Segment)) {
			return false;
		}
		Segment ss = (Segment) s;
		return ss.lPoint.equals(this.lPoint) && ss.rPoint.equals(this.rPoint);
	}

	@Override
	public String toString() {
		return lPoint + "     " + rPoint;
	}

	@Override
	public int hashCode() {
		// + 1 in y points so (10,5) and (5,10) (for example) hash to different values
		return Float.floatToIntBits(lPoint.x + rPoint.x) ^ Float.floatToIntBits(lPoint.y + rPoint.y + 1);
	}

	private static int compareTo(PVector a, PVector b) {
		if (a.x < b.x || (a.x == b.x && a.y < b.y)) {
			return -1;
		} else if ((a.x == b.x) && (a.y == b.y)) {
			return 0;
		} else {
			return 1;
		}
	}
}
