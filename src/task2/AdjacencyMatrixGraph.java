package task2;

import task1.Graph;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Solution for the task 2
 *
 * @author Ilya Siluyanov
 * @version 1.0
 * @since 2021-04-21
 */
public class AdjacencyMatrixGraph<V, E> implements Graph<V, E> {
    Graph.Edge<V, E>[][] adjMatrix;

    //indices and vertices have one-to-one mapping
    HashMap<Vertex<V>, Integer> indices;
    ArrayList<Graph.Vertex<V>> vertices;

    public AdjacencyMatrixGraph() {
        int initialSize = 0;
        this.adjMatrix = (Graph.Edge<V, E>[][]) new Graph.Edge[initialSize][initialSize];
        this.vertices = new ArrayList<>();
        this.indices = new HashMap<>();
    }

    /**
     * adds a vertex to the graph
     *
     * @param value - value corresponding to a new vertex
     * @return reference to vertex with the corresponding value
     */
    @Override
    public Graph.Vertex<V> addVertex(V value) { //O(n^2)
        Graph.Vertex<V> v = this.findVertex(value);
        if (v != null)
            return v;
        v = new Vertex<>(value);
        this.indices.put(v, this.vertices.size());
        this.vertices.add(v);
        int newSize = this.vertices.size();
        Graph.Edge<V, E>[][] temp = (Graph.Edge<V, E>[][]) new Graph.Edge[newSize][newSize];
        for (int i = 0; i < this.adjMatrix.length; i++) {
            System.arraycopy(this.adjMatrix[i], 0, temp[i], 0, this.adjMatrix[i].length);
        }
        this.adjMatrix = temp;
        return v;
    }

    /**
     * removes the vertex v references to
     *
     * @param v - reference to the vertex to remove
     */
    @Override
    public void removeVertex(Graph.Vertex<V> v) { //O(n^2)
        if (v == null)
            return;
        int vertexIndex = this.indices.get(v);
        this.indices.remove(v);
        for (int i = vertexIndex + 1; i < this.vertices.size(); i++) {
            this.indices.put(this.vertices.get(i), i - 1);
            this.vertices.set(i - 1, this.vertices.get(i));
        }
        this.vertices.remove(this.vertices.size() - 1);

        int newSize = this.vertices.size();
        Graph.Edge<V, E>[][] temp = (Graph.Edge<V, E>[][]) new Graph.Edge[newSize][newSize];

        for (int i = 0; i < vertexIndex; i++) {
            for (int j = 0; j < vertexIndex; j++) {
                temp[i][j] = this.adjMatrix[i][j];
            }
            for (int j = vertexIndex; j < this.adjMatrix.length - 1; j++) {
                temp[i][j] = this.adjMatrix[i][j + 1];
            }
        }
        for (int i = vertexIndex; i < this.adjMatrix.length - 1; i++) {
            for (int j = 0; j < vertexIndex; j++) {
                temp[i][j] = this.adjMatrix[i + 1][j];
            }
            for (int j = vertexIndex; j < this.adjMatrix.length - 1; j++) {
                temp[i][j] = this.adjMatrix[i + 1][j + 1];
            }
        }
        this.adjMatrix = temp;
    }

    /**
     * adds an edge to the graph. Here edge is an ordered pair {from, to, w}
     *
     * @param from   - vertex from which the edge goes outside
     * @param to     - vertex to which the edge goes to
     * @param weight - weight of the edge
     * @return - reference to corresponding edge
     */
    @Override
    public Graph.Edge<V, E> addEdge(Graph.Vertex<V> from, Graph.Vertex<V> to, E weight) { //O(1)
        if (from == null || to == null)
            return null;
        Graph.Edge<V, E> edge = new Graph.Edge<>(from, to, weight);
        this.adjMatrix[this.indices.get(from)][this.indices.get(to)] = edge;
        return edge;
    }

    /**
     * removes the edge e references to
     *
     * @param e - reference to edges to remove
     */
    @Override
    public void removeEdge(Graph.Edge<V, E> e) { //O(1)
        if (e == null)
            return;
        this.adjMatrix[this.indices.get(e.getFrom())][this.indices.get(e.getTo())] = null;
    }

    /**
     * @param v - reference to a vertex for which to return edges going outside
     * @return collection of references to edges which go outside from the vertex v references to
     */
    @Override
    public Collection<Graph.Edge<V, E>> edgesFrom(Graph.Vertex<V> v) { //O(n)
        if (v == null)
            return new ArrayList<>();
        int index = this.indices.get(v);
        Collection<Graph.Edge<V, E>> edgesFrom = new ArrayList<>();
        Arrays.stream(this.adjMatrix[index]).filter(Objects::nonNull).forEach(edgesFrom::add);
        return edgesFrom;
    }

    /**
     * @param v - reference to a vertex for which to return edges going to
     * @return a collection of references to edges which go to the vertex v references to
     */
    @Override
    public Collection<Graph.Edge<V, E>> edgesTo(Graph.Vertex<V> v) {//O(n)
        if (v == null)
            return new ArrayList<>();
        int index = this.indices.get(v);
        Collection<Graph.Edge<V, E>> edgesTo = new ArrayList<>();
        for (Graph.Edge<V, E>[] row : this.adjMatrix) {
            if (row[index] != null)
                edgesTo.add(row[index]);
        }
        return edgesTo;
    }

