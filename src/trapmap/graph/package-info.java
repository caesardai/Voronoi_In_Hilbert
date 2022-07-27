/**
 * <h3>Package</h3>
 * 
 * Components of the <i>History Graph</i>, a Directed Acyclical Graph (DAG) that
 * stores trapezoids in nodes. This DAG is navigated during point location
 * queries to find the trapezoid that contains the query point. The root
 * corresponds to the bounding box (modelled as a trapezoid) of the line
 * segments.
 * 
 * 
 * <h3>Queries</h3>
 * 
 * <li>The query point q is given by its coordinates</li>
 * <li>If the current node is a leaf, then we are done</li>
 * <li>Otherwise, one of the descendants of the current trapezoid is a trapezoid
 * that contains q</li>
 * <li>Since there are at most 4 descendants, we can find it in O(1) time</li>
 * <li>Go down to this descendant and repeat the process</li>
 * 
 */
package trapmap.graph;
