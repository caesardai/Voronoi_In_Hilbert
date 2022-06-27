package drawing;

import java.awt.event.ItemEvent;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Button;
import java.awt.Panel;
import java.awt.event.ItemListener;
import java.awt.event.ActionListener;
import processing.core.PApplet;

public class DrawingApplet extends PApplet implements ActionListener, ItemListener {
	private boolean record;
	private static final Mode[] MODES;
	private static final Ball_Mode[] BALL_MODES;
	private int currentMode;
	private int currentBallMode;
	private static int appletMode;
	private static final int NUMBER_MODES = 2;
	private static final int NUMBER_BALL_MODES = 3;
	private static final String SWITCH_MODE = "Change to mode: ";
	static String FILENAME;
	public Panel draw;
	public Panel convexPanel;
	public Button reinit;
//	public Button plusButton;
//	public Button minusButton;
//	public Button printPDF;
	public CheckboxGroup drawMode;
	public CheckboxGroup ballMode;
	public Checkbox toggleRays;
	public Checkbox drawConvex;
	public Checkbox unitBall;
	public Checkbox blueBall;
	public Checkbox redBall;
	public Checkbox normalBall;
	public HilbertGeometryDraw geometry;
	public VoronoiDraw voronoi;
	static final double epsilon = 4.0;
	static double radius;
	static final double RADIUS_STEP = 0.1;
	private float xOffset;
	private float yOffset;
	private int indexOfMovingPoint;
	private int indexOfSelectedPoint;
	private boolean locked;
	private DemoUtil demoHandler;

	static {
		MODES = new Mode[] { Mode.DRAW_CONVEX, Mode.UNIT_BALL };
		BALL_MODES = new Ball_Mode[] { Ball_Mode.NORMAL, Ball_Mode.RED, Ball_Mode.BLUE };
		DrawingApplet.appletMode = 0;
		DrawingApplet.radius = 1.0;
	}

	public DrawingApplet(final String[] args) {
		this.record = false;
		this.currentMode = 0;
		this.currentBallMode = 0;
		this.xOffset = 0.0f;
		this.yOffset = 0.0f;
		this.indexOfMovingPoint = -1;
		this.indexOfSelectedPoint = -1;
		this.locked = false;
		if (args != null && args.length > 0) {
			DrawingApplet.FILENAME = args[0];
			DrawingApplet.appletMode = Integer.parseInt(args[1]);
		}
	}

	public static void main(final String[] args) {
		if (args != null && args.length > 0) {
			DrawingApplet.FILENAME = args[0];
			DrawingApplet.appletMode = Integer.parseInt(args[1]);
		}
		PApplet.main(new String[] { "drawing.DrawingApplet" });
	}

	@Override
	public void setup() {
		this.size(600, 650);
		this.background(255);
		this.textFont(this.createFont("Arial", 12.0f, true), 12.0f);
		this.fill(0);
		this.geometry = new HilbertGeometryDraw(this, DrawingApplet.FILENAME);
		this.demoHandler = new DemoUtil(this.geometry);
		switch (DrawingApplet.appletMode) {
		case 0: {
			this.frameRate(60.0f);
			this.convexPanel.setEnabled(true);
			break;
		}
		case 1:
		case 8: {
			this.frameRate(5.0f);
			this.demoHandler.setSquare(50.0, 50.0, 500.0);
			this.convexPanel.setEnabled(false);
			break;
		}
		case 2:
		case 5:
		case 6:
		case 7: {
			this.frameRate(5.0f);
			this.demoHandler.setTriangle();
			this.convexPanel.setEnabled(false);
			break;
		}
		case 3:
		case 4:
		case 41: {
			this.frameRate(5.0f);
			this.demoHandler.setQuadrangle();
			this.convexPanel.setEnabled(false);
			break;
		}
		}
		this.voronoi = new VoronoiDraw(this.geometry, this);
	}

