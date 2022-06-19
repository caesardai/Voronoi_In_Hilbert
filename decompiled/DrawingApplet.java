import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.awt.Component;
import java.awt.Button;
import java.awt.event.ActionListener;
import processing.core.PApplet;

public class DrawingApplet extends PApplet implements ActionListener
{
    private static final DrawingApplet.Mode[] MODES;
    private int currentMode;
    private static final int NUMBER_MODES = 3;
    private static final String SWITCH_MODE = "Change to mode: ";
    static String FILENAME;
    private Button newConvex;
    private Button plusButton;
    private Button minusButton;
    private Button toggleMode;
    HilbertGeometry geometry;
    Voronoi voronoi;
    static final double epsilon = 4.0;
    static double radius;
    static final double RADIUS_STEP = 0.1;
    public Point_2 selectedPoint;
    private float xOffset;
    private float yOffset;
    private int indexOfMovingPoint;
    private boolean locked;
    
    static {
        MODES = new DrawingApplet.Mode[] { DrawingApplet.Mode.DRAW_CONVEX, DrawingApplet.Mode.UNIT_BALL, DrawingApplet.Mode.VORONOI_DEF };
        DrawingApplet.radius = 1.0;
    }
    
    public DrawingApplet() {
        this.currentMode = 0;
        this.selectedPoint = null;
        this.xOffset = 0.0f;
        this.yOffset = 0.0f;
        this.indexOfMovingPoint = -1;
        this.locked = false;
    }
    
    public static void main(final String[] args) {
        if (args != null && args.length > 0) {
            DrawingApplet.FILENAME = args[0];
        }
        PApplet.main(new String[] { "DrawingApplet" });
    }
    
    public void setup() {
        this.size(600, 600);
        this.initButton();
        this.geometry = new HilbertGeometry(this, DrawingApplet.FILENAME);
        this.voronoi = new Voronoi(this.geometry, this);
    }
    
    public void initButton() {
        this.add((Component)(this.toggleMode = new Button("Change to mode: " + DrawingApplet.MODES[(this.currentMode + 1) % 3].toString())));
        this.toggleMode.addActionListener(this);
        this.add((Component)(this.newConvex = new Button("New object")));
        this.newConvex.addActionListener(this);
        this.add((Component)(this.plusButton = new Button("+")));
        this.plusButton.addActionListener(this);
        this.add((Component)(this.minusButton = new Button("-")));
        this.minusButton.addActionListener(this);
    }
    
    public void draw() {
        this.background(220);
        this.textFont(this.createFont("Arial", 12.0f, true), 12.0f);
        this.fill(0);
        if (this.geometry.convex.convexHull.length < 3) {
            return;
        }
        if (DrawingApplet.MODES[this.currentMode].toString().contains("VORONOI")) {
            this.voronoi.drawPoints();
            this.geometry.draw(false, 0.0);
        }
        else {
            this.geometry.draw(true, DrawingApplet.radius);
        }
    }
    
