package drawing;

import java.awt.geom.Point2D;

public class DemoUtil {
	public int numberOfIterations;
	public HilbertGeometryDraw geometry;
	private int movingStep;
	private int waitCounter;

	public DemoUtil(final HilbertGeometryDraw geom) {
		this.numberOfIterations = 0;
		this.movingStep = 0;
		this.waitCounter = 0;
		this.geometry = geom;
	}

	public void setSquare(final double X, final double Y, final double length) {
		this.geometry.reset();
		this.geometry.convex.addPoint(new Point2D.Double(X, Y));
		this.geometry.convex.addPoint(new Point2D.Double(X + length, Y));
		this.geometry.convex.addPoint(new Point2D.Double(X, Y + length));
		this.geometry.convex.addPoint(new Point2D.Double(X + length, Y + length));
	}

	public void setTriangle() {
		this.geometry.reset();
		this.geometry.convex.addPoint(new Point2D.Double(583.0, 528.0));
		this.geometry.convex.addPoint(new Point2D.Double(47.0, 526.0));
		this.geometry.convex.addPoint(new Point2D.Double(292.0, 84.0));
	}

	public void setQuadrangle() {
		this.geometry.reset();
		this.geometry.convex.addPoint(new Point2D.Double(442.0, 85.0));
		this.geometry.convex.addPoint(new Point2D.Double(463.0, 475.0));
		this.geometry.convex.addPoint(new Point2D.Double(152.0, 259.0));
		this.geometry.convex.addPoint(new Point2D.Double(68.0, 580.0));
	}

	public void demoIntersectionSquare() {
		if (this.numberOfIterations == 0) {
			this.geometry.addCenterPoint(new Point2D.Double(493.0, 81.0), 1.0);
			this.geometry.addBallMode(DrawingApplet.Ball_Mode.RED);
			this.geometry.addToggleMode(false);
			this.geometry.addCenterPoint(new Point2D.Double(307.0, 182.0), 1.0);
			this.geometry.addBallMode(DrawingApplet.Ball_Mode.BLUE);
			this.geometry.addToggleMode(false);
			this.geometry.addCenterPoint(new Point2D.Double(307.0, 182.0), 2.0);
			this.geometry.addBallMode(DrawingApplet.Ball_Mode.NORMAL);
			this.geometry.addToggleMode(false);
			++this.numberOfIterations;
			this.geometry.draw(true, -1);
		}
		if (this.movingStep == 0) {
			this.demoMoving(493.0, 81.0, 493.0, 278.0, 10);
		} else if (this.movingStep == 1) {
			this.demoMoving(493.0, 278.0, 460.0, 413.0, 10);
		} else if (this.movingStep == 2) {
			this.demoMoving(460.0, 413.0, 146.0, 413.0, 10);
		} else if (this.movingStep == 3) {
			this.demoMoving(146.0, 413.0, 113.0, 288.0, 10);
		} else if (this.movingStep == 4) {
			this.demoMoving(113.0, 288.0, 113.0, 82.0, 10);
		} else if (this.movingStep == 5) {
			this.demoMoving(113.0, 82.0, 235.0, 73.0, 10);
		} else if (this.movingStep == 6) {
			this.demoMoving(235.0, 73.0, 383.0, 73.0, 10);
		} else if (this.movingStep == 7) {
			this.demoMoving(383.0, 73.0, 493.0, 81.0, 10);
		} else {
			this.movingStep = 0;
			this.geometry.draw(true, -1);
		}
	}

	public void demoRadius(final double X, final double Y, final int total, final double step) {
		if (this.numberOfIterations == 0) {
			this.geometry.addCenterPoint(new Point2D.Double(X, Y), 0.2);
			this.geometry.addBallMode(DrawingApplet.Ball_Mode.NORMAL);
			this.geometry.addToggleMode(false);
			++this.numberOfIterations;
		} else if (this.movingStep == 0 && this.numberOfIterations < total) {
			this.geometry.updateRadius(0, step);
			++this.numberOfIterations;
		} else if (this.movingStep == 0 && this.numberOfIterations == total) {
			this.geometry.updateRadius(0, step);
			this.movingStep = 1;
		} else if (this.movingStep == 1 && this.numberOfIterations > 1) {
			this.geometry.updateRadius(0, -step);
			--this.numberOfIterations;
		} else if (this.movingStep == 1 && this.numberOfIterations == 1) {
			this.geometry.updateRadius(0, -step);
			this.movingStep = 0;
		}
		this.geometry.draw(true, 0);
	}

