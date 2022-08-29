package geometry;

import java.awt.geom.Point2D;
// import java.awt.geom.Point2D.Double;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

import trapmap.Segment;
import processing.core.PVector;

/*
 * Class to handle operations on 2D Convex shapes.
 */
public class Convex {
	// User defined points
	public Point2D.Double[] points;
	// Actual points defining the convex hull.
	public Point2D.Double[] convexHull;
	// Spoke segments
	public List<Segment> spokeSegments;

	// Default constructor
	public Convex() {
		points = new Point2D.Double[0];
		convexHull = new Point2D.Double[0];
		spokeSegments = new ArrayList<Segment>();
	}

	// Constructs convex from file.
	public Convex(String filename) {
		points = load(filename);
		this.computeConvexHull();
	}

	/**
	 * Inserting a site and find the segments around it
	 * 
	 * @param hullVertex get information
	 * @param site
	 * @return
	 */
	public Map<Point2D.Double, List<Segment>> siteSegment(Point2D.Double[] hullVertex, Point2D.Double site) {
		int numHullVertex = hullVertex.length;
		// Segments with site as end point
		Map<Point2D.Double, List<Segment>> siteSegments = new HashMap<Point2D.Double, List<Segment>>();

		// Find equations for hull edges
		Point3d[] hullEdges = new Point3d[numHullVertex]; // stores hull edges as Point3d object
		for (int h = 0; h < numHullVertex; h++) {
			Point3d v_1 = HilbertGeometry.toHomogeneous(hullVertex[h]);
			Point3d v_2 = HilbertGeometry.toHomogeneous(hullVertex[(h + 1) % numHullVertex]);
			hullEdges[h] = v_1.crossProduct(v_2);
		}
		// number of hull edges
		int numHullEdges = hullEdges.length;

		// Find spoke segments
		Point3d sproj = HilbertGeometry.toHomogeneous(site);
		for (int h = 0; h < numHullVertex; h++) {
			Point3d hproj = HilbertGeometry.toHomogeneous(hullVertex[h]);
			Point3d spoke = sproj.crossProduct(hproj);

			// Construct segments from current hull vertex to site
			// Convert 2d points to PVector object
			Segment spokeHullVertexSegments = constructSegment(site, hullVertex[h]); // site to vertex
			spokeSegments.add(spokeHullVertexSegments);

			for (int hVertex = 0; hVertex < numHullEdges; hVertex++) {
				// Construct segments from this hull intersect to site
				Point3d spokeHullEdgeIntersect = spoke.crossProduct(hullEdges[hVertex]);
				Point2D.Double spokeHullEdgeIntersectPoint = HilbertGeometry.toCartesian(spokeHullEdgeIntersect);

				Segment spokeHullEdgeSegments = constructSegment(site, spokeHullEdgeIntersectPoint); // site to edge

				// check if intersection point is one of the convex body vertices
				if (spokeSegments.contains(spokeHullEdgeSegments))
					continue;

				// check if intersection point is in the closure of the convex body
				else if (this.isOnConvexBoundary(spokeHullEdgeIntersectPoint)) {
					spokeSegments.add(spokeHullEdgeSegments);
				}
			}
		}

		// Constructing hashmap with site as the key and list of site-containing
		// segments as value
		siteSegments.put(site, spokeSegments);
		return siteSegments;
	}

