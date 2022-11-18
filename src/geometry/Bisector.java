package geometry;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import geometry.Point3d;

import trapmap.Segment;

import Jama.Matrix;

/*
 * Class to store bisector for some given sector
 * Notation:
 * - if p1 and p2 are points, then p1-p2 is the line segment with endpoints p1 and p2
 * Assumptions:
 * - let X be a point on the bisector
 * - let S be the sector which the bisector corresponds to
 * - edge1 is the forward perspective of site1
 * - edge2 is the backward perspective of site1
 * - edge3 is the forward perspective of site2
 * - edge4 is the backward perspective of site2
 */
public class Bisector {
	// constants for conic; equation for conic: Ax^2 + By^2 + Cxy + Dx + Ey + F = 0
	// FOR NEW METHOD: these coefficients are for the bisector in the projected unit
	// square
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

	// stores the classification of the conic
	private short classifiction;

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
//	private Matrix P;
//	private Matrix inverseP;

	// FOR DEBUGGING PURPOSES
	public Bisector() {
	}

	// constructors - end points unknown
	public Bisector(Point2D.Double site1, Point2D.Double site2, Segment edge1, Segment edge2, Segment edge3,
			Segment edge4) {
		this.site1 = site1;
		this.site2 = site2;
		this.edge1 = edge1;
		this.edge2 = edge2;
		this.edge3 = edge3;
		this.edge4 = edge4;
		this.leftEndPoint = null;
		this.rightEndPoint = null;
		this.constantsComputed = false;
		this.classifiction = -1;
//		this.P = null;
//		this.inverseP = null;
		// this.computeProjectiveMatrices();
		this.computeBisector();
	}

	// constructors - end points known
	public Bisector(Point2D.Double site1, Point2D.Double site2, Segment edge1, Segment edge2, Segment edge3,
			Segment edge4, Point2D.Double leftEndPoint, Point2D.Double rightEndPoint) {
		this.site1 = site1;
		this.site2 = site2;
		this.edge1 = edge1;
		this.edge2 = edge2;
		this.edge3 = edge3;
		this.edge4 = edge4;
		this.leftEndPoint = leftEndPoint;
		this.rightEndPoint = rightEndPoint;
		this.constantsComputed = false;
		this.classifiction = -1;
//		this.P = null;
//		this.inverseP = null;
		// this.computeProjectiveMatrices();
		this.computeBisector();
	}

	public Double returnA() {
		return this.A;
	}

	public void setAValue(Double val) {
		this.A = val;
	}

	public double lineDistance(Point3d line1, Point3d line2) {
		return (line1.x - line2.x) * (line1.x - line2.x) + (line1.y - line2.y) * (line1.y - line2.y)
				+ (line1.z - line2.z) * (line1.z - line2.z);

	}

	/**
	 * Computes the coefficients of the bisector in its conic form
	 */
	public void computeBisector() {
		// ensure that all necessary fields are not null
		if (this.site1 == null || this.site2 == null || this.edge1 == null || this.edge2 == null || this.edge3 == null
				|| this.edge4 == null)
			return;

		// compute equations of relevant lines on the boundary of the convex body
		this.computeEdgeLineEquations();

		// compute constants
		this.K = (Math.abs(line1.x * site1.x + line1.y * site1.y + line1.z)
				/ Math.abs(line2.x * site1.x + line2.y * site1.y + line2.z))
				* (Math.abs(line4.x * site2.x + line4.y * site2.y + line4.z)
						/ Math.abs(line3.x * site2.x + line3.y * site2.y + line3.z));
		this.s = 1d;

		System.out.println("-----------------------------");
		System.out.println("Site1: " + site1);
		System.out.println("Line1: " + line1.x + "x " + line1.y + "y " + line1.z + "z ");
		System.out.println("Line2: " + line2.x + "x " + line2.y + "y " + line2.z + "z ");
		System.out.println("Site2: " + site2);
		System.out.println("Line3: " + line3.x + "x " + line3.y + "y " + line3.z + "z ");
		System.out.println("Line4: " + line4.x + "x " + line4.y + "y " + line4.z + "z ");
		System.out.println("-----------------------------");

		if (lineDistance(line1, line3) < .00000001) {
			System.out.println("test++++++++++++");
			this.A = 0.0;
			this.B = 0.0;
			this.C = 0.0;
			this.D = line4.x - this.K * line2.x;
			this.E = line4.y - this.K * line2.y;
			this.F = line4.z - this.K * line2.z;
		} else if (lineDistance(line2, line4) < .00000001) {
			System.out.println("test++++++++++++");
			this.A = 0.0;
			this.B = 0.0;
			this.C = 0.0;
			this.D = line1.x - this.K * line3.x;
			this.E = line1.y - this.K * line3.y;
			this.F = line1.z - this.K * line3.z;
		} else {
			this.A = line4.x * line1.x - this.K * this.s * (line3.x * line2.x);
			this.B = line4.y * line1.y - this.K * this.s * (line3.y * line2.y);
			this.C = line4.y * line1.x + line4.x * line1.y - this.K * this.s * (line3.y * line2.x + line3.x * line2.y);
			this.D = line4.z * line1.x + line4.x * line1.z - this.K * this.s * (line3.z * line2.x + line3.x * line2.z);
			this.E = line4.z * line1.y + line4.y * line1.z - this.K * this.s * (line3.z * line2.y + line3.y * line2.z);
			this.F = line4.z * line1.z - this.K * this.s * (line3.z * line2.z);
		}
		System.out.println(this.A + "x^2 + " + this.B + "y^2 + " + this.C + "xy + " + this.D + "x + " + this.E + "y + "
				+ this.F + " = 0");
		System.out.println("-----------------------------");

		// compute coefficients of bisector curve

		// store the fact that coefficients have been computed
		this.constantsComputed = true;

		// determine the classification of the bisector
		this.classifyBisector();
	}

