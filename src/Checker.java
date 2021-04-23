import java.util.*;
import java.util.stream.Collectors;

public class Checker {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AdjacencyMatrixGraph<String, Long> graph = new AdjacencyMatrixGraph<>();
        String name;
        String fromName, toName, weight;
        while (scanner.hasNext()) {
            String op = scanner.next();
            switch (op) {
                case "ADD_VERTEX":
                    name = scanner.next();
                    graph.addVertex(name);
                    break;
                case "REMOVE_VERTEX":
                    name = scanner.next();
                    graph.removeVertex(graph.findVertex(name));
                    break;
                case "ADD_EDGE":
                    fromName = scanner.next();
                    toName = scanner.next();
                    weight = scanner.next();
                    graph.addEdge(graph.findVertex(fromName), graph.findVertex(toName), Long.parseLong(weight));
                    break;
                case "REMOVE_EDGE":
                    fromName = scanner.next();
                    toName = scanner.next();
                    graph.removeEdge(graph.findEdge(fromName, toName));
                    break;
                case "HAS_EDGE":
                    fromName = scanner.next();
                    toName = scanner.next();
                    System.out.println(
                            graph.hasEdge(graph.findVertex(fromName), graph.findVertex(toName)) ? "TRUE" : "FALSE"
                    );
                    break;
                case "IS_ACYCLIC":
                    if (!graph.isAcyclic()) {
                        List<Graph.Vertex<String>> cycle = graph.getCycle();
                        long cycleWeight = 0;
                        for (int i = 0; i < cycle.size(); i++) {
                            cycleWeight += graph.findEdge(cycle.get(i).getValue(), cycle.get((i + 1) % cycle.size()).getValue()).getWeight();
                        }
                        System.out.print(cycleWeight + " ");
                        for (Graph.Vertex<String> stringVertex : cycle) {
                            System.out.print(stringVertex.getValue() + " ");
                        }
                        System.out.println();
                    } else {
                        System.out.println("ACYCLIC");
                    }
                    break;
                case "TRANSPOSE":
                    graph.transpose();
                    break;
            }
        }
    }
}

class AdjacencyMatrixGraph<V, E> implements Graph<V, E> {
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

    public List<Graph.Vertex<V>> getCycle() {
        this.color = new int[this.vertices.size()];
        this.p = new int[this.vertices.size()];
        this.cycleContainer = null;
        for (int i = 0; i < this.vertices.size(); i++) {
            if (color[i] == 0) {
                this.cycleContainer = new ArrayList<>();
                dfs(i);
                if (!this.cycleContainer.isEmpty()) {
                    break;
                } else {
                    this.cycleContainer = null;
                }
            }
        }
        if (this.cycleContainer == null)
            return null;
        return this.cycleContainer.stream().map(x -> this.vertices.get(x)).collect(Collectors.toList());

    }

    /**
     * @param x              - current vertex we consider
     * @param from           - vertex from we came to x
     * @param p              - array of parents of vertices, p[i] - vertex from which we came to i
     * @param color          - color of a vertex.
     * <p>
     * p[i] = 0, if vertex was not visited
     * p[i] = 1, if visited, but we are travelling through its children
     * p[i] = 2, if dfs visited the vertex i and its children (and its children,and its children...)
     * @param cycleContainer - if there will be a cycle, the method will fill the container with vertices
     * from the cycle, otherwise it will be empty
     */
    private int[] p;
    private int[] color;
    private List<Integer> cycleContainer;

    private void dfs(int x) {
        color[x] = 1;
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
                    if (i >= mirrorIndex)
                        break;
                    cycleContainer.set(i, cycleContainer.get(mirrorIndex));
                    cycleContainer.set(mirrorIndex, temp);
                }
                break;
            } else if (color[to] == 0) {
                p[to] = x;
                dfs(to);
                if (!cycleContainer.isEmpty())
                    break;
            }
        }
        color[x] = 2;
    }
}

interface Graph<V, E> {
    Graph.Vertex<V> addVertex(V value);

    void removeVertex(Graph.Vertex<V> v);

    Graph.Edge<V, E> addEdge(Graph.Vertex<V> from, Graph.Vertex<V> to, E weight);

    void removeEdge(Graph.Edge<V, E> e);

    Collection<Graph.Edge<V, E>> edgesFrom(Graph.Vertex<V> v);

    Collection<Graph.Edge<V, E>> edgesTo(Graph.Vertex<V> v);

    Graph.Vertex<V> findVertex(V value);

    Graph.Edge<V, E> findEdge(V fromValue, V toValue);

    boolean hasEdge(Graph.Vertex<V> v, Graph.Vertex<V> u);

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

            Graph.Vertex<V> vertex = (Graph.Vertex<V>) o;

            return value.equals(vertex.value);
        }

        @Override
        public int hashCode() {
            return this.value.hashCode();
        }
    }

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
            //TODO: place of possible errors
            String hashString = this.from + "" + this.to + "" + this.weight;
            return hashString.hashCode();
        }
    }
}


