import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.FileReader;
import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;

public class Convex
{
    public DrawingApplet frame;
    private Point_2[] points;
    protected Point_2[] convexHull;
    
    public Convex(final DrawingApplet frame) {
        this.frame = frame;
        this.points = new Point_2[0];
        this.convexHull = new Point_2[0];
    }
    
    public Convex(final DrawingApplet frame, final String filename) {
        this.frame = frame;
        this.points = this.load(filename);
        this.computeConvexHull();
    }
    
    public Convex(final DrawingApplet frame, final LinkedList<Point_2> controlPoints) {
        this.frame = frame;
        this.points = new Point_2[controlPoints.size()];
        int i = 0;
        for (final Point_2 p : controlPoints) {
            this.points[i] = new Point_2(p.getX(), p.getY());
            ++i;
        }
    }
    
    public int findPoint(final Point_2 p) {
        for (int i = 0; i < this.convexHull.length; ++i) {
            final Point_2 testPoint = this.convexHull[i];
            if (Util.closePoints(testPoint, p)) {
                return i;
            }
        }
        return -1;
    }
    
    public Point_2 getPoint(final int index) {
        if (index >= 0 && index < this.convexHull.length) {
            return this.convexHull[index];
        }
        return null;
    }
    
    public void movePoint(final int index, final Point_2 q) {
        this.convexHull[index] = q;
        this.points = this.convexHull;
        this.computeConvexHull();
    }
    
    public void addPoint(final Point_2 p) {
        final Point_2[] oldControlPoints = this.points.clone();
        this.points = new Point_2[oldControlPoints.length + 1];
        for (int i = 0; i < oldControlPoints.length; ++i) {
            this.points[i] = oldControlPoints[i];
        }
        this.points[oldControlPoints.length] = p;
        this.computeConvexHull();
    }
    
    public void removePoint(final Point_2 p) {
        int index = -1;
        for (int i = 0; i < this.convexHull.length; ++i) {
            if (Util.closePoints(p, this.convexHull[i])) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            this.points = new Point_2[this.convexHull.length - 1];
            int count = 0;
            for (int j = 0; j < this.convexHull.length; ++j) {
                if (j != index) {
                    this.points[count] = this.convexHull[j];
                    ++count;
                }
            }
            this.computeConvexHull();
        }
    }
    
    public boolean isInConvex(final Point_2 p) {
        final int N = this.convexHull.length;
        int out = 0;
        int in = 0;
        for (int i = 0; i < N; ++i) {
            final Point_2 a = this.convexHull[i % N];
            final Point_2 b = this.convexHull[(i + 1) % N];
            final Vector_2 ab = b.minus((Point_)a);
            final Vector_2 ap = p.minus((Point_)a);
            if (ab.crossProduct((Vector_)ap) > 0.0) {
                ++out;
            }
            else if (ab.crossProduct((Vector_)ap) < 0.0) {
                ++in;
            }
            if (out > 0 && in > 0) {
                return false;
            }
        }
        return true;
    }
    
    public void updateControlPoints(final LinkedList<Point_2> controlPoints) {
        this.points = new Point_2[controlPoints.size()];
        int i = 0;
        for (final Point_2 p : controlPoints) {
            this.points[i] = new Point_2(p.getX(), p.getY());
            ++i;
        }
        this.computeConvexHull();
    }
    
    public void computeConvexHull() {
        if (this.points.length < 3) {
            return;
        }
        final List<Point_2> initialPoints = new LinkedList<Point_2>();
        Point_2[] points;
        for (int length = (points = this.points).length, j = 0; j < length; ++j) {
            final Point_2 p = points[j];
            initialPoints.add(p);
        }
        final List<Point_2> convexList = (List<Point_2>)GrahamScan.getConvexHull((List)initialPoints);
        this.convexHull = new Point_2[convexList.size()];
        for (int i = 0; i < convexList.size(); ++i) {
            this.convexHull[i] = convexList.get(i);
        }
    }
    
    public void drawPoint(final Point_2 p) {
        this.frame.ellipse((float)p.getX(), (float)p.getY(), 5.0f, 5.0f);
    }
    
    public void drawSegment(final Point_2 p, final Point_2 q) {
        this.frame.line((float)p.getX(), (float)p.getY(), (float)q.getX(), (float)q.getY());
    }
    
    public void drawControlPoints() {
        for (int i = 0; i < this.points.length; ++i) {
            this.drawPoint(this.points[i]);
        }
    }
    
    public void drawControlPolygon() {
        Point_2[] points;
        for (int length = (points = this.points).length, i = 0; i < length; ++i) {
            final Point_2 p = points[i];
            this.frame.ellipse((float)p.getX(), (float)p.getY(), 1.0f, 1.0f);
        }
    }
    
    public void drawCenterPoints(final Point_2[] centerPoints) {
        for (int i = 0; i < centerPoints.length; ++i) {
            this.drawPoint(centerPoints[i]);
        }
    }
    
    public void drawConvexHull() {
        if (this.convexHull.length < 3) {
            return;
        }
        Point_2 beginPoint = this.convexHull[0];
        for (int i = 1; i < this.convexHull.length; ++i) {
            final Point_2 endPoint = this.convexHull[i];
            this.drawPoint(beginPoint);
            this.drawSegment(beginPoint, endPoint);
            beginPoint = endPoint;
        }
    }
    
    public Point_2[] load(final String filename) {
        Scanner in;
        try {
            in = new Scanner(new FileReader(filename));
        }
        catch (FileNotFoundException e1) {
            e1.printStackTrace();
            return null;
        }
        int N = 0;
        try {
            N = in.nextInt();
        }
        catch (Exception e2) {
            System.out.println(e2.getCause());
        }
        final Point_2[] controlPoints = new Point_2[N];
        for (int i = 0; i < N; ++i) {
            final Double X = in.nextDouble();
            final Double Y = in.nextDouble();
            controlPoints[i] = new Point_2((double)X, (double)Y);
        }
        return controlPoints;
    }
}