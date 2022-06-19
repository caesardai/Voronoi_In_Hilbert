import java.util.Comparator;
import java.util.TreeSet;
import java.util.Set;
import java.util.Stack;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public final class GrahamScan
{
    protected static boolean areAllCollinear(final List<Point_2> points) {
        if (points.size() < 2) {
            return true;
        }
        final Point_2 a = points.get(0);
        final Point_2 b = points.get(1);
        for (int i = 2; i < points.size(); ++i) {
            final Point_2 c = points.get(i);
            if (getTurn(a, b, c) != GrahamScan.Turn.COLLINEAR) {
                return false;
            }
        }
        return true;
    }
    
    public static List<Point_2> getConvexHull(final int[] xs, final int[] ys) throws IllegalArgumentException {
        if (xs.length != ys.length) {
            throw new IllegalArgumentException("xs and ys don't have the same size");
        }
        final List<Point_2> points = new ArrayList<Point_2>();
        for (int i = 0; i < xs.length; ++i) {
            points.add(new Point_2((double)xs[i], (double)ys[i]));
        }
        return getConvexHull(points);
    }
    
    public static List<Point_2> getConvexHull(final List<Point_2> points) throws IllegalArgumentException {
        final List<Point_2> sorted = new ArrayList<Point_2>(getSortedPointSet(points));
        if (sorted.size() < 3) {
            throw new IllegalArgumentException("can only create a convex hull of 3 or more unique points");
        }
        if (areAllCollinear(sorted)) {
            throw new IllegalArgumentException("cannot create a convex hull from collinear points");
        }
        final Stack<Point_2> stack = new Stack<Point_2>();
        stack.push(sorted.get(0));
        stack.push(sorted.get(1));
        for (int i = 2; i < sorted.size(); ++i) {
            final Point_2 head = sorted.get(i);
            final Point_2 middle = stack.pop();
            final Point_2 tail = stack.peek();
            final GrahamScan.Turn turn = getTurn(tail, middle, head);
            switch (turn) {
                case COUNTER_CLOCKWISE: {
                    stack.push(middle);
                    stack.push(head);
                    break;
                }
                case CLOCKWISE: {
                    --i;
                    break;
                }
                case COLLINEAR: {
                    stack.push(head);
                    break;
                }
            }
        }
        stack.push(sorted.get(0));
        return new ArrayList<Point_2>(stack);
    }
    
    protected static Point_2 getLowestPoint(final List<Point_2> points) {
        Point_2 lowest = points.get(0);
        for (int i = 1; i < points.size(); ++i) {
            final Point_2 temp = points.get(i);
            if (temp.y < lowest.y || (temp.y == lowest.y && temp.x < lowest.x)) {
                lowest = temp;
            }
        }
        return lowest;
    }
    
    public static Set<Point_2> getSortedPointSet(final List<Point_2> points) {
        final Point_2 lowest = getLowestPoint(points);
        final TreeSet<Point_2> set = new TreeSet<Point_2>((Comparator<? super Point_2>)new GrahamScan.GrahamScan$1(lowest));
        set.addAll(points);
        return set;
    }
    
    protected static GrahamScan.Turn getTurn(final Point_2 a, final Point_2 b, final Point_2 c) {
        final double crossProduct = (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
        if (crossProduct > 0.0) {
            return GrahamScan.Turn.COUNTER_CLOCKWISE;
        }
        if (crossProduct < 0.0) {
            return GrahamScan.Turn.CLOCKWISE;
        }
        return GrahamScan.Turn.COLLINEAR;
    }
}