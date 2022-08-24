package drawing;

import javax.swing.*;

import geometry.Convex;
import geometry.KdTree;
import geometry.Util;
import geometry.Sector;

import java.awt.*;
import java.awt.event.*;

import processing.core.PApplet;
import processing.event.Event;
import processing.core.PVector;

import trapmap.Segment;
import trapmap.Trapezoid;
import trapmap.TrapMap;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.*;
import java.util.List;

public class DrawingApplet extends PApplet implements ActionListener {
	private static final long serialVersionUID = 1L;

	/* Modes */
	enum Mode {
		DRAW_CONVEX, INCONVEXTEST, UNIT_BALL, VORONOI_DEF, VORONOI_FIND
	};

	private final static Mode[] MODES = { Mode.DRAW_CONVEX, /* Mode.INCONVEXTEST, */ /* Mode.UNIT_BALL, */
			Mode.VORONOI_DEF, /* Mode.VORONOI_FIND */ };
	private int currentMode = 0;
	private final static int NUMBER_MODES = 2;
	private final static String SWITCH_MODE = "Change to mode: ";
	static String FILENAME_CONVEX;
	static String FILENAME_VORONOI;

	// Buttons
	private Button button1, button2, button3;

	// Geometric objects
	public HilbertGeometryDraw geometry;
	public VoronoiDraw voronoi;
	final static double epsilon = 4.0;
	static double radius = 1;
	final static double RADIUS_STEP = 0.1;

	/* Variables for moving points */
	private float xOffset = 0.0f;
	private float yOffset = 0.0f;
	private boolean locked = false;
	private int indexOfMovingPoint = -1;
	private int indexOfSelectedPoint = -1;

	/* Panels and Buttons */
	private Button reinit;
	private Panel convexPanel;
	private CheckboxGroup drawMode;
	private Checkbox drawConvex;
	private Checkbox drawSpokes;
	private Checkbox drawVoronoi;

	// DEBUGGING
	KdTree<KdTree.XYZPoint> tree;
	List<Sector> secs;

	public static void main(String[] args) {
		if (args != null) {
			if (args.length > 0)
				FILENAME_CONVEX = args[0];
			if (args.length > 1)
				FILENAME_VORONOI = args[1];
		}
		PApplet.main(new String[] { "drawing.DrawingApplet" });
	}

	/*
	 * Setting up visualization interface
	 */
	public void setup() {
		size(1060, 600);
		initButton();

		this.geometry = new HilbertGeometryDraw(this, FILENAME_CONVEX);
		if (FILENAME_VORONOI != null)
			this.voronoi = new VoronoiDraw(geometry, FILENAME_VORONOI, this);
		else
			this.voronoi = new VoronoiDraw(geometry, this);

		/* TEST SECTOR GRAPH */
		// placing all input parameters to construct tree
		Convex c = this.geometry.convex;
		c.addPoint(new Point2D.Double(154d, 620d));
		c.addPoint(new Point2D.Double(67d, 190d));
		c.addPoint(new Point2D.Double(406d, 20d));
		c.addPoint(new Point2D.Double(488d, 500d));
		Point2D.Double site1 = new Point2D.Double(253d, 170d);
		Point2D.Double site2 = new Point2D.Double(343d, 500d);

		// construct tree
		KdTree<KdTree.XYZPoint> tree = this.voronoi.constructGraph(site1, site2);

		// Constructing sectors
		secs = Convex.constructSector(site1, site2, tree);

		/*
		 * traverse through all sectors for each sector => call all edges => color each
		 * sector with a random color
		 */

		/* ----------------- */

		// set starting mode
		this.currentMode = 0;

		// if points in Convex is not on the hull, add it to the HilbertGeometry object
		// if(this.geometry.convex.points.length > 0) {

//		// make sure there are no balls in geometry object
//		if (this.geometry.ballCount() > 0) {
//			for (int index = 0; index < this.geometry.ballCount(); index++) {
//				this.geometry.removeBall(index);
//			}
//		}
//
//		// Inserted non-hull points into HilbertGeometry object; no longer desired
//		for (Point2D.Double p : this.geometry.convex.points) {
//			if (this.geometry.convex.findPoint(p) == -1) {
//				double r = 2;
//				this.geometry.addCenterPoint(p, r);
//			}
//		}
	}

