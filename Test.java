import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import cg.*;

public class Test {
    public static void main(String[] args) {
        if(args.length < 6 || args.length % 2 == 1) {
            System.out.println("three points must be passed as arguments");
            System.out.println("command: java Test <x1> <y1> <x2> <y2> ...");
            return;
        }

        ArrayList<Point> sites = new ArrayList<Point>(args.length / 2);

        for(int i = 0; i < args.length; i += 2)
            sites.add(new Point(Integer.parseInt(args[i]), Integer.parseInt(args[i+1])));

        List<Point> hull = GrahamScan.getConvexHull(sites);

        for(int i = 0; i < hull.size(); i++) {
            Point p = hull.get(i);
            System.out.println("(" + p.x + ", " + p.y + ")");
        }
    }
}
