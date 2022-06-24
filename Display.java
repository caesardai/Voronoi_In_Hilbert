import java.awt.Point;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Component;
import javax.swing.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

public class Display extends JFrame implements MouseListener {
	// private field
	private Canvas c;
	private ArrayList<Point> sites;
	private ArrayList<Point> hull;

	// constructor
	public Display() {
		super("Voronoi in Hilbert");

		// create a empty canvas
		c = new Canvas() {
		    public void paint(Graphics g) {}
		};

                // initialize fields
		this.sites = new ArrayList<Point>();
                this.hull = new ArrayList<Point>();

                // debuggging
                // this.sites.add(new Point(200, 200));
                // this.hull.add(new Point(100, 100));
                // this.hull.add(new Point(500, 100));
                // this.hull.add(new Point(500, 500));
                // this.hull.add(new Point(100, 500));
                // this.hull.add(new Point(100, 100));

                // this.sites.add(new Point(400, 300));
                // this.hull.add(new Point(400, 100));
                // this.hull.add(new Point(700, 300));
                // this.hull.add(new Point(500, 500));
                // this.hull.add(new Point(300, 500));
                // this.hull.add(new Point(100, 300));
                // this.hull.add(new Point(400, 100));

		// set background
		c.setBackground(Color.white);

		// add mouse listener
		c.addMouseListener(this);

		add(c);
		setSize(800, 600);

        // add windows closer
        addWindowListener (new WindowAdapter() { 
			public void windowClosing (WindowEvent e) {    
            dispose();    
            }    
        });  

    	show();

        this.draw(c.getGraphics());
	}

	// mouse listener and mouse motion listener methods
	public void mouseClicked(MouseEvent e) {
		Graphics g = c.getGraphics();

		g.setColor(Color.black);

		// get X and y position
		int x, y;
		x = e.getX();
		y = e.getY();

                Point newPoint = new Point(x, y);
		this.hull.add(newPoint);
                // copy elements in old hull
                ArrayList<Point> oldHull = new ArrayList<Point>(this.hull.size());
                for(Point p : this.hull) {
                    oldHull.add(new Point(p.x, p.y));
                }

                // compute convex hull on sites
		if (this.hull.size() >= 3) {
			List<Point> convexHull = cg.GrahamScan.getConvexHull(this.hull);
                        this.hull = new ArrayList<Point>(convexHull);
			for(java.awt.Point p : this.hull) {
				System.out.println(p);
			}
		}

                // check if the new point is in the hull
		if(!this.hull.contains(newPoint))
			this.sites.add(newPoint);

                // place former hull points into the sites list
                for(Point p : oldHull) {
                    if(!this.hull.contains(p))
                        this.sites.add(new Point(p.x, p.y));
                }

                // draw on canvas
                this.draw(g);
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e){
	}

	public void mouseReleased(MouseEvent e){
	}

	public void mousePressed(MouseEvent e){
	}

