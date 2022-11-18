package trapmap;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;

import geometry.Util;

import trapmap.graph.Leaf;

import processing.core.PConstants;
import processing.core.PShape;
import processing.core.PVector;

/**
 * Represents a trapezoid object in the trapezoidal map or search structure.
 * <p>
 * Each trapezoid ∆ is determined by:
 * <li>A bottom segment bottom(∆)</li>
 * <li>A top segment top(∆)</li>
 * <li>A left vertex leftp(∆)</li>
 * <li>A right vertex rightp(∆)</li>
 * 
 * @author Tyler Chenhall
 * @author Michael Carleton
 */
public final class Trapezoid {

	// Neighbors of this trapezoid
	// Two trapezoids are neighbors if they share a vertical edge
	private Trapezoid neighborUL; // upper left
	private Trapezoid neighborLL; // lower left
	private Trapezoid neighborUR; // upper right
	private Trapezoid neighborLR; // lower right
	private Leaf owner;

	// variables describing the trapezoid shape
	private PVector leftP;
	private PVector rightP;
	private Segment topSeg;
	private Segment botSeg;
	private PShape poly; // polygonal representation of trapezoid
	private List<PVector> polyVertices;
	
	// determine Voronoi cell Trapezoid belongs to
	private Point2D.Double site = null;

	/**
	 * Boolean flag that indicates whether the mapping to the polygonal face this
	 * trapezoid belongs to has been computed.
	 */
	boolean computedFace = false;
	/**
	 * The original polygon face/cell this trapezoid belongs to (computed lazily).
	 * May remain null (and will always be null if TrapMap was created from segments
	 * only).
	 */
	private PShape face = null;

	/**
	 * Constructs a trapezoid object based on the x boundaries and bounding
	 * segments. Sets the neighbor trapezoids to null currently
	 *
	 * @param left   Left x boundary
	 * @param right  Right x boundary
	 * @param top    Segment determining the upper boundary
	 * @param bottom Segment determining the lower boundary
	 */
	Trapezoid(PVector left, PVector right, Segment top, Segment bottom) {
		leftP = left;
		rightP = right;
		topSeg = top;
		botSeg = bottom;

		neighborUL = null;
		neighborLL = null;
		neighborUR = null;
		neighborLR = null;
		owner = null;
		
//		if(topSeg.getSite1() != null && botSeg.getSite1() != null) {
//			if(topSeg.getSite1().equals(botSeg.getSite1()))
//				this.site = topSeg.getSite1();
//
//			else if(topSeg.getSite2() != null) {
//				if(topSeg.getSite2().equals(botSeg.getSite1())) 
//					this.site = topSeg.getSite2();
//			}
//			
//			else if(botSeg.getSite2() != null) {
//				if(topSeg.getSite1().equals(botSeg.getSite2()))
//					this.site = topSeg.getSite1();
//				
//			}
//			
//			else if(topSeg.getSite2() != null && botSeg.getSite2() != null) {
//				if(topSeg.getSite2().equals(botSeg.getSite2())) 
//					this.site = topSeg.getSite2();
//			}
//		}
	}

	/**
	 * Get the left bounding point
	 * 
	 * @return The left vertex
	 */
	public PVector getLeftBound() {
		return leftP;
	}

	/**
	 * Get the right bounding point
	 * 
	 * @return The right bounding vertex
	 */
	public PVector getRightBound() {
		return rightP;
	}

	/**
	 * Get the lower bounding segment
	 * 
	 * @return The lower segment
	 */
	public Segment getLowerBound() {
		return botSeg;
	}

	/**
	 * Get the upper bounding segment for the trapezoid
	 * 
	 * @return The upper segment
	 */
	public Segment getUpperBound() {
		return topSeg;
	}

	/**
	 * Get the trapezoid which lies to the left of this trapezoid below the left
	 * boundary vertex
	 * 
	 * @return The lower left neighbor (possibly null)
	 */
	public Trapezoid getLowerLeftNeighbor() {
		return neighborLL;
	}

	/**
	 * Get the trapezoid which lies to the left of this one, above the left boundary
	 * vertex
	 * 
	 * @return the upper left neighbor trapezoid (possibly null)
	 */
	public Trapezoid getUpperLeftNeighbor() {
		return neighborUL;
	}

	public Trapezoid getLowerRightNeighbor() {
		return neighborLR;
	}

	public Trapezoid getUpperRightNeighbor() {
		return neighborUR;
	}
	
	public Point2D.Double getSite() {
		return this.site;
	}

	void setLowerLeftNeighbor(Trapezoid t) {
		neighborLL = t;
	}

	void setUpperLeftNeighbor(Trapezoid t) {
		neighborUL = t;
	}

	void setLowerRightNeighbor(Trapezoid t) {
		neighborLR = t;
	}

