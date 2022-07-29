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
		this.computeBisector();
		this.computeProjectiveMatrices();
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
		this.computeBisector();
		this.computeProjectiveMatrices();
	}
	
	/*
	 * Computes the coefficients of the bisector in its conic form
	 */
	public void computeBisector() {
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

		this.constantsComputed = true;
	}
	
	/*
	 * Given some x, compute the corresponding real y value on the conic
	 */
	public Double[] computeY(Double x) {
		// compute constants if not already computed
		if(!this.constantsComputed)
			this.computeBisector();
		
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
	}

	/*
	 * Given some y, compute the corresponding real x value on the conic
	 */
	public Double[] computeX(Double y) {
		// compute constants if not already computed
		if(!this.constantsComputed)
			this.computeBisector();
		
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
	}
	
	/*
	 * Given a line, determine all intersection between the bisector and this line
	 */
	public LinkedList<Point2D.Double> intersectionPointsWithLine(Convex c, Double[] line) {
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
		Double error = 1e-8;
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
		
		// return all intersection points
		return intersectionPoints;
		
	}
	
	/*
	 * Computes an approximate solution to the intersection to two conics/bisectors
	 */
	public static Double[] computeTwoBisectorIntersection(Bisector b1, Bisector b2) {
		// express all variables in a nice manner
		Double A1 = b1.A;
		Double B1 = b1.B;
		Double C1 = b1.C;
		Double D1 = b1.D;
		Double E1 = b1.E;
		Double F1 = b1.F;
		
		Double A2 = b2.A;
		Double B2 = b2.B;
		Double C2 = b2.C;
		Double D2 = b2.D;
		Double E2 = b2.E;
		Double F2 = b2.F;
		
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
		
		// DEBUGGING, MAKE SURE THAT COEFFICIENTS ARE CORRECT
		return new Double[] {P, Q, R, S, T};
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
	 */
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

	/*
	 * Compute discriminant for the intersection point between the bisector and a line; this corresponds for solution of y
	 */
	private Double computeDiscriminantY(Double[] line) {
		Double a = line[0];
		Double b = line[1];
		Double c = line[2];
		
		Double first = Math.pow(2 * this.A * b * c - this.C * c * a - this.D * b * a + this.E * Math.pow(a, 2), 2);
		Double second = this.A * Math.pow(b, 2) - this.C * b * a + this.B * Math.pow(a, 2);
		Double third = this.A * Math.pow(c, 2) - this.D * c * a + this.F * Math.pow(a, 2);

		return first - 4 * second * third; 
	}
	
	/*
	 * Compute the x-values of any intersection points between a given line and the bisector
	 */
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

	/*
	 * Compute the y-values of any intersection points between a given line and the bisector
	 */
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
	}
	
	/*
	 * compute projective matrix
	 */
	private void computeProjectiveMatrices() {
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
	
	/*
	 * computes coefficients of the equations of two given points
	 */
	public static Point3d computeLineEquation(Point2D.Double p1, Point2D.Double p2) {
		Point3d lp = HilbertGeometry.toHomogeneous(p1);
		Point3d rp = HilbertGeometry.toHomogeneous(p2);
		
		return lp.crossProduct(rp);
	}
	
	/*
	 * Test method for this object
	 */
	public static void main(String[] argv) {
		Segment s1 = new Segment(0, 0, 10, 30);
		Segment s2 = new Segment(10, 30, 35, 20);
		Segment s3 = new Segment(35, 20, 40, 5);
		Segment s4 = new Segment(40, 5, 0, 0);
		
		
		Bisector b1 = new Bisector(new Point2D.Double(1, 1), new Point2D.Double(2, 2), s1, s2, s3, s4);
		// Bisector b2 = new Bisector();
		
		/*
		// assign conic coefficients
		b1.A = -56.844;
		b1.B = -2.984;
		b1.C = 82.018;
		b1.D = 1d;
		b1.E = 6d;
		b1.F = -1.7;

		b2.A = 3.6;
		b2.B = 3.8;
		b2.C = -5.8;
		b2.D = -4.7;
		b2.E = -3.9;
		b2.F = -6.9;
		
		Double[] results = Bisector.computeTwoBisectorIntersection(b1, b2);
		int dummy = 0;
		*/
	}	
}