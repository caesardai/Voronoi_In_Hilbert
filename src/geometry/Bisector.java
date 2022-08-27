package geometry;

import java.awt.geom.Point2D;
import java.util.LinkedList;

import trapmap.Segment;

import Jama.Matrix;

/*
 * Class to store bisector for some given sector
 * Notation:
 * - if p1 and p2 are points, then p1-p2 is the line segment with endpoints p1 and p2
 * Assumptions:
 * - let X be a point on the bisector
 * - let S be the sector which the bisector corresponds to
 * - segment site1-X passes through edges edge2 and edge4
 * - segment site2-X passes through edges edge1 and edge3
 * - X is closer to edge4 with respect to the Euclidean distance
 * - X is closer to edge1 with respect to the Euclidean distance
 * - site1 is closer to edge2 with respect to the Euclidean distance
 * - site2 is closer to edge3 with respect to the Euclidean distance
 */
public class Bisector {
	// constants for conic; equation for conic: Ax^2 + By^2 + Cxy + Dx + Ey + F = 0
	// FOR NEW METHOD: these coefficients are for the bisector in the projected unit square
	private Double A;
	private Double B;
	private Double C;
	private Double D;
	private Double E;
	private Double F;

	// other relevant constants
	private Double K;
	private Double s;
	
	// check if constants are computed
	private boolean constantsComputed;
	
	// sites
	private Point2D.Double site1;
	private Point2D.Double site2;
	
	// edges
	private Segment edge1; 
	private Segment edge2;
	private Segment edge3;
	private Segment edge4;
	private int uniqueEdges;
	private Point3d line1; // a_1 x + a_2 y + a_3 = 0
	private Point3d line2; // b_1 x + b_2 y + b_3 = 0
	private Point3d line3; // c_1 x + c_2 y + c_3 = 0
	private Point3d line4; // d_1 x + d_2 y + d_3 = 0
	
	// end points
	private Point2D.Double leftEndPoint;
	private Point2D.Double rightEndPoint;
	
	// projective matrix transformation
	private Matrix P;
	private Matrix inverseP;
	
	// FOR DEBUGGING PURPOSES
	public Bisector() {}
	
	// constructors - end points unknown
	public Bisector(Point2D.Double site1, Point2D.Double site2, Segment edge1, Segment edge2, Segment edge3, Segment edge4) {
		this.site1 = site1;
		this.site2 = site2;
		this.edge1 = edge1;
		this.edge2 = edge2;
		this.edge3 = edge3;
		this.edge4 = edge4;
		this.leftEndPoint = null;
		this.rightEndPoint = null;
		this.constantsComputed = false;
		this.P = null;
		this.inverseP = null;
		this.computeEdgeLineEquations();
		this.computeProjectiveMatrices();
		this.computeBisector();
	}

	// constructors - end points known
	public Bisector(Point2D.Double site1, Point2D.Double site2, Segment edge1, Segment edge2, Segment edge3, Segment edge4, Point2D.Double leftEndPoint, Point2D.Double rightEndPoint) {
		this.site1 = site1;
		this.site2 = site2;
		this.edge1 = edge1;
		this.edge2 = edge2;
		this.edge3 = edge3;
		this.edge4 = edge4;
		this.leftEndPoint = leftEndPoint;
		this.rightEndPoint = rightEndPoint;
		this.constantsComputed = false;
		this.P = null;
		this.inverseP = null;
		this.computeEdgeLineEquations();
		this.computeProjectiveMatrices();
		this.computeBisector();
	}
	
