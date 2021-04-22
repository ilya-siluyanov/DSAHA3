package task2;

import task1.Graph;

import java.util.*;
import java.util.stream.Collectors;

public class AdjacencyMatrixGraph<V, E> implements Graph<V, E> {
    Edge<V, E>[][] adjMatrix;

    //indices and vertices have one-to-one mapping
    HashMap<Vertex<V>, Integer> indices;
    ArrayList<Vertex<V>> vertices;

    public AdjacencyMatrixGraph() {
        int initialSize = 0;
        this.adjMatrix = (Edge<V, E>[][]) new Edge[initialSize][initialSize];
        this.vertices = new ArrayList<>();
        this.indices = new HashMap<>();
    }

    @Override
    public Vertex<V> addVertex(V value) { //O(n^2)
        Vertex<V> v = new Vertex<>(value);
        this.indices.put(v, this.vertices.size());
        this.vertices.add(v);
        int newSize = this.vertices.size();
        Edge<V, E>[][] temp = (Edge<V, E>[][]) new Edge[newSize][newSize];
        for (int i = 0; i < this.adjMatrix.length; i++) {
            System.arraycopy(this.adjMatrix[i], 0, temp[i], 0, this.adjMatrix[i].length);
        }
        this.adjMatrix = temp;
        return v;
    }

    @Override
    public void removeVertex(Vertex<V> v) { //O(n^2)
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
        Edge<V, E>[][] temp = (Edge<V, E>[][]) new Edge[newSize][newSize];

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

    @Override
    public Edge<V, E> addEdge(Vertex<V> from, Vertex<V> to, E weight) { //O(1)
        Edge<V, E> edge = new Edge<>(from, to, weight);
        this.adjMatrix[this.indices.get(from)][this.indices.get(to)] = edge;
        return edge;
    }

    @Override
    public void removeEdge(Edge<V, E> e) { //O(1)
        if (e == null)
            return;
        this.adjMatrix[this.indices.get(e.getFrom())][this.indices.get(e.getTo())] = null;
    }

    @Override
    public Collection<Edge<V, E>> edgesFrom(Vertex<V> v) { //O(n)
        int index = this.indices.get(v);
        Collection<Edge<V, E>> edgesFrom = new ArrayList<>();
        Arrays.stream(this.adjMatrix[index]).filter(Objects::nonNull).forEach(edgesFrom::add);
        return edgesFrom;
    }

    @Override
    public Collection<Edge<V, E>> edgesTo(Vertex<V> v) {//O(n)
        int index = this.indices.get(v);
        Collection<Edge<V, E>> edgesTo = new ArrayList<>();
        for (Edge<V, E>[] row : this.adjMatrix) {
            if (row[index] != null)
                edgesTo.add(row[index]);
        }
        return edgesTo;
    }

    @Override
    public Vertex<V> findVertex(V value) {
        for (Vertex<V> v : this.vertices)
            if (v.getValue().equals(value))
                return v;
        return null;
    }

    @Override
    public Edge<V, E> findEdge(V fromValue, V toValue) {
        Vertex<V> from = this.findVertex(fromValue);
        Vertex<V> to = this.findVertex(toValue);
        return this.adjMatrix[this.indices.get(from)][this.indices.get(to)];
    }

    @Override
    public boolean hasEdge(Vertex<V> v, Vertex<V> u) {
        return this.adjMatrix[this.indices.get(v)][this.indices.get(u)] != null;
    }

    public void transpose() {
        List<Edge<V, E>> newEdges = new ArrayList<>();
        for (Vertex<V> firstVertex : this.vertices) {
            for (Vertex<V> secondVertex : this.vertices) {
                if (this.hasEdge(firstVertex, secondVertex)) {
                    Edge<V, E> prevEdge = this.findEdge(firstVertex.getValue(), secondVertex.getValue());
                    this.removeEdge(prevEdge);
                    newEdges.add(new Edge<>(prevEdge.getTo(), prevEdge.getFrom(), prevEdge.getWeight()));
                }
            }
        }
        newEdges.forEach(x -> this.addEdge(x.getFrom(), x.getTo(), x.getWeight()));
    }


    public boolean isAcyclic() {
        return this.getCycle() == null;
    }

    public List<Vertex<V>> getCycle() {
        int[] color = new int[this.vertices.size()];
        int[] p = new int[this.vertices.size()];
        List<Integer> cycleContainer = null;
        for (int i = 0; i < this.vertices.size(); i++) {
            if (color[i] == 0) {
                cycleContainer = new ArrayList<>();
                dfs(i, -1, p, color, cycleContainer);
                if (!cycleContainer.isEmpty())
                    break;
                else {
                    cycleContainer = null;
                }
            }
        }
        if (cycleContainer == null)
            return null;
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
     *                       *                       from the cycle
     */
    private void dfs(int x, int from, int[] p, int[] color, List<Integer> cycleContainer) {
        color[x] = 1;
        p[x] = from;
        for (int to : this.edgesFrom(this.vertices.get(x)).stream().map(v -> this.indices.get(v.getTo())).collect(Collectors.toList())) {
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