	public void demoInnerTangencyTriangle() {
		if (this.numberOfIterations == 0) {
			this.geometry.addCenterPoint(new Point2D.Double(299.0, 223.0), 1.0);
			this.geometry.addBallMode(DrawingApplet.Ball_Mode.RED);
			this.geometry.addToggleMode(false);
			this.geometry.addCenterPoint(new Point2D.Double(304.0, 329.0), 2.0);
			this.geometry.addBallMode(DrawingApplet.Ball_Mode.BLUE);
			this.geometry.addToggleMode(false);
			this.geometry.addCenterPoint(new Point2D.Double(304.0, 329.0), 1.0);
			this.geometry.addBallMode(DrawingApplet.Ball_Mode.NORMAL);
			this.geometry.addToggleMode(false);
			++this.numberOfIterations;
			this.geometry.draw(true, -1);
		}
		switch (this.movingStep) {
		case 0: {
			this.demoMoving(299.0, 223.0, 245.0, 287.0, 10);
			break;
		}
		case 1: {
			this.demoMoving(245.0, 287.0, 221.0, 393.0, 10);
			break;
		}
		case 2: {
			this.demoMoving(221.0, 393.0, 309.0, 425.0, 10);
			break;
		}
		case 3: {
			this.demoMoving(309.0, 425.0, 394.0, 393.0, 10);
			break;
		}
		case 4: {
			this.demoMoving(394.0, 393.0, 356.0, 287.0, 10);
			break;
		}
		case 5: {
			this.demoMoving(356.0, 287.0, 299.0, 223.0, 10);
			break;
		}
		case 6: {
			this.movingStep = 0;
			this.geometry.draw(true, -1);
			break;
		}
		}
	}

	public void demoNoIntersectionInsideTriangle() {
		if (this.numberOfIterations == 0) {
			this.geometry.addCenterPoint(new Point2D.Double(376.0, 394.0), 1.0);
			this.geometry.addBallMode(DrawingApplet.Ball_Mode.RED);
			this.geometry.addToggleMode(false);
			this.geometry.addCenterPoint(new Point2D.Double(305.0, 330.0), 2.0);
			this.geometry.addBallMode(DrawingApplet.Ball_Mode.BLUE);
			this.geometry.addToggleMode(false);
			this.geometry.addCenterPoint(new Point2D.Double(305.0, 330.0), 1.0);
			this.geometry.addBallMode(DrawingApplet.Ball_Mode.NORMAL);
			this.geometry.addToggleMode(false);
			++this.numberOfIterations;
			this.geometry.draw(true, -1);
		}
		switch (this.movingStep) {
		case 0: {
			this.demoMoving(376.0, 394.0, 247.0, 352.0, 10);
			break;
		}
		case 1: {
			this.demoMoving(247.0, 352.0, 306.0, 273.0, 10);
			break;
		}
		case 2: {
			this.demoMoving(306.0, 273.0, 376.0, 394.0, 10);
			break;
		}
		case 3: {
			this.movingStep = 0;
			this.geometry.draw(true, -1);
			break;
		}
		}
	}