	/**
	 * Given that the bisector coefficients are computed, determine the type of
	 * conic the bisector is
	 */
	private void classifyBisector() {
		// ensure constants are computed
		if (!this.constantsComputed)
			this.computeBisector();

		/*
		 * Let -1 be when classification is not been performed Let 0 be a some type of
		 * line Let 1 be a parabola Let 2 be a hyperbola Let 3 be a ellipse
		 */

		// determine if conic is degenerate
		Matrix m = new Matrix(new double[] { this.A, this.B, this.D, this.B, this.C, this.E, this.D, this.E, this.F },
				3);
		if (m.det() < 1e-12) {
			this.classifiction = 0;
			return;
		}

		// conic must not be degenerate here, thus, use this equation and solve based on
		// its value
		double expression = Math.pow(this.C, 2) - 4 * this.A * this.B;
		if (expression > 0)
			this.classifiction = 2;
		else if (expression < 0)
			this.classifiction = 3;
		else
			this.classifiction = 1;
	}

	/*
	 * Given some x, compute the corresponding real y value on the conic
	 */
	public Double[] computeX(Double x) {
		// compute constants if not already computed
		if (!this.constantsComputed)
			this.computeBisector();

		// determine constants for this parameterized (in terms of x) conic
		Double K1 = this.B;
		Double K2 = this.C * x + this.E;
		Double K3 = this.A * Math.pow(x, 2) + this.D * x + this.F;

		// horzontal line
		if (A == 0 && C == 0 && D == 0 && B == 0) {
			Double[] solutions = new Double[1];
			solutions[0] = -this.F / this.E;
			return solutions;
		}

		if (A == 0 && C == 0 && D == 0) {
			Double[] solutions = new Double[2];
			solutions[0] = (-this.E + Math.sqrt(Math.pow(this.E, 2) - 4 * this.B * this.F)) / (2 * this.B);
			solutions[1] = (-this.E - Math.sqrt(Math.pow(this.E, 2) - 4 * this.B * this.F)) / (2 * this.B);
			return solutions;
		}

		// if K1 is zero, then use a different parameterization
		if (K1 == 0d) {
//			Double[] solutions = new Double[2];
//			Double discriminant = Math.pow(E+ B*x,2) - 4 * C * (D*x+F);
//			solutions[0]=(-(E+B*x)+Math.sqrt(discriminant)) / (2*C);
//			solutions[1]=(-(E+B*x)-Math.sqrt(discriminant)) / (2*C);
//			return solutions;
			return new Double[] { -K3 / K2 };
		} else {
			// determine if discriminant is negative or not; if negative, no real solution
			// exist, otherwise, real solutions exist
			Double discriminant = Math.pow(K2, 2) - 4 * K1 * K3;
			if (discriminant < 0)
				return new Double[0];

			Double[] solutions = new Double[2];
			solutions[0] = (-K2 + Math.sqrt(discriminant)) / (2 * K1);
			solutions[1] = (-K2 - Math.sqrt(discriminant)) / (2 * K1);

			return solutions;
		}

//		Double numerator = this.E * y + this.F;
//		Double denominator = this.C * y + this.D;
//		if(denominator == 0d) {
//			System.out.println("division by zero error");
//			return Double.MIN_VALUE;
//		} else
//			return - numerator / denominator;
	}

