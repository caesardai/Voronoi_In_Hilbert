package drawing;
import processing.core.PApplet;

import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.*;

public class DrawingApplet extends PApplet implements ActionListener {
  /* Modes */
  enum Mode {DRAW_CONVEX, INCONVEXTEST, UNIT_BALL, VORONOI_DEF, VORONOI_FIND};
  private final static Mode[] MODES = {Mode.DRAW_CONVEX, /*Mode.INCONVEXTEST,*/ Mode.UNIT_BALL, Mode.VORONOI_DEF, /*Mode.VORONOI_FIND*/};  
  private int currentMode = 0;
  private final static int NUMBER_MODES = 3;
  private final static String SWITCH_MODE = "Change to mode: ";
  static String FILENAME;
  /* Buttons */ 
  private Button newConvex, plusButton, minusButton, toggleMode;
  
  /* Geometric objects */ 
  public HilbertGeometryDraw geometry;
  public VoronoiDraw voronoi;
  final static double epsilon=4.;
  static double radius = 1;
  final static double RADIUS_STEP = 0.1;
  
  /* Variables for moving points */ 
  private float xOffset = 0.0f;
  private float yOffset = 0.0f;
  private int indexOfMovingPoint = -1;
  private int indexOfSelectedPoint = -1;
  private boolean locked = false;
 
  public static void main(String[] args) {
    if (args != null && args.length > 0) {
      FILENAME = args[0];
    }
    PApplet.main(new String[] {"drawing.DrawingApplet"});
  }
  
    public void setup() {
      size(600, 600);
      initButton();
      this.geometry = new HilbertGeometryDraw(this, FILENAME);
      this.voronoi = new VoronoiDraw(geometry, this);
    }
    
    public void initButton() {
      toggleMode = new Button(SWITCH_MODE + MODES[(currentMode + 1) % NUMBER_MODES].toString());
      add(toggleMode);
      toggleMode.addActionListener(this);
      
      newConvex = new Button("New object");
      add(newConvex);
      newConvex.addActionListener(this);
      
      plusButton = new Button("+");
      add(plusButton);
      plusButton.addActionListener(this);
      
      minusButton = new Button("-");
      add(minusButton);
      minusButton.addActionListener(this);
    }

    public void draw() {
      background(220);
      textFont(createFont("Arial",12,true),12);                 // STEP 4 Specify font to be used
      fill(0);// STEP 5 Specify font color
      if(this.geometry.convex.convexHull.length < 3) return; // no convex Hull to display.
      if (MODES[currentMode].toString().contains("VORONOI")) {
        voronoi.drawPoints();
        geometry.draw(true, this.indexOfSelectedPoint);
      } else {
        geometry.draw(true, this.indexOfSelectedPoint);
      }
      
    }
    
    public int findPoint(int x, int y, LinkedList<Point2D.Double> pts) {
      Point2D.Double p=new Point2D.Double(x, y);
      
      int index=0;
      boolean found=false;
      for(Point2D.Double q : pts) {
        if(q.distanceSq(p) < epsilon) {
          found=true;
          break;
        }
        index++;
      }
      if(found==true)
        return index;
      else
        return -1;
    }

    public void mouseClicked() {
      Point2D.Double p=new Point2D.Double(mouseX, mouseY);
      
      if(MODES[currentMode] == Mode.DRAW_CONVEX && mouseButton==LEFT) {
        this.geometry.convex.addPoint(p);
        this.voronoi.hasChanged = true;
        System.out.println("Point added to convex: ("+mouseX+", "+mouseY+")");
      }
      else if (MODES[currentMode] == Mode.UNIT_BALL && mouseButton == LEFT) {
        this.indexOfSelectedPoint = this.geometry.findCenterPoint(p);
        if (this.indexOfSelectedPoint == - 1) {
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
        this.voronoi.addPoint(p);
        this.voronoi.computeVoronoi();
      } else if (MODES[currentMode] == Mode.VORONOI_DEF && mouseButton == RIGHT) {
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
      if(MODES[currentMode] == Mode.DRAW_CONVEX) {
        indexOfMovingPoint = this.geometry.findPoint(p);
      } else if (MODES[currentMode] == Mode.UNIT_BALL) {
        indexOfMovingPoint = this.geometry.findCenterPoint(p);
      } else if (MODES[currentMode] == Mode.VORONOI_DEF) {
        indexOfMovingPoint = this.voronoi.findPoint(p);
      }
    if (indexOfMovingPoint > -1) {
       if(MODES[currentMode] == Mode.DRAW_CONVEX) {
         locked = true;
         xOffset = mouseX - (float)(double)this.geometry.getPoint(indexOfMovingPoint).x;
         yOffset = mouseY - (float)(double)this.geometry.getPoint(indexOfMovingPoint).y;
       } else if (MODES[currentMode] == Mode.UNIT_BALL) {
         locked = true;
         xOffset = mouseX - (float)(double)this.geometry.getCenterPoint(indexOfMovingPoint).x;
         yOffset = mouseY - (float)(double)this.geometry.getCenterPoint(indexOfMovingPoint).y;
       } else if (MODES[currentMode] == Mode.VORONOI_DEF) {
         locked = true;
         xOffset = mouseX - (float)(double)this.voronoi.getPoint(indexOfMovingPoint).x;
         yOffset = mouseY - (float)(double)this.voronoi.getPoint(indexOfMovingPoint).y;
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
        if(MODES[currentMode] == Mode.DRAW_CONVEX) {
          this.geometry.movePoint(indexOfMovingPoint, q);
        } else if (MODES[currentMode] == Mode.UNIT_BALL) {
          this.geometry.moveCenterPoint(indexOfMovingPoint, q);
        } else if (MODES[currentMode] == Mode.VORONOI_DEF) {
          this.voronoi.movePoint(indexOfMovingPoint, q);
        }
      }
    }
    
    public void keyPressed(){
    }

    @Override
    public void actionPerformed(ActionEvent event) {
      if (event.getSource() == newConvex) {
        this.geometry.reset();
        synchronized(this.voronoi) {
          this.voronoi.reset(); 
        } 
        radius = 1;
      } else if (event.getSource() == plusButton) {
        if (this.indexOfSelectedPoint != - 1) {
          this.geometry.updateRadius(indexOfSelectedPoint, RADIUS_STEP);
        }
      } else if (event.getSource() == minusButton) {
        if (this.indexOfSelectedPoint != -1) {
          this.geometry.updateRadius(indexOfSelectedPoint, -RADIUS_STEP);
        }
      } else if (event.getSource() == toggleMode) {
        currentMode = (currentMode + 1) % NUMBER_MODES;
        toggleMode.setLabel(SWITCH_MODE + MODES[(currentMode + 1) % NUMBER_MODES].toString());
      }
    }
}