	/**
	 * Find spoke intersections when more than one spoke is inserted
	 * 
	 * @param hullVertex
	 * @param newSite
	 * @param segments
	 * @return
	 */
	public List<Segment> spokeIntersects(Point2D.Double[] hullVertex, Point2D.Double[] oldSites,
			Point2D.Double newSite) {
		// list to store segments from intersection
		List<Segment> segs = new ArrayList<Segment>();

		// construct spokes from old sites
		Point3d[][] oldSpokes = new Point3d[oldSites.length][hullVertex.length];
		for (int siteCount = 0; siteCount < oldSites.length; siteCount++) {
			for (int vertexCount = 0; vertexCount < hullVertex.length; vertexCount++) {
				Point3d sproj = HilbertGeometry.toHomogeneous(oldSites[siteCount]);
				Point3d vproj = HilbertGeometry.toHomogeneous(hullVertex[vertexCount]);
				oldSpokes[siteCount][vertexCount] = sproj.crossProduct(vproj);
			}
		}

		// construct spokes from new site
		Point3d[] newSpokes = new Point3d[hullVertex.length];
		Point3d sproj = HilbertGeometry.toHomogeneous(newSite);
		for (int vertexCount = 0; vertexCount < hullVertex.length; vertexCount++) {
			Point3d vproj = HilbertGeometry.toHomogeneous(hullVertex[vertexCount]);
			newSpokes[vertexCount] = sproj.crossProduct(vproj);
		}

		// find the intersection point between every newSpoke and oldSpoke
		// for each newSpoke
		ArrayList<Point2D.Double> intersectionPoints = new ArrayList<Point2D.Double>();
		List<Point2D.Double> vertices = Arrays.asList(hullVertex);
		for (int i = 0; i < hullVertex.length; i++) {
			Point3d spoke = newSpokes[i];

			// find the intersection point of the spoke with every old spoke
			intersectionPoints.clear();
			for (int row = 0; row < oldSpokes.length; row++) {
				for (int col = 0; col < oldSpokes[0].length; col++) {
					Point3d s = oldSpokes[row][col];
					Point2D.Double intersect = HilbertGeometry.toCartesian(spoke.crossProduct(s));

					if (this.isInConvex(intersect) && !Convex.almostContainsElement(vertices, intersect))
						intersectionPoints.add(intersect);
				}
			}

			// add site to points to sort and the intersection point between spoke and
			// convex hull
			intersectionPoints.add(newSite);
			intersectionPoints.add(hullVertex[i]);
			Segment edgeIntersect = null;
			for (int vertexCount = 0; vertexCount < hullVertex.length; vertexCount++) {
				Point3d e1proj = HilbertGeometry.toHomogeneous(hullVertex[vertexCount]);
				Point3d e2proj = HilbertGeometry.toHomogeneous(hullVertex[(vertexCount + 1) % hullVertex.length]);
				Point3d edge = e1proj.crossProduct(e2proj);
				Point2D.Double p = HilbertGeometry.toCartesian(spoke.crossProduct(edge));

				if (this.isOnConvexBoundary(p) && !Convex.almostContainsElement(vertices, p)) {
					intersectionPoints.add(p);
					edgeIntersect = this.constructSegment(hullVertex[vertexCount],
							hullVertex[(vertexCount + 1) % hullVertex.length]);
					break;
				}
			}

			// sort intersection points
			if (Math.abs(spoke.y) > 1e-8) {
				ArrayList<Double> compare = new ArrayList<Double>();
				for (Point2D.Double p : intersectionPoints)
					compare.add(p.x);
				Convex.quickSort(intersectionPoints, compare, 0, intersectionPoints.size() - 1);
			} else {
				ArrayList<Double> compare = new ArrayList<Double>();
				for (Point2D.Double p : intersectionPoints)
					compare.add(p.y);
				Convex.quickSort(intersectionPoints, compare, 0, intersectionPoints.size() - 1);
			}

			// construct segments to return
			for (int index = 0; index < intersectionPoints.size() - 1; index++) {
				Segment newSeg = this.constructSegment(intersectionPoints.get(index),
						intersectionPoints.get(index + 1));
				newSeg.setEdge(edgeIntersect);
				newSeg.setSites(newSite);
				segs.add(newSeg);
			}
		}
		return segs;
	}