	void setUpperRightNeighbor(Trapezoid t) {
		neighborUR = t;
	}

	/**
	 * Set the leaf which contains this trapezoid
	 * 
	 * @param l The leaf containing this trapezoid
	 */
	void setLeaf(Leaf l) {
		owner = l;
	}

	/**
	 * Get the leaf containing this trapezoid
	 * 
	 * @return The leaf pointing to this trapezoid
	 */
	Leaf getLeaf() {
		return owner;
	}

	/**
	 * Gets the mapped polygonal face that this trapezoid is a part of.
	 * 
	 * @return Null if trapezoid lies outside polygons, or no polygons were set up.
	 */
	public PShape getFace() {
		if (!computedFace) {
			final PShape f1 = topSeg.faceA;
			final PShape f2 = topSeg.faceB;
			final PShape f3 = botSeg.faceA;
			final PShape f4 = botSeg.faceB;

			/*
			 * If the trapezoid is mapped to a face, then the polygonal face in which the
			 * trapezoid lies can be computed by first retrieving the enclosing segments,
			 * and then finding the face that is shared by two of these segments (this is
			 * the face that is properly enclosed by the trapezoid's top and bottom
			 * segments). NOTE doesn't always work on very concave shapes.
			 */
			if (f1 != null) {
				if (f1 == f2 || f1 == f3 || f1 == f4) {
					face = f1;
					computedFace = true;
					return face;
				}
			}
			if (f2 != null) {
				if (f2 == f3 || f2 == f4) {
					face = f2;
					computedFace = true;
					return face;
				}
			}
			if (f3 != null) {
				if (f3 == f4) {
					face = f3;
					computedFace = true;
					return face;
				}
			}
		}

		return face;
	}

	/**
	 * Return the boundary polygon for this trapezoid
	 * 
	 * @return The boundary Polygon
	 */
	public PShape getBoundaryPolygon() {
		if (poly == null) {
			poly = getPrivateBoundaryPolygon(leftP, rightP, topSeg, botSeg);
		}
		return poly;
	}

	/**
	 * Gets the four coordinates that make up this trapezoid (from top left
	 * clockwise).
	 * 
	 * @return
	 */
	public List<PVector> getBoundaryVertices() {
		if (poly == null) {
			poly = getPrivateBoundaryPolygon(leftP, rightP, topSeg, botSeg);
		}
		return polyVertices;
	}

	/**
	 * Returns the boundary of the trapezoid as a Polygon object for easy display.
	 *
	 * @return The polygon object representing the boundary of the Trapezoid
	 */
	private PShape getPrivateBoundaryPolygon(PVector left, PVector right, Segment top, Segment bottom) {
		final PVector tl = top.intersect(left.x);
		final PVector tr = top.intersect(right.x);
		final PVector bl = bottom.intersect(left.x);
		final PVector br = bottom.intersect(right.x);
		polyVertices = Arrays.asList(tl, tr, br, bl);

		final PShape polygon = new PShape(PShape.PATH);
		// polygon.setFamily(PShape.PATH);
		polygon.setFill(true);
		polygon.setFill(-255);
		polygon.beginShape();
		polygon.vertex((int) tl.x, (int) tl.y);
		polygon.vertex((int) tr.x, (int) tr.y);
		polygon.vertex((int) br.x, (int) br.y);
		polygon.vertex((int) bl.x, (int) bl.y);
		polygon.endShape(PConstants.CLOSE);
		return polygon;
	}

	/**
	 * Return true if this trapezoid has zero width
	 * 
	 * @return True if the trapezoid is a sliver with zero width
	 */
	boolean hasZeroWidth() {
		return leftP.x == rightP.x;
	}

	boolean hasZeroHeight() {
		return leftP.y == rightP.y;
	}

	@Override
	public String toString() {
		final PVector tl = topSeg.intersect(leftP.x);
		final PVector tr = topSeg.intersect(rightP.x);
		final PVector bl = botSeg.intersect(leftP.x);
		final PVector br = botSeg.intersect(rightP.x);
		String strSite = "";
		if(this.site == null)
			strSite = "null";
		else
			strSite = Util.printCoordinate(this.site);

		return String.join(", ", tl.toString(), tr.toString(), br.toString(), bl.toString(), strSite);
	}

	@Override
	public int hashCode() {
		return (topSeg.hashCode()) ^ botSeg.hashCode() ^ leftP.hashCode() ^ rightP.hashCode();
	}

	@Override
	/**
	 * Two trapezoids are equal iff they have the same bounding segments
	 */
	public boolean equals(Object t) {
		if (!(t instanceof Trapezoid)) {
			return false;
		}
		final Trapezoid tt = (Trapezoid) t;
		return (this.topSeg.equals(tt.topSeg) && this.botSeg.equals(tt.botSeg));
	}
}
