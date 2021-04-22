package task2;

import task1.Graph;

import java.util.*;

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

    public boolean isAcyclic() {
        if (this.vertices.size() == 0)
            return false;
        Stack<Vertex<V>> q = new Stack<>();
        int[] color = new int[this.vertices.size()];
        q.push(this.vertices.get(0));

        while (!q.isEmpty()) {
            Vertex<V> curr = q.pop();
            color[this.indices.get(curr)] = 1;
            for (Edge<V, E> edge : this.edgesFrom(curr)) {
                if (color[this.indices.get(edge.getTo())] == 1) {
                    return false;
                } else if (color[this.indices.get(edge.getTo())] == 0)
                    q.push(edge.getTo());
            }

        }
        return true;
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

    public List<Vertex<V>> getCycle() {
        if (this.vertices.size() == 0)
            return null;
        List<Vertex<V>> tempCycle = new ArrayList<>();
        boolean isCycleFound = false;
        Queue<Vertex<V>> q = new ArrayDeque<>();
        boolean[] used = new boolean[this.vertices.size()];
        q.offer(this.vertices.get(0));
        while (!q.isEmpty()) {
            Vertex<V> curr = q.poll();
            used[this.indices.get(curr)] = true;
            tempCycle.add(curr);
            for (Edge<V, E> edge : this.edgesFrom(curr)) {
                if (used[this.indices.get(edge.getTo())]) {
                    tempCycle.add(edge.getTo());
                    isCycleFound = true;
                    break;
                }
                q.offer(edge.getTo());
            }
            if (isCycleFound)
                break;
        }
        if (isCycleFound) {
            //TODO: there is an error
            List<Vertex<V>> cycle = new ArrayList<>();
            Vertex<V> cycleStart = tempCycle.get(tempCycle.size() - 1);
            cycle.add(cycleStart);
            for (int i = tempCycle.size() - 2; i >= 0; i--) {
                if (tempCycle.get(i).equals(cycleStart))
                    break;
                cycle.add(tempCycle.get(i));
            }
            for (int i = 0; i < cycle.size() / 2; i++) {
                Vertex<V> tmp = cycle.get(i);
                cycle.set(i, cycle.get(cycle.size() - 1 - i));
                cycle.set(cycle.size() - 1 - i, tmp);
            }
            return cycle;
        } else
            return null;
    }
}