	/*
	 * Construct sectors with given sites and segments Each sector is associated
	 * with an edge and site
	 */
	public List<Sector> constructSector(Point2D.Double site1, Point2D.Double site2, KdTree<KdTree.XYZPoint> graph) {
		// returning list of sectors
		List<Sector> sectors = new ArrayList<Sector>();

		/*
		 * SITE 1
		 */
		// get all site's neighboring segment
		KdTree.XYZPoint s1XYZPoint = Util.toXYZPoint(site1);
		// get site1 node
		KdTree.KdNode s1Node = KdTree.getNode(graph, s1XYZPoint);
		// get site1 ID
		KdTree.XYZPoint s1ID = s1Node.getID();
		// get all neighbor points for site 1
		ArrayList<EdgeData> s1Endpoints = s1ID.getNeighbors();

		/*
		 * SITE 2
		 */
		// get all site's neighboring segment
		KdTree.XYZPoint s2XYZPoint = Util.toXYZPoint(site2);
		// get site1 node
		KdTree.KdNode s2Node = KdTree.getNode(graph, s2XYZPoint);
		// get site2 ID
		KdTree.XYZPoint s2ID = s2Node.getID();
		// get all neighbor points for site 1
		ArrayList<EdgeData> s2Endpoints = s2ID.getNeighbors();

		/*
		 * For convenient to traverse through the segments sort the end points base on
		 * angles regarding to the site(origin)
		 */
		ArrayList<Double> s1Angles = new ArrayList<Double>();
		// Calculate site 1 angles
		for (int i = 0; i < s1Endpoints.size(); i++) {
			s1Angles.add(Voronoi.spokeAngle(site1, s1Endpoints.get(i).otherNode));

		}
		// Sort through site 1 angles
		Convex.quickSort(s1Endpoints, s1Angles, 0, s1Angles.size() - 1);

		ArrayList<Double> s2Angles = new ArrayList<Double>();
		// Calculate site 2 angles
		for (int i = 0; i < s2Endpoints.size(); i++) {
			s2Angles.add(Voronoi.spokeAngle(site2, s2Endpoints.get(i).otherNode));
		}

		// Sort through site 2 angles
		Convex.quickSort(s2Endpoints, s2Angles, 0, s2Angles.size() - 1);

		// Loop through all the neighbors and construct nearby sectors
		int neighborSize = s1Endpoints.size();
		for (int i = 0; i < neighborSize; i++) {
			Point2D.Double p1 = s1Endpoints.get(i).otherNode;
			KdTree.KdNode nodeP1 = KdTree.getNode(graph, Util.toXYZPoint(p1));
			KdTree.XYZPoint p1XYZ = nodeP1.getID();
			Point2D.Double p2 = s1Endpoints.get((i + 1) % neighborSize).otherNode;
			KdTree.KdNode nodeP2 = KdTree.getNode(graph, Util.toXYZPoint(p2));
			KdTree.XYZPoint p2XYZ = nodeP2.getID();

			// 3 edge case
			if (p1XYZ.containsNeighbor(p2)) {
				ArrayList<Point2D.Double> vertices = new ArrayList<Point2D.Double>();
				vertices.add(site1);
				vertices.add(p1);
				vertices.add(p2);

				ArrayList<Point2D.Double> sites = new ArrayList<Point2D.Double>();
				sites.add(s1ID.getNeighbor(s1ID.indexOf(p1)).site);
				sites.add(p1XYZ.getNeighbor(p1XYZ.indexOf(p2)).site);
				sites.add(p2XYZ.getNeighbor(p2XYZ.indexOf(site1)).site);

				Segment[] edges = this.determineEdges(site1, site2, s1Angles, i);

//				// compute the angular coordinate of points site1, p1, and p2 with respect to the polar coordinate system centered at site2
//				ArrayList<Double> verticesToSite2Angles = new ArrayList<Double>();
//				verticesToSite2Angles.add(Voronoi.spokeAngle(site2, site1));
//				verticesToSite2Angles.add(Voronoi.spokeAngle(site2, p1));
//				verticesToSite2Angles.add(Voronoi.spokeAngle(site2, p2));
//				
//				// find the smallest and largest angular coordinate from the arraylist above
//				Double smallestAngle = Collections.min(verticesToSite2Angles);
//				Double largestAngle = Collections.max(verticesToSite2Angles);
//				
//				int indexEdge1 = Convex.getMax(s2Angles, smallestAngle);
//				int indexEdge2 = Convex.getMin(s2Angles, largestAngle);
//
//				Segment edge1 = s2ID.getEdge(indexEdge2);
//				Segment edge2 = s1ID.getEdge((i + 1) % neighborSize);
//				Segment edge3 = s2ID.getEdge(indexEdge1);
//				Segment edge4 = s1ID.getEdge(i);

				Sector sector = new Sector(site1, site2, edges[1], edges[2], edges[3], edges[4], vertices, sites);
				sectors.add(sector);
			}

			// 4 edge case
			else {
				// we need to determine the four point of the sector
				Point2D.Double p3 = null;
				ArrayList<EdgeData> p1Points = p1XYZ.getNeighbors();
				ArrayList<EdgeData> p2Points = p2XYZ.getNeighbors();

				for (EdgeData e : p1Points) {
					if (!e.otherNode.equals(site1) && p2XYZ.containsNeighbor(e.otherNode)) {
						p3 = e.otherNode;
						break;
					}
				}

				ArrayList<Point2D.Double> vertices = new ArrayList<Point2D.Double>();
				vertices.add(site1);
				vertices.add(p1);
				vertices.add(p2);
				vertices.add(p3);

				ArrayList<Point2D.Double> sites = new ArrayList<Point2D.Double>();
				sites.add(s1ID.getNeighbor(s1ID.indexOf(p1)).site);
				sites.add(p1XYZ.getNeighbor(p1XYZ.indexOf(p3)).site);
				sites.add(p2XYZ.getNeighbor(p2XYZ.indexOf(p3)).site);
				sites.add(s1ID.getNeighbor(s1ID.indexOf(p2)).site);

				Segment[] edges = this.determineEdges(site1, site2, s1Angles, i);

				Sector sector = new Sector(site1, site2, edges[1], edges[2], edges[3], edges[4], vertices, sites);
				sectors.add(sector);
			}
		}

		// if endPointt[i] has another endPoint where it is not the site

		// loop through the segment endpoints i, i+1 and check whether there is an
		// existing line segment
		return sectors;
	}
	
