
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Checker {
    public static void main(String[] args) throws IOException {
//        System.setIn(new BufferedInputStream(new FileInputStream("in.txt")));
//        System.setOut(new PrintStream("out.txt"));
        Scanner scanner = new Scanner(System.in);
        AdjacencyMatrixGraph<String, Integer> graph = new AdjacencyMatrixGraph<>();
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
                    graph.addEdge(graph.findVertex(fromName), graph.findVertex(toName), Integer.parseInt(weight));
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
                        for (int i = 0; i < cycle.size() - 1; i++) {
                            cycleWeight += graph.findEdge(cycle.get(i).getValue(), cycle.get(i + 1).getValue()).getWeight();
                        }
                        cycleWeight += graph.findEdge(cycle.get(cycle.size() - 1).getValue(), cycle.get(0).getValue()).getWeight();
                        System.out.print(cycleWeight + " ");
                        cycle.forEach(x -> System.out.print(x.getValue() + " "));
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
    Graph.Edge<V, E>[][] adjMatrix;

    //indices and vertices have one-to-one mapping
    HashMap<Graph.Vertex<V>, Integer> indices;
    ArrayList<Graph.Vertex<V>> vertices;

    public AdjacencyMatrixGraph() {
        int initialSize = 0;
        this.adjMatrix = (Graph.Edge<V, E>[][]) new Graph.Edge[initialSize][initialSize];
        this.vertices = new ArrayList<>();
        this.indices = new HashMap<>();
    }

    @Override
    public Graph.Vertex<V> addVertex(V value) { //O(n^2)
        Graph.Vertex<V> v = new Graph.Vertex<>(value);
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

    @Override
    public Graph.Edge<V, E> addEdge(Graph.Vertex<V> from, Graph.Vertex<V> to, E weight) { //O(1)
        Graph.Edge<V, E> edge = new Graph.Edge<>(from, to, weight);
        this.adjMatrix[this.indices.get(from)][this.indices.get(to)] = edge;
        return edge;
    }

    @Override
    public void removeEdge(Graph.Edge<V, E> e) { //O(1)
        if (e == null)
            return;
        this.adjMatrix[this.indices.get(e.getFrom())][this.indices.get(e.getTo())] = null;
    }

    @Override
    public Collection<Graph.Edge<V, E>> edgesFrom(Graph.Vertex<V> v) { //O(n)
        if (v == null)
            return new ArrayList<>();
        int index = this.indices.get(v);
        Collection<Graph.Edge<V, E>> edgesFrom = new ArrayList<>();
        Arrays.stream(this.adjMatrix[index]).filter(Objects::nonNull).forEach(edgesFrom::add);
        return edgesFrom;
    }

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

    @Override
    public Graph.Vertex<V> findVertex(V value) {
        for (Graph.Vertex<V> v : this.vertices)
            if (v.getValue().equals(value))
                return v;
        return null;
    }

    @Override
    public Graph.Edge<V, E> findEdge(V fromValue, V toValue) {
        Graph.Vertex<V> from = this.findVertex(fromValue);
        Graph.Vertex<V> to = this.findVertex(toValue);
        if (from == null || to == null)
            return null;
        return this.adjMatrix[this.indices.get(from)][this.indices.get(to)];
    }

    @Override
    public boolean hasEdge(Graph.Vertex<V> from, Graph.Vertex<V> to) {
        return this.adjMatrix[this.indices.get(from)][this.indices.get(to)] != null;
    }

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
     *                          from the cycle, otherwise it will be empty
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


interface Graph<V, E> {
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


