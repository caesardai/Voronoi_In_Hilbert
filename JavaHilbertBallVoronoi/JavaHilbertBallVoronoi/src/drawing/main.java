package drawing;
import drawing.DrawingApplet;

public class main {
  static String PATH;
  
  /**
   * @param args
   */
  public static void main(String[] args) {
    PATH = "convexes/square.in";
    String[] argument = new String[]{PATH, ""};
    System.out.println(argument[0]);
    DrawingApplet.main(argument);
  }
}
