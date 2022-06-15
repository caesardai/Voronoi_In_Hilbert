import java.awt.Point;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

public class Display extends JFrame implements MouseListener {
	// private field
	private Canvas c;
	private ArrayList<Point> sites;

	// constructor
	public Display() {
		super("Voronoi in Hilbert");

		// create a empty canvas
		c = new Canvas() {
		    public void paint(Graphics g) {}
		};

		this.sites = new ArrayList<Point>();

		// set background
		c.setBackground(Color.white);

		// add mouse listener
		c.addMouseListener(this);

		add(c);
		setSize(800, 500);

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

		// draw a Oval at the point
		g.fillOval(x, y, 5, 5);
		System.out.println("(" + x + ", " + y + ")"); // print out mouse click coordinates

		if (this.sites.size() >= 3) {
			List<Point> convexHull = cg.GrahamScan.getConvexHull(sites);
			for(java.awt.Point p : convexHull) {
				System.out.println(p);
			}
		}
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
  
	// main
	public static void main(String args[]) {
		Display d = new Display();

	}
}
