package task1;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class AdjacencyMatrixGraph<V extends Comparable<V>, E extends Comparable<E>> implements Graph<V, E> {
    Map<Vertex<V>, List<Edge<V, E>>> adjMatrix; //for each value of adjMatrix it holds:
    // the size of the list equals to the number of vertices graph contains

    @Override
    public Vertex<V> addVertex(V value) {

        return null;
    }

    @Override
    public void removeVertex(Vertex<V> v) {

    }

    @Override
    public Edge<V, E> addEdge(Vertex<V> from, Vertex<V> to, double weight) {
        return null;
    }

    @Override
    public void removeEdge(Edge<V, E> e) {

    }

    @Override
    public Collection<Edge<V, E>> edgesFrom(Vertex<V> v) {
        return null;
    }

    @Override
    public Collection<Edge<V, E>> edgesTo(Vertex<V> v) {
        return null;
    }

    @Override
    public Vertex<V> findVertex(V value) {
        return null;
    }

    @Override
    public Edge<V, E> findEdge(V fromValue, V toValue) {
        return null;
    }

    @Override
    public boolean hasEdge(Vertex<V> v, Vertex<V> u) {
        return false;
    }
}
