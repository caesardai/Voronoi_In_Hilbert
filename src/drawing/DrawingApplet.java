package drawing;

import javax.swing.*;

import geometry.Util;

import java.awt.*;
import java.awt.event.*;

import processing.core.PApplet;
import processing.event.Event;

import trapmap.Segment;
import trapmap.Trapezoid;
import trapmap.TrapMap;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.*;

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
	private java.util.List<Trapezoid> allTraps;

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
		size(800, 600);
		initButton();
		
		/*
		// DEBUGGING -------------------------------------------------------
		java.util.List<Segment> segments = new ArrayList<>();
		Point2D.Double site1 = new Point2D.Double(10, 20);
		Point2D.Double site2 = new Point2D.Double(55, 10);
		Point2D.Double site3 = new Point2D.Double(200, 18);

		// larger box
		Segment s1 = new Segment(100, 100, 200, 400, site1);
		Segment s2 = new Segment(200, 400, 400, 550, site1);
		Segment s3 = new Segment(400, 550, 550, 300, site3);
		Segment s4 = new Segment(550, 300, 600, 100, site3);
		Segment s5 = new Segment(450, 100, 600, 100, site3);
		Segment s6 = new Segment(250, 100, 450, 100, site2);
		Segment s7 = new Segment(100, 100, 250, 100, site1);
		Segment s8 = new Segment(250, 100, 400, 550, site1, site2);
		Segment s9 = new Segment(450, 100, 400, 550, site2, site3);
		segments.addAll(Arrays.asList(s1, s2, s3, s4, s5, s6, s7, s8, s9));

		TrapMap trapMap = new TrapMap(segments);
		
		// get all trapezoids
		this.allTraps = trapMap.getAllTrapezoids();
		size(800, 600);
		// END DEBUGGING -------------------------------------------------------
	 */

		this.geometry = new HilbertGeometryDraw(this, FILENAME_CONVEX);
		if (FILENAME_VORONOI != null)
			this.voronoi = new VoronoiDraw(geometry, FILENAME_VORONOI, this);
		else
			this.voronoi = new VoronoiDraw(geometry, this);

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
		
		/*
		// DEBUGGING -----------------------------------------
		int n = 3;
		int siteCount = 0;
		ArrayList<Point2D.Double> sites = new ArrayList<Point2D.Double>(n);
		ArrayList<Color> siteColors = new ArrayList<Color>(n);
		ArrayList<Trapezoid> listTraps = new ArrayList<Trapezoid>();
		listTraps.addAll(this.allTraps);

		// keep track of every segment
		ArrayList<Segment> segs = new ArrayList<Segment>();
		ArrayList<Color> colors = new ArrayList<Color>();
		for(int index = 0; index < listTraps.size(); index++) {
			Trapezoid t = listTraps.get(index);
			Segment top = t.getUpperBound();
			Segment bottom = t.getLowerBound();
			
			// default color option
			Color c = DrawUtil.BLACK;
			if(t.getSite() != null) {
				// populate sites if they are null
				if(siteCount < n && !sites.contains(t.getSite())) {
					// add site
					sites.add(t.getSite());

					// set color
					if(siteCount == 0)
						c = DrawUtil.PURPLE;
					else if(siteCount == 1)
						c = DrawUtil.GREEN;
					else if(siteCount == 2)
						c = DrawUtil.BLUE;
					else
						System.out.println("ERROR: COLOR OVERFLOW");
					
					siteColors.add(c);
					siteCount++;
				}
				
				// determine color to use
				int siteIndex = sites.indexOf(t.getSite());
				c = siteColors.get(siteIndex);
			}

			// determine if the segment already exists in the ArrayList
			int i1 = segs.indexOf(top);
			int i2 = segs.indexOf(bottom);
			
			if(i1 == -1) {
				segs.add(top);
				colors.add(c);
			} else {
				Color oldColor = colors.get(i1);
				if(oldColor.equals(DrawUtil.BLACK) && !c.equals(DrawUtil.BLACK))
					colors.set(i1, c);
			}

			if(i2 == -1) {
				segs.add(bottom);
				colors.add(c);
			} else {
				Color oldColor = colors.get(i2);
				if(oldColor.equals(DrawUtil.BLACK) && !c.equals(DrawUtil.BLACK))
					colors.set(i2, c);
			}
			
			// draw vertical lines
			Point2D.Double tl = Util.toPoint2D( t.getUpperBound().intersect(t.getLeftBound().x) );
			Point2D.Double tr = Util.toPoint2D( t.getUpperBound().intersect(t.getRightBound().x) );
			Point2D.Double bl = Util.toPoint2D( t.getLowerBound().intersect(t.getLeftBound().x) );
			Point2D.Double br = Util.toPoint2D( t.getLowerBound().intersect(t.getRightBound().x) );

			DrawUtil.changeColor(this, c);
			DrawUtil.drawSegment(bl, tl, this);
			DrawUtil.drawSegment(br, tr, this);
		}
		
		for(int index = 0; index < segs.size(); index++) {
			Segment s = segs.get(index);
			DrawUtil.changeColor(this, colors.get(index));
			DrawUtil.drawSegment(Util.toPoint2D(s.getLeftPoint()), Util.toPoint2D(s.getRightPoint()), this);
			continue;
		}

		// END DEBUGGING -----------------------------------------
	 */

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