	/**
	 * Computes the coefficients of the bisector in its conic form
	 */
	public void computeBisector() {
		/* OLD METHOD OF COMPUTING THE BISECTORS
		 * -----------------------------------------------------------------------------------------------------------------------------------
		// ensure that all necessary fields are not null
		if(this.site1 == null || this.site2 == null || this.edge1 == null || this.edge2 == null || this.edge3 == null || this.edge4 == null)
			return;
		
		// compute equations of relevant lines on the boundary of the convex body
		this.computeEdgeLineEquations();
		
		// compute constants
		this.K = (Math.abs(line4.x * site1.x + line4.y * site1.y + line4.z) / Math.abs(line2.x * site1.x + line2.y * site1.y + line2.z))
				* (Math.abs(line3.x * site2.x + line3.y * site2.y + line3.z) / Math.abs(line1.x * site2.x + line1.y * site2.y + line1.z));
		this.s = 1d;
		
		// compute coefficients of bisector curve
		this.A = line3.x * line4.x - this.K * this.s * (line1.x * line2.x); 
		this.B = line3.y * line4.y - this.K * this.s * line1.y * line2.y; 
		this.C = line3.y * line4.x + line3.x * line4.y - this.K * this.s * (line1.y * line2.x + line1.x * line2.y); 
		this.D = line3.z * line4.x + line3.x * line4.z - this.K * this.s * (line1.z * line2.x + line1.x * line2.z); 
		this.E = line3.z * line4.y + line3.y * line4.z - this.K * this.s * (line1.z * line2.y + line1.y * line2.z); 
		this.F = line3.z * line4.z - this.K * this.s * (line1.z * line2.z); 

		 * -----------------------------------------------------------------------------------------------------------------------------------
		 */
		
		// in the four edge case
		if(this.uniqueEdges == 4) {
			// ensure that projective matrices have been computed
			if(this.P == null || this.inverseP == null)
				this.computeProjectiveMatrices();
			
			// determine sites in the projected unit square
			Point2D.Double pSite1 = Bisector.convertToPoint2D( this.P.times( Bisector.convertToMatrix(this.site1) ) );
			Point2D.Double pSite2 = Bisector.convertToPoint2D( this.P.times( Bisector.convertToMatrix(this.site2) ) ); // let this be point C in math: (c, d)
			
			// determine the point with smaller y-coordinate
			Point2D.Double top = pSite1;
			Point2D.Double bottom = pSite2;
			if(pSite1.y < pSite2.y) {
				top = pSite2;
				bottom = pSite1;
			}
			
			// compute the coefficients depending on the quadrant that top lies on the plane centered at bottom 
			if(bottom.x < top.x) {
				this.A = 0d;
				this.B = 0d;
				this.C = -(bottom.x + top.y - 1);
				this.D = bottom.x * top.y;
				this.E = bottom.x * top.y;
				this.F = - bottom.x * top.y;
			} else {
				this.A = 0d;
				this.B = 0d;
				this.C = bottom.x - top.y;
				this.D = top.y * (1 - bottom.x);
				this.E = bottom.x * (top.y - 1);
				this.F = 0d;
			}

			this.constantsComputed = true;
		}
		// in the three edge case
		else if(this.uniqueEdges == 3) {
			// TBD
		}
		// in the two edge case
		// assume the 
		else if(this.uniqueEdges == 2) {
			this.K = (Math.abs(line4.x * site1.x + line4.y * site1.y + line4.z) / Math.abs(line2.x * site1.x + line2.y * site1.y + line2.z))
				* (Math.abs(line3.x * site2.x + line3.y * site2.y + line3.z) / Math.abs(line1.x * site2.x + line1.y * site2.y + line1.z));
		}
	}
	
	/**
	 * Given some x, compute the corresponding real y value on the conic
	 * 
	 * @param x the x-value to plug into the parameterized function of the bisector
	 * @return the corresponding y-value on the bisector
	 */
	public Double computeY(Double x) {
		// compute constants if not already computed
		if(!this.constantsComputed)
			this.computeBisector();
		
		/*
		// determine constants for this parameterized (in terms of x) conic
		Double K1 = this.B;
		Double K2 = this.C * x + this.E;
		Double K3 = this.A * Math.pow(x, 2) + this.D * x + this.F;
		
		// determine if discriminant is negative or not; if negative, no real solution exist, otherwise, real solutions exist
		Double discriminant = Math.pow(K2, 2) - 4 * K1 * K3;
		if(discriminant < 0)
			return new Double[0];
		
		Double[] solutions = new Double[2];
		 solutions[0] = (-K2 + Math.sqrt(discriminant)) / (2 * K1);
		 solutions[1] = (-K2 - Math.sqrt(discriminant)) / (2 * K1);
		
		 return solutions;
		 */
		
		Double numerator = this.D * x + this.F;
		Double denominator = this.C * x + this.E;
		if(denominator == 0d) {
			System.out.println("division by zero error");
			return Double.MIN_VALUE;
		} else
			return - numerator / denominator;
	}

	/*
	 * Given some y, compute the corresponding real x value on the conic
	 */
	public Double computeX(Double y) {
		// compute constants if not already computed
		if(!this.constantsComputed)
			this.computeBisector();
		
		/*
		// determine constants for this parameterized (in terms of x) conic
		Double K1 = this.A;
		Double K2 = this.C * y + this.D;
		Double K3 = this.B * Math.pow(y, 2) + this.E * y + this.F;
		
		// determine if discriminant is negative or not; if negative, no real solution exist, otherwise, real solutions exist
		Double discriminant = Math.pow(K2, 2) - 4 * K1 * K3;
		if(discriminant < 0)
			return new Double[0];
		
		Double[] solutions = new Double[2];
		 solutions[0] = (-K2 + Math.sqrt(discriminant)) / (2 * K1);
		 solutions[1] = (-K2 - Math.sqrt(discriminant)) / (2 * K1);
		
		 return solutions;
		 */

		Double numerator = this.E * y + this.F;
		Double denominator = this.C * y + this.D;
		if(denominator == 0d) {
			System.out.println("division by zero error");
			return Double.MIN_VALUE;
		} else
			return - numerator / denominator;
	}
	