	/**
	 * Given some x, compute the corresponding real y value on the conic
	 * 
	 * @param x the x-value to plug into the parameterized function of the bisector
	 * @return the corresponding y-value on the bisector
	 */
	public Double[] computeY(Double y) {
		// compute constants if not already computed
		if (!this.constantsComputed)
			this.computeBisector();

		// determine constants for this parameterized (in terms of x) conic
		Double K1 = this.B;
		Double K2 = this.C * y + this.E;
		Double K3 = this.A * Math.pow(y, 2) + this.D * y + this.F;

		// determine if discriminant is negative or not; if negative, no real solution
		// exist, otherwise, real solutions exist
		Double discriminant = Math.pow(K2, 2) - 4 * K1 * K3;
		if (discriminant < 0)
			return new Double[0];

		if (K1 == 0) {
			return new Double[] { K3 / K2 };
		} else {
			Double[] solutions = new Double[2];
			solutions[0] = (-K2 + Math.sqrt(discriminant)) / (2 * K1);
			solutions[1] = (-K2 - Math.sqrt(discriminant)) / (2 * K1);

			return solutions;
		}

//		Double numerator = this.D * x + this.F;
//		Double denominator = this.C * x + this.E;
//		if(denominator == 0d) {
//			System.out.println("division by zero error");
//			return Double.MIN_VALUE;
//		} else
//			return - numerator / denominator;
	}

	/*
	 * Compute discriminant for the intersection point between the bisector and a
	 * line; this corresponds for solution of x
	 */
	private Double computeDiscriminantX(Point3d line) {
		Double a = line.x;
		Double b = line.y;
		Double c = line.z;

		// System.out.println("line: " + Util.printLineEq(new Double[] {a, b, c}));
		Double first = Math.pow(-this.C * c * b + 2 * this.B * a * c + this.D * Math.pow(b, 2) - this.E * a * b, 2);
		Double second = this.A * Math.pow(b, 2) - this.C * a * b + this.B * Math.pow(a, 2);
		Double third = this.B * Math.pow(c, 2) - this.E * c * b + this.F * Math.pow(b, 2);

		return first - 4 * second * third;
	}

	/*
	 * Compute discriminant for the intersection point between the bisector and a
	 * line; this corresponds for solution of y
	 */
	private Double computeDiscriminantY(Point3d line) {
		Double a = line.x;
		Double b = line.y;
		Double c = line.z;

		Double first = Math.pow(2 * this.A * b * c - this.C * c * a - this.D * b * a + this.E * Math.pow(a, 2), 2);
		Double second = this.A * Math.pow(b, 2) - this.C * b * a + this.B * Math.pow(a, 2);
		Double third = this.A * Math.pow(c, 2) - this.D * c * a + this.F * Math.pow(a, 2);

		return first - 4 * second * third;
	}

	/*
	 * Compute the x-values of any intersection points between a given line and the
	 * bisector
	 */
	private Double[] intersectionBisectorLineX(Point3d line) {
		Double[] solutions = new Double[2];
		Double a = line.x;
		Double b = line.y;
		Double c = line.z;

		// Double firstTerm = -(2 * this.B * a * c + this.D * Math.pow(b, 2) - this.E *
		// a * b - this.C * c * b);
		Double firstTerm = -(-this.C * b * c + 2 * this.B * a * c + this.D * Math.pow(b, 2) - this.E * a * b);
		Double discriminant = this.computeDiscriminantX(line);
		Double denominator = 2 * (this.A * Math.pow(b, 2) - this.C * a * b + this.B * Math.pow(a, 2));

		solutions[0] = (firstTerm + Math.sqrt(discriminant)) / denominator;
		solutions[1] = (firstTerm - Math.sqrt(discriminant)) / denominator;
		if (Math.min(lineDistance(line1, line3), lineDistance(line2, line4)) < .0000000001) {// this needs to be set to
																								// some confidence level
																								// later
			Point3d p = new Point3d(this.D, this.E, this.F);
			solutions[0] = Util.lineIntersection(p, line).x;
			solutions[1] = -100000000000000000.0;
		}
		return solutions;
	}