	public Segment[] determineEdges(Point2D.Double site1, Point2D.Double site2, ArrayList<Double> s1Angles, int i) {
		// Segments to return
		Segment[] edges = new Segment[4]; 
		
		// constants
		int neighborSize = s1Angles.size();
		
		Double angle1 = s1Angles.get(i);
		Double angle2 = s1Angles.get((i + 1) % neighborSize);
		Double midAngle = Math.abs(angle2 - angle1) / 2 + angle1;

		Point2D.Double midPoint = Voronoi.rotatePoint(site1, midAngle);
		Point2D.Double[] p = Util.intersectionPoints(site1, midPoint, this);

		ArrayList<Point2D.Double> directionLinePoints = (ArrayList<Point2D.Double>) Arrays.asList(p);
		directionLinePoints.add(site1);
		directionLinePoints.add(midPoint);
		sortColinearPoints(directionLinePoints);

		// determine the edge convex hull intersection points are located
		Point2D.Double[] intersectionPoints = new Point2D.Double[] {directionLinePoints.get(0), directionLinePoints.get(3)};
		Segment[] directionSegs = new Segment[2];
		for (int index = 0; index < intersectionPoints.length; index++) {
			for(int edgeCount = 0; edgeCount < this.convexHull.length - 1; edgeCount++) {
				// get line equation for current edge
				Point2D.Double v1 = this.convexHull[edgeCount];
				Point2D.Double v2 = this.convexHull[edgeCount + 1];
				Point3d hv1 = HilbertGeometry.toHomogeneous(v1);
				Point3d hv2 = HilbertGeometry.toHomogeneous(v2);
				Point3d line = hv1.crossProduct(hv2);
				
				if (Math.abs(line.scalarProduct(HilbertGeometry.toHomogeneous(intersectionPoints[i]))) < 1e-14) {
					directionSegs[index] = new Segment(Util.toPVector(v1), Util.toPVector(v2));
					break;
				}
			}
		}

		// Forward
		if (directionLinePoints.get(2).equals(site1)) {
			edges[2] = directionSegs[1];
			edges[4] = directionSegs[0];
		}

		// Backward
		else {
			edges[2] = directionSegs[0];
			edges[4] = directionSegs[1];
		}
		
		// compute convex hull intersection point from line above
		intersectionPoints = Util.intersectionPoints(site1, site2, this);
		
		directionLinePoints.clear();
		directionLinePoints = (ArrayList<Point2D.Double>) Arrays.asList(intersectionPoints);
		directionLinePoints.add(site1);
		directionLinePoints.add(site2);
		sortColinearPoints(directionLinePoints);

		// determine the edge convex hull intersection points are located
		intersectionPoints = new Point2D.Double[] {directionLinePoints.get(0), directionLinePoints.get(3)};
		directionSegs = new Segment[2];
		for (int index = 0; index < intersectionPoints.length; index++) {
			for(int edgeCount = 0; edgeCount < this.convexHull.length - 1; edgeCount++) {
				// get line equation for current edge
				Point2D.Double v1 = this.convexHull[edgeCount];
				Point2D.Double v2 = this.convexHull[edgeCount + 1];
				Point3d hv1 = HilbertGeometry.toHomogeneous(v1);
				Point3d hv2 = HilbertGeometry.toHomogeneous(v2);
				Point3d line = hv1.crossProduct(hv2);
				
				if (Math.abs(line.scalarProduct(HilbertGeometry.toHomogeneous(intersectionPoints[i]))) < 1e-14) {
					directionSegs[index] = new Segment(Util.toPVector(v1), Util.toPVector(v2));
					break;
				}
			}
		}

		// Forward
		if (directionLinePoints.get(2).equals(site2)) {
			edges[1] = directionSegs[0];
			edges[3] = directionSegs[1];
		}

		// Backward
		else {
			edges[1] = directionSegs[1];
			edges[3] = directionSegs[0];
		}
		
		return edges;
	}

	/*
	 * Check whether a given segment lies on one of the spokes
	 */
	public void segmentVerify(Segment seg, Segment spoke) {
		// Construct spoke with site and convex Hull vertices
		PVector seg1 = seg.getLeftPoint();
		PVector seg2 = seg.getRightPoint();
		double seg1x = seg1.x;
		double seg1y = seg1.y;
		double seg2x = seg2.x;
		double seg2y = seg2.y;
		PVector spoke1 = spoke.getLeftPoint();
		PVector spoke2 = spoke.getRightPoint();
		double spoke1x = spoke1.x;
		double spoke1y = spoke1.y;
		double spoke2x = spoke1.x;
		double spoke2y = spoke2.y;

		// Line equation formula:
		// a = y1 - y2
		// b = x2 - x1
		// c = (x1 - x2) * y1 + (y2 - y1) * x1

		// double seg_a = seg1y - seg2y;
		double spoke_a = spoke1y - spoke2y;
		// double seg_b = seg2x - seg1x;
		double spoke_b = spoke2x - spoke1x;
		// double seg_c = (seg1x - seg2x) * seg1y + (seg2y - seg1y) * seg1x;
		double spoke_c = (spoke1x - spoke2x) * spoke1y + (spoke2y - spoke1y) * spoke1x;

		if (spoke_a * seg1x + spoke_b * seg1y + spoke_c == 0 && spoke_a * seg2x + spoke_b * seg2y + spoke_c == 0) {
			System.out.println("Segment is on the spoke");
		} else {
			System.out.println("Segment is not on the spoke");
		}
	}

//				Point3d s1lh = HilbertGeometry.toHomogeneous(seg1LeftPoint);
//				Point3d s1rh = HilbertGeometry.toHomogeneous(seg1RightPoint);
//				Point3d s2lh = HilbertGeometry.toHomogeneous(seg2LeftPoint);
//				Point3d s2rh = HilbertGeometry.toHomogeneous(seg2RightPoint);
//				Point3d line1 = s1lh.crossProduct(s1rh);
//				Point3d line2 = s2lh.crossProduct(s2rh);
//				
//				double denom = line1.scalarProduct(line2);
//			    if (denom == 0) return new Point3d();
//			    double t = - line1.scalarProduct(q) / denom;
//			    if (t >= 0 && t <= 1) {
//			      double[] scalarCoeff = {t, 1-t};
//			      Point3d[] pointCoeff = {p, q};
//			      return Point3d.linearCombination(pointCoeff, scalarCoeff);
//			    }
//			    // return new Point3d();

	/*
	 * Converting 2d points to PVectors
	 */
	public Segment constructSegment(Point2D.Double p1, Point2D.Double p2) {
		double p1x = p1.x;
		double p1y = p1.y;
		double p2x = p2.x;
		double p2y = p2.y;
		PVector v1 = new PVector();
		PVector v2 = new PVector();
		v1.set((float) p1x, (float) p1y);
		v2.set((float) p2x, (float) p2y);
		Segment segment = new Segment(v1, v2);
		return segment;
	}