	/**
	 * Given a line, determine all intersection between the bisector and this line
	 * 
	 * @param line the line in the projected square that intersections the projected bisector
	 * @return all intersections between the line and the bisector
	 */
	public LinkedList<Point2D.Double> intersectionPointsWithLine(Double[] line) {
		// list of intersection points
		LinkedList<Point2D.Double> intersectionPoints = new LinkedList<Point2D.Double>();
		
		// ensure that all necessary fields are not null
		if(this.site1 == null || this.site2 == null || this.edge1 == null || this.edge2 == null || this.edge3 == null || this.edge4 == null)
			return null;
		
		if(line.length != 3)
			return null;
		
		// compute constants if not already computed
		if(!this.constantsComputed)
			this.computeBisector();
		
		// compute coefficients of the quadratic to determine intersection points
		Double K1 = this.C * line[0];
		Double K2 = this.E * line[0] + this.C * line[2] - this.D * line[1];
		Double K3 = this.E * line[2] - this.F * line[1];
		
		// compute discriminant
		Double discriminant = Math.pow(K2, 2) - 4 * K1 * K3; 
		
		// determine if the discriminant is zero
		if(K1 == 0d || discriminant < 0)
			return intersectionPoints;
		
		// get potential solutions
		Double x1 = (-K2 + Math.sqrt(discriminant)) / (2 * K1);
		Double x2 = (-K2 - Math.sqrt(discriminant)) / (2 * K1);
		Point2D.Double solution1 = new Point2D.Double(x1, this.computeY(x1));
		Point2D.Double solution2 = new Point2D.Double(x2, this.computeY(x2));
		
		// determine if solution is in the convex body
		if( (solution1.x >= 0 && solution1.x <= 1) && (solution1.y >= 0 && solution1.y <= 1) )
			intersectionPoints.add(solution1);
		if( (solution2.x >= 0 && solution2.x <= 1) && (solution2.y >= 0 && solution2.y <= 1) )
			intersectionPoints.add(solution2);
		
		// return all intersection points
		return intersectionPoints;
		
		/*
		Double dX = this.computeDiscriminantX(line);
		Double dY = this.computeDiscriminantY(line);
	
		LinkedList<Point2D.Double> xIntersect = new LinkedList<Point2D.Double>();
		LinkedList<Point2D.Double> yIntersect = new LinkedList<Point2D.Double>();
		
		if(dX >= 0) {
			Double[] solutionsX = this.intersectionBisectorLineX(line);
			for(Double x : solutionsX)
				xIntersect.add(new Point2D.Double(x, - line[0] / line[1] * x - line[2] / line[1]));
		}
		if(dY >= 0) {
			Double[] solutionsY = this.intersectionBisectorLineY(line);
			for(Double y : solutionsY)
				yIntersect.add(new Point2D.Double(- line[1] / line[0] * y - line[2] / line[0], y));
		}
		
		// check if the points computed above map to the same point using the conic equations
		// Double error = 1e-8;
		for(Point2D.Double p : xIntersect) {
			// check if the point p is in the convex body; if not, move to next point
			if(!c.isInConvex(p)) {
				// check if the point is on the boundary of the convex body. this checks for the closure of the convex body
				boolean onEdge = false;
				Point3d homogenousP = HilbertGeometry.toHomogeneous(p);
				for(int index = 0; index < c.convexHull.length-1; index++) {
					Point3d edge = Bisector.computeLineEquation(c.convexHull[index], c.convexHull[index+1]);
					if(Math.abs(edge.scalarProduct(homogenousP)) <= 1e-8) {
						onEdge = true;
						break;
					}
				}
				if(!onEdge)
					continue;
			}
			
			// compute y value for given x values
			Double[] yPoints = this.computeY(p.x);
			Point2D.Double test1 = new Point2D.Double(p.x, yPoints[0]);
			Point2D.Double test2 = new Point2D.Double(p.x, yPoints[1]);
			if(Util.roughlySamePoints(p, test1, error)) {
				if(!Bisector.listRouglyContainsPoint(intersectionPoints, p, error))
					intersectionPoints.add(p);
			}
			else if(Util.roughlySamePoints(p, test2, error)) {
				if(!Bisector.listRouglyContainsPoint(intersectionPoints, p, error))
					intersectionPoints.add(p);
			}
		}
		for(Point2D.Double p : yIntersect) {
			// check if the point p is in the convex body; if not, move to next point
			if(!c.isInConvex(p)) {
				// check if the point is on the boundary of the convex body. this checks for the closure of the convex body
				boolean onEdge = false;
				Point3d homogenousP = HilbertGeometry.toHomogeneous(p);
				for(int index = 0; index < c.convexHull.length-1; index++) {
					Point3d edge = Bisector.computeLineEquation(c.convexHull[index], c.convexHull[index+1]);
					if(Math.abs(edge.scalarProduct(homogenousP)) <= 1e-8) {
						onEdge = true;
						break;
					}
				}
				if(!onEdge)
					continue;
			}
			
			// compute y value for given x values
			Double[] xPoints = this.computeX(p.y);
			Point2D.Double test1 = new Point2D.Double(xPoints[0], p.y);
			Point2D.Double test2 = new Point2D.Double(xPoints[1], p.y);
			if(Util.roughlySamePoints(p, test1, error)) {
				if(!Bisector.listRouglyContainsPoint(intersectionPoints, p, error))
					intersectionPoints.add(p);
			}
			else if(Util.roughlySamePoints(p, test2, error)) {
				if(!Bisector.listRouglyContainsPoint(intersectionPoints, p, error))
					intersectionPoints.add(p);
			}
		}
		*/
	}
	