	public void initButton() {
		this.drawMode = new CheckboxGroup();
		this.drawConvex = new Checkbox("Insert Convex", this.drawMode, true);
		this.drawVoronoi = new Checkbox("Draw Voronoi", this.drawMode, false);
		// this.drawConvex.setBackground(DrawUtil.WHITE);
		// this.drawSpokes.setBackground(DrawUtil.WHITE);
		// this.drawSpokes = new Checkbox("Insert Sites", this.drawMode, false);
		this.reinit = new Button("Reinitialize");
		// this.reinit.setBackground(DrawUtil.WHITE);
		this.convexPanel = new Panel();
		this.convexPanel.add(this.drawConvex);
		this.convexPanel.add(this.drawVoronoi);
		this.convexPanel.add(this.reinit);
		// this.convexPanel.add(this.drawSpokes);
		// this.convexPanel.setBackground(DrawUtil.WHITE);
		this.convexPanel.setName("Drawing mode");
		this.add((Component) this.convexPanel);
		this.frame.setTitle("Voronoi In Hilbert Metrics");

		// System.out.println(this.frame.getTitle());

		// Adding ActionListener to bottoms
		drawConvex.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				currentMode = 0;
			}
		});

		drawVoronoi.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				currentMode = 1;
			}
		});

		reinit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				geometry.reset();
				synchronized (voronoi) {
					voronoi.reset();
				}
			}
		});
		/*
		 * drawSpokes.addItemListener(new ItemListener() {
		 * 
		 * @Override public void itemStateChanged(ItemEvent event) { currentMode = 1;
		 * voronoi.computeVoronoi(); } });
		 */
	}

	/*
	 * Method for drawing convex hull
	 */
	public void draw() {
		background(220);
		textFont(createFont("Arial", 12, true), 12); // font used
		fill(0); // font color


		/* TEST SECTOR GRAPH */
		// Coloring spokes with different colors

		/* TEST SECTOR GRAPH */
		Point2D.Double p1 = this.geometry.convex.convexHull[0];
		Point2D.Double p2 = this.geometry.convex.convexHull[1];
		Point2D.Double p3 = this.geometry.convex.convexHull[2];
		Point2D.Double p4 = this.geometry.convex.convexHull[3];
      	
		Segment e1 = new Segment((float) p1.x, (float) p1.y, (float) p2.x, (float) p2.y);
		Segment e2 = new Segment((float) p2.x, (float) p2.y, (float) p3.x, (float) p3.y);
		Segment e3 = new Segment((float) p3.x, (float) p3.y, (float) p4.x, (float) p4.y);
		Segment e4 = new Segment((float) p4.x, (float) p4.y, (float) p1.x, (float) p1.y);
		
//		ArrayList<KdTree.XYZPoint> endPoints = this.tree.getAllNodes();
//		for(KdTree.XYZPoint p : endPoints) {
//			// draw segment from point p to its neighbors
//			for(int index = 0; index < p.numOfNeighbors(); index++) {
//				Point2D.Double q = p.getNeighbor(index);
//				if(p.getSite(index) == null)
//					DrawUtil.changeColor(this, DrawUtil.BLACK);
//				else if(p.getEdge(index).equals(e1))
//					DrawUtil.changeColor(this, DrawUtil.PURPLE);
//				else if(p.getEdge(index).equals(e2))
//					DrawUtil.changeColor(this, DrawUtil.GREEN);
//				else if(p.getEdge(index).equals(e3))
//					DrawUtil.changeColor(this, DrawUtil.RED);
//				else if(p.getEdge(index).equals(e4))
//					DrawUtil.changeColor(this, DrawUtil.BLUE);
//					
//				DrawUtil.drawSegment(Util.toPoint2D(p), q, this);
//			}
//		}

		/*
		 * Color sectors
		 */
		
		for (Sector sec : secs) {
			// Find random color
			Color c = null;
			int colorNum = (int) (Math.random() * 8);
			if (colorNum == 0)
				c = DrawUtil.DEFAULT;
			if (colorNum == 1)
				c = DrawUtil.RED;
			if (colorNum == 2)
				c = DrawUtil.BLUE;
			if (colorNum == 3)
				c = DrawUtil.GREEN;
			if (colorNum == 4)
				c = DrawUtil.PURPLE;
			if (colorNum == 5)
				c = DrawUtil.BLACK;
			if (colorNum == 6)
				c = DrawUtil.GREY;
			if (colorNum == 7)
				c = DrawUtil.WHITE;

			this.fill((float) c.getRed(), (float) c.getGreen(), (float) c.getBlue());
			
			int numPoints = sec.sector.convexHull.length - 1;
			if (numPoints == 3) {
				this.quad((float) sec.sector.convexHull[0].x, (float) sec.sector.convexHull[0].y,
						(float) sec.sector.convexHull[1].x, (float) sec.sector.convexHull[1].y,
						(float) sec.sector.convexHull[2].x, (float) sec.sector.convexHull[2].y,
						(float) sec.sector.convexHull[2].x, (float) sec.sector.convexHull[2].y);
			}
			
			if (numPoints == 4) {
				quad((float) sec.sector.convexHull[0].x, (float) sec.sector.convexHull[0].y,
						(float) sec.sector.convexHull[1].x, (float) sec.sector.convexHull[1].y,
						(float) sec.sector.convexHull[2].x, (float) sec.sector.convexHull[2].y,
						(float) sec.sector.convexHull[3].x, (float) sec.sector.convexHull[3].y);
			}
		}

		/* ----------------- */

		if (this.geometry.convex.convexHull.length < 3)
			return; // no convex Hull to display.
		else {
			this.geometry.draw(false, -1);
		}

		if (MODES[currentMode].toString().contains("VORONOI")) { // true
			voronoi.drawPoints();
			this.voronoi.hasChanged = false;
		}

		for (this.indexOfSelectedPoint = 0; this.indexOfSelectedPoint < this.geometry
				.ballCount(); this.indexOfSelectedPoint++) {
			geometry.draw(true, this.indexOfSelectedPoint);
		}
		this.indexOfSelectedPoint = -1;
	}
	
	/*
	 * Determines if a point is within EPSILON distance away from some point on a
	 * list Q: What is the rationale for choosing the value of EPSILON?
	 */
	public int findPoint(int x, int y, LinkedList<Point2D.Double> pts) {
		Point2D.Double p = new Point2D.Double(x, y);

		int index = 0;
		boolean found = false;
		for (Point2D.Double q : pts) {
			if (q.distanceSq(p) < epsilon) {
				found = true;
				break;
			}
			index++;
		}
		if (found == true)
			return index;
		else
			return -1;
	}

	public void mouseClicked() {
		Point2D.Double p = new Point2D.Double(mouseX, mouseY);

		if (MODES[currentMode] == Mode.DRAW_CONVEX && mouseButton == LEFT) {
			this.geometry.convex.addPoint(p);
			this.voronoi.hasChanged = true;
			System.out.println("Point added to convex: (" + mouseX + ", " + mouseY + ")");
			if (this.voronoi.numPoints() > 0)
				this.voronoi.computeVoronoi();
		} else if (MODES[currentMode] == Mode.UNIT_BALL && mouseButton == LEFT) {
			this.indexOfSelectedPoint = this.geometry.findCenterPoint(p);
			if (this.indexOfSelectedPoint == -1) {
				this.geometry.addCenterPoint(p, radius);
				this.indexOfSelectedPoint = this.geometry.findCenterPoint(p);
			}
		} else if (MODES[currentMode] == Mode.UNIT_BALL && mouseButton == RIGHT) {
			int removedPoint = this.geometry.findCenterPoint(p);
			if (removedPoint == this.indexOfSelectedPoint) {
				this.indexOfSelectedPoint = -1;
			}
			this.geometry.convex.removePoint(p);
		} else if (MODES[currentMode] == Mode.INCONVEXTEST && mouseButton == LEFT) {
			if (this.geometry.isInConvex(p)) {
				System.out.println("Is in convex.");
			} else {
				System.out.println("Not in convex.");
			}
		} else if (MODES[currentMode] == Mode.VORONOI_DEF && mouseButton == LEFT) {
			// add to HilbertGeometry Object
			this.indexOfSelectedPoint = this.geometry.findCenterPoint(p);
			if (this.indexOfSelectedPoint == -1) {
				this.geometry.addCenterPoint(p, radius);
				this.indexOfSelectedPoint = this.geometry.findCenterPoint(p);
			}

			// add to Voronoi Object
			this.voronoi.addPoint(p);
			this.voronoi.computeVoronoi();
			this.voronoi.hasChanged = true;
		} else if (MODES[currentMode] == Mode.VORONOI_DEF && mouseButton == RIGHT) {
			// remove point from HilbertGeometry object
			int removedPoint = this.geometry.findCenterPoint(p);
			if (removedPoint == this.indexOfSelectedPoint) {
				this.indexOfSelectedPoint = -1;
			}

			// remove point from Voronoi object
			this.voronoi.removePoint(p);
			this.voronoi.computeVoronoi();
		} else if (MODES[currentMode] == Mode.VORONOI_FIND && mouseButton == LEFT) {
			this.voronoi.colorPoint(p);
		}
	}

	public void mousePressed() {
		Point2D.Double p = new Point2D.Double();
		p.x = (double) mouseX;
		p.y = (double) mouseY;
		if (MODES[currentMode] == Mode.DRAW_CONVEX) {
			indexOfMovingPoint = this.geometry.findPoint(p);
		} else if (MODES[currentMode] == Mode.UNIT_BALL) {
			indexOfMovingPoint = this.geometry.findCenterPoint(p);
		} else if (MODES[currentMode] == Mode.VORONOI_DEF) {
			indexOfMovingPoint = this.voronoi.findPoint(p);
		}
		if (indexOfMovingPoint > -1) {
			if (MODES[currentMode] == Mode.DRAW_CONVEX) {
				locked = true;
				xOffset = mouseX - (float) (double) this.geometry.getPoint(indexOfMovingPoint).x;
				yOffset = mouseY - (float) (double) this.geometry.getPoint(indexOfMovingPoint).y;
			} else if (MODES[currentMode] == Mode.UNIT_BALL) {
				locked = true;
				xOffset = mouseX - (float) (double) this.geometry.getCenterPoint(indexOfMovingPoint).x;
				yOffset = mouseY - (float) (double) this.geometry.getCenterPoint(indexOfMovingPoint).y;
			} else if (MODES[currentMode] == Mode.VORONOI_DEF) {
				locked = true;
				xOffset = mouseX - (float) (double) this.voronoi.getPoint(indexOfMovingPoint).x;
				yOffset = mouseY - (float) (double) this.voronoi.getPoint(indexOfMovingPoint).y;
			}
		}
	}

	public void mouseReleased() {
		if (locked) {
			locked = false;
			indexOfMovingPoint = -1;
		}
	}

	public void mouseDragged() {
		if (locked) {
			Point2D.Double q = new Point2D.Double(mouseX - xOffset, mouseY - yOffset);
			if (MODES[currentMode] == Mode.DRAW_CONVEX) {
				this.geometry.movePoint(indexOfMovingPoint, q);
				this.voronoi.hasChanged = true;
			} else if (MODES[currentMode] == Mode.UNIT_BALL) {
				this.geometry.moveCenterPoint(indexOfMovingPoint, q);
			} else if (MODES[currentMode] == Mode.VORONOI_DEF) {
				this.geometry.moveCenterPoint(indexOfMovingPoint, q);
				this.voronoi.movePoint(indexOfMovingPoint, q);
				this.voronoi.hasChanged = true;
			}
		}
	}

	/*
	 * Switch modes with key board input
	 */