	public void demoNoIntersectionTriangle() {
		if (this.numberOfIterations == 0) {
			this.geometry.addCenterPoint(new Point2D.Double(127.0, 489.0), 0.5);
			this.geometry.addBallMode(DrawingApplet.Ball_Mode.RED);
			this.geometry.addToggleMode(false);
			this.geometry.addCenterPoint(new Point2D.Double(312.0, 328.0), 1.0);
			this.geometry.addBallMode(DrawingApplet.Ball_Mode.BLUE);
			this.geometry.addToggleMode(false);
			this.geometry.addCenterPoint(new Point2D.Double(312.0, 328.0), 1.5);
			this.geometry.addBallMode(DrawingApplet.Ball_Mode.NORMAL);
			this.geometry.addToggleMode(false);
			++this.numberOfIterations;
			this.geometry.draw(true, -1);
		}
		switch (this.movingStep) {
		case 0: {
			this.demoMoving(127.0, 489.0, 329.0, 477.0, 10);
			break;
		}
		case 1: {
			this.demoMoving(329.0, 477.0, 436.0, 439.0, 10);
			break;
		}
		case 2: {
			this.demoMoving(436.0, 439.0, 516.0, 508.0, 10);
			break;
		}
		case 3: {
			this.demoMoving(516.0, 508.0, 127.0, 489.0, 30);
			break;
		}
		case 4: {
			this.movingStep = 0;
			this.geometry.draw(true, -1);
			break;
		}
		}
	}

	public void demoMovingQuadrangle() {
		if (this.movingStep == 0) {
			this.demoMoving(150.0, 300.0, 400.0, 250.0, 50);
		} else if (this.movingStep == 1) {
			this.demoMoving(400.0, 250.0, 256.0, 331.0, 20);
		} else if (this.movingStep == 2) {
			if (this.waitCounter < 10) {
				++this.waitCounter;
			} else {
				this.waitCounter = 0;
				++this.movingStep;
			}
			this.geometry.draw(true, 1);
		} else if (this.movingStep == 3) {
			this.demoMoving(256.0, 331.0, 414.0, 441.0, 30);
		} else if (this.movingStep == 4) {
			this.demoMoving(414.0, 441.0, 150.0, 300.0, 20);
		} else {
			this.movingStep = 0;
			this.geometry.draw(true, 1);
		}
	}

	public void demoMovingSquare() {
		if (this.movingStep == 0) {
			this.demoMoving(298.0, 192.0, 451.0, 451.0, 20);
		} else if (this.movingStep == 1) {
			this.demoMoving(451.0, 451.0, 60.0, 60.0, 50);
		} else if (this.movingStep == 2) {
			this.demoMoving(60.0, 60.0, 60.0, 350.0, 20);
		} else if (this.movingStep == 3) {
			this.demoMoving(60.0, 350.0, 300.0, 300.0, 20);
		} else if (this.movingStep == 4) {
			if (this.waitCounter < 10) {
				++this.waitCounter;
			} else {
				this.waitCounter = 0;
				++this.movingStep;
			}
			this.geometry.draw(true, 0);
		} else if (this.movingStep == 5) {
			this.demoMoving(300.0, 300.0, 298.0, 192.0, 20);
		} else {
			this.movingStep = 0;
			this.geometry.draw(true, 0);
		}
	}

	public void demoMovingTriangle() {
		if (this.movingStep == 0) {
			this.demoMoving(298.0, 192.0, 474.0, 451.0, 20);
		} else if (this.movingStep == 1) {
			this.demoMoving(474.0, 451.0, 307.0, 496.0, 20);
		} else if (this.movingStep == 2) {
			this.demoMoving(307.0, 496.0, 267.0, 291.0, 20);
		} else if (this.movingStep == 3) {
			this.demoMoving(267.0, 291.0, 298.0, 192.0, 20);
		} else {
			this.movingStep = 0;
			this.geometry.draw(true, 0);
		}
	}

	private void demoMoving(final double X0, final double Y0, final double X1, final double Y1, final int N) {
		if (this.numberOfIterations == 0) {
			// this.geometry.resetCenterPoints();
			this.geometry.addCenterPoint(new Point2D.Double(X0, Y0), 1.0);
			this.geometry.addBallMode(DrawingApplet.Ball_Mode.NORMAL);
			this.geometry.addToggleMode(false);
			++this.numberOfIterations;
		}
		if (this.numberOfIterations <= N) {
			this.geometry.moveCenterPoint(0, new Point2D.Double(X0 + this.numberOfIterations * (X1 - X0) / N,
					Y0 + this.numberOfIterations * (Y1 - Y0) / N));
			++this.numberOfIterations;
		}
		if (this.numberOfIterations == N + 1) {
			++this.movingStep;
			this.numberOfIterations = 1;
		}
		this.geometry.draw(true, 0);
	}
}
