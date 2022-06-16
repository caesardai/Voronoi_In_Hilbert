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

                // compute convex hull on sites
		if (this.hull.size() >= 3) {
			List<Point> convexHull = cg.GrahamScan.getConvexHull(this.hull);
                        this.hull = new ArrayList<Point>(convexHull);
			for(java.awt.Point p : this.hull) {
				System.out.println(p);
			}
		}

		if(!this.hull.contains(newPoint))
			this.sites.add(newPoint);

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
                    int b;
                    double a1, a2, c1, c2;

                    // site we are looking at
                    Point sitePoint = this.sites.get(s);

                    // a1 hull edge to site
                    Point hullPoint = this.hull.get(h);
                    a1 = (sitePoint.y - hullPoint.y) / (sitePoint.x - hullPoint.x);
                    b = -1;
                    c1 = a1 * hullPoint.x - hullPoint.y;
                    
                    // loop through all hull line segements
                    for(int index = 0; index < this.hull.size() - 1; index++) {
                        // get line segment line equation
                        Point h1 = hull.get(index);
                        Point h2 = hull.get(index + 1);
                        a2 = (h2.y - h1.y) / (h2.x - h1.x);

                        c2 = a2 * h1.x - h1.y;

                        double[] sol = computeLine(a1, a2, b, c1, c2); // returns sol double[]

                        if (sol == null) {
                                continue;
                        }

                        System.out.println("(" + sol[0] + ", " + sol[1] + ")");

                        for (int i = 0; i < this.hull.size(); i++) {
                            Point p1 = this.hull.get(i);
                            Point p2 = this.hull.get( (i+1) % this.hull.size() );

                            // determine how the points are positioned
                            int x1, x2, y1, y2;
                            if(p1.x < p2.x) {
                                x1 = p1.x;
                                x2 = p2.x;
                            } else {
                                x2 = p1.x;
                                x1 = p2.x;
                            }

                            if(p1.y < p2.y) {
                                y1 = p1.y;
                                y2 = p2.y;
                            } else {
                                y2 = p1.y;
                                y1 = p2.y;
                            }

                            // check if solution is inside the rectangle
                            if( (sol[0] > x1 && sol[0] < x2) && (sol[1] > y1 && sol[1] < y2) ) {
                                g.drawLine(this.hull.get(h).x, this.hull.get(h).y, (int) sol[0], (int) sol[1]);
                            }	
                        }
                    }
                }
            }
            System.out.println("Finished drawing spokes");
        }

        private double[] computeLine(double a1, double a2, int b, double c1, double c2) {
            double x, y, k;

            if (a1 == a2)
                return null;
            
            k = a1 / a2;
            y = (c2 - k * c1) / (k - 1);
            x = (c1 - b * y) / a1;

            double[] sol = {x, y};
            return sol;
        }
  
	// main
	public static void main(String args[]) {
		Display d = new Display();
	}
}