//	public void keyPressed() {
//
//		if (this.key == 'q') {
//			this.currentMode = (this.currentMode + 1) % 3;
//			if (this.currentMode == 0) {
//				System.out.println("in drawing convex hull mode");
//			}
//		}
//		if (this.currentMode == 1) {
//			System.out.println("in drawing ball mode");
//		}
//		if (this.currentMode == 2) {
//			System.out.println("in drawing voronoi diagram mode");
//		}
//	}

	@Override
	public void actionPerformed(ActionEvent event) {
//       if (event.getSource() == newConvex) {
//         this.geometry.reset();
//         synchronized(this.voronoi) {
//           this.voronoi.reset(); 
//         } 
//		 radius = 1;
//		 } else if (event.getSource() == plusButton) {
//		 if (this.indexOfSelectedPoint != - 1) {
//		 this.geometry.updateRadius(indexOfSelectedPoint, RADIUS_STEP);
//		 }
//		 } else if (event.getSource() == minusButton) {
//		 if (this.indexOfSelectedPoint != -1) {
//		 this.geometry.updateRadius(indexOfSelectedPoint, -RADIUS_STEP);
//		 }
//		 } else if (event.getSource() == toggleMode) {
//		 currentMode = (currentMode + 1) % NUMBER_MODES;
//		 toggleMode.setLabel(SWITCH_MODE + MODES[(currentMode + 1) %
//		 NUMBER_MODES].toString());
//		 }
	}
}
