package task1;

import java.util.Collection;

public interface Graph<V extends Comparable<V>,E extends Comparable<E> > {
    Vertex<V> addVertex(V value);

    void removeVertex(Vertex<V> v);

    Edge<V, E> addEdge(Vertex<V> from, Vertex<V> to, double weight);

    void removeEdge(Edge<V, E> e);

    Collection<Edge<V, E>> edgesFrom(Vertex<V> v);

    Collection<Edge<V, E>> edgesTo(Vertex<V> v);

    Vertex<V> findVertex(V value);

    Edge<V, E> findEdge(V fromValue, V toValue);

    boolean hasEdge(Vertex<V> v, Vertex<V> u);

    class Vertex<V  extends Comparable<V>> {
        V value;
        public Vertex(V value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Vertex<?> vertex = (Vertex<?>) o;

            return value.equals(vertex.value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }

    class Edge<V extends Comparable<V>, E extends Comparable<E>> {
        Vertex<V> from;
        Vertex<V> to;
        E weight;

        public Edge(Vertex<V> from, Graph.Vertex<V> to, E weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Edge<?, ?> edge = (Edge<?, ?>) o;

            if (!from.equals(edge.from)) return false;
            if (!to.equals(edge.to)) return false;
            return weight.equals(edge.weight);
        }

        @Override
        public int hashCode() {
            int result = from.hashCode();
            result = 31 * result + to.hashCode();
            result = 31 * result + weight.hashCode();
            return result;
        }
    }
}
