import processing.core.PApplet;
import java.util.Iterator;
import java.util.LinkedList;

// 
// Decompiled by Procyon v0.5.36
// 

public class HilbertGeometry
{
    static final double step = 0.001;
    protected Convex convex;
    private DrawingApplet frame;
    private Point_2[] centerPoints;
    private int max_X;
    private int max_Y;
    private int min_X;
    private int min_Y;
    
    public HilbertGeometry(final DrawingApplet frame, final String filename) {
        this.centerPoints = new Point_2[0];
        this.max_X = Integer.MIN_VALUE;
        this.max_Y = Integer.MIN_VALUE;
        this.min_X = Integer.MAX_VALUE;
        this.min_Y = Integer.MAX_VALUE;
        if (filename == null) {
            this.convex = new Convex(frame);
        }
        else {
            this.convex = new Convex(frame, filename);
        }
        this.frame = frame;
    }
    
    public void reset() {
        this.centerPoints = new Point_2[0];
        this.convex = new Convex(this.frame);
        this.max_X = Integer.MIN_VALUE;
        this.max_Y = Integer.MIN_VALUE;
        this.min_X = Integer.MAX_VALUE;
        this.min_Y = Integer.MAX_VALUE;
    }
    
    public double distance(final Point_2 p, final Point_2 q) {
        if (p.x == q.x && p.y == q.y) {
            return 0.0;
        }
        final Point_2[] intersectionPoints = this.intersectionPoints(p, q);
        final Point_2 P = intersectionPoints[0];
        final Point_2 Q = intersectionPoints[1];
        return Math.abs(0.5 * Math.log(p.minus((Point_)P).squaredLength() * q.minus((Point_)Q).squaredLength() / (p.minus((Point_)Q).squaredLength() * q.minus((Point_)P).squaredLength())));
    }
    
    public boolean isInConvex(final Point_2 p) {
        return this.convex.isInConvex(p);
    }
    
    public int findPoint(final Point_2 p) {
        return this.convex.findPoint(p);
    }
    
    public int findCenterPoint(final Point_2 p) {
        for (int i = 0; i < this.centerPoints.length; ++i) {
            if (Util.closePoints(p, this.centerPoints[i])) {
                return i;
            }
        }
        return -1;
    }
    
    public Point_2 getPoint(final int index) {
        return this.convex.getPoint(index);
    }
    
    public Point_2 getCenterPoint(final int index) {
        return this.centerPoints[index];
    }
    
    public void movePoint(final int index, final Point_2 q) {
        this.convex.movePoint(index, q);
    }
    
    public void moveCenterPoint(final int index, final Point_2 p) {
        this.centerPoints[index] = p;
    }
    
    public void addCenterPoint(final Point_2 p) {
        final Point_2[] oldCenterPoints = this.centerPoints.clone();
        this.centerPoints = new Point_2[oldCenterPoints.length + 1];
        for (int i = 0; i < oldCenterPoints.length; ++i) {
            this.centerPoints[i] = oldCenterPoints[i];
        }
        this.centerPoints[oldCenterPoints.length] = p;
    }
    
