package trapmap.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an abstract tree node with parents, left child, and right child.
 * <p>
 * The abstract node holds no data; extensions hold different types of data.
 * 
 * @author Tyler Chenhall
 */
public abstract class Node {

	private Node parent = null;
	private Node leftChild = null;
	private Node rightChild = null;
	private List<Node> parents;

	protected Node() {
		parents = new ArrayList<>(2);
	}

	/**
	 * Get the last parent node assigned to this Node. This method is outdated since
	 * Nodes may have multiple parents in the final implementation
	 * 
	 * @return The value last stored as a parent Node
	 */
	public Node getParentNode() {
		return parent;
	}

	/**
	 * This is an updated method which returns the ArrayList of parents Nodes. It
	 * returns the original object, hence trusts the user not to modify this list
	 * 
	 * @return The ArrayList containing all parent nodes
	 */
	public List<Node> getParentNodes() {
		return parents;
	}

	/**
	 * Add a parent to the list for this Node
	 * 
	 * @param newParent The new parent Node to add
	 */
	public void setParentNode(Node newParent) {
		parent = newParent;
		parents.add(newParent);
	}

	/**
	 * Get the left child node of this Node
	 * 
	 * @return The left child node
	 */
	public Node getLeftChildNode() {
		return leftChild;
	}

	/**
	 * Sets the left child node. (Also sets the parent node of the new child to
	 * this)
	 * 
	 * @param newLChild The new left child node
	 */
	public void setLeftChildNode(Node newLChild) {
		leftChild = newLChild;
		leftChild.setParentNode(this);
	}

	/**
	 * Get the right child node of this Node
	 * 
	 * @return The right child node
	 */
	public Node getRightChildNode() {
		return rightChild;
	}

	/**
	 * Sets the right child node. (Also sets the parent node of the new child to
	 * this)
	 * 
	 * @param newRChild The new right child node
	 */
	public void setRightChildNode(Node newRChild) {
		rightChild = newRChild;
		rightChild.setParentNode(this);
	}
}