	/*
	 * Computes an approximate solution to the intersection to two conics/bisectors
	 */
	public static LinkedList<Point2D.Double> computeTwoBisectorIntersection(Bisector b1, Bisector b2) {
		// express all variables in a nice manner
		Double A1 = b1.D;
		Double B1 = b1.C;
		Double C1 = b1.E;
		Double D1 = b1.F;

		Double A2 = b2.D;
		Double B2 = b2.C;
		Double C2 = b2.E;
		Double D2 = b2.F;
		
		// list of intersection points
		LinkedList<Point2D.Double> intersectionPoints = new LinkedList<Point2D.Double>();
		
		// coefficients of quadratic to solve
		Double G1 = A1 * B2 - A2 * B1;
		Double G2 = A1 * C2 + B2 * D1 - A2 * C1 - B1 * D2;
		Double G3 = C2 * D1 - C1 * D2;
		
		// compute discriminant
		Double discriminant = Math.pow(G2, 2) - 4 * G1 * G3;
		
		// determine if quadratic is solvable
		if(G1 == 0 || discriminant < 0)
			return intersectionPoints;
		
		// if the quadratic is solvable, then solve the quadratic
		Double x1 = (-G2 + Math.sqrt(discriminant)) / (2 * G1);
		Double x2 = (-G2 - Math.sqrt(discriminant)) / (2 * G1);
		Point2D.Double solution1 = new Point2D.Double(x1, b1.computeY(x1));
		Point2D.Double solution2 = new Point2D.Double(x2, b1.computeY(x2));
		
		// determine if solution is in the convex body
		if( (solution1.x >= 0 && solution1.x <= 1) && (solution1.y >= 0 && solution1.y <= 1) )
			intersectionPoints.add(solution1);
		if( (solution2.x >= 0 && solution2.x <= 1) && (solution2.y >= 0 && solution2.y <= 1) )
			intersectionPoints.add(solution2);
		
		// return all intersection points
		return intersectionPoints;
		
		/*
		// determine the coefficients of the quartic; expressed as Px^4 + Qx^3 + Rx^2 + Sx + T = 0
		Double T = -16 * Math.pow(B1, 2) * Math.pow(B2, 3) * E1 * E2 * F1 +
				16 * Math.pow(B1, 3) * Math.pow(B2, 2) * Math.pow(E2, 2) * F1 +
				16 * Math.pow(B1, 2) * Math.pow(B2, 4) * Math.pow(F1, 2) + 
				16 * Math.pow(B1, 2) * Math.pow(B2, 3) * Math.pow(E1, 2) * F2 -
				16 * Math.pow(B1, 3) * Math.pow(B2, 2) * E1 * E2 * F2 - 
				32 * Math.pow(B1, 3) * Math.pow(B2, 3) * F1 * F2  + 
				16 * Math.pow(B1, 4) * Math.pow(B2, 2) * Math.pow(F2, 2);
		
		Double S = 16 * Math.pow(B1, 2) * Math.pow(B2, 3) * D2 * Math.pow(E1, 2) -
				16 * Math.pow(B1, 2) * Math.pow(B2, 3) * D1 * E1 * E2 - 
				16 * Math.pow(B1, 3) * Math.pow(B2, 2) * D2 * E1 * E2 + 
				16 * Math.pow(B1, 3) * Math.pow(B2, 2) * D1 * Math.pow(E2, 2) + 
				32 * Math.pow(B1, 2) * Math.pow(B2, 4) * D1 * F1 - 
				32 * Math.pow(B1, 3) * Math.pow(B2, 3) * D2 * F1 - 
				16 * Math.pow(B1, 2) * Math.pow(B2, 3) * C2 * E1 * F1 - 
				16 * Math.pow(B1, 2) * Math.pow(B2, 3) * C1 * E2 * F1 + 
				32 * Math.pow(B1, 3) * Math.pow(B2, 2) * C2 * E2 * F1 - 
				32 * Math.pow(B1, 3) * Math.pow(B2, 3) * D1 * F2 + 
				32 * Math.pow(B1, 4) * Math.pow(B2, 2) * D2 * F2 + 
				32 * Math.pow(B1, 2) * Math.pow(B2, 3) * C1 * E1 * F2 - 
				16 * Math.pow(B1, 3) * Math.pow(B2, 2) * C2 * E1 * F2 - 
				16 * Math.pow(B1, 3) * Math.pow(B2, 2) * C1 * E2 * F2;

		Double R = 16 * Math.pow(B1, 2) * Math.pow(B2, 4) * Math.pow(D1, 2) -
				32 * Math.pow(B1, 3) * Math.pow(B2, 3) * D1 * D2 + 
				16 * Math.pow(B1, 4) * Math.pow(B2, 2) * Math.pow(D2, 2) - 
				16 * Math.pow(B1, 2) * Math.pow(B2, 3) * C2 * D1 * E1 + 
				32 * Math.pow(B1, 2) * Math.pow(B2, 3) * C1 * D2 * E1 - 
				16 * Math.pow(B1, 3) * Math.pow(B2, 2) * C2 * D2 * E1 + 
				16 * A2 * Math.pow(B1, 2) * Math.pow(B2, 3) * Math.pow(E1, 2) - 
				16 * Math.pow(B1, 2) * Math.pow(B2, 3) * C1 * D1 * E2 + 
				32 * Math.pow(B1, 3) * Math.pow(B2, 2) * C2 * D1 * E2 - 
				16 * Math.pow(B1, 3) * Math.pow(B2, 2) * C1 * D2 * E2 - 
				16 * A2 * Math.pow(B1, 3) * Math.pow(B2, 2) *  E1 * E2 - 
				16 * A1 * Math.pow(B1, 2) * Math.pow(B2, 3) *  E1 * E2 + 
				16 * A1 * Math.pow(B1, 3) * Math.pow(B2, 2) *  Math.pow(E2, 2) - 
				32 * A2 * Math.pow(B1, 3) * Math.pow(B2, 3) *  F1 + 
				32 * A1 * Math.pow(B1, 2) * Math.pow(B2, 4) *  F1 - 
				16 * Math.pow(B1, 2) * Math.pow(B2, 3) * C1 * C2 *  F1 + 
				16 * Math.pow(B1, 3) * Math.pow(B2, 2) * Math.pow(C2, 2) *  F1 + 
				32 * A2 * Math.pow(B1, 4) * Math.pow(B2, 2) * F2 - 
				32 * A1 * Math.pow(B1, 3) * Math.pow(B2, 3) * F2 + 
				16 * Math.pow(B1, 2) * Math.pow(B2, 3) * Math.pow(C1, 2) * F2 - 
				16 * Math.pow(B1, 3) * Math.pow(B2, 2) * C1 * C2 * F2;
				
		Double Q = -32 * A2 * Math.pow(B1, 3) * Math.pow(B2, 3) * D1 +
				32 * A1 * Math.pow(B1, 2) * Math.pow(B2, 4) * D1 -
				16 * Math.pow(B1, 2) * Math.pow(B2, 3) * C1 * C2 * D1 +
				16 * Math.pow(B1, 3) * Math.pow(B2, 2) * Math.pow(C2, 2) * D1 +
				32 * A2 * Math.pow(B1, 4) * Math.pow(B2, 2) * D2 -
				32 * A1 * Math.pow(B1, 3) * Math.pow(B2, 3) * D2 +
				16 * Math.pow(B1, 2) * Math.pow(B2, 3) * Math.pow(C1, 2) * D2 -
				16 * Math.pow(B1, 3) * Math.pow(B2, 2) * C1 * C2 * D2 +
				32 * A2 * Math.pow(B1, 2) * Math.pow(B2, 3) * C1 * E1 -
				16 * A2 * Math.pow(B1, 3) * Math.pow(B2, 2) * C2 * E1 -
				16 * A1 * Math.pow(B1, 2) * Math.pow(B2, 3) * C2 * E1 -
				16 * A2 * Math.pow(B1, 3) * Math.pow(B2, 2) * C1 * E2 -
				16 * A1 * Math.pow(B1, 2) * Math.pow(B2, 3) * C1 * E2 +
				32 * A1 * Math.pow(B1, 3) * Math.pow(B2, 2) * C2 * E2;
		
		Double P = 16 * Math.pow(A2, 2) * Math.pow(B1, 4) * Math.pow(B2, 2) - 
				32 * A1 * A2 * Math.pow(B1, 3) * Math.pow(B2, 3) + 
				16 * Math.pow(A1, 2) * Math.pow(B1, 2) * Math.pow(B2, 4) + 
				16 * A2 * Math.pow(B1, 2) * Math.pow(B2, 3) * Math.pow(C1, 2) - 
				16 * A2 * Math.pow(B1, 3) * Math.pow(B2, 2) * C1 * C2 - 
				16 * A1 * Math.pow(B1, 2) * Math.pow(B2, 3) * C1 * C2 + 
				16 * A1 * Math.pow(B1, 3) * Math.pow(B2, 2) * Math.pow(C2, 2);
		*/
	}
	