	@Override
	public void draw() {
		if (this.record) {
			final String filepath = "frames/frame-####.pdf";
			this.beginRecord("processing.pdf.PGraphicsPDF", filepath);
			System.out.println("Recording started in " + filepath);
		}
		this.background(255);
		if (DrawingApplet.MODES[this.currentMode].toString().contains("VORONOI")) {
			this.voronoi.drawPoints();
			this.voronoi.drawCircles();
			this.geometry.draw(false, -1);
		} else {
			switch (DrawingApplet.appletMode) {
			case 0: {
				this.geometry.draw(true, this.indexOfSelectedPoint);
				break;
			}
			case 1: {
				this.toggleRays.setState(false);
				this.indexOfSelectedPoint = 0;
				this.demoHandler.demoMovingSquare();
				break;
			}
			case 2: {
				this.toggleRays.setState(false);
				this.indexOfSelectedPoint = 0;
				this.demoHandler.demoMovingTriangle();
				break;
			}
			case 3: {
				this.toggleRays.setState(false);
				this.indexOfSelectedPoint = 0;
				this.demoHandler.demoMovingQuadrangle();
				break;
			}
			case 4: {
				this.toggleRays.setState(false);
				this.indexOfSelectedPoint = 0;
				this.demoHandler.demoRadius(256.0, 331.0, 15, 0.2);
				break;
			}
			case 41: {
				this.toggleRays.setState(false);
				this.indexOfSelectedPoint = 0;
				this.demoHandler.demoRadius(300.0, 331.0, 15, 0.2);
				break;
			}
			case 5: {
				this.demoHandler.demoInnerTangencyTriangle();
				break;
			}
			case 6: {
				this.demoHandler.demoNoIntersectionInsideTriangle();
				break;
			}
			case 7: {
				this.demoHandler.demoNoIntersectionTriangle();
				break;
			}
			case 8: {
				this.demoHandler.demoIntersectionSquare();
				break;
			}
			}
		}
		if (this.record) {
			this.endRecord();
			System.out.println("Recording ended in ");
			this.record = false;
		}
	}

	public int findPoint(final int x, final int y, final LinkedList<Point2D.Double> pts) {
		final Point2D.Double p = new Point2D.Double(x, y);
		int index = 0;
		boolean found = false;
		for (final Point2D.Double q : pts) {
			if (q.distanceSq(p) < 4.0) {
				found = true;
				break;
			}
			++index;
		}
		if (found) {
			return index;
		}
		return -1;
	}