    public int findPoint(final int x, final int y, final LinkedList<Point_2> pts) {
        final Point_2 p = new Point_2((double)x, (double)y);
        int index = 0;
        boolean found = false;
        for (final Point_2 q : pts) {
            if (q.squareDistance(p) < 4.0) {
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
    
    public void mouseClicked() {
        final Point_2 p = new Point_2((double)this.mouseX, (double)this.mouseY);
        if (DrawingApplet.MODES[this.currentMode] == DrawingApplet.Mode.DRAW_CONVEX && this.mouseButton == 37 && this.selectedPoint == null) {
            this.geometry.convex.addPoint(p);
            this.voronoi.hasChanged = true;
            System.out.println("Point added to convex: (" + this.mouseX + ", " + this.mouseY + ")");
        }
        else if (DrawingApplet.MODES[this.currentMode] == DrawingApplet.Mode.UNIT_BALL && this.mouseButton == 37 && this.selectedPoint == null) {
            this.geometry.addCenterPoint(p);
        }
        else if (DrawingApplet.MODES[this.currentMode] == DrawingApplet.Mode.UNIT_BALL && this.mouseButton == 39) {
            this.geometry.convex.removePoint(p);
        }
        else if (DrawingApplet.MODES[this.currentMode] == DrawingApplet.Mode.INCONVEXTEST && this.mouseButton == 37 && this.selectedPoint == null) {
            if (this.geometry.isInConvex(p)) {
                System.out.println("Is in convex.");
            }
            else {
                System.out.println("Not in convex.");
            }
        }
        else if (DrawingApplet.MODES[this.currentMode] == DrawingApplet.Mode.VORONOI_DEF && this.mouseButton == 37 && this.selectedPoint == null) {
            this.voronoi.addPoint(p);
            this.voronoi.computeVoronoi();
        }
        else if (DrawingApplet.MODES[this.currentMode] == DrawingApplet.Mode.VORONOI_FIND && this.mouseButton == 37 && this.selectedPoint == null) {
            this.voronoi.colorPoint(p);
        }
    }
    
    public void mousePressed() {
        final Point_2 p = new Point_2();
        p.x = this.mouseX;
        p.y = this.mouseY;
        if (DrawingApplet.MODES[this.currentMode] == DrawingApplet.Mode.DRAW_CONVEX) {
            this.indexOfMovingPoint = this.geometry.findPoint(p);
        }
        else if (DrawingApplet.MODES[this.currentMode] == DrawingApplet.Mode.UNIT_BALL) {
            this.indexOfMovingPoint = this.geometry.findCenterPoint(p);
        }
        if (this.indexOfMovingPoint > -1) {
            if (DrawingApplet.MODES[this.currentMode] == DrawingApplet.Mode.DRAW_CONVEX) {
                this.locked = true;
                this.xOffset = this.mouseX - (float)this.geometry.getPoint(this.indexOfMovingPoint).x;
                this.yOffset = this.mouseY - (float)this.geometry.getPoint(this.indexOfMovingPoint).y;
            }
            else if (DrawingApplet.MODES[this.currentMode] == DrawingApplet.Mode.UNIT_BALL) {
                this.locked = true;
                this.xOffset = this.mouseX - (float)this.geometry.getCenterPoint(this.indexOfMovingPoint).x;
                this.yOffset = this.mouseY - (float)this.geometry.getCenterPoint(this.indexOfMovingPoint).y;
            }
        }
    }
    
    public void mouseReleased() {
        if (this.locked) {
            this.locked = false;
            this.indexOfMovingPoint = -1;
        }
    }
    
    public void mouseDragged() {
        if (this.locked) {
            final Point_2 q = new Point_2((double)(this.mouseX - this.xOffset), (double)(this.mouseY - this.yOffset));
            if (DrawingApplet.MODES[this.currentMode] == DrawingApplet.Mode.DRAW_CONVEX) {
                this.geometry.movePoint(this.indexOfMovingPoint, q);
            }
            else if (DrawingApplet.MODES[this.currentMode] == DrawingApplet.Mode.UNIT_BALL) {
                this.geometry.moveCenterPoint(this.indexOfMovingPoint, q);
            }
        }
    }
    
    public void keyPressed() {
    }
    
    public void actionPerformed(final ActionEvent event) {
        if (event.getSource() == this.newConvex) {
            this.geometry.reset();
            synchronized (this.voronoi) {
                this.voronoi.reset();
            }
            // monitorexit(this.voronoi)
            DrawingApplet.radius = 1.0;
        }
        else if (event.getSource() == this.plusButton) {
            DrawingApplet.radius += 0.1;
            System.out.println("Radius: " + DrawingApplet.radius);
        }
        else if (event.getSource() == this.minusButton) {
            DrawingApplet.radius = Math.max(0.0, DrawingApplet.radius - 0.1);
            System.out.println("Radius: " + DrawingApplet.radius);
        }
        else if (event.getSource() == this.toggleMode) {
            this.currentMode = (this.currentMode + 1) % 3;
            this.toggleMode.setLabel("Change to mode: " + DrawingApplet.MODES[(this.currentMode + 1) % 3].toString());
        }
    }
}