	/*
	 * Checks if list contains a point that is approximately equal to some other points. the approximation is based on the error parameter
	 */
	private static boolean listRouglyContainsPoint(LinkedList<Point2D.Double> list, Point2D.Double p, Double error) {
		for(Point2D.Double t : list) {
			if(t.distance(p) <= error)
				return true;
		}
		return false;
	}
	
	/*
	 * Compute discriminant for the intersection point between the bisector and a line; this corresponds for solution of x
	private Double computeDiscriminantX(Double[] line) {
		Double a = line[0];
		Double b = line[1];
		Double c = line[2];
		
		// System.out.println("line: " + Util.printLineEq(new Double[] {a, b, c}));
		Double first = Math.pow(- this.C * c * b + 2 * this.B * a * c +  this.D * Math.pow(b, 2) - this.E * a * b, 2);
		Double second = this.A * Math.pow(b, 2) - this.C * a * b + this.B * Math.pow(a, 2);
		Double third = this.B * Math.pow(c, 2) - this.E * c * b + this.F * Math.pow(b, 2);

		return first - 4 * second * third; 
	}
	 */

	/*
	 * Compute discriminant for the intersection point between the bisector and a line; this corresponds for solution of y
	private Double computeDiscriminantY(Double[] line) {
		Double a = line[0];
		Double b = line[1];
		Double c = line[2];
		
		Double first = Math.pow(2 * this.A * b * c - this.C * c * a - this.D * b * a + this.E * Math.pow(a, 2), 2);
		Double second = this.A * Math.pow(b, 2) - this.C * b * a + this.B * Math.pow(a, 2);
		Double third = this.A * Math.pow(c, 2) - this.D * c * a + this.F * Math.pow(a, 2);

		return first - 4 * second * third; 
	}
	 */
	