    /**
     * @param value - value of vertex for which to return a reference
     * @return reference to a vertex with value 'value', otherwise null
     */
    @Override
    public Graph.Vertex<V> findVertex(V value) {
        for (Graph.Vertex<V> v : this.vertices)
            if (v.getValue().equals(value))
                return v;
        return null;
    }

    /**
     * @param fromValue - value of vertex from which an edge goes outside
     * @param toValue   - value of vertex to which the same edge goes to
     * @return reference to the edge from vertex with value 'fromValue' to vertex with value 'toValue' if present,
     * otherwise null
     */
    @Override
    public Graph.Edge<V, E> findEdge(V fromValue, V toValue) {
        Graph.Vertex<V> from = this.findVertex(fromValue);
        Graph.Vertex<V> to = this.findVertex(toValue);
        if (from == null || to == null)
            return null;
        return this.adjMatrix[this.indices.get(from)][this.indices.get(to)];
    }

    /**
     * detects whether or not the graph have an edge from vertex 'from' to 'to'
     *
     * @param from - reference to a vertex from which there is probably an edge going outside to 'to'
     * @param to   - reference to a vertex to for  there is probably an edge going to from 'from'
     * @return whether or there is such an edge
     */
    @Override
    public boolean hasEdge(Graph.Vertex<V> from, Graph.Vertex<V> to) {
        if (from == null || to == null)
            return false;
        return this.adjMatrix[this.indices.get(from)][this.indices.get(to)] != null;
    }

    /**
     * transposes the graph. If there is an edge from i to j, it removes the edge and adds an edge from j to i
     * with the same weight (and bandwidth if present)
     */
    public void transpose() {
        List<Graph.Edge<V, E>> newEdges = new ArrayList<>();
        for (Graph.Vertex<V> firstVertex : this.vertices) {
            for (Graph.Vertex<V> secondVertex : this.vertices) {
                if (this.hasEdge(firstVertex, secondVertex)) {
                    Graph.Edge<V, E> prevEdge = this.findEdge(firstVertex.getValue(), secondVertex.getValue());
                    newEdges.add(new Edge<>(firstVertex, secondVertex, prevEdge.getWeight()));
                    this.removeEdge(prevEdge);
                }
            }
        }
        newEdges.forEach(x -> this.addEdge(x.getTo(), x.getFrom(), x.getWeight()));
    }

    /**
     * @return whether or not the graph is acyclic
     */
    public boolean isAcyclic() {
        return this.getCycle().isEmpty();
    }

    /**
     * @return list of vertices in a cycle from the graph if it is present, otherwise empty list
     */
    public List<Vertex<V>> getCycle() {
        int[] color = new int[this.vertices.size()];
        int[] p = new int[this.vertices.size()];
        List<Integer> cycleContainer = new ArrayList<>();
        for (int i = 0; i < this.vertices.size(); i++) {
            if (color[i] == 0) {
                cycleContainer = new ArrayList<>();
                dfs(i, -1, p, color, cycleContainer);
                if (!cycleContainer.isEmpty())
                    break;
            }
        }
        return cycleContainer.stream().map(x -> this.vertices.get(x)).collect(Collectors.toList());

    }

    /**
     * @param x              - current vertex we consider
     * @param from           - vertex from we came to x
     * @param p              - array of parents of vertices, p[i] - vertex from which we came to i
     * @param color          - color of a vertex.
     *                       <p>
     *                       p[i] = 0, if vertex was not visited
     *                       p[i] = 1, if visited, but we are travelling through its children
     *                       p[i] = 2, if dfs visited the vertex i and its children (and its children,and its children...)
     * @param cycleContainer -  if there will be a cycle, the method will fill the container with vertices
     *                       from the cycle, otherwise it will be empty
     */
    private void dfs(int x, int from, int[] p, int[] color, List<Integer> cycleContainer) {
        color[x] = 1;
        p[x] = from;
        for (int to : this.edgesFrom(this.vertices.get(x)).stream().map(Edge::getTo).filter(Objects::nonNull).map(v -> this.indices.get(v)).collect(Collectors.toList())) {
            if (color[to] == 1) { //cycle is found
                int curr = x;
                while (curr != to) {
                    cycleContainer.add(curr);
                    curr = p[curr];
                }
                cycleContainer.add(curr);
                for (int i = 0; i < cycleContainer.size() / 2; i++) {
                    int temp = cycleContainer.get(i);
                    int mirrorIndex = cycleContainer.size() - 1 - i;
                    cycleContainer.set(i, cycleContainer.get(mirrorIndex));
                    cycleContainer.set(mirrorIndex, temp);
                }
                break;
            } else if (color[to] == 0) {
                dfs(to, x, p, color, cycleContainer);
                if (!cycleContainer.isEmpty())
                    break;
            }
        }
        color[x] = 2;
    }
}