	/*
	 * Check 3 points orientation
	 */
	public int threePointsOrientation(Point2D.Double p1, Point2D.Double p2, Point2D.Double p3) {
		int orientation;
		int val = (int) ((p2.y - p1.y) * (p3.x - p2.x) - (p2.x - p1.x) * (p3.y - p2.y));

		if (val == 0) {
			orientation = 0; // Collinear
		} else if (val == 1) {
			orientation = 1; // Clockwise
		} else {
			orientation = 2; // Counter Clockwise
		}
		return orientation;
	}

	/**
	 * Find where the spokes intersects the convex hull
	 * 
	 * @param hullVertex
	 * @param sitePoints
	 * @return
	 */
	public List<Segment> spokeHullIntersection(Point2D.Double[] hullVertex, Point2D.Double[] sitePoints) {
		// list of hold all intersection points
		List<Segment> segs = new ArrayList<Segment>();

		// constants
		int numHullVertex = hullVertex.length;

		// compute convex hull edge equations
		Point3d[] edge_lines = new Point3d[numHullVertex];
		for (int hv = 0; hv < numHullVertex; hv++) {
			Point3d e1proj = HilbertGeometry.toHomogeneous(hullVertex[hv]);
			Point3d e2proj = HilbertGeometry.toHomogeneous(hullVertex[(hv + 1) % numHullVertex]);
			edge_lines[hv] = e1proj.crossProduct(e2proj);
		}

		// compute spoke equations
		Point3d[] spokes = new Point3d[sitePoints.length * hullVertex.length];
		for (int siteCount = 0; siteCount < sitePoints.length; siteCount++) {
			Point2D.Double site = sitePoints[siteCount];
			Point3d sproj = HilbertGeometry.toHomogeneous(site);
			for (int vertexCount = 0; vertexCount < hullVertex.length; vertexCount++) {
				Point2D.Double vertex = hullVertex[vertexCount];
				Point3d vproj = HilbertGeometry.toHomogeneous(vertex);
				spokes[siteCount * numHullVertex + vertexCount] = sproj.crossProduct(vproj);
			}
		}

		// for each edge, compute the intersection points of all the spoikes
		ArrayList<Point2D.Double> intersectionPoints = new ArrayList<Point2D.Double>();
		List<Point2D.Double> vertices = Arrays.asList(hullVertex);
		for (int edgeCount = 0; edgeCount < edge_lines.length; edgeCount++) {
			Point3d edge = edge_lines[edgeCount];
			intersectionPoints.clear();
			for (Point3d s : spokes) {
				// compute intersection point between spoke and edge
				Point2D.Double intersect = HilbertGeometry.toCartesian(edge.crossProduct(s));

				// check if that intersection point is a convex hull point
				if (this.isOnConvexBoundary(intersect) && !Convex.almostContainsElement(vertices, intersect))
					intersectionPoints.add(intersect);
			}

			// add edge vertex points
			intersectionPoints.add(hullVertex[edgeCount]);
			intersectionPoints.add(hullVertex[(edgeCount + 1) % numHullVertex]);

			// sort points
			if (Math.abs(edge.y) > 1e-8) {
				ArrayList<Double> compare = new ArrayList<Double>();
				for (Point2D.Double p : intersectionPoints) {
					compare.add(p.x);
				}
				Convex.quickSort(intersectionPoints, compare, 0, intersectionPoints.size() - 1);
			} else {
				ArrayList<Double> compare = new ArrayList<Double>();
				for (Point2D.Double p : intersectionPoints)
					compare.add(p.x);
				Convex.quickSort(intersectionPoints, compare, 0, intersectionPoints.size() - 1);
			}

			// add segments
			for (int index = 0; index < intersectionPoints.size() - 1; index++)
				segs.add(this.constructSegment(intersectionPoints.get(index), intersectionPoints.get(index + 1)));
		}

		return segs;
	}

	/*
	 * Constructs convex from a list of control points.
	 */
	public Convex(LinkedList<Point2D.Double> controlPoints) {
		controlPoints = this.deleteDoubles(controlPoints);
		this.points = new Point2D.Double[controlPoints.size()];
		int i = 0;
		for (Point2D.Double p : controlPoints) {
			this.points[i] = new Point2D.Double(p.x, p.y);
			i++;
		}
		this.convexHull = new Point2D.Double[0];
		this.computeConvexHull();
	}

	private LinkedList<Point2D.Double> deleteDoubles(LinkedList<Point2D.Double> controlPoints) {
		LinkedList<Point2D.Double> newControlPoints = new LinkedList<Point2D.Double>();
		for (Point2D.Double p : controlPoints) {
			boolean isAlreadyInside = false;
			for (Point2D.Double q : newControlPoints) {
				if (Util.samePoints(p, q)) {
					isAlreadyInside = true;
				}
				if (isAlreadyInside) {
					break;
				}
			}
			if (!isAlreadyInside) {
				newControlPoints.add(p);
			}
		}
		return newControlPoints;
	}

	/*
	 * Finds point P in convexHull and gives the index.
	 */
	public int findPoint(Point2D.Double p) {
		for (int i = 0; i < this.convexHull.length; i++) {
			Point2D.Double testPoint = this.convexHull[i];
			if (Util.closePoints(testPoint, p)) {
				return i;
			}
		}
		return -1;
	}