	/*
	 * Compute the y-values of any intersection points between a given line and the
	 * bisector
	 */
	private Double[] intersectionBisectorLineY(Point3d line) {
		Double[] solutions = new Double[2];
		Double a = line.x;
		Double b = line.y;
		Double c = line.z;

		Double firstTerm = -(2 * this.A * b * c - this.C * c * a - this.D * b * a + this.E * Math.pow(a, 2));
		Double discriminant = this.computeDiscriminantY(line);
		Double denominator = 2 * (this.A * Math.pow(b, 2) - this.C * b * a + this.B * Math.pow(a, 2));

		solutions[0] = (firstTerm + Math.sqrt(discriminant)) / denominator;
		solutions[1] = (firstTerm - Math.sqrt(discriminant)) / denominator;
		if (Math.min(lineDistance(line1, line3), lineDistance(line2, line4)) < .0000000001) {// this needs to be set to
																								// some confidence level
																								// later
			Point3d p = new Point3d(this.D, this.E, this.F);
			solutions[0] = Util.lineIntersection(p, line).y;
			solutions[1] = -1000000000000000000.0;
		}
		return solutions;
	}

	/**
	 * 
	 * @param c
	 * @param line
	 * @return
	 */
	public LinkedList<Point2D.Double> intersectionPointsWithLine(Convex c, Point3d line) {
		// list of intersection points
		LinkedList<Point2D.Double> intersectionPoints = new LinkedList<Point2D.Double>();

		// ensure that all necessary fields are not null
		if (this.site1 == null || this.site2 == null || this.edge1 == null || this.edge2 == null || this.edge3 == null
				|| this.edge4 == null)
			return null;

		// compute constants if not already computed
		if (!this.constantsComputed)
			this.computeBisector();

		Double dX = this.computeDiscriminantX(line);
		Double dY = this.computeDiscriminantY(line);

		LinkedList<Point2D.Double> intersect = new LinkedList<Point2D.Double>();

		// compute the point of potential intersection
		if (dX >= 0) {
			Double[] solutionsX = this.intersectionBisectorLineX(line);
			for (Double x : solutionsX)
				intersect.add(new Point2D.Double(x, -line.x / line.y * x - line.z / line.y));
		}
		if (dY >= 0) {
			Double[] solutionsY = this.intersectionBisectorLineY(line);
			for (Double y : solutionsY)
				intersect.add(new Point2D.Double(-line.y / line.x * y - line.z / line.x, y));
		}

		// check if the points computed above map to the same point using the conic
		for (Point2D.Double p : intersect) {
			// check if the point p is in the convex body; if not, move to next point
			if (c.isInConvex(p) || c.isOnConvexBoundary(p)) {
				// checks if the points is already in the list
				if (!Bisector.listRouglyContainsPoint(intersectionPoints, p, 1e-8)) {
					intersectionPoints.add(p);
				}
			}
		}

		// return all intersection points
		return intersectionPoints;
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
		if (G1 == 0 || discriminant < 0)
			return intersectionPoints;

		// if the quadratic is solvable, then solve the quadratic
		Double x1 = (-G2 + Math.sqrt(discriminant)) / (2 * G1);
		Double x2 = (-G2 - Math.sqrt(discriminant)) / (2 * G1);
		Point2D.Double solution1 = new Point2D.Double(x1, b1.computeY(x1)[0]);
		Point2D.Double solution2 = new Point2D.Double(x2, b1.computeY(x2)[0]);

		// determine if solution is in the convex body
		if ((solution1.x >= 0 && solution1.x <= 1) && (solution1.y >= 0 && solution1.y <= 1))
			intersectionPoints.add(solution1);
		if ((solution2.x >= 0 && solution2.x <= 1) && (solution2.y >= 0 && solution2.y <= 1))
			intersectionPoints.add(solution2);

		// return all intersection points
		return intersectionPoints;

		/*
		 * // determine the coefficients of the quartic; expressed as Px^4 + Qx^3 + Rx^2
		 * + Sx + T = 0 Double T = -16 * Math.pow(B1, 2) * Math.pow(B2, 3) * E1 * E2 *
		 * F1 + 16 * Math.pow(B1, 3) * Math.pow(B2, 2) * Math.pow(E2, 2) * F1 + 16 *
		 * Math.pow(B1, 2) * Math.pow(B2, 4) * Math.pow(F1, 2) + 16 * Math.pow(B1, 2) *
		 * Math.pow(B2, 3) * Math.pow(E1, 2) * F2 - 16 * Math.pow(B1, 3) * Math.pow(B2,
		 * 2) * E1 * E2 * F2 - 32 * Math.pow(B1, 3) * Math.pow(B2, 3) * F1 * F2 + 16 *
		 * Math.pow(B1, 4) * Math.pow(B2, 2) * Math.pow(F2, 2);
		 * 
		 * Double S = 16 * Math.pow(B1, 2) * Math.pow(B2, 3) * D2 * Math.pow(E1, 2) - 16
		 * * Math.pow(B1, 2) * Math.pow(B2, 3) * D1 * E1 * E2 - 16 * Math.pow(B1, 3) *
		 * Math.pow(B2, 2) * D2 * E1 * E2 + 16 * Math.pow(B1, 3) * Math.pow(B2, 2) * D1
		 * * Math.pow(E2, 2) + 32 * Math.pow(B1, 2) * Math.pow(B2, 4) * D1 * F1 - 32 *
		 * Math.pow(B1, 3) * Math.pow(B2, 3) * D2 * F1 - 16 * Math.pow(B1, 2) *
		 * Math.pow(B2, 3) * C2 * E1 * F1 - 16 * Math.pow(B1, 2) * Math.pow(B2, 3) * C1
		 * * E2 * F1 + 32 * Math.pow(B1, 3) * Math.pow(B2, 2) * C2 * E2 * F1 - 32 *
		 * Math.pow(B1, 3) * Math.pow(B2, 3) * D1 * F2 + 32 * Math.pow(B1, 4) *
		 * Math.pow(B2, 2) * D2 * F2 + 32 * Math.pow(B1, 2) * Math.pow(B2, 3) * C1 * E1
		 * * F2 - 16 * Math.pow(B1, 3) * Math.pow(B2, 2) * C2 * E1 * F2 - 16 *
		 * Math.pow(B1, 3) * Math.pow(B2, 2) * C1 * E2 * F2;
		 * 
		 * Double R = 16 * Math.pow(B1, 2) * Math.pow(B2, 4) * Math.pow(D1, 2) - 32 *
		 * Math.pow(B1, 3) * Math.pow(B2, 3) * D1 * D2 + 16 * Math.pow(B1, 4) *
		 * Math.pow(B2, 2) * Math.pow(D2, 2) - 16 * Math.pow(B1, 2) * Math.pow(B2, 3) *
		 * C2 * D1 * E1 + 32 * Math.pow(B1, 2) * Math.pow(B2, 3) * C1 * D2 * E1 - 16 *
		 * Math.pow(B1, 3) * Math.pow(B2, 2) * C2 * D2 * E1 + 16 * A2 * Math.pow(B1, 2)
		 * * Math.pow(B2, 3) * Math.pow(E1, 2) - 16 * Math.pow(B1, 2) * Math.pow(B2, 3)
		 * * C1 * D1 * E2 + 32 * Math.pow(B1, 3) * Math.pow(B2, 2) * C2 * D1 * E2 - 16 *
		 * Math.pow(B1, 3) * Math.pow(B2, 2) * C1 * D2 * E2 - 16 * A2 * Math.pow(B1, 3)
		 * * Math.pow(B2, 2) * E1 * E2 - 16 * A1 * Math.pow(B1, 2) * Math.pow(B2, 3) *
		 * E1 * E2 + 16 * A1 * Math.pow(B1, 3) * Math.pow(B2, 2) * Math.pow(E2, 2) - 32
		 * * A2 * Math.pow(B1, 3) * Math.pow(B2, 3) * F1 + 32 * A1 * Math.pow(B1, 2) *
		 * Math.pow(B2, 4) * F1 - 16 * Math.pow(B1, 2) * Math.pow(B2, 3) * C1 * C2 * F1
		 * + 16 * Math.pow(B1, 3) * Math.pow(B2, 2) * Math.pow(C2, 2) * F1 + 32 * A2 *
		 * Math.pow(B1, 4) * Math.pow(B2, 2) * F2 - 32 * A1 * Math.pow(B1, 3) *
		 * Math.pow(B2, 3) * F2 + 16 * Math.pow(B1, 2) * Math.pow(B2, 3) * Math.pow(C1,
		 * 2) * F2 - 16 * Math.pow(B1, 3) * Math.pow(B2, 2) * C1 * C2 * F2;
		 * 
		 * Double Q = -32 * A2 * Math.pow(B1, 3) * Math.pow(B2, 3) * D1 + 32 * A1 *
		 * Math.pow(B1, 2) * Math.pow(B2, 4) * D1 - 16 * Math.pow(B1, 2) * Math.pow(B2,
		 * 3) * C1 * C2 * D1 + 16 * Math.pow(B1, 3) * Math.pow(B2, 2) * Math.pow(C2, 2)
		 * * D1 + 32 * A2 * Math.pow(B1, 4) * Math.pow(B2, 2) * D2 - 32 * A1 *
		 * Math.pow(B1, 3) * Math.pow(B2, 3) * D2 + 16 * Math.pow(B1, 2) * Math.pow(B2,
		 * 3) * Math.pow(C1, 2) * D2 - 16 * Math.pow(B1, 3) * Math.pow(B2, 2) * C1 * C2
		 * * D2 + 32 * A2 * Math.pow(B1, 2) * Math.pow(B2, 3) * C1 * E1 - 16 * A2 *
		 * Math.pow(B1, 3) * Math.pow(B2, 2) * C2 * E1 - 16 * A1 * Math.pow(B1, 2) *
		 * Math.pow(B2, 3) * C2 * E1 - 16 * A2 * Math.pow(B1, 3) * Math.pow(B2, 2) * C1
		 * * E2 - 16 * A1 * Math.pow(B1, 2) * Math.pow(B2, 3) * C1 * E2 + 32 * A1 *
		 * Math.pow(B1, 3) * Math.pow(B2, 2) * C2 * E2;
		 * 
		 * Double P = 16 * Math.pow(A2, 2) * Math.pow(B1, 4) * Math.pow(B2, 2) - 32 * A1
		 * * A2 * Math.pow(B1, 3) * Math.pow(B2, 3) + 16 * Math.pow(A1, 2) *
		 * Math.pow(B1, 2) * Math.pow(B2, 4) + 16 * A2 * Math.pow(B1, 2) * Math.pow(B2,
		 * 3) * Math.pow(C1, 2) - 16 * A2 * Math.pow(B1, 3) * Math.pow(B2, 2) * C1 * C2
		 * - 16 * A1 * Math.pow(B1, 2) * Math.pow(B2, 3) * C1 * C2 + 16 * A1 *
		 * Math.pow(B1, 3) * Math.pow(B2, 2) * Math.pow(C2, 2);
		 */
	}

