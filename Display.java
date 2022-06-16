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
                this.hull = null;

		// set background
		c.setBackground(Color.white);

		// add mouse listener
		c.addMouseListener(this);

		add(c);
		setSize(1400, 900);

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
		sites.add(new Point(x, y));

                // compute convex hull on sites
		if (this.sites.size() >= 3) {
			List<Point> convexHull = cg.GrahamScan.getConvexHull(sites);
                        this.hull = new ArrayList<Point>(convexHull);
			for(java.awt.Point p : this.hull) {
				System.out.println(p);
			}
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
                System.out.println("(" + p.x + ", " + p.y + ")"); // print out mouse click coordinates
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
        }
  
	// main
	public static void main(String args[]) {
		Display d = new Display();

	}
}