	/*
	 * Finds point of index i in convex hull.
	 */
	public Point2D.Double getPoint(int index) {
		if (index >= 0 && index < this.convexHull.length) {
			return this.convexHull[index];
		}
		return null;
	}

	/*
	 * Changes the point of index i.
	 */
	public void movePoint(int index, Point2D.Double q) {
		this.convexHull[index] = q;
		this.points = this.convexHull;
		this.computeConvexHull();
	}

	/*
	 * Adds a control points to change the definition of the convex.
	 */
	public void addPoint(Point2D.Double p) {
		Point2D.Double[] oldControlPoints = this.points.clone();
		this.points = new Point2D.Double[oldControlPoints.length + 1];
		for (int i = 0; i < oldControlPoints.length; i++) {
			this.points[i] = oldControlPoints[i];
		}
		this.points[oldControlPoints.length] = p;
		this.computeConvexHull();
	}

	/*
	 * Removes a control point to change the definition of the convex.
	 */
	public void removePoint(Point2D.Double p) {
		int index = -1;
		for (int i = 0; i < this.convexHull.length; i++) {
			if (Util.closePoints(p, convexHull[i])) {
				index = i;
				break;
			}
		}
		if (index != -1) {
			this.points = new Point2D.Double[convexHull.length - 1];
			int count = 0;
			for (int j = 0; j < this.convexHull.length; j++) {
				if (j != index) {
					this.points[count] = this.convexHull[j];
					count++;
				}
			}
			this.computeConvexHull();
		}
	}

	/*
	 * Decides whether point p lies in the interior convex.
	 */
	public boolean isInConvex(Point2D.Double p) {
		int N = convexHull.length;
		Point2D.Double a, b;
		int out = 0, in = 0;
		for (int i = 0; i < N; i++) {
			a = convexHull[i % N];
			b = convexHull[(i + 1) % N];
			Point2D.Double ab = new Point2D.Double(b.x - a.x, b.y - a.y);
			Point2D.Double ap = new Point2D.Double(p.x - a.x, p.y - a.y);
			double crossProduct = ab.x * ap.y - ab.y * ap.x;
			if (crossProduct > 0) {
				out++;
			} else if (crossProduct < 0) {
				in++;
			}
			if (out > 0 && in > 0)
				return false;
		}
		return true;
	}

	/**
	 * Determines if the query point lays on the boundary of the convex body
	 * 
	 * @param p query point
	 * @return true if the query point is on the convex body. otherwise, return
	 *         false
	 */
	public boolean isOnConvexBoundary(Point2D.Double p) {
		if (p == null)
			return false;

		int N = convexHull.length;
		Point2D.Double a, b;
		boolean onBoundary = false;

		for (int i = 0; i < N - 1; i++) {
			a = convexHull[i];
			b = convexHull[i + 1];
			Point2D.Double ab = new Point2D.Double(b.x - a.x, b.y - a.y);
			Point2D.Double ap = new Point2D.Double(p.x - a.x, p.y - a.y);
			double crossProduct = ab.x * ap.y - ab.y * ap.x;
			if (Math.abs(crossProduct) <= 1e-8) {
				// check if the point is within the correct bounds
				double smallX = a.x;
				double largeX = b.x;
				double smallY = a.y;
				double largeY = b.y;

				// swap values if not correct
				if (smallX > largeX) {
					double tmp = smallX;
					smallX = largeX;
					largeX = tmp;
				}
				if (smallY > largeY) {
					double tmp = smallY;
					smallY = largeY;
					largeY = tmp;
				}

				if ((p.x >= smallX && p.x <= largeX) && (p.y >= smallY && p.y <= largeY))
					onBoundary = true;
			}
		}
		return onBoundary;
	}

