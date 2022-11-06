package geometry;

import java.awt.geom.Point2D;
// import java.awt.geom.Point2D.Double;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

import Jama.Matrix;
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
//				newSeg.setEdge(edgeIntersect);
//				newSeg.setSites(newSite);
				segs.add(newSeg);
			}
		}
		return segs;
	}

	/*
	 * Construct sectors with given sites and segments Each sector is associated
	 * with an edge and site
	 */
	public List<Sector> constructSector(Segment edge, Point2D.Double site1, Point2D.Double site2,
			KdTree<KdTree.XYZPoint> graph) {
		// returning list of sectors
		List<Sector> sectors = new ArrayList<Sector>();

		Point2D.Double ep1 = Util.toPoint2D(edge.getLeftPoint());
		Point2D.Double ep2 = Util.toPoint2D(edge.getRightPoint());

		/*
		 * SEGMENT's ENDPOINT 1
		 */
		// get all center point's neighboring segment
		KdTree.XYZPoint ep1XYZPoint = Util.toXYZPoint(ep1);
		// get site1 node
		KdTree.KdNode ep1Node = KdTree.getNode(graph, ep1XYZPoint);
		// get site1 ID
		KdTree.XYZPoint ep1ID = ep1Node.getID();
		// get all neighbor points for site 1
		// ArrayList<EdgeData> ep1Endpoints = ep1ID.getNeighbors();

		/*
		 * SEGMENT's ENDPOINT 2
		 */
		// get all center point's neighboring segment
		KdTree.XYZPoint ep2XYZPoint = Util.toXYZPoint(ep2);
		// get site1 node
		KdTree.KdNode ep2Node = KdTree.getNode(graph, ep2XYZPoint);
		// get site1 ID
		KdTree.XYZPoint ep2ID = ep2Node.getID();
		// get all neighbor points for site 1
		ArrayList<EdgeData> ep2Endpoints = ep2ID.getNeighbors();

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
		// ArrayList<EdgeData> s1Endpoints = s1ID.getNeighbors();

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
		// ArrayList<EdgeData> s2Endpoints = s2ID.getNeighbors();

		// compute the angular coordinates of ep2's nearest neighbors and sort these
		// points
		ArrayList<Double> ep2Angles = this.computeAngles(ep2Endpoints, ep2);
		Convex.quickSort(ep2Endpoints, ep2Angles, 0, ep2Endpoints.size() - 1);

		// find the third vertex point
		ArrayList<Point2D.Double> ep2Candidates = this.findClosestAngles(ep2Endpoints, ep1);

		// look at the two neighbors
		Sector sector1 = this.constructSectorFromThreePoints(ep1ID, ep2ID,
				KdTree.getNode(graph, Util.toXYZPoint(ep2Candidates.get(0))).getID(), graph);
		Sector sector2 = this.constructSectorFromThreePoints(ep1ID, ep2ID,
				KdTree.getNode(graph, Util.toXYZPoint(ep2Candidates.get(1))).getID(), graph);

		// populate sector with all metadata needed
		this.assignSectorMetaData(sector1, site1, site2, graph);
		this.assignSectorMetaData(sector2, site1, site2, graph);

		// return ArrayList of sectors
		sectors.add(sector1);
		sectors.add(sector2);
		return sectors;
	}

	// assume that p1 <- edge -> p2 <- edge -> p3
	public Sector constructSectorFromThreePoints(KdTree.XYZPoint p1, KdTree.XYZPoint p2, KdTree.XYZPoint p3,
			KdTree<KdTree.XYZPoint> graph) {
		// copy of XYZPoint as Point2D.Double objects
		Point2D.Double p12D = Util.toPoint2D(p1);
		Point2D.Double p22D = Util.toPoint2D(p2);
		Point2D.Double p32D = Util.toPoint2D(p3);

		// get neighbors of p1 and p3
		ArrayList<EdgeData> p1Neighbors = p1.getNeighbors();
		ArrayList<EdgeData> p3Neighbors = p3.getNeighbors();

		// sort neighbors
		ArrayList<Double> p1Angles = this.computeAngles(p1Neighbors, p12D);
		ArrayList<Double> p3Angles = this.computeAngles(p3Neighbors, p32D);
		Convex.quickSort(p1Neighbors, p1Angles, 0, p1Neighbors.size() - 1);
		Convex.quickSort(p3Neighbors, p3Angles, 0, p3Neighbors.size() - 1);

		// find best candidates for points of interest
		ArrayList<Point2D.Double> candidates1 = this.findClosestAngles(p1Neighbors, p22D);
		ArrayList<Point2D.Double> candidates2 = this.findClosestAngles(p3Neighbors, p22D);

		// 3 edge case; p1 and p3 share an edge
		if (candidates1.contains(p32D) && candidates2.contains(p12D)) {
			ArrayList<Point2D.Double> vertices = new ArrayList<Point2D.Double>();
			vertices.add(p12D);
			vertices.add(p22D);
			vertices.add(p32D);

			return new Sector(null, null, null, null, null, null, vertices, null);
		}

		// 4 edge case; both arrays contains a common point that is not the center point
		else if (candidates1.contains(candidates2.get(0)) || candidates1.contains(candidates2.get(1))) {
			// we need to determine the four point of the sector
			Point2D.Double p42D = null;

			for (Point2D.Double p : candidates2) {
				if (!p.equals(p22D) && candidates1.contains(p)) {
					p42D = p;
					break;
				}
			}

			ArrayList<Point2D.Double> vertices = new ArrayList<Point2D.Double>();
			vertices.add(p12D);
			vertices.add(p22D);
			vertices.add(p32D);
			vertices.add(p42D);

			return new Sector(null, null, null, null, null, null, vertices, null);
		}

		// 5 edge case; look at neighbors of all 4 pairing of the candidates above
		else {
			// get orientation of p1p2p3
			GrahamScan.Turn turn = GrahamScan.getTurn(p12D, p22D, p32D);
			// for neighbors of p3

			Point2D.Double p4 = null;

			for (Point2D.Double neighbor : candidates2) {
				if (GrahamScan.getTurn(p22D, p32D, neighbor) == turn) {
					p4 = neighbor;
				}

			}
			turn = GrahamScan.getTurn(p32D, p22D, p12D);
			// for neighbors of p3

			Point2D.Double p5 = null;

			for (Point2D.Double neighbor : candidates1) {
				if (GrahamScan.getTurn(p22D, p12D, neighbor) == turn) {
					p5 = neighbor;
				}

			}
			// we should have p4-p3-p2-p1-p5
			ArrayList<Point2D.Double> vertices = new ArrayList<Point2D.Double>();
			vertices.add(p5);
			vertices.add(p4);
			vertices.add(p32D);
			vertices.add(p12D);
			vertices.add(p22D);
			return new Sector(null, null, null, null, null, null, vertices, null);
		}
	}

	private void assignSectorMetaData(Sector sector, Point2D.Double site1, Point2D.Double site2,
			KdTree<KdTree.XYZPoint> graph) {
		ArrayList<Point2D.Double> vertices = new ArrayList<Point2D.Double>();
		for (int index = 0; index < sector.sector.convexHull.length - 1; index++)
			vertices.add(sector.sector.convexHull[index]);

		// get sites of segments
		ArrayList<Point2D.Double> sites = this.getSegSites(vertices, graph);

		// determine the associated edges of convex hull edges with sector
		Segment[] edges = this.determineEdges(vertices, site1, site2);

		// set values
		sector.setSite1(site1);
		sector.setSite2(site2);
		sector.setSegSites(sites);
		sector.setEdges(edges);
	}

	/**
	 * For convenient to traverse through the segments sort the end points base on
	 * angles regarding to the site(origin)
	 * 
	 * @param endPoints the nearest points to the center point
	 * @param center    the center point
	 * @return the sorted angular coordinates of the center point's nearest
	 *         neighbors on the graph
	 */
	protected static ArrayList<Double> computeAngles(ArrayList<EdgeData> endPoints, Point2D.Double center) {
		/*
		 */
		ArrayList<Double> angles = new ArrayList<Double>();
		// Calculate site angles
		for (int i = 0; i < endPoints.size(); i++) {
			angles.add(Voronoi.spokeAngle(center, endPoints.get(i).otherNode));
		}

		// Sort through site 1 angles
		Convex.quickSort(endPoints, angles, 0, angles.size() - 1);

		// return all angles
		return angles;
	}

	/**
	 * 
	 * @param points polar coordinates of interest; sorted based on the angular
	 *               coordinates
	 * @param center the point of interest. should be contained in the points
	 *               ArrayList
	 * @return the two points with the closest angle to input angle
	 */
	private ArrayList<Point2D.Double> findClosestAngles(ArrayList<EdgeData> points, Point2D.Double center) {
		// array to store our results
		ArrayList<Point2D.Double> closestPoints = new ArrayList<Point2D.Double>(2);

		// get index of center
		int angleSize = points.size();
		int index = 0;
		for (int i = 0; i < angleSize; i++) {
			if (Util.samePoints(center, points.get(i).otherNode)) {
				index = i;
				break;
			}
		}

		int lowerIndex = index - 1;
		if (lowerIndex < 0)
			lowerIndex += angleSize;

		// populate results
		closestPoints.add(0, (Point2D.Double) points.get(lowerIndex).otherNode.clone());
		closestPoints.add(1, (Point2D.Double) points.get((index + 1) % angleSize).otherNode.clone());

		// return results
		return closestPoints;
	}

	private Point2D.Double[] findNearestPoint(ArrayList<Point2D.Double> points, ArrayList<Double> angles, double a11,
			double a12) {
		// indices to return
		int lowerIndex = -1, upperIndex = -1;

		// determine which input angle is smaller
		double a1 = a11;
		double a2 = a12;
		if (a2 < a1) {
			double temp = a1;
			a1 = a2;
			a2 = a1;
		}

		// loop through angles and find the interval angleOfInterest lays in
		// if the angle of interest is contained by angles, then return that angle
		int currIndex = 0;
		boolean loop = true;
		while (loop && currIndex < points.size() - 1) {
			if (a1 >= angles.get(currIndex) && a1 <= angles.get(currIndex)) {
				lowerIndex = currIndex;
			}

			if (a2 >= angles.get(currIndex) && a2 <= angles.get(currIndex)) {
				upperIndex = currIndex;
			}

			if (lowerIndex >= 0 && upperIndex >= 0) {
				loop = false;
			}
		}

		return new Point2D.Double[] { points.get(lowerIndex), points.get(upperIndex) };

	}

	private ArrayList<Point2D.Double> getSegSites(ArrayList<Point2D.Double> vertices, KdTree<KdTree.XYZPoint> graph) {
		ArrayList<Point2D.Double> sites = new ArrayList<Point2D.Double>();
		for (int index = 0; index < vertices.size(); index++) {
			// the naming is arbitrary. don't make fun of me for my weird naming convention
			Point2D.Double c1 = vertices.get(index);
			Point2D.Double c2 = vertices.get((index + 1) % vertices.size());

			KdTree.XYZPoint c1XYZ = KdTree.getNode(graph, Util.toXYZPoint(c1)).getID();
			Point2D.Double segSiteOrigin = c1XYZ.getSite(c1XYZ.indexOf(c2));
			sites.add(segSiteOrigin);
		}
		return sites;
	}

	public Segment[] determineEdges(ArrayList<Point2D.Double> sectorVertices, Point2D.Double site1,
			Point2D.Double site2) {
		// Segments to return
		Segment[] edges = new Segment[4];

		// compute angular coordinate of vertices with respect to site1
		ArrayList<Double> s1Angles = new ArrayList<Double>(sectorVertices.size());
		for (Point2D.Double p : sectorVertices) {
			if (p.distance(site1) > 1e-10)
				s1Angles.add(Voronoi.spokeAngle(site1, p));
		}

		// compute angular coordinate of vertices with respect to site2
		ArrayList<Double> s2Angles = new ArrayList<Double>(sectorVertices.size());
		for (Point2D.Double p : sectorVertices) {
			if (p.distance(site2) > 1e-10)
				s2Angles.add(Voronoi.spokeAngle(site2, p));
		}

		// find the min and max angle of the ArrayList above for site1 angles
		double s1MinAngle = Collections.min(s1Angles);
		double s1MaxAngle = Collections.max(s1Angles);

		// find the min and max angle of the ArrayList above for site2 angles
		double s2MinAngle = Collections.min(s2Angles);
		double s2MaxAngle = Collections.max(s2Angles);

		// compute midpoint angle of the min and max for each site; checks that the
		// angle in question is the acute angle
		double s1MidAngle;
		if (Math.abs(s1MaxAngle - s1MinAngle) > Math.PI) {
			s1MaxAngle -= 2 * Math.PI;
			s1MidAngle = Math.abs(s1MinAngle - s1MaxAngle) / 2 + s1MaxAngle;
		} else {
			s1MidAngle = Math.abs(s1MaxAngle - s1MinAngle) / 2 + s1MinAngle;
		}
		if (s1MidAngle < 0)
			s1MidAngle += 2 * Math.PI;

		// ensure we are computing the mid-angle of the acute angle formed by all line
		// segments
		double s2MidAngle;
		if (Math.abs(s2MaxAngle - s2MinAngle) > Math.PI) {
			s2MaxAngle -= 2 * Math.PI;
			s2MidAngle = Math.abs(s2MinAngle - s2MaxAngle) / 2 + s2MaxAngle;
		} else {
			s2MidAngle = Math.abs(s2MaxAngle - s2MinAngle) / 2 + s2MinAngle;
		}
		if (s2MidAngle < 0)
			s2MidAngle += 2 * Math.PI;

		// compute the point that lays on the line we desire to construct
		Matrix s1Rotation = new Matrix(new double[] { Math.cos(s1MidAngle), Math.sin(s1MidAngle), -Math.sin(s1MidAngle),
				Math.cos(s1MidAngle) }, 2);
		Matrix s2Rotation = new Matrix(new double[] { Math.cos(s2MidAngle), Math.sin(s2MidAngle), -Math.sin(s2MidAngle),
				Math.cos(s2MidAngle) }, 2);

		// determine horizontal and vertical distance from the hull with respect to Euclidean metric (site 1).
        Point3d horizontalLine = new Point3d(0, 1, -1 * site1.y);
        Point3d verticalLine = new Point3d(1, 0, -1 * site1.x);
        Point2D.Double[] hintersectPoints = Util.intersectionPoints(horizontalLine, this);
        Point2D.Double[] vintersectPoints = Util.intersectionPoints(verticalLine, this);

        // find distance between all intersection points with site 1. get minimum
        // find correct radius for site 1
        double x0 = -1;
        ArrayList<Double> distances = new ArrayList<Double>();
        for(Point2D.Double p : hintersectPoints)
            distances.add(p.distance(site1));
        for(Point2D.Double p : vintersectPoints)
            distances.add(p.distance(site1));
        x0 = Collections.min(distances) / 10;


        // determine horizontal and vertical distance from the hull with respect to Euclidean metric (site 1).
        horizontalLine = new Point3d(0, 1, -1 * site2.y);
        verticalLine = new Point3d(1, 0, -1 * site2.x);
        hintersectPoints = Util.intersectionPoints(horizontalLine, this);
        vintersectPoints = Util.intersectionPoints(verticalLine, this);

        // find correct radius for site 2
        double y0 = -1;
        distances = new ArrayList<Double>();
        for(Point2D.Double p : hintersectPoints)
            distances.add(p.distance(site2));
        for(Point2D.Double p : vintersectPoints)
            distances.add(p.distance(site2));
        y0 = Collections.min(distances) / 10;

		Matrix nearOrigin1 = new Matrix(new double[] { x0, 0d }, 2);
		Matrix nearOrigin2 = new Matrix(new double[] { y0, 0d }, 2);
		Point2D.Double s1LinePoint = Util.toPoint2D(s1Rotation.times(nearOrigin1));
		Point2D.Double s2LinePoint = Util.toPoint2D(s2Rotation.times(nearOrigin2));
		Util.addToPoint(s1LinePoint, site1);
		Util.addToPoint(s2LinePoint, site2);

		// determine intersection points against the convex hull boundary and sort them
		ArrayList<Point2D.Double> s1ColinearPoints = new ArrayList<Point2D.Double>(4);
		Point2D.Double[] intersectionPoints = Util.intersectionPoints(s1LinePoint, site1, this);
		s1ColinearPoints.add(site1);
		s1ColinearPoints.add(s1LinePoint);
		s1ColinearPoints.add(intersectionPoints[0]);
		s1ColinearPoints.add(intersectionPoints[1]);
		Convex.sortColinearPoints(s1ColinearPoints);

		// determine what edges the intersection points lay on
		Point2D.Double[] ip = new Point2D.Double[] { s1ColinearPoints.get(0), s1ColinearPoints.get(3) };
		Segment[] directionSegs = new Segment[2];
		for (int index = 0; index < ip.length; index++) {
			for (int edgeCount = 0; edgeCount < this.convexHull.length - 1; edgeCount++) {
				// get line equation for current edge
				Point2D.Double v1 = this.convexHull[edgeCount];
				Point2D.Double v2 = this.convexHull[edgeCount + 1];
				Point3d hv1 = HilbertGeometry.toHomogeneous(v1);
				Point3d hv2 = HilbertGeometry.toHomogeneous(v2);
				Point3d line = hv1.crossProduct(hv2);

				if (Math.abs(line.scalarProduct(HilbertGeometry.toHomogeneous(ip[index]))) < 1e-4) {
					directionSegs[index] = new Segment(v1, v2);
					break;
				}
			}
		}

		// Forward
		if (s1ColinearPoints.get(2).equals(site1)) {
			edges[0] = directionSegs[0];
			edges[1] = directionSegs[1];
		}

		// Backward
		else {
			edges[0] = directionSegs[1];
			edges[1] = directionSegs[0];
		}

		// determine intersection points against the convex hull boundary and sort them
		ArrayList<Point2D.Double> s2ColinearPoints = new ArrayList<Point2D.Double>(4);
		intersectionPoints = Util.intersectionPoints(s2LinePoint, site2, this);
		s2ColinearPoints.add(site2);
		s2ColinearPoints.add(s2LinePoint);
		s2ColinearPoints.add(intersectionPoints[0]);
		s2ColinearPoints.add(intersectionPoints[1]);
		Convex.sortColinearPoints(s2ColinearPoints);

		// determine the edge convex hull intersection points are located
		ip = new Point2D.Double[] { s2ColinearPoints.get(0), s2ColinearPoints.get(3) };
		directionSegs = new Segment[2];
		for (int index = 0; index < ip.length; index++) {
			for (int edgeCount = 0; edgeCount < this.convexHull.length - 1; edgeCount++) {
				// get line equation for current edge
				Point2D.Double v1 = this.convexHull[edgeCount];
				Point2D.Double v2 = this.convexHull[edgeCount + 1];
				Point3d hv1 = HilbertGeometry.toHomogeneous(v1);
				Point3d hv2 = HilbertGeometry.toHomogeneous(v2);
				Point3d line = hv1.crossProduct(hv2);

				if (Math.abs(line.scalarProduct(HilbertGeometry.toHomogeneous(ip[index]))) < 1e-4) {
					directionSegs[index] = new Segment(v1, v2);
					break;
				}
			}
		}

		// Forward
		if (s2ColinearPoints.get(2).equals(site2)) {
			edges[2] = directionSegs[0];
			edges[3] = directionSegs[1];
		}

		// Backward
		else {
			edges[2] = directionSegs[1];
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
					compare.add(p.y);
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
			if (a.distanceSq(p)<.01) {
				onBoundary = true;
			}
			Segment seg = new Segment(a,b);
			Point2D.Double ab = new Point2D.Double(b.x - a.x, b.y - a.y);
			Point2D.Double ap = new Point2D.Double(p.x - a.x, p.y - a.y);
			double crossProduct = ab.x * ap.y - ab.y * ap.x;
			if (Math.abs(crossProduct) <= 1e-2) {
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
	public static void sortColinearPoints(ArrayList<Point2D.Double> points) {
		// only sort if there are more than 1 point in our list
		if (points.size() < 2)
			return;

		// determine if the set of points are colinear with a vertical line
		ArrayList<Double> compare = new ArrayList<Double>();
		if (Math.abs(points.get(0).x - points.get(1).x) <= 1e-8) {
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