	@Override
	public void mouseClicked() {
		final Point2D.Double p = new Point2D.Double(this.mouseX, this.mouseY);
		if (DrawingApplet.appletMode != 0) {
			return;
		}
		if (DrawingApplet.MODES[this.currentMode] == Mode.DRAW_CONVEX && this.mouseButton == 37) {
			this.geometry.convex.addPoint(p);
			this.voronoi.hasChanged = true;
			System.out.println("Point added to convex: (" + this.mouseX + ", " + this.mouseY + ")");
		} else if (DrawingApplet.MODES[this.currentMode] == Mode.UNIT_BALL && this.mouseButton == 37) {
			this.indexOfSelectedPoint = this.geometry.findCenterPoint(p);
			if (this.indexOfSelectedPoint == -1) {
				this.geometry.addCenterPoint(p, DrawingApplet.radius);
				this.geometry.addBallMode(DrawingApplet.BALL_MODES[this.currentBallMode]);
				this.geometry.addToggleMode(this.toggleRays.getState());
				this.indexOfSelectedPoint = this.geometry.findCenterPoint(p);
			} else {
				final Ball_Mode currentMode = this.geometry.getCurrentBallMode(this.indexOfSelectedPoint);
				switch (currentMode) {
				case NORMAL: {
					this.ballMode.setSelectedCheckbox(this.normalBall);
					break;
				}
				case BLUE: {
					this.ballMode.setSelectedCheckbox(this.blueBall);
					break;
				}
				case RED: {
					this.ballMode.setSelectedCheckbox(this.redBall);
					break;
				}
				}
				this.toggleRays.setState(this.geometry.getCurrentToggleMode(this.indexOfSelectedPoint));
			}
		} else if (DrawingApplet.MODES[this.currentMode] == Mode.UNIT_BALL && this.mouseButton == 39) {
			final int removedPoint = this.geometry.findCenterPoint(p);
			if (removedPoint == this.indexOfSelectedPoint) {
				this.indexOfSelectedPoint = -1;
			}
			// this.geometry.removeCenterPoint(removedPoint);
			this.geometry.removeBallMode(removedPoint);
			this.geometry.removeToggleMode(removedPoint);
		} else if (DrawingApplet.MODES[this.currentMode] == Mode.INCONVEXTEST && this.mouseButton == 37) {
			if (this.geometry.isInConvex(p)) {
				System.out.println("Is in convex.");
			} else {
				System.out.println("Not in convex.");
			}
		} else {
			if (DrawingApplet.MODES[this.currentMode] == Mode.VORONOI_DEF && this.mouseButton == 37) {
				synchronized (this.voronoi) {
					this.voronoi.addPoint(p);
					this.voronoi.computeVoronoi();
					// monitorexit(this.voronoi)
					return;
				}
			}
			if (DrawingApplet.MODES[this.currentMode] == Mode.VORONOI_DEF && this.mouseButton == 39) {
				this.voronoi.removePoint(p);
			} else if (DrawingApplet.MODES[this.currentMode] == Mode.VORONOI_FIND && this.mouseButton == 37) {
				this.voronoi.colorPoint(p);
			}
		}
	}

	@Override
	public void mousePressed() {
		final Point2D.Double p = new Point2D.Double();
		p.x = this.mouseX;
		p.y = this.mouseY;
		if (DrawingApplet.MODES[this.currentMode] == Mode.DRAW_CONVEX) {
			this.indexOfMovingPoint = this.geometry.findPoint(p);
		} else if (DrawingApplet.MODES[this.currentMode] == Mode.UNIT_BALL) {
			this.indexOfMovingPoint = this.geometry.findCenterPoint(p);
		} else if (DrawingApplet.MODES[this.currentMode] == Mode.VORONOI_DEF) {
			this.indexOfMovingPoint = this.voronoi.findPoint(p);
		}
		if (this.indexOfMovingPoint > -1) {
			if (DrawingApplet.MODES[this.currentMode] == Mode.DRAW_CONVEX) {
				this.locked = true;
				this.xOffset = this.mouseX - (float) this.geometry.getPoint(this.indexOfMovingPoint).x;
				this.yOffset = this.mouseY - (float) this.geometry.getPoint(this.indexOfMovingPoint).y;
			} else if (DrawingApplet.MODES[this.currentMode] == Mode.UNIT_BALL) {
				this.locked = true;
				this.xOffset = this.mouseX - (float) this.geometry.getCenterPoint(this.indexOfMovingPoint).x;
				this.yOffset = this.mouseY - (float) this.geometry.getCenterPoint(this.indexOfMovingPoint).y;
			} else if (DrawingApplet.MODES[this.currentMode] == Mode.VORONOI_DEF) {
				this.locked = true;
				synchronized (this.voronoi) {
					this.xOffset = this.mouseX - (float) this.voronoi.getPoint(this.indexOfMovingPoint).x;
					this.yOffset = this.mouseY - (float) this.voronoi.getPoint(this.indexOfMovingPoint).y;
				}
				// monitorexit(this.voronoi)
			}
		}
	}

	@Override
	public void mouseReleased() {
		if (this.locked) {
			this.locked = false;
			this.indexOfMovingPoint = -1;
		}
	}

