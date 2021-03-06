package geometry;

import java.awt.geom.Point2D;
import java.util.LinkedList;

import micycle.trapmap.Segment;

// class to store bisectors between two sites and the end points that define the boundary between two different Voronoi cells
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
		this.computeBisector();
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
		this.s = (double) 1;
		
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
	}	
}