// JAVA Code to find Orientation of 3 ordered points
class Point
{
	int x, y;
	Point(int x,int y) {
		this.x = x;
		this.y = y;
	}
}

class GFG {
	
	// To find orientation of ordered tripletp1, p2, p3). 
	// The function returns following values:
	// 0 --> colinear
	// 1 --> Clockwise
	// 2 --> Counterclockwise

	public static int orientation(Point p1, Point p2, Point p3) {
		// See 10th slides from following link
		// for derivation of the formula
		int val = (p2.getY() - p1.getY()) * (p3.getX() - p2.getX()) - 
			(p2.getX() - p1.getX()) * (p3.getY() - p2.getY());
	
		if (val == 0) { // collinear
			return 0; 
		} 
		if (val > 0) { // clockwise
			return 1;
		}
		else {		   // counter clockwise
			return 2;
		}
	}
	
	/* Driver program to test above function */
	public static void main(String[] args) {
			Point p1 = new Point(0, 0);
			Point p2 = new Point(4, 4);
			Point p3 = new Point(1, 2);
			
			int o = orientation(p1, p2, p3);
			
			if (o == 0)	
			System.out.print("Linear");
			else if (o == 1)
			System.out.print("Clockwise");
			else			
			System.out.print("CounterClockwise");

	}
}