	/*
	 * Compute the x-values of any intersection points between a given line and the bisector
	private Double[] intersectionBisectorLineX(Double[] line) {
		Double[] solutions = new Double[2];
		Double a = line[0];
		Double b = line[1];
		Double c = line[2];
		
		// Double firstTerm = -(2 * this.B * a * c + this.D * Math.pow(b, 2) - this.E * a * b - this.C * c * b);
		Double firstTerm = -(-this.C * b * c + 2 * this.B * a * c + this.D * Math.pow(b, 2) - this.E * a * b);
		Double discriminant = this.computeDiscriminantX(line);
		Double denominator = 2 * (this.A * Math.pow(b, 2) - this.C * a * b + this.B * Math.pow(a, 2));
		
		solutions[0] = (firstTerm + Math.sqrt(discriminant)) / denominator;
		solutions[1] = (firstTerm - Math.sqrt(discriminant)) / denominator;
		
		return solutions;
	}
	 */

	/*
	 * Compute the y-values of any intersection points between a given line and the bisector
	private Double[] intersectionBisectorLineY(Double[] line) {
		Double[] solutions = new Double[2];
		Double a = line[0];
		Double b = line[1];
		Double c = line[2];
		
		Double firstTerm = -(2 * this.A * b * c - this.C * c * a - this.D * b * a + this.E * Math.pow(a, 2));
		Double discriminant = this.computeDiscriminantY(line);
		Double denominator = 2 * (this.A * Math.pow(b, 2) - this.C * b * a + this.B * Math.pow(a, 2));
		
		solutions[0] = (firstTerm + Math.sqrt(discriminant)) / denominator;
		solutions[1] = (firstTerm - Math.sqrt(discriminant)) / denominator;

		return solutions;
	}
	 */
	
	/*
	 * Computes the coefficients of the equations of the segments on the boundary of the convex body
	 */
	private void computeEdgeLineEquations() {
		if(this.edge1 == null || this.edge2 == null || this.edge3 == null || this.edge4 == null)
			return;
		
		// convert end points of Segment from PVectors to Point2D.Double objects
		Point2D.Double leftPoint1 = new Point2D.Double(edge1.getLeftPoint().x, edge1.getLeftPoint().y);
		Point2D.Double rightPoint1 = new Point2D.Double(edge1.getRightPoint().x, edge1.getRightPoint().y);
		Point2D.Double leftPoint2 = new Point2D.Double(edge2.getLeftPoint().x, edge2.getLeftPoint().y);
		Point2D.Double rightPoint2 = new Point2D.Double(edge2.getRightPoint().x, edge2.getRightPoint().y);
		Point2D.Double leftPoint3 = new Point2D.Double(edge3.getLeftPoint().x, edge3.getLeftPoint().y);
		Point2D.Double rightPoint3 = new Point2D.Double(edge3.getRightPoint().x, edge3.getRightPoint().y);
		Point2D.Double leftPoint4 = new Point2D.Double(edge4.getLeftPoint().x, edge4.getLeftPoint().y);
		Point2D.Double rightPoint4 = new Point2D.Double(edge4.getRightPoint().x, edge4.getRightPoint().y);

		// compute line equations
		this.line1 = Bisector.computeLineEquation(leftPoint1, rightPoint1); // a_1 x + a_2 y + a_3 = 0
		this.line2 = Bisector.computeLineEquation(leftPoint2, rightPoint2); // b_1 x + b_2 y + b_3 = 0
		this.line3 = Bisector.computeLineEquation(leftPoint3, rightPoint3); // c_1 x + c_2 y + c_3 = 0
		this.line4 = Bisector.computeLineEquation(rightPoint4, leftPoint4); // d_1 x + d_2 y + d_3 = 0
		
		// determine number of unique edges
		this.numUniqueEdges();
	}
	
