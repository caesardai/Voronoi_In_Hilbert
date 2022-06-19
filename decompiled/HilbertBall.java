import java.util.LinkedList;


public class HilbertBall
{
    static final double step = 0.001;
    Convex convex;
    
    public HilbertBall(final Convex convex) {
        this.convex = convex;
    }
    
    public void drawHilbertBall(final Point_2 p) {
        final LinkedList<Point_2> points = new LinkedList<Point_2>();
        for (double theta = 0.0; theta < 3.141592653589793; theta += 0.001) {
            final Point_2 p2 = new Point_2(p.x + Math.cos(theta), p.y + Math.sin(theta));
            final Point_2[] intersectionPoints = this.intersectionPoints(p, p2);
            if (intersectionPoints[0] != null) {
                if (intersectionPoints[1] != null) {
                    final double alpha = (p.x - intersectionPoints[1].x) / (intersectionPoints[0].x - intersectionPoints[1].x);
                    final double A = alpha / (1.0 - alpha);
                    final double beta1 = A / (A + Math.exp(1.0));
                    final double beta2 = A / (A + Math.exp(-1.0));
                    final double[] coeff1 = { beta1, 1.0 - beta1 };
                    final double[] coeff2 = { beta2, 1.0 - beta2 };
                    points.add((Point_2)Point_2.linearCombination(intersectionPoints, coeff1));
                    points.add((Point_2)Point_2.linearCombination(intersectionPoints, coeff2));
                }
            }
        }
        final Convex hilbertBall = new Convex(this.convex.frame, points);
        hilbertBall.drawControlPolygon();
    }
    
    public Point_2[] intersectionPoints(final Point_2 p, final Point_2 q) {
        final Point_3 pProj = (Point_3)p.toHomogeneous();
        final Point_3 qProj = (Point_3)q.toHomogeneous();
        final Point_3 pqLine = pProj.crossProduct(qProj);
        Point_3 beginPoint = (Point_3)this.convex.convexHull[0].toHomogeneous();
        final Point_2[] intersectionPoints = new Point_2[2];
        int count = 0;
        for (int i = 1; i < this.convex.convexHull.length; ++i) {
            final Point_3 endPoint = (Point_3)this.convex.convexHull[i].toHomogeneous();
            final Point_3 intersect = this.intersection(pqLine, beginPoint, endPoint);
            if (intersect.z != 0.0 && count < 2) {
                intersectionPoints[count] = (Point_2)intersect.toCartesian();
                ++count;
            }
            beginPoint = endPoint;
        }
        return intersectionPoints;
    }
    
    private Point_3 intersection(final Point_3 line, final Point_3 p, final Point_3 q) {
        final Point_3 vectorPQ = new Point_3(p.x - q.x, p.y - q.y, p.z - q.z);
        final double denom = line.scalarProduct(vectorPQ);
        if (denom == 0.0) {
            return new Point_3();
        }
        final double t = -line.scalarProduct(q) / denom;
        if (t >= 0.0 && t <= 1.0) {
            final double[] scalarCoeff = { t, 1.0 - t };
            final Point_3[] pointCoeff = { p, q };
            return Point_3.linearCombination(pointCoeff, scalarCoeff);
        }
        return new Point_3();
    }
}