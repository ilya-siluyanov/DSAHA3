package task1;

import java.util.Collection;

public interface Graph<V, E> {
    Vertex<V> addVertex(V value);

    void removeVertex(Vertex<V> v);

    Edge<V, E> addEdge(Vertex<V> from, Vertex<V> to, E weight);

    void removeEdge(Edge<V, E> e);

    Collection<Edge<V, E>> edgesFrom(Vertex<V> v);

    Collection<Edge<V, E>> edgesTo(Vertex<V> v);

    Vertex<V> findVertex(V value);

    Edge<V, E> findEdge(V fromValue, V toValue);

    boolean hasEdge(Vertex<V> v, Vertex<V> u);

    class Vertex<V> {
        private final V value;

        public Vertex(V value) {
            this.value = value;
        }

        public V getValue() {
            return this.value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;

            Vertex<V> vertex = (Vertex<V>) o;

            return value.equals(vertex.value);
        }

        @Override
        public int hashCode() {
            return this.value.hashCode();
        }
    }

    class Edge<V, E> {
        private final Vertex<V> from;
        private final Vertex<V> to;
        private final E weight;

        public Edge(Vertex<V> from, Graph.Vertex<V> to, E weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }

        public Vertex<V> getFrom() {
            return from;
        }

        public Vertex<V> getTo() {
            return to;
        }

        public E getWeight() {
            return weight;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Edge<V, E> edge = (Edge<V, E>) o;

            if (!from.equals(edge.from)) return false;
            if (!to.equals(edge.to)) return false;
            return weight.equals(edge.weight);
        }

        @Override
        public int hashCode() {
            //TODO: place of possible errors
            String hashString = this.from + "" + this.to + "" + this.weight;
            return hashString.hashCode();
        }
    }
}
