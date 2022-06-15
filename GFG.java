/*
 * REU-CAAR: Hilbert Geometry
 * Class to compute orientation test
 * Copied from <https://www.geeksforgeeks.org/orientation-3-ordered-points/>
 */

public class GFG {
	// To find orientation of ordered triplet (p1, p2, p3). 
	// The function returns following values
	// 0 --> Collinear
	// 1 --> Clockwise
	// 2 --> Counterclockwise
	public static int orientation(Point p1, Point p2, Point p3) {
		double val = (p2.getY() - p1.getY()) * (p3.getX() - p2.getX()) -
				(p2.getX() - p1.getX()) * (p3.getY() - p2.getY());

		// collinear
		if (val == 0) { 
			return 0;
		}

		// clockwise
		if (val > 0) { 
			return 1;
		}
		
		// counter clockwise
		else {		   
			return 2;
		}
	}
	
	// Driver program to test above function
	public static void main(String[] args) {
                        if(args.length < 6) {
                            System.out.println("need 6 arguments");
                            return;
                        }

                        for(int i = 0; i < args.length; i++)
                            System.out.print(args[i] + ", ");
                        System.out.println();

                        int a = Integer.parseInt(args[0]);
                        int b = Integer.parseInt(args[1]);
                        int c = Integer.parseInt(args[2]);
                        int d = Integer.parseInt(args[3]);
                        int e = Integer.parseInt(args[4]);
                        int f = Integer.parseInt(args[5]);

			Point p1 = new Point(a, b);
			Point p2 = new Point(c, d);
			Point p3 = new Point(e, f);
			
			int o = orientation(p1, p2, p3);
			
			if (o == 0)	
			System.out.println("Colinear");
			else if (o == 1)
			System.out.println("Clockwise");
			else			
			System.out.println("CounterClockwise");
		
	}
}