	@Override
	public void mouseDragged() {
		if (this.locked) {
			final Point2D.Double q = new Point2D.Double(this.mouseX - this.xOffset, this.mouseY - this.yOffset);
			if (DrawingApplet.MODES[this.currentMode] == Mode.DRAW_CONVEX) {
				this.geometry.movePoint(this.indexOfMovingPoint, q);
			} else if (DrawingApplet.MODES[this.currentMode] == Mode.UNIT_BALL) {
				this.geometry.moveCenterPoint(this.indexOfMovingPoint, q);
			} else if (DrawingApplet.MODES[this.currentMode] == Mode.VORONOI_DEF) {
				this.voronoi.movePoint(this.indexOfMovingPoint, q);
			}
		}
	}

	@Override
	public void keyPressed() {
		switch (this.key) {
		case 'p': {
			System.out.println("Pressed p");
			break;
		}
		case 'q': {
			System.out.println("Pressed q");
			break;
		}
		}
	}

	@Override
	public void actionPerformed(final ActionEvent event) {
		if (event.getSource() == this.reinit) {
			this.reinit.setFocusable(false);
			switch (DrawingApplet.MODES[this.currentMode]) {
			case UNIT_BALL: {
				// this.geometry.resetCenterPoints();
				DrawingApplet.radius = 1.0;
				break;
			}
			case VORONOI_DEF: {
				synchronized (this.voronoi) {
					this.voronoi.reset();
					// monitorexit(this.voronoi)
					break;
				}
			}
			case DRAW_CONVEX: {
				this.geometry.reset();
				break;
			}
			}
			
		} else if (event.getSource() == this.plusButton) {
			if (this.indexOfSelectedPoint != -1) {
				this.geometry.updateRadius(this.indexOfSelectedPoint, 0.1);
			}
		} else if (event.getSource() == this.minusButton) {
			if (this.indexOfSelectedPoint != -1) {
				this.geometry.updateRadius(this.indexOfSelectedPoint, -0.1);
			}
		} else if (event.getSource() == this.printPDF) {
			this.record = !this.record;
		}
	}

	@Override
	public void itemStateChanged(final ItemEvent event) {
		if (event.getItemSelectable() == this.toggleRays) {
			if (this.indexOfSelectedPoint > -1) {
				this.geometry.updateToggleMode(this.indexOfSelectedPoint);
			}
		} else if (event.getItemSelectable() == this.drawConvex) {
			this.currentMode = 0;
			this.draw.setEnabled(false);
			this.drawConvex.setFocusable(false);
		} else if (event.getItemSelectable() == this.unitBall) {
			this.currentMode = 1;
			this.draw.setEnabled(true);
			this.unitBall.setFocusable(false);
		} else if (event.getItemSelectable() == this.blueBall) {
			this.currentBallMode = 2;
			this.geometry.updateBallMode(DrawingApplet.BALL_MODES[this.currentBallMode], this.indexOfSelectedPoint);
			this.blueBall.setFocusable(false);
		} else if (event.getItemSelectable() == this.redBall) {
			this.currentBallMode = 1;
			this.geometry.updateBallMode(DrawingApplet.BALL_MODES[this.currentBallMode], this.indexOfSelectedPoint);
			this.redBall.setFocusable(false);
		} else if (event.getItemSelectable() == this.normalBall) {
			this.currentBallMode = 0;
			this.geometry.updateBallMode(DrawingApplet.BALL_MODES[this.currentBallMode], this.indexOfSelectedPoint);
			this.normalBall.setFocusable(false);
		}
	}

	public void initBallModePanel() {
	}

	enum Ball_Mode {
		NORMAL("NORMAL", 0), RED("RED", 1), BLUE("BLUE", 2);

		private Ball_Mode(final String name, final int ordinal) {
		}
	}

	enum Mode {
		DRAW_CONVEX("DRAW_CONVEX", 0), INCONVEXTEST("INCONVEXTEST", 1), UNIT_BALL("UNIT_BALL", 2),
		VORONOI_DEF("VORONOI_DEF", 3), VORONOI_FIND("VORONOI_FIND", 4);

		private Mode(final String name, final int ordinal) {
		}
	}
	
}