	public void setEndPoints(Point2D.Double p) {
		if (this.leftEndPoint != null && this.rightEndPoint != null) {
			return;
		} else if (this.leftEndPoint != null) {
			if (this.leftEndPoint.x < p.x) {
				this.rightEndPoint = p;
			} else {
				this.rightEndPoint = (java.awt.geom.Point2D.Double) this.leftEndPoint.clone();
				this.leftEndPoint = p;
			}
		} else {
			this.leftEndPoint = p;
		}
	}

	/*
	 * Checks if list contains a point that is approximately equal to some other
	 * points. the approximation is based on the error parameter
	 */
	private static boolean listRouglyContainsPoint(LinkedList<Point2D.Double> list, Point2D.Double p, Double error) {
		for (Point2D.Double t : list) {
			if (t.distance(p) <= error)
				return true;
		}
		return false;
	}

	public Point2D.Double getLeftEndPoint() {
		return this.leftEndPoint;
	}

	public Point2D.Double getRightEndPoint() {
		return this.rightEndPoint;
	}

	public short getClassification() {
		return this.classifiction;
	}

	/*
	 * Computes the coefficients of the equations of the segments on the boundary of
	 * the convex body
	 */
	private void computeEdgeLineEquations() {
		if (this.edge1 == null || this.edge2 == null || this.edge3 == null || this.edge4 == null)
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
		this.line4 = Bisector.computeLineEquation(leftPoint4, rightPoint4); // d_1 x + d_2 y + d_3 = 0

		// determine number of unique edges
//		this.numUniqueEdges();
	}

