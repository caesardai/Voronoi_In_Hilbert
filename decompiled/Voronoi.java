import java.util.Iterator;
import java.util.Random;
import java.util.HashMap;
import java.util.LinkedList;
 

public class Voronoi
{
    private DrawingApplet frame;
    private HilbertGeometry geometry;
    private LinkedList<Point_2> centerPoints;
    private LinkedList<Point_3> colors;
    private HashMap<Point_2, Integer> voronoiPoints;
    private int max_X;
    private int max_Y;
    private int min_X;
    private int min_Y;
    private static final Random RANDOM_INT;
    protected boolean hasChanged;
    
    static {
        RANDOM_INT = new Random();
    }
    
    public Voronoi(final HilbertGeometry g, final DrawingApplet frame) {
        this.centerPoints = new LinkedList<Point_2>();
        this.colors = new LinkedList<Point_3>();
        this.voronoiPoints = new HashMap<Point_2, Integer>();
        this.max_X = Integer.MIN_VALUE;
        this.max_Y = Integer.MIN_VALUE;
        this.min_X = Integer.MAX_VALUE;
        this.min_Y = Integer.MAX_VALUE;
        this.hasChanged = true;
        this.geometry = g;
        this.frame = frame;
    }
    
    public void addPoint(final Point_2 p) {
        this.centerPoints.add(p);
        this.colors.add(new Point_3(Voronoi.RANDOM_INT.nextInt(255), Voronoi.RANDOM_INT.nextInt(255), Voronoi.RANDOM_INT.nextInt(255)));
        this.hasChanged = true;
    }
    
    public void reset() {
        this.centerPoints = new LinkedList<Point_2>();
        this.colors = new LinkedList<Point_3>();
        this.voronoiPoints.clear();
        this.hasChanged = true;
    }
    
    public void drawPoints() {
        synchronized (this) {
            final int N = this.centerPoints.size();
            for (final Point_2 p : this.voronoiPoints.keySet()) {
                final Point_3 color = this.colors.get(this.voronoiPoints.get(p));
                this.frame.fill((float)color.x, (float)color.y, (float)color.z);
                this.frame.stroke((float)color.x, (float)color.y, (float)color.z);
                this.frame.ellipse((float)p.x, (float)p.y, 1.0f, 1.0f);
            }
            for (int i = 0; i < N; ++i) {
                final Point_3 color2 = this.colors.get(i);
                final Point_2 p2 = this.centerPoints.get(i);
                this.frame.fill((float)color2.x, (float)color2.y, (float)color2.z);
                this.frame.stroke(255.0f, 255.0f, 255.0f);
                this.frame.ellipse((float)p2.x, (float)p2.y, 10.0f, 10.0f);
            }
        }
        this.frame.fill(0.0f, 0.0f, 0.0f);
        this.frame.stroke(255.0f, 255.0f, 255.0f);
    }
    
    public void colorPoint(final Point_2 p) {
        final int nearestPoint = this.nearestPoint(p);
        this.voronoiPoints.put(p, nearestPoint);
    }
    
    private int nearestPoint(final Point_2 p) {
        if (p == null) {
            System.out.println("P is null");
        }
        if (this.centerPoints.size() == 1) {
            return 0;
        }
        int nearestPoint = 0;
        double nearestDistance = this.geometry.distance(p, this.centerPoints.get(0));
        for (int i = 1; i < this.centerPoints.size(); ++i) {
            final double tempDist = this.geometry.distance(p, this.centerPoints.get(i));
            if (tempDist < nearestDistance) {
                nearestDistance = tempDist;
                nearestPoint = i;
            }
        }
        return nearestPoint;
    }
    
    public void computeVoronoi() {
        if (this.hasChanged) {
            this.extremePoints();
            this.voronoiPoints.clear();
            for (int x = this.min_X; x < this.max_X; x += 2) {
                for (int y = this.min_Y; y < this.max_Y; y += 2) {
                    final Point_2 p = new Point_2(x, y);
                    if (this.geometry.convex.isInConvex(p)) {
                        this.colorPoint(p);
                    }
                }
            }
        }
        this.hasChanged = false;
    }
    
    private void extremePoints() {
        final Convex convex = this.geometry.convex;
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