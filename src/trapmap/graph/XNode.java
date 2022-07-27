package trapmap.graph;

import processing.core.PVector;

/**
 * An X node stores a segment end point.
 * 
 * @author Tyler Chenhall
 */
public class XNode extends Node {
	
	private PVector data;

	public XNode(PVector p) {
		super();
		data = p;
	}

	/**
	 * Return the Point contained in the Node
	 * 
	 * @return The Point data
	 */
	public PVector getData() {
		return data;
	}
}
