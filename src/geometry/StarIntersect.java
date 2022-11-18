package geometry;

import java.util.*;

public class StarIntersect {
	// This gets the angle between a site and the origin
	public static Double getAngle(Double[] siteinfo) {
		if (0 <= siteinfo[1]) {
			double solution = Math
					.acos(siteinfo[0] / (Math.sqrt(siteinfo[0] * siteinfo[0] + siteinfo[1] * siteinfo[1])));
			return (solution);
		} else {
			double solution = 2 * Math.PI
					- Math.acos(siteinfo[0] / (Math.sqrt(siteinfo[0] * siteinfo[0] + siteinfo[1] * siteinfo[1])));
			return (solution);
		}
	}

	// This is an orientation test between three ordered points
	public static String orient(Double[] a, Double[] b, Double[] c) {
		Double orientation = ((b[1] - a[1]) * (c[0] - b[0])) - ((c[1] - b[1]) * (b[0] - a[0]));
		if (orientation < 0)
			return ("CCW");
		else if (orientation > 0)
			return ("CW");
		else
			return ("COL");
	}

	// This finds the intersection points between the bisector described by
	// arrayPoints1 and the cell arrayPoints2
	public static void IntersectionPoints(Double[] site1, Double[] site2, Double[][] arrayPoints1,
			Double[][] arrayPoints2) {

		// Let's make SiteA the leftmost of site1 and site2
		Double[][] arrayA;
		Double[][] arrayB;
		Double[] siteA = site1;
		Double[] siteB = site2;
		if (site1[0] < site2[0]) {
			arrayA = arrayPoints1;
			arrayB = arrayPoints2;
		} else {
			siteA = site2;
			siteB = site1;
			arrayA = arrayPoints2;
			arrayB = arrayPoints1;
		}

		// Now let's get the angle and shift we need to rotate everything be to get
		// siteA and siteB horizontal with B at the origin
		Double theta = Math.atan((siteB[1] - siteA[1]) / (siteB[0] - siteA[0]));
		theta = -theta;
		Double shiftX = siteB[0];
		Double shiftY = siteB[1];
		siteB[0] = 0.0;
		siteB[1] = 0.0;
		siteA[0] -= shiftX;
		siteA[1] -= shiftY;

		// Here's the rotation matrix for that angle
		Double[] rotationMatrix = { Math.cos(theta), -Math.sin(theta), Math.sin(theta), Math.cos(theta) };

		// Let's rotate our original sites
		Double[] siteAtemp = { siteA[0], siteA[1] };
		Double[] siteBtemp = { siteB[0], siteB[1] };
		siteA[0] = rotationMatrix[0] * siteAtemp[0] + rotationMatrix[1] * siteAtemp[1];
		siteA[1] = rotationMatrix[2] * siteAtemp[0] + rotationMatrix[3] * siteAtemp[1];
		siteB[0] = rotationMatrix[0] * siteBtemp[0] + rotationMatrix[1] * siteBtemp[1];
		siteB[1] = rotationMatrix[2] * siteBtemp[0] + rotationMatrix[3] * siteBtemp[1];

		// Now lets rotate and shift everything in our arrays along with them
		for (int i = 0; i < arrayA.length; i++) {
			arrayA[i][0] -= shiftX;
			arrayA[i][1] -= shiftY;
			Double AXtemp = arrayA[i][0];
			Double AYtemp = arrayA[i][1];
			arrayA[i][0] = rotationMatrix[0] * AXtemp + rotationMatrix[1] * AYtemp;
			arrayA[i][1] = rotationMatrix[2] * AXtemp + rotationMatrix[3] * AYtemp;

		}
		for (int j = 0; j < arrayB.length; j++) {
			arrayB[j][0] -= shiftX;
			arrayB[j][1] -= shiftY;
			Double BXtemp = arrayB[j][0];
			Double BYtemp = arrayB[j][1];
			arrayB[j][0] = rotationMatrix[0] * BXtemp + rotationMatrix[1] * BYtemp;
			arrayB[j][1] = rotationMatrix[2] * BXtemp + rotationMatrix[3] * BYtemp;

		}

		// Next we combine them into one array
		Double[][] U = new Double[arrayA.length + arrayB.length][3];
		for (int k = 0; k < arrayA.length + arrayB.length; k++) {
			if (k < arrayA.length) {
				U[k] = arrayA[k];
			} else {
				U[k] = arrayB[k - arrayA.length];
			}
		}

		// Let's find the crossings in this array!
		findCrossing(U);
	}

