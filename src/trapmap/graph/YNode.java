package trapmap.graph;

import trapmap.Segment;

/**
 * A y-node stores a segment.
 * 
 * @author Tyler Chenhall
 */
public class YNode extends Node {

	private Segment data;

	public YNode(Segment s) {
		super();
		data = s;
	}

	/**
	 * Return the segment data held by this Node
	 * 
	 * @return the segment data
	 */
	public Segment getData() {
		return data;
	}
}
