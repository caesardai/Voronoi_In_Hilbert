package test;

import geometry.KdTree;
import geometry.Util;
import trapmap.Segment;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class KdTreeTest {
	public static void testKdTree() {
		// make segments of graph
		ArrayList<Segment> graph = new ArrayList<Segment>();
		graph.add(new Segment(0f, 0f, 2f, 0f));
		graph.add(new Segment(2f, 0f, 2f, 2f));
		graph.add(new Segment(2f, 2f, 0f, 2f));
		graph.add(new Segment(0f, 2f, 0f, 0f));
		
		graph.add(new Segment(0f, 0f, 1f, 1f));
		graph.add(new Segment(2f, 0f, 1f, 1f));
		graph.add(new Segment(0f, 2f, 1f, 1f));
		graph.add(new Segment(2f, 2f, 1f, 1f));
		
		KdTree<KdTree.XYZPoint> tree = new KdTree<KdTree.XYZPoint>(null, 2);
		
		// insert segments into graph
		for(Segment s : graph) {
			Point2D.Double left = Util.toPoint2D(s.getLeftPoint());
			Point2D.Double right = Util.toPoint2D(s.getRightPoint());

			KdTree.KdNode node = KdTree.getNode(tree, Util.toXYZPoint(left));
			if(node == null) {
				tree.add(Util.toXYZPoint(left));
				node = KdTree.getNode(tree, Util.toXYZPoint(left));
			}

			KdTree.XYZPoint point = node.getID();
			point.addNeighbor(right);

			node = KdTree.getNode(tree, Util.toXYZPoint(right));
			if(node == null) {
				tree.add(Util.toXYZPoint(right));
				node = KdTree.getNode(tree, Util.toXYZPoint(right));
			}

			point = node.getID();
			point.addNeighbor(left);
		}
		
		// look at resulting graph
		ArrayList<KdTree.XYZPoint> points = tree.getAllNodes();
		
		System.out.println("look at results");
	}
	
	public static void main(String[] argv) {
		KdTreeTest.testKdTree();
	}
}
