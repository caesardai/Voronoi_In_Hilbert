/*
 * REU-CAAR: Hilbert Geometry
 * Copied from <PLEASE GIVE CREDIT TO THE ORIGINAL SOURCE>
 * Class to compute orientation test
 */

public class GFG {
	// To find orientation of ordered triplet
	// (p1, p2, p3). The function returns
	// following values
	// 0 --> p, q and r are collinear
	// 1 --> Clockwise
	// 2 --> Counterclockwise
	public static int orientation(Point p1, Point p2, Point p3) {
		// See 10th slides from following link
		// for derivation of the formula
		double val = (p2.getY() - p1.getY()) * (p3.getX() - p2.getX()) -
				(p2.getX() - p1.getX()) * (p3.getY() - p2.getY());
	
		if (val == 0) return 0; // collinear
	
		// clock or counterclock wise
		return (val > 0) ? 1: 2;
	}
	
	/* Driver program to test above function */
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
			
			if (o==0)	
			System.out.println("Linear");
			else if (o == 1)
			System.out.println("Clockwise");
			else			
			System.out.println("CounterClockwise");
		
	}
}

