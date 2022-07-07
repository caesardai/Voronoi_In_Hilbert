package geometry;

import java.awt.geom.Point2D;

/******************************************************************************
 *  Compilation:  javac QuadTree.java
 *  Execution:    java QuadTree M N
 *
 *  Quad tree.
 * 
 * Modified by REU-CAAR: Hilbert Geometry Code
 ******************************************************************************/

public class QuadTree {
    private Node root;

    // helper node data type
    private class Node {
    	Point2D.Double p;
        Node NW, NE, SE, SW;   // four subtrees

        Node(double x, double y) {
        	this.p = new Point2D.Double(x, y);
        }

        Node(Point2D.Double p) {
        	this.p.setLocation(p);
        }
    }


  /***********************************************************************
    *  Insert (x, y) into appropriate quadrant
    ***************************************************************************/
    public void insert(Point2D.Double newPoint) {
        root = insert(root, newPoint);
    }

    private Node insert(Node h, Point2D.Double newPoint) {
        if (h == null) return new Node(newPoint);
        //// if (eq(x, h.x) && eq(y, h.y)) h.value = value;  // duplicate
        else if ( less(newPoint.x, h.p.x) &&  less(newPoint.y, h.p.y)) h.SW = insert(h.SW, newPoint);
        else if ( less(newPoint.x, h.p.x) && !less(newPoint.y, h.p.y)) h.NW = insert(h.NW, newPoint);
        else if (!less(newPoint.x, h.p.x) &&  less(newPoint.y, h.p.y)) h.SE = insert(h.SE, newPoint);
        else if (!less(newPoint.x, h.p.x) && !less(newPoint.y, h.p.y)) h.NE = insert(h.NE, newPoint);
        return h;
    }


  /***********************************************************************
    *  Range search.
    ***************************************************************************/

    public void query2D(Point2D.Double p) {
        query2D(root, p);
    }

    private void query2D(Node h, Point2D.Double p) {
        if (h == null) return;
        if (rect.contains(h.x, h.y))
            System.out.println("    (" + h.x + ", " + h.y + ") " + h.value);
        if ( less(xmin, h.x) &&  less(ymin, h.y)) query2D(h.SW, rect);
        if ( less(xmin, h.x) && !less(ymax, h.y)) query2D(h.NW, rect);
        if (!less(xmax, h.x) &&  less(ymin, h.y)) query2D(h.SE, rect);
        if (!less(xmax, h.x) && !less(ymax, h.y)) query2D(h.NE, rect);
    }


   /***************************************************************************
    *  helper comparison functions
    ***************************************************************************/

    private boolean less(double k1, double k2) { return k1 < k2; }
    private boolean eq(double k1, double k2) { return k1 == k2; }


   /***************************************************************************
    *  test client
    ***************************************************************************/
    /*
    public static void main(String[] args) {
        int M = Integer.parseInt(args[0]);   // queries
        int N = Integer.parseInt(args[1]);   // points

        QuadTree<Integer, String> st = new QuadTree<Integer, String>();

        // insert N random points in the unit square
        for (int i = 0; i < N; i++) {
            Integer x = (int) (100 * Math.random());
            Integer y = (int) (100 * Math.random());
            // StdOut.println("(" + x + ", " + y + ")");
            st.insert(x, y, "P" + i);
        }
        StdOut.println("Done preprocessing " + N + " points");

        // do some range searches
        for (int i = 0; i < M; i++) {
            Integer xmin = (int) (100 * Math.random());
            Integer ymin = (int) (100 * Math.random());
            Integer xmax = xmin + (int) (10 * Math.random());
            Integer ymax = ymin + (int) (20 * Math.random());
            Interval<Integer> intX = new Interval<Integer>(xmin, xmax);
            Interval<Integer> intY = new Interval<Integer>(ymin, ymax);
            Interval2D<Integer> rect = new Interval2D<Integer>(intX, intY);
            StdOut.println(rect + " : ");
            st.query2D(rect);
        }
    }
    */
}