	/*
	 * compute projective matrix
	 */
//	private void computeProjectiveMatrices() {
//		// make sure that lines of edges are computed
//		if (this.line1 == null || this.line2 == null || this.line3 == null || this.line4 == null)
//			this.computeEdgeLineEquations();
//
//		// compute the vertices of the quad
//		Point2D.Double[] vertices = new Point2D.Double[4];
//		vertices[0] = HilbertGeometry.toCartesian(this.line1.crossProduct(this.line2));
//		vertices[1] = HilbertGeometry.toCartesian(this.line2.crossProduct(this.line3));
//		vertices[2] = HilbertGeometry.toCartesian(this.line3.crossProduct(this.line4));
//		vertices[3] = HilbertGeometry.toCartesian(this.line4.crossProduct(this.line1));
//
//		// determine relative position of all these points (i.e. which point is the top
//		// left vertex, which is the top right vertex, etc.)
//		// vertices[0] is bottom left corner
//		// vertices[1] is bottom right corner
//		// vertices[2] is top right corner
//		// vertices[3] is top left corner
//
//		// check vertical lines, if they exist permute the vertices position
//		if (vertices[0].x == vertices[1].x || vertices[2].x == vertices[3].x) {
//			Point2D.Double first = (Point2D.Double) vertices[0].clone();
//			for (int index = 0; index < vertices.length; index++) {
//				if (index == vertices.length - 1)
//					vertices[index] = first;
//				else
//					vertices[index] = vertices[(index + 1) % vertices.length];
//			}
//		}
//
//		// sort bottom segment by value of x
//		if (vertices[0].x > vertices[1].x) {
//			Point2D.Double temp = vertices[0];
//			vertices[0] = vertices[1];
//			vertices[1] = temp;
//		}
//
//		// sort top segment by value of x
//		if (vertices[3].x > vertices[2].x) {
//			Point2D.Double temp = vertices[2];
//			vertices[2] = vertices[3];
//			vertices[3] = temp;
//		}
//
//		// sort left segment by value of y
//		if (vertices[0].y > vertices[3].y) {
//			Point2D.Double temp = vertices[0];
//			vertices[0] = vertices[3];
//			vertices[3] = temp;
//
//		}
//
//		// sort left segment by value of y
//		if (vertices[1].y > vertices[2].y) {
//			Point2D.Double temp = vertices[2];
//			vertices[2] = vertices[1];
//			vertices[1] = temp;
//
//		}
//
//		// construct forward projective matrix
//		// we map to the unit square
//		Matrix T = new Matrix(new double[][] {
//				{ vertices[0].x, vertices[0].y, 1d, 0d, 0d, 0d, -vertices[0].x * 0d, -vertices[0].y * 0d },
//				{ 0d, 0d, 0d, vertices[0].x, vertices[0].y, 1d, -vertices[0].x * 0d, -vertices[0].y * 0d },
//				{ vertices[1].x, vertices[1].y, 1d, 0d, 0d, 0d, -vertices[1].x * 1d, -vertices[1].y * 1d },
//				{ 0d, 0d, 0d, vertices[1].x, vertices[1].y, 1d, -vertices[1].x * 0d, -vertices[1].y * 0d },
//				{ vertices[2].x, vertices[2].y, 1d, 0d, 0d, 0d, -vertices[2].x * 1d, -vertices[2].y * 1d },
//				{ 0d, 0d, 0d, vertices[2].x, vertices[2].y, 1d, -vertices[2].x * 1d, -vertices[2].y * 1d },
//				{ vertices[3].x, vertices[3].y, 1d, 0d, 0d, 0d, -vertices[3].x * 0d, -vertices[3].y * 0d },
//				{ 0d, 0d, 0d, vertices[3].x, vertices[3].y, 1d, -vertices[3].x * 1d, -vertices[3].y * 1d } });
//		Matrix Q = new Matrix(new double[][] { { 0d }, { 0d }, { 1d }, { 0d }, { 1d }, { 1d }, { 0d }, { 1d } });
//
//		Matrix C = T.inverse().times(Q);
//
//		this.P = new Matrix(new double[][] { { C.get(0, 0), C.get(1, 0), C.get(2, 0) },
//				{ C.get(3, 0), C.get(4, 0), C.get(5, 0) }, { C.get(6, 0), C.get(7, 0), 1d } });
//
//		// construct backwards projective matrix
//		// we map from the unit square back to the original quad
//		T = new Matrix(new double[][] { { 0d, 0d, 1d, 0d, 0d, 0d, -0d * vertices[0].x, -0d * vertices[0].x },
//				{ 0d, 0d, 0d, 0d, 0d, 1d, -0d * vertices[0].y, -0d * vertices[0].y },
//				{ 1d, 0d, 1d, 0d, 0d, 0d, -1d * vertices[1].x, -0d * vertices[1].x },
//				{ 0d, 0d, 0d, 1d, 0d, 1d, -1d * vertices[1].y, -0d * vertices[1].y },
//				{ 1d, 1d, 1d, 0d, 0d, 0d, -1d * vertices[2].x, -1d * vertices[2].x },
//				{ 0d, 0d, 0d, 1d, 1d, 1d, -1d * vertices[2].y, -1d * vertices[2].y },
//				{ 0d, 1d, 1d, 0d, 0d, 0d, -0d * vertices[3].x, -1d * vertices[3].x },
//				{ 0d, 0d, 0d, 0d, 1d, 1d, -0d * vertices[3].y, -1d * vertices[3].y } });
//		Matrix PP = new Matrix(new double[][] { { vertices[0].x }, { vertices[0].y }, { vertices[1].x },
//				{ vertices[1].y }, { vertices[2].x }, { vertices[2].y }, { vertices[3].x }, { vertices[3].y } });
//
//		C = T.inverse().times(PP);
//
//		this.inverseP = new Matrix(new double[][] { { C.get(0, 0), C.get(1, 0), C.get(2, 0) },
//				{ C.get(3, 0), C.get(4, 0), C.get(5, 0) }, { C.get(6, 0), C.get(7, 0), 1d } });
//
//	}