	/*
	 * compute projective matrix
	 */
	private void computeProjectiveMatrices() {
		// make sure that lines of edges are computed
		if(this.line1 == null || this.line2 == null || this.line3 == null || this.line4 == null)
			this.computeEdgeLineEquations();
		
		// compute the vertices of the quad
		Point2D.Double[] vertices = new Point2D.Double[4];
		vertices[0] = HilbertGeometry.toCartesian( this.line1.crossProduct(this.line2) );
		vertices[1] = HilbertGeometry.toCartesian( this.line2.crossProduct(this.line3) );
		vertices[2] = HilbertGeometry.toCartesian( this.line3.crossProduct(this.line4) );
		vertices[3] = HilbertGeometry.toCartesian( this.line4.crossProduct(this.line1) );
		
		// determine relative position of all these points (i.e. which point is the top left vertex, which is the top right vertex, etc.)
		// vertices[0] is bottom left corner
		// vertices[1] is bottom right corner
		// vertices[2] is top right corner
		// vertices[3] is top left corner
		
		// check vertical lines, if they exist permute the vertices position
		if(vertices[0].x == vertices[1].x || vertices[2].x == vertices[3].x) {
			Point2D.Double first = (Point2D.Double) vertices[0].clone();
			for(int index = 0; index < vertices.length; index++) {
				if(index == vertices.length - 1)
					vertices[index] = first;
				else
					vertices[index] = vertices[ (index + 1)%vertices.length ];
			}
		}
		
		// sort bottom segment by value of x
		if(vertices[0].x > vertices[1].x) {
			Point2D.Double temp = vertices[0];
			vertices[0] = vertices[1];
			vertices[1] = temp;
		}

		// sort top segment by value of x
		if(vertices[3].x > vertices[2].x) {
			Point2D.Double temp = vertices[2];
			vertices[2] = vertices[3];
			vertices[3] = temp;
		}
		
		// sort left segment by value of y
		if(vertices[0].y > vertices[3].y) {
			Point2D.Double temp = vertices[0];
			vertices[0] = vertices[3];
			vertices[3] = temp;
			
		}

		// sort left segment by value of y
		if(vertices[1].y > vertices[2].y) {
			Point2D.Double temp = vertices[2];
			vertices[2] = vertices[1];
			vertices[1] = temp;
			
		}
		
		// construct forward projective matrix
		// we map to the unit square
		Matrix T = new Matrix(
			new double[][] {
				{ vertices[0].x, vertices[0].y, 1d, 0d, 0d, 0d, -vertices[0].x * 0d, -vertices[0].y * 0d  },
				{ 0d, 0d, 0d, vertices[0].x, vertices[0].y, 1d, -vertices[0].x * 0d, -vertices[0].y * 0d  },
				{ vertices[1].x, vertices[1].y, 1d, 0d, 0d, 0d, -vertices[1].x * 1d, -vertices[1].y * 1d  },
				{ 0d, 0d, 0d, vertices[1].x, vertices[1].y, 1d, -vertices[1].x * 0d, -vertices[1].y * 0d  },
				{ vertices[2].x, vertices[2].y, 1d, 0d, 0d, 0d, -vertices[2].x * 1d, -vertices[2].y * 1d  },
				{ 0d, 0d, 0d, vertices[2].x, vertices[2].y, 1d, -vertices[2].x * 1d, -vertices[2].y * 1d  },
				{ vertices[3].x, vertices[3].y, 1d, 0d, 0d, 0d, -vertices[3].x * 0d, -vertices[3].y * 0d  },
				{ 0d, 0d, 0d, vertices[3].x, vertices[3].y, 1d, -vertices[3].x * 1d, -vertices[3].y * 1d  }
			}
		);
		Matrix Q = new Matrix(
			new double[][] {
				{0d},
				{0d},
				{1d},
				{0d},
				{1d},
				{1d},
				{0d},
				{1d}
			}
		);
		
		Matrix C = T.inverse().times(Q);
		
		this.P = new Matrix(
			new double[][] {
				{ C.get(0, 0), C.get(1, 0), C.get(2, 0) }, 
				{ C.get(3, 0), C.get(4, 0), C.get(5, 0) }, 
				{ C.get(6, 0), C.get(7, 0), 1d }
			}
		);
		
		// construct backwards projective matrix
		// we map from the unit square back to the original quad
		T = new Matrix(
			new double[][] {
				{0d, 0d, 1d, 0d, 0d, 0d, -0d * vertices[0].x, -0d * vertices[0].x},
				{0d, 0d, 0d, 0d, 0d, 1d, -0d * vertices[0].y, -0d * vertices[0].y},
				{1d, 0d, 1d, 0d, 0d, 0d, -1d * vertices[1].x, -0d * vertices[1].x},
				{0d, 0d, 0d, 1d, 0d, 1d, -1d * vertices[1].y, -0d * vertices[1].y},
				{1d, 1d, 1d, 0d, 0d, 0d, -1d * vertices[2].x, -1d * vertices[2].x},
				{0d, 0d, 0d, 1d, 1d, 1d, -1d * vertices[2].y, -1d * vertices[2].y},
				{0d, 1d, 1d, 0d, 0d, 0d, -0d * vertices[3].x, -1d * vertices[3].x},
				{0d, 0d, 0d, 0d, 1d, 1d, -0d * vertices[3].y, -1d * vertices[3].y}
			}
		);
		Matrix PP = new Matrix(
			new double[][] {
				{vertices[0].x},
				{vertices[0].y},
				{vertices[1].x},
				{vertices[1].y},
				{vertices[2].x},
				{vertices[2].y},
				{vertices[3].x},
				{vertices[3].y}
			}
		);
		
		C = T.inverse().times(PP);
		
		this.inverseP = new Matrix(
			new double[][] {
				{ C.get(0, 0), C.get(1, 0), C.get(2, 0) }, 
				{ C.get(3, 0), C.get(4, 0), C.get(5, 0) }, 
				{ C.get(6, 0), C.get(7, 0), 1d }
			}
		);
		
	}