    private Point_2[] intersectionPoints(final Point_2 p, final Point_2 q) {
        final Point_3 pProj = (Point_3)p.toHomogeneous();
        final Point_3 qProj = (Point_3)q.toHomogeneous();
        final Point_3 pqLine = pProj.crossProduct(qProj);
        Point_3 beginPoint = (Point_3)this.convex.convexHull[0].toHomogeneous();
        final Point_2[] intersectionPoints = new Point_2[2];
        int count = 0;
        for (int i = 1; i < this.convex.convexHull.length; ++i) {
            final Point_3 endPoint = (Point_3)this.convex.convexHull[i].toHomogeneous();
            final Point_3 intersect = this.intersection(pqLine, beginPoint, endPoint);
            if (intersect.z != 0.0 && count < 2 && !Util.contains(intersectionPoints, (Point_2)intersect)) {
                intersectionPoints[count] = (Point_2)intersect;
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
    
    public void updateInputPoints(final LinkedList<Point_2> controlPoints, final LinkedList<Point_2> centerPoints) {
        this.updateCenterPoints(centerPoints);
        this.convex.updateControlPoints(controlPoints);
    }
    
    private void updateCenterPoints(final LinkedList<Point_2> centerPoints) {
        this.centerPoints = new Point_2[centerPoints.size()];
        int i = 0;
        for (final Point_2 p : centerPoints) {
            this.centerPoints[i] = new Point_2(p.getX(), p.getY());
            ++i;
        }
    }
    
    public void draw(final boolean mode, final double radius) {
        this.frame.stroke(255.0f, 0.0f, 0.0f);
        this.drawConvexHull();
        if (mode) {
            this.frame.stroke(0.0f, 0.0f, 0.0f);
            this.drawCenterPoints();
            this.frame.stroke(255.0f, 255.0f, 0.0f);
            this.drawHilbertBalls(radius);
        }
        this.frame.stroke(0.0f, 0.0f, 0.0f);
    }
    
    private void drawControlPoints() {
        this.convex.drawControlPoints();
    }
    
    private void drawConvexHull() {
        this.convex.drawConvexHull();
    }
    
    private void drawCenterPoints() {
        this.convex.drawCenterPoints(this.centerPoints);
    }
    
    private void drawHilbertBalls(final double radius) {
        if (this.centerPoints.length < 1) {
            return;
        }
        this.extremePoints();
        for (int i = 0; i < this.centerPoints.length; ++i) {
            final Point_2 Q = this.centerPoints[i];
            this.drawHilbertBall(Q, radius);
        }
    }
    
    private void bruteForceHilbertBall(final Point_2 p) {
        for (int x = this.min_X; x < this.max_X; ++x) {
            for (int y = this.min_Y; y < this.max_Y; ++y) {
                final Point_2 q = new Point_2((double)x, (double)y);
                if (this.convex.isInConvex(q) && this.distance(p, q) < 1.0) {
                    this.frame.ellipse((float)q.x, (float)q.y, 1.0f, 1.0f);
                }
            }
        }
    }
    
    private void drawHilbertBall(final Point_2 p, final double radius) {
        final LinkedList<Point_2> points = new LinkedList<Point_2>();
        this.drawHilbertBallPoints(p, radius);
        for (double theta = 0.0; theta < 3.141592653589793; theta += 0.001) {
            final Point_2 p2 = new Point_2(p.x + Math.cos(theta), p.y + Math.sin(theta));
            final Point_2[] intersectionPoints = this.intersectionPoints(p, p2);
            if (intersectionPoints[0] != null) {
                if (intersectionPoints[1] != null) {
                    final double alpha = (p.x - intersectionPoints[1].x) / (intersectionPoints[0].x - intersectionPoints[1].x);
                    final double A = alpha / (1.0 - alpha);
                    final double beta1 = A / (A + Math.exp(radius));
                    final double beta2 = A / (A + Math.exp(-radius));
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
    
    private void drawHilbertBallPoints(final Point_2 p, final double radius) {
        Util.changeColor((PApplet)this.frame, Util.PURPLE);
        for (int i = 0; i < this.convex.convexHull.length; ++i) {
            final Point_2 q = this.convex.convexHull[i];
            final Point_2[] intersectionPoints = this.intersectionPoints(p, q);
            if (intersectionPoints[0] != null) {
                if (intersectionPoints[1] != null) {
                    final double alpha = (p.x - intersectionPoints[1].x) / (intersectionPoints[0].x - intersectionPoints[1].x);
                    final double A = alpha / (1.0 - alpha);
                    final double beta1 = A / (A + Math.exp(radius));
                    final double beta2 = A / (A + Math.exp(-radius));
                    final double[] coeff1 = { beta1, 1.0 - beta1 };
                    final double[] coeff2 = { beta2, 1.0 - beta2 };
                    final Point_2 pA = (Point_2)Point_2.linearCombination(intersectionPoints, coeff1);
                    final Point_2 pB = (Point_2)Point_2.linearCombination(intersectionPoints, coeff2);
                    Util.drawPoint(pA, (PApplet)this.frame);
                    Util.drawPoint(pB, (PApplet)this.frame);
                }
            }
        }
        Util.changeColor((PApplet)this.frame, Util.DEFAULT);
    }
    
    private void extremePoints() {
        final Convex convex = this.convex;
        for (int N = convex.convexHull.length, i = 0; i < N; ++i) {
            if (this.max_X < convex.convexHull[i].x) {
                this.max_X = (int)convex.convexHull[i].x;
            }
            if (this.max_Y < convex.convexHull[i].y) {
                this.max_Y = (int)convex.convexHull[i].y;
            }
            if (this.min_X > convex.convexHull[i].x) {
                this.min_X = (int)convex.convexHull[i].x;
            }
            if (this.min_Y > convex.convexHull[i].y) {
                this.min_Y = (int)convex.convexHull[i].y;
            }
        }
    }
}