package trapmap.graph;

import trapmap.Trapezoid;

/**
 * Leafs model trapezoids at the lowest level of the History Graph.
 * 
 * @author Tyler Chenhall
 */
public class Leaf extends Node {

	private Trapezoid data;

	public Leaf(Trapezoid t) {
		super();
		data = t;
	}

	/**
	 * Return the trapezoid stored by this Leaf
	 * 
	 * @return The trapezoid
	 */
	public Trapezoid getData() {
		return data;
	}
}
