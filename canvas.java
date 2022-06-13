import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

class canvas extends JFrame implements MouseListener, MouseMotionListener {

	// create a canvas
	Canvas c;

	// constructor
	canvas() {
		super("Voronoi in Hilbert");

		// create a empty canvas
		c = new Canvas() {
			public void paint(Graphics g) {
      
			}
		};

		// set background
		c.setBackground(Color.white);

		// add mouse listener
		c.addMouseListener(this);
		c.addMouseMotionListener(this);

		add(c);
		setSize(800, 500);

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

		// draw a Oval at the point
		// where mouse is moved
		g.fillOval(x, y, 5, 5);
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
		Graphics g = c.getGraphics();

		g.setColor(Color.blue);

		// get X and y position
		int x, y;
		x = e.getX();
		y = e.getY();

		// draw a Oval at the point where mouse is moved
		g.fillOval(x, y, 5, 5);
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
		canvas c = new canvas();
	}
}