        // this method draws all sites, convex hull, and any other features on the graph
        private void draw(Graphics g) {
            // clear the canvas
            g.clearRect(0, 0, ((Component) c).getWidth(), ((Component) c).getHeight());

            // draw in the site
            for(Point p : this.sites) {
                g.fillOval(p.x, p.y, 5, 5);
                System.out.println("(" + p.x + ", " + p.y + ") site"); // print out mouse click coordinates
            }
			for(Point p : this.hull) {
                g.fillOval(p.x, p.y, 5, 5);
                System.out.println("(" + p.x + ", " + p.y + ") hull"); // print out mouse click coordinates
            }

            // draw the convex hull if it can be created
            if(this.hull != null) {
                for(int i = 0; i < this.hull.size(); i++) {
                    Point p1 = this.hull.get(i);
                    Point p2 = this.hull.get( (i+1) % this.hull.size() );

                    g.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }

            // draw the spokes
            if (hull.size() >= 3 && sites.size() >= 1) {
                    drawSpokes(g);
            }
        }

        private void drawSpokes(Graphics g) {
            System.out.println("Drawing Spokes");
            // computes line euqations between hull vertex and sites
            for (int s = 0; s < sites.size(); s++) {
                for (int h = 0; h < hull.size() - 1; h++) {
                    double m1, m2, c1, c2;

                    // site we are looking at
                    Point sitePoint = this.sites.get(s);

                    // a1 hull edge to site
                    Point hullPoint = this.hull.get(h);
                    System.out.println("\nHull vertex (" + hullPoint.x + ", " + hullPoint.y + ")");

                    // check if our line is vertical
                    if(sitePoint.x == hullPoint.x) {
                        System.out.println("vertical site-hull spoke");
                        // return;

                        for(int index = 0; index < this.hull.size() - 1; index++) {
                            Point h1 = this.hull.get(index);
                            Point h2 = this.hull.get(index + 1);

                            // determine order of x-coordinates
                            int x1, x2;
                            if(h1.x < h2.x) {
                                x1 = h1.x;
                                x2 = h2.x;
                            } else {
                                x2 = h1.x;
                                x1 = h2.x;
                            }

                            if(sitePoint.x >= x1 && sitePoint.x <= x2) {
                                // check if site is not the vertex we are drawing a spoke from
                                if(!h1.equals(hullPoint) && !h2.equals(hullPoint)) {
                                    double x = sitePoint.x;
                                    double m = ((double) h2.y - h1.y) / ((double) h2.x - h1.x);
                                    double y = m * x - (m * h1.x - h1.y);
                                    g.drawLine(hullPoint.x, hullPoint.y, (int) x, (int) y);
                                    System.out.println("drew points between line-segment (" + h1.x + ", " + h1.y + ")-(" + h2.x + ", " + h2.y + ")" );
                                    break;
                                }
                            }
                        }
                        continue;
                    }
                    // if line is not vertical
                    m1 = ((double) (sitePoint.y - hullPoint.y)) / ((double) (sitePoint.x - hullPoint.x));
                    c1 = m1 * hullPoint.x - hullPoint.y;
                    
                    // loop through all hull line segements
                    for(int index = 0; index < this.hull.size() - 1; index++) {
                        // get line segment line equation
                        Point h1 = hull.get(index);
                        Point h2 = hull.get(index + 1);

                        // variable to hold our intersection point
                        double[] sol = new double[2];

                        // if our line segment is vertical
                        if(h1.x == h2.x) {
                            sol[0] = h1.x;
                            sol[1] = m1 * h1.x - c1;
                        } else {
                            m2 = ((double) (h2.y - h1.y)) / ((double) (h2.x - h1.x));
                            c2 = m2 * h1.x - h1.y;

                            System.out.println("slope for hull-site: " + m1);
                            System.out.println("slope for hull-hull: " + m2);

                            // if we have parallel lines
                            if(m1 == m2) {
                                System.out.println("parallel lines");
                                continue;
                            }

                            sol = computeLine(m1, m2, c1, c2); // returns solution to system of equations

                            System.out.println("(" + sol[0] + ", " + sol[1] + ") for line (" + h1.x + ", " + h1.y + ")-(" + h2.x + ", " + h2.y +  ")");
                        }

                        // check if the solution is the vertex we are drawing the spoke
                        if(sol[0] == (double) hullPoint.x && sol[1] == (double) hullPoint.y) {
                            continue;
                        }

                        // determine how the points are positioned
                        int x1, x2, y1, y2;
                        if(h1.x < h2.x) {
                            x1 = h1.x;
                            x2 = h2.x;
                        } else {
                            x2 = h1.x;
                            x1 = h2.x;
                        }

                        if(h1.y < h2.y) {
                            y1 = h1.y;
                            y2 = h2.y;
                        } else {
                            y2 = h1.y;
                            y1 = h2.y;
                        }

                        // check if solution is inside the rectangle
                        if( (sol[0] >= x1 && sol[0] <= x2) && (sol[1] >= y1 && sol[1] <= y2) ) {
                            g.drawLine(this.hull.get(h).x, this.hull.get(h).y, (int) sol[0], (int) sol[1]);
                            // break;
                        }	
                    }

                }
            }
            System.out.println("Finished drawing spokes");
        }

        private double[] computeLine(double m1, double m2, double c1, double c2) {
            double x, y;
            
            x = (c1 - c2) / (m1 - m2);
            y = m1 * x - c1;

            double[] sol = {x, y};
            return sol;
        }
  
	// main
	public static void main(String args[]) {
		Display d = new Display();
	}
}