	public static String findCrossing(Double[][] U) {

		// Let's sort U by angle!
		Arrays.sort(U, Comparator.comparingDouble(a -> getAngle(a)));

		// for (int u=0;u<U.length;u++ ) {System.out.println(Arrays.toString(U[u]));}

		// Here we initialize everything we're going to need
		String PreviousOrientation = "null";
		Double Red = U[0][2];
		int BlueSegmentStart = 0;
		int BlueSegmentEnd = 0;
		int RedSegmentStart = 0;
		int RedSegmentEnd = 0;
		int RedBeforeBlueSegmentStart = 0;

		// What we do here is we have two variables C1a and C1b. C1a is set to be the
		// first point with color Color1
		// let this be RED, let the other color be BLUE. PreviousC1a is going to store
		// the C1a before the last BLUE.
		// Now we loop through our points between C1a and C1b if there are any. If there
		// are
		// "https://www.youtube.com/watch?v=i1ojUmdF42U"
		for (int i = 1; i < U.length; i++) {
			if (Math.abs(Red - U[i][2]) < .5) { // If Red
				RedSegmentEnd = i;
				for (int j = RedSegmentStart + 1; j < RedSegmentEnd; j++) {
					if (j != RedSegmentEnd) {// So it works like for loop is supposed to
						if (Math.abs(U[j][2] - Red) > .5) // If Blue
							BlueSegmentEnd = j;
						if (PreviousOrientation == "null") {
							PreviousOrientation = orient(U[RedSegmentStart], U[j], U[RedSegmentEnd]);
							RedBeforeBlueSegmentStart = RedSegmentStart;
						} else if (PreviousOrientation == orient(U[RedSegmentStart], U[j], U[RedSegmentEnd])) {
							RedBeforeBlueSegmentStart = RedSegmentStart;
						} else {
//							System.out.print(PreviousOrientation);
//							System.out.print(Arrays.toString(U[RedSegmentStart]));
//							System.out.print(Arrays.toString(U[RedSegmentEnd]));
//							System.out.println(Arrays.toString(U[j]));
//							System.out.print(Arrays.toString(U[RedBeforeBlueSegmentStart]));
//							System.out.print(Arrays.toString(U[BlueSegmentStart]));
//							System.out.println(Arrays.toString(U[BlueSegmentEnd]));

							for (int k = RedBeforeBlueSegmentStart + 1; k < RedSegmentEnd + 1; k++) {
								if (Math.abs(U[k][2] - Red) < .5) {// If Red
									SegmentIntersections(U[RedBeforeBlueSegmentStart], U[k], U[BlueSegmentStart],
											U[BlueSegmentEnd]);
									RedBeforeBlueSegmentStart = k;
									PreviousOrientation = orient(U[RedSegmentStart], U[j], U[RedSegmentEnd]);
								}
							}
						}
						BlueSegmentStart = BlueSegmentEnd;
					}
				}
				RedSegmentStart = RedSegmentEnd;
			} else {
				BlueSegmentEnd = i;
			}
		}
		return "a";
	}

	public static boolean SegmentIntersections(Double[] p1, Double[] q1, Double[] p2, Double[] q2) {

		String o1 = orient(p1, q1, p2);
		String o2 = orient(p1, q1, q2);
		String o3 = orient(p2, q2, p1);
		String o4 = orient(p2, q2, q1);
		if (o1 != o2 && o3 != o4) {
			return true;
		} else {
			return false;
		}

	}
}
