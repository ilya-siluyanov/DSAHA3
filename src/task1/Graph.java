package task1;

import java.util.Collection;

/**
 * Solution for the task 1
 *
 * @author Ilya Siluyanov
 * @version 1.0
 * @since 2021-04-21
 */
public interface Graph<V, E> {
    /**
     * adds a vertex to the graph
     *
     * @param value - value corresponding to a new vertex
     * @return reference to vertex with the corresponding value
     */
    Graph.Vertex<V> addVertex(V value);

    /**
     * removes the vertex v references to
     *
     * @param v - reference to the vertex to remove
     */
    void removeVertex(Graph.Vertex<V> v);

    /**
     * adds an edge to the graph. Here edge is an ordered pair {from, to, w}
     *
     * @param from   - vertex from which the edge goes outside
     * @param to     - vertex to which the edge goes to
     * @param weight - weight of the edge
     * @return - reference to corresponding edge
     */
    Graph.Edge<V, E> addEdge(Graph.Vertex<V> from, Graph.Vertex<V> to, E weight);

    /**
     * removes the edge e references to
     *
     * @param e - reference to edges to remove
     */
    void removeEdge(Graph.Edge<V, E> e);

    /**
     * @param v - reference to a vertex for which to return edges going outside
     * @return collection of references to edges which go outside from the vertex v references to
     */
    Collection<Graph.Edge<V, E>> edgesFrom(Graph.Vertex<V> v);

    /**
     * @param v - reference to a vertex for which to return edges going to
     * @return a collection of references to edges which go to the vertex v references to
     */
    Collection<Graph.Edge<V, E>> edgesTo(Graph.Vertex<V> v);

    /**
     * @param value - value of vertex for which to return a reference
     * @return reference to a vertex with value 'value', otherwise null
     */
    Graph.Vertex<V> findVertex(V value);

    /**
     * @param fromValue - value of vertex from which an edge goes outside
     * @param toValue   - value of vertex to which the same edge goes to
     * @return reference to the edge from vertex with value 'fromValue' to vertex with value 'toValue' if present,
     * otherwise null
     */
    Graph.Edge<V, E> findEdge(V fromValue, V toValue);

    /**
     * @param v - reference to a vertex from which an edge probably goes outside
     * @param u - reference to a vertex to which the same edge probably goes to
     * @return whether or not there exists an edge from vertex v to vertex u
     */
    boolean hasEdge(Graph.Vertex<V> v, Graph.Vertex<V> u);

    /**
     * class representing a vertex in a graph
     *
     * @param <V> type of value of vertex
     */
    class Vertex<V> {
        private final V value;

        public Vertex(V value) {
            this.value = value;
        }

        public V getValue() {
            return this.value;
        }

        /**
         * vertices are equal if they have the same value
         *
         * @param o - another object
         * @return whether or not vertices are equal
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;

            Graph.Vertex<V> vertex = (Graph.Vertex<V>) o;

            return value.equals(vertex.value);
        }

        /**
         * hashcode of a vertex === hashcode of the value corresponding to the vertex
         *
         * @return hashcode of a vertex
         */
        @Override
        public int hashCode() {
            return this.value.hashCode();
        }
    }

    /**
     * class representing an edge in a graph
     *
     * @param <V> type of value in vertices
     * @param <E> type of weights in edges
     */
    class Edge<V, E> {
        private final Graph.Vertex<V> from;
        private final Graph.Vertex<V> to;
        private final E weight;

        public Edge(Graph.Vertex<V> from, Graph.Vertex<V> to, E weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }

        public Graph.Vertex<V> getFrom() {
            return from;
        }

        public Graph.Vertex<V> getTo() {
            return to;
        }

        public E getWeight() {
            return weight;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Graph.Edge<V, E> edge = (Graph.Edge<V, E>) o;

            if (!from.equals(edge.from)) return false;
            if (!to.equals(edge.to)) return false;
            return weight.equals(edge.weight);
        }

        @Override
        public int hashCode() {
            String hashString = this.from + "" + this.to + "" + this.weight;
            return hashString.hashCode();
        }
    }
}