	/**
	 * computes coefficients of the equations of two given points
	 * 
	 * @param p1 the first point on the line
	 * @param p2 the second point on the lines
	 * @return, Point3d that contains the coefficients c1, c2, c3 of the line c1 x + c2 y + c3 = 0
	 */
	public static Point3d computeLineEquation(Point2D.Double p1, Point2D.Double p2) {
		Point3d lp = HilbertGeometry.toHomogeneous(p1);
		Point3d rp = HilbertGeometry.toHomogeneous(p2);
		
		return lp.crossProduct(rp);
	}
	
	/**
	 * Converts point p into a matrix that corresponds the homogeneous coordinate corresponds to p 
	 * 
	 * @param p, the intended Point2D.Double point to being converted
	 * @return a (n+1) x 1 matrix that corresponds the homogeneous coordinates of p
	 */
	private static Matrix convertToMatrix(Point2D.Double p) {
		return new Matrix(new double[][] {{p.x}, {p.y}, {1}});
	}

	/**
	 * Converts matrix m that corresponds the homogeneous coordinate p back into an Point object
	 * 
	 * @param m the matrix m intended to be converted into homogeneous coordinate 
	 * @return the homogeneous coordinate p that corresponds to m
	 */
	private static Point2D.Double convertToPoint2D(Matrix m) {
		if(m.getRowDimension() != 3 && m.getColumnDimension() != 1)
			return null;
		
		if(m.get(2, 0) == 0)
			return new Point2D.Double(Integer.MAX_VALUE, Integer.MAX_VALUE);
		else
			return new Point2D.Double(m.get(0, 0) / m.get(2, 0), m.get(1, 0) / m.get(2, 0));
	}
	
	/**
	 * Computes the number of unique edges
	 */
	private void numUniqueEdges() {
		// ensure that lines are computed
		if(this.line1 == null || this.line2 == null || this.line3 == null || this.line4 == null)
			this.computeEdgeLineEquations();
		
		// place equations into an array
		Point3d[] lines = new Point3d[] {this.line1, this.line2, this.line3, this.line4};
		LinkedList<Point3d> uniqueLines = new LinkedList<Point3d>();
		uniqueLines.add(lines[0]);
		
		for(int i = 1; i < 4; i++) {
			boolean isUnique = true;
			for(Point3d l : uniqueLines) {
				if(l.x == lines[i].x && l.y == lines[i].y) {
					isUnique = false;
					break;
				}
			}
			if(isUnique)
				uniqueLines.add(lines[i]);
		}
		
		this.uniqueEdges = uniqueLines.size();
		
		if(this.uniqueEdges < 4 && this.uniqueEdges >= 2) {
			this.line1 = uniqueLines.get(0);
			this.line2 = uniqueLines.get(1);
		}
		if(this.uniqueEdges == 3) {
			this.line3 = uniqueLines.get(2);
		}
	}
	
	/*
	 * Test method for this object
	 */
	public static void main(String[] argv) {
		// construct convex body; it will be a unit square to easily test the following square without worrying projective transformations
		Point2D.Double e1 = new Point2D.Double(0d, 0d);
		Point2D.Double e2 = new Point2D.Double(1d, 0d);
		Point2D.Double e3 = new Point2D.Double(1d, 1d);
		Point2D.Double e4 = new Point2D.Double(0d, 1d);
		Segment edge1 = new Segment( Util.toPVector(e1), Util.toPVector(e2) );
		Segment edge2 = new Segment( Util.toPVector(e2), Util.toPVector(e3) );
		Segment edge3 = new Segment( Util.toPVector(e3), Util.toPVector(e4) );
		Segment edge4 = new Segment( Util.toPVector(e4), Util.toPVector(e1) );
		Point2D.Double site1 = new Point2D.Double(0.5, 0.9);
		Point2D.Double site2 = new Point2D.Double(0.3, 0.3);
		Point2D.Double site3 = new Point2D.Double(0.8, 0.3);
		Bisector b1 = new Bisector(site1, site2, edge1, edge2, edge3, edge4);
		Bisector b2 = new Bisector(site1, site3, edge1, edge2, edge3, edge4);
		
		// compute intersection points
		LinkedList<Point2D.Double> points = Bisector.computeTwoBisectorIntersection(b1, b2);
		
		for(Point2D.Double p : points)
			System.out.println(Util.printCoordinate(p));
		
		
		/*
		// determine line to compute
		int k = 1, n = 10;
		Double theta = k * 2 * Math.PI / n;
		Point2D.Double s = new Point2D.Double(site1.x + Math.cos(theta), site1.y + Math.sin(theta));
		Point3d l = Bisector.computeLineEquation(site1, s);
		Double[] line = new Double[] {l.x, l.y, l.z};
		
		// compute intersection points
		LinkedList<Point2D.Double> points = b.intersectionPointsWithLine(line);
		
		for(Point2D.Double p : points)
			System.out.println(Util.printCoordinate(p));
		*/

	}	
}