	/*
	 * Computes the convex hull of the set of control points defined by the user.
	 */
	private void computeConvexHull() {
		if (this.points.length < 3)
			return;
		List<Point2D.Double> initialPoints = new LinkedList<Point2D.Double>();
		for (Point2D.Double p : this.points) {
			initialPoints.add(p);
		}
		List<Point2D.Double> convexList = null;
		try {
			convexList = GrahamScan.getConvexHull(initialPoints);
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {

		}
		if (convexList == null) {
			Set<Point2D.Double> sortedPoints = GrahamScan.getSortedPointSet(initialPoints);
			convexList = new LinkedList<Point2D.Double>();
			for (Point2D.Double p : sortedPoints) {
				convexList.add(p);
			}
		}
		this.convexHull = new Point2D.Double[convexList.size()];
		for (int i = 0; i < convexList.size(); i++) {
			convexHull[i] = convexList.get(i);
		}
	}

	/**
	 * Determines if there exists a point in the list that is approximately the same
	 * as the query point
	 * 
	 * @param list list of points
	 * @param p    query point
	 * @return returns a boolean that corresponds to if there is an approximately
	 *         equal query point in the list
	 */
	private static boolean almostContainsElement(List<Point2D.Double> list, Point2D.Double p) {
		for (Point2D.Double q : list) {
			if (p.distance(q) <= 1e-12)
				return true;
		}
		return false;
	}

	/**
	 * Given a set of points that are all colinear with each other, sort the points
	 * from left to right on this line
	 * 
	 * @param points the set of colinear points
	 */
	private static void sortColinearPoints(ArrayList<Point2D.Double> points) {
		// only sort if there are more than 1 point in our list
		if (points.size() < 2)
			return;

		// determine if the set of points are colinear with a vertical line
		ArrayList<Double> compare = new ArrayList<Double>();
		if (Math.abs(points.get(0).y - points.get(1).y) <= 1e-8) {
			for (Point2D.Double p : points)
				compare.add(p.y);
		}
		// otherwise, sort points by the x-coordinate
		// we are ignoring any float-point errors that may occur here
		else {
			for (Point2D.Double p : points)
				compare.add(p.x);
		}
		Convex.quickSort(points, compare, 0, points.size() - 1);
	}

//	private static int partition(ArrayList<Point2D.Double> array, ArrayList<Double> compare, int begin, int end) {
//		int pivot = end;
//
//		int counter = begin;
//		for (int i = begin; i < end; i++) {
//			// compare either x or y
//			double compare1 = compare.get(i);
//			double compare2 = compare.get(pivot);
//
//			if (compare1 < compare2) {
//				// temp for both arrays
//				Point2D.Double temp = array.get(counter);
//				Double temp1 = compare.get(counter);
//
//				// swap in array
//				array.set(counter, array.get(i));
//				array.set(i, temp);
//
//				// swap in compare
//				compare.set(counter, compare.get(i));
//				compare.set(i, temp1);
//
//				counter++;
//			}
//		}
//		// temp for both arrays
//		Point2D.Double temp = array.get(pivot);
//		Double temp1 = compare.get(counter);
//
//		// swap in array
//		array.set(pivot, array.get(counter));
//		array.set(counter, temp);
//
//		// swap in compare
//		compare.set(pivot, compare.get(counter));
//		compare.set(counter, temp1);
//
//		return counter;
//	}
//
//	private static void quickSort(ArrayList<Point2D.Double> array, ArrayList<Double> compare, int begin, int end) {
//		// ensure that the two arrays are of the same length
//		if (array.size() != compare.size())
//			return;
//
//		if (end <= begin)
//			return;
//		
//		int pivot = partition(array, compare, begin, end);
//		quickSort(array, compare, begin, pivot - 1);
//		quickSort(array, compare, pivot + 1, end);
//	}

	// A utility function to swap two elements
	@SuppressWarnings("unchecked")
	static <T> void swap(ArrayList<T> arr, int i, int j) {
		T obj_i = arr.get(i);
		T obj_j = arr.get(j);

		// determine type T
		if (obj_i instanceof EdgeData) {
			EdgeData temp_i = (EdgeData) ((EdgeData) obj_i).clone();
			EdgeData temp_j = (EdgeData) ((EdgeData) obj_j).clone();
			arr.set(i, (T) temp_j);
			arr.set(j, (T) temp_i);

		} else if (obj_i instanceof Point2D.Double) {
			Point2D.Double temp_i = (Point2D.Double) ((Point2D.Double) obj_i).clone();
			Point2D.Double temp_j = (Point2D.Double) ((Point2D.Double) obj_j).clone();
			arr.set(i, (T) temp_j);
			arr.set(j, (T) temp_i);
		} else if (obj_i instanceof Double) {
			Double temp_i = (Double) obj_i;
			Double temp_j = (Double) obj_j;
			arr.set(i, (T) temp_j);
			arr.set(j, (T) temp_i);
		} else {
			// error has occurred if we have reached here
			return;
		}
	}

	/*
	 * This function takes last element as pivot, places the pivot element at its
	 * correct position in sorted array, and places all smaller (smaller than pivot)
	 * to left of pivot and all greater elements to right of pivot
	 */
	static <T> int partition(ArrayList<T> arr, ArrayList<Double> compare, int low, int high) {

		// pivot
		Double pivot = compare.get(high);

		// Index of smaller element and
		// indicates the right position
		// of pivot found so far
		int i = (low - 1);

		for (int j = low; j <= high - 1; j++) {

			// If current element is smaller
			// than the pivot
			if (compare.get(j) < pivot) {
				// Increment index of
				// smaller element
				i++;
				swap(arr, i, j);
				swap(compare, i, j);
			}
		}
		swap(arr, i + 1, high);
		swap(compare, i + 1, high);
		return (i + 1);
	}

	/*
	 * The main function that implements QuickSort arr[] --> Array to be sorted, low
	 * --> Starting index, high --> Ending index
	 */
	public static <T> void quickSort(ArrayList<T> arr, ArrayList<Double> compare, int low, int high) {
		if (low < high) {

			// pi is partitioning index, arr[p]
			// is now at right place
			int pi = partition(arr, compare, low, high);

			// Separately sort elements before
			// partition and after partition
			quickSort(arr, compare, low, pi - 1);
			quickSort(arr, compare, pi + 1, high);
		}
	}

	private static int getMax(ArrayList<Double> angles, double maxAngle) {
		int index = 0;
		Double currLargestAngle = angles.get(index);
		for (int i = 1; i < angles.size(); i++) {
			if (castDecimal(angles.get(i), 4) <= castDecimal(maxAngle, 4) && angles.get(i) > currLargestAngle) {
				index = i;
				currLargestAngle = angles.get(i);
			}
		}
		return index;
	}

	private static int getMin(ArrayList<Double> angles, double minAngle) {
		int index = angles.size() - 1;
		Double currSmallestAngle = angles.get(index);
		for (int i = 0; i < angles.size() - 1; i++) {
			if (castDecimal(angles.get(i), 4) >= castDecimal(minAngle, 4) && angles.get(i) < currSmallestAngle) {
				index = i;
				currSmallestAngle = angles.get(i);
			}
		}
		return index;
	}

	public static Double castDecimal(Double n, int numDecimalPlace) {
		return (int) (n * Math.pow(10, numDecimalPlace)) / Math.pow(10, numDecimalPlace);
	}

	/* Loads control points from input file */
	public Point2D.Double[] load(String filename) {
		Scanner in;
		try {
			in = new Scanner(new FileReader(filename));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		// Retrieving number of control points
		int N = 0;
		try {
			N = in.nextInt();
		} catch (Exception e) {
			System.out.println(e.getCause());
		}
		Point2D.Double[] controlPoints = new Point2D.Double[N];

		// Retrieving control points coordinates.
		double X, Y;
		for (int i = 0; i < N; i++) {
			X = in.nextDouble();
			Y = in.nextDouble();
			controlPoints[i] = new Point2D.Double(X, Y);
		}

		in.close();
		return controlPoints;
	}
}

/*
 * Under spokeHullIntersection
 */

//double i_x, i_y;
//double s, t, s1_x, s1_y, s2_x, s2_y;

//for (int i = 0; i < numHullSegments; i++) {
//	Segment s1 = hullSegments.get(i);
//	PVector s1l = s1.getLeftPoint();
//	double s1lx = s1l.x;
//	double s1ly = s1l.y;
//	PVector s1r = s1.getRightPoint();
//	double s1rx = s1r.x;
//	double s1ry = s1r.y;
//
//	for (int j = 0; j < numSiteEdgeSegments; j++) {
//		Point2D.Double spokeHullIntersectionPoint = null;
//		Segment s2 = hullSegments.get(j);
//		PVector s2l = s2.getLeftPoint();
//		double s2lx = s2l.x;
//		double s2ly = s2l.y;
//		PVector s2r = s2.getRightPoint();
//		double s2rx = s2r.x;
//		double s2ry = s2r.y;
//
//		s1_x = s1rx - s1lx;
//		s1_y = s1ry - s1ly;
//		s2_x = s2rx - s2lx;
//		s2_y = s2ry - s2ly;
//
//		s = (-s1_y * (s1lx - s2lx) + s1_x * (s1ly - s2ly)) / (-s2_x * s1_y + s1_x * s2_y);
//		t = (s2_x * (s1ly - s2ly) - s2_y * (s1lx - s2lx)) / (-s2_x * s1_y + s1_x * s2_y);
//
//		// Collision detected
//		if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
//			i_x = s1lx + (t * s1_x);
//			i_y = s1ly + (t * s1_y);
//			spokeHullIntersectionPoint = new Point2D.Double();
//			spokeHullIntersectionPoint.setLocation(i_x, i_y);
//		}
//		spokeHullIntersects.add(spokeHullIntersectionPoint);
//	}
//}

//List<Point2D.Double> allPoint = new ArrayList<Point2D.Double>(Arrays.asList(allPoints));
//List<Point2D.Double> hullPoint = new ArrayList<Point2D.Double>(Arrays.asList(hullVertex));
//List<Point2D.Double> sites = new ArrayList<Point2D.Double>();
//allPoint.removeAll(hullPoint);
//sites.addAll(allPoint);
//Point2D.Double[] allSites = new Point2D.Double[sites.size()];
//allSites = sites.toArray(new Point2D.Double[sites.size()]);

//	// Find hull edge intersects
//	List<Point3d> spokeHullIntersects = new ArrayList<Point3d>();
//	List<Point2D.Double> spokeHullIntersectsDouble = new ArrayList<Point2D.Double>();
//	// Point3d[] spokeHullIntersects = new Point3d[sitesArrayLength * 2];
//	for (int s = 0; s < numSites; s++) {
//		Point3d sproj = HilbertGeometry.toHomogeneous(sitePoints[s]);
//
//		for (int h = 0; h < numHullVertex; h++) {
//			Point3d hproj = HilbertGeometry.toHomogeneous(hullVertex[h]);
//			Point3d shLine = hproj.crossProduct(sproj); // spoke
//
//			for (int i = 0; i < hullEdgesArrayLength; i++) {
//				Point3d intersection = hullEdges[i].crossProduct(shLine);
//				Point2D.Double intersectionPoint = HilbertGeometry.toCartesian(intersection);
//				
//				// check if the intersection is already in intersection point list
//				if(  Convex.almostContainsElement(spokeHullIntersectsDouble, intersectionPoint) )
//					continue;
//				
//				if( this.isOnConvexBoundary( intersectionPoint ) ) {
//					spokeHullIntersects.add(intersection);
//					spokeHullIntersectsDouble.add(intersectionPoint);
//				}
//			}
//		}
//	}
//	return spokeHullIntersects;
