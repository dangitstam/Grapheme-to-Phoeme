import java.util.*;

/**
 * Graph is an implementation of a generic, directed, multi-labeled graph.
 * <p>
 *
 * A graph consists of nodes and edges, such that the nodes themselves contain
 * values, and edges serve as relations between nodes.
 * <p>
 *
 * Nodes are represented simply as the the data they contain, whereas edges
 * are represented as a label/weight and a direction. Edge direction is specified
 * when a new connection between two nodes is made.
 *
 * @author Tam Dang
 */
public class Graph<V, W> {

    private static boolean CHECK_REP_ENABLED = false;

    // Serves as the concrete representation of the graph.
    // Nodes map to another map, whose keys are their children and whose values
    // are the edges between themselves and their child.
    private Map<V, Map<V, W>> graph;

    // Abstraction function:
    //
    // All keys in the map "graph" are the nodes of the graph.
    // The nested keys inside "graph" are also nodes of the graph, such that they are a destination
    // node for their parent, which would be the corresponding key to the nested map containin the child.
    //
    // The list of edges from node "n1" to "n2" is graph.get("n1").get("n2").
    // Note that "n1" is a key to the graph, and that "n2" is a key to the result of the lookup to graph
    // with "n1". The order in lookup then designates the direction of the edge.
    //
    // ex. The list of edges from "n2" going to "n1" instead would be graph.get("n2").get("n1")

    // Representation Invariant:
    //
    // Values in the graph (nodes, edge weights) cannot be null.
    // Nested keys representing children nodes must also be keys of the outer map "graph".

    /**
     * @effects constructs an empty graph
     */
    public Graph() {
        graph = new HashMap<V, Map<V, W>>();
    }

    /**
     * Checks that the representation invariant was maintained.
     */
    private void checkRep() {
        // All values must be non-null.
        assert (graph != null);
        for (V n1 : graph.keySet()) {
            assert (n1 != null);
            Map<V, W> children = graph.get(n1);
            for (V n2 : children.keySet()) {
                assert (n2 != null);

                // Children are also members of the graph.
                assert (graph.containsKey(n2));
                W currEdge = children.get(n2);
                assert (currEdge != null);
            }
        }
    }

    /**
     * Adds a node to the graph
     * @param value Value of the node to be added to the graph
     * @modifies this
     * @effects creates a node in the graph that has the provided value
     * @return Returns true on success, false if the graph already contains this node
     * @throws IllegalArgumentException if value is null
     */
    public boolean addNode(V value) {
        if (value == null) {
            throw new IllegalArgumentException();
        }

        if (graph.containsKey(value)) {
            return false;
        }
        graph.put(value, new HashMap<V, W>());
        if (CHECK_REP_ENABLED) {
            checkRep();
        }
        return true;
    }

    /**
     * Adds an edge to the graph.
     * @param n1 The node in which the edge will come from
     * @param n2 The node in which the edge will point to
     * @param edgeWeight The weight of the edge
     * @modifies this
     * @effects creates an edge from node n1 to node n2. If adding to an existing
     *          edge, will rewrite the original
     * @return Returns a reference to the edge on success, returns null otherwise
     * @throws IllegalArgumentException if edgeWeight is null
     * @throws IllegalStateException if either n1 or n2 are not in the graph
     */
    public boolean addEdge(V n1, V n2, W edgeWeight) {
        if (edgeWeight == null) {
            throw new IllegalArgumentException();
        }

        if (!graph.containsKey(n1) || !graph.containsKey(n2)) {
            throw new IllegalStateException();
        }

        // Add the edge weight to the set of edges b/t n1 and n2
        Map<V, W> children = graph.get(n1);
        children.put(n2, edgeWeight);
        if (CHECK_REP_ENABLED) {
            checkRep();
        }
        return true;
    }

    /**
     * Checks whether a node is contained in the graph
     * @param value The node of inquiry
     * @return Returns true if the node is in the graph, false otherwise
     */
    public boolean containsNode(V value) {
        return graph.containsKey(value);
    }

    /**
     * Returns the edges between two nodes in the graph
     * @param n1 The node in which the edges come from
     * @param n2 The node in which the edges points to
     * @requires n1 and n2 are within the graph
     * @return Returns an unmodifiable set containing the edges from n1 to n2.
     *         Returns null if either n1 or n2 is not in the graph.
     */
    public W getEdgeBetween(V n1, V n2) {
        if (!graph.containsKey(n1) || !graph.containsKey(n2)) {
            return null;
        }

        Map<V, W> n1_children = graph.get(n1);
        if (n1_children.containsKey(n2)) {
            return n1_children.get(n2);
        }

        // There are no edges from n1 to n2, so return an empty set.
        return null;
    }

    /**
     * Returns the set of nodes such that they are the destinations of the given node's edges
     * @param n1 The node of interest
     * @requires n1 is within the graph
     * @return Returns a set containing the descendant nodes of n1 if n1 is in the graph, null otherwise
     */
    public Set<V> getChildrenOf(V n1) {
        if (graph.containsKey(n1)) {
            return Collections.unmodifiableSet(graph.get(n1).keySet());
        }
        return null;
    }

    /**
     * Returns the set of all nodes contained in the graph
     * @return Returns a set containing all vertices in the graph.
     */
    public Set<V> getNodes() {
        return Collections.unmodifiableSet(graph.keySet());
    }

    /**
     * Returns the set of resulting nodes after traveling along a given edge
     * from a given source node
     * @param src The source node in which the edge comes from
     * @param edge The edge to travel along
     * @requires src is within the graph
     * @return Returns the set of destination nodes as a result of traveling from src along edge
     */
    public Set<V> traverseOnEdge(V src, W edge) {
        Set<V> children = getChildrenOf(src);
        Set<V> res = new HashSet<V>();
        for (V c : children) {
            W currEdge = getEdgeBetween(src, c);
            if (getEdgeBetween(src, c).equals(currEdge)) {
                res.add(c);
            }
        }

        return Collections.unmodifiableSet(res);
    }


    public String toString() {
        Set<V> nodes = getNodes();
        String res = "";
        for (V n : nodes) {
            res += n.toString();
            res += ":\n";
            Set<V> children = getChildrenOf(n);
            for (V c : children) {
                W edge = getEdgeBetween(n, c);
                res += ("\t" + c.toString() + " via " + edge.toString() + "\n");
            }
        }

        return res;
    }
}