	/**
	 * computes coefficients of the equations of two given points
	 * 
	 * @param p1 the first point on the line
	 * @param p2 the second point on the lines @return, Point3d that contains the
	 *           coefficients c1, c2, c3 of the line c1 x + c2 y + c3 = 0
	 */
	public static Point3d computeLineEquation(Point2D.Double p1, Point2D.Double p2) {
		Point3d lp = HilbertGeometry.toHomogeneous(p1);
		Point3d rp = HilbertGeometry.toHomogeneous(p2);

		return lp.crossProduct(rp);
	}

	/**
	 * Computes the number of unique edges
	 */
//	private void numUniqueEdges() {
//		// ensure that lines are computed
//		if (this.line1 == null || this.line2 == null || this.line3 == null || this.line4 == null)
//			this.computeEdgeLineEquations();
//
//		// place equations into an array
//		Point3d[] lines = new Point3d[] { this.line1, this.line2, this.line3, this.line4 };
//		LinkedList<Point3d> uniqueLines = new LinkedList<Point3d>();
//		uniqueLines.add(lines[0]);
//
//		for (int i = 1; i < 4; i++) {
//			boolean isUnique = true;
//			for (Point3d l : uniqueLines) {
//				if (l.x == lines[i].x && l.y == lines[i].y) {
//					isUnique = false;
//					break;
//				}
//			}
//			if (isUnique)
//				uniqueLines.add(lines[i]);
//		}
//
//		this.uniqueEdges = uniqueLines.size();
//
//		if (this.uniqueEdges < 4 && this.uniqueEdges >= 2) {
//			this.line1 = uniqueLines.get(0);
//			this.line2 = uniqueLines.get(1);
//		}
//		if (this.uniqueEdges == 3) {
//			this.line3 = uniqueLines.get(2);
//		}
//	}

	/**
	 * Cloning method for bisector
	 */
	public Bisector clone() {
		Bisector rtn = new Bisector(this.site1, this.site2, this.edge1, this.edge2, this.edge3, this.edge4,
				this.leftEndPoint, this.rightEndPoint);
		return rtn;
	}

	public String toString() {
		String rtn = "";
		rtn += this.A + "x^2 + ";
		rtn += this.B + "y^2 + ";
		rtn += this.C + "xy + ";
		rtn += this.D + "x + ";
		rtn += this.E + "y + ";
		rtn += this.F + " = 0";

		Pattern p = Pattern.compile("E[0-9]+");
		Matcher m = p.matcher(rtn);
		while (m.find()) {
			String pattern = m.group(0);
			String replacement = " * 10^{" + pattern.substring(1, pattern.length()) + "}";
			rtn = rtn.replaceAll(pattern, replacement);
		}

		// There is a issue with this that the bisector may bulge out so we may want to
		// expand this
		// This happens when the bisector is nearly vertical
		rtn += "\\left\\{" + this.getLeftEndPoint().x + " \\le x \\le " + this.getRightEndPoint().x + "\\right\\}";
		return rtn;
	}
}