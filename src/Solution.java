import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.min;

public class Solution {
    public static void main(String[] args) {
        new Solution().go();
    }

    public static final long INF = (long) 10e15;

    public void go() {
        Scanner scanner = new Scanner(System.in);
        int n = Integer.parseInt(scanner.next());
        int m = Integer.parseInt(scanner.next());
        AdjacencyMatrixGraph<Integer, EdgeInfo> graph = new AdjacencyMatrixGraph<>();
        List<Graph.Edge<Integer, EdgeInfo>> edges = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            int from = Integer.parseInt(scanner.next());
            int to = Integer.parseInt(scanner.next());
            int weight = Integer.parseInt(scanner.next());
            int bandwidth = Integer.parseInt(scanner.next());

            Graph.Vertex<Integer> vertexFrom = graph.findVertex(from);
            Graph.Vertex<Integer> vertexTo = graph.findVertex(to);
            if (vertexFrom == null)
                vertexFrom = graph.addVertex(from);
            if (vertexTo == null)
                vertexTo = graph.addVertex(to);
            edges.add(graph.addEdge(vertexFrom, vertexTo, new EdgeInfo(weight, bandwidth)));
        }

        Graph.Vertex<Integer> start = graph.findVertex(Integer.parseInt(scanner.next()));
        Graph.Vertex<Integer> end = graph.findVertex(Integer.parseInt(scanner.next()));
        int w = Integer.parseInt(scanner.next());
        for (Graph.Edge<Integer, EdgeInfo> edge : edges) {
            if (edge.getWeight().getBandwidth() < w) {
                graph.removeEdge(edge);
            }
        }

        List<Graph.Vertex<Integer>> path = this.dijkstraAlgorithm(n, graph, start, end);
        if (path != null) {
            long vertices = path.size();
            long length = 0;
            long bandwidth = INF;
            for (int i = 0; i < vertices - 1; i++) {
                Graph.Edge<Integer, EdgeInfo> edge = graph.findEdge(path.get(i).getValue(), path.get(i + 1).getValue());
                length += edge.getWeight().getWeight();
                bandwidth = min(bandwidth, edge.getWeight().getBandwidth());
            }

            System.out.println(vertices + " " + length + " " + bandwidth + " ");
            for (int i = 0; i < path.size()-1; i++) {
                Graph.Vertex<Integer> v = path.get(i);
                System.out.print(v.getValue() + " ");
            }
            System.out.println(path.get(path.size()-1).getValue());
        } else {
            System.out.println("IMPOSSIBLE");
        }
    }

    public List<Graph.Vertex<Integer>> dijkstraAlgorithm(int n, AdjacencyMatrixGraph<Integer, EdgeInfo> graph, Graph.Vertex<Integer> start, Graph.Vertex<Integer> end) {
        long[] d = new long[n + 1];
        int[] p = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            d[i] = INF;
            p[i] = -1;
        }
        d[start.getValue()] = 0;
        PriorityQueue<Pair> q = new PriorityQueue<>();
        q.offer(new Pair(start, 0));
        while (!q.isEmpty()) {
            Pair currCase = q.poll();
            Graph.Vertex<Integer> currVertex = currCase.getVertex();
            if (currCase.getDistance() > d[currCase.getVertex().getValue()])
                continue;

            for (Graph.Edge<Integer, EdgeInfo> outEdge : graph.edgesFrom(currCase.getVertex())) {
                if (d[currVertex.getValue()] + outEdge.getWeight().getWeight() < d[outEdge.getTo().getValue()]) {
                    d[outEdge.getTo().getValue()] = d[currVertex.getValue()] + outEdge.getWeight().getWeight();
                    p[outEdge.getTo().getValue()] = currVertex.getValue();
                    q.offer(new Pair(outEdge.getTo(), d[outEdge.getTo().getValue()]));
                }
            }
        }

        if (d[end.getValue()] >= INF) {
            return null;
        } else {
            List<Graph.Vertex<Integer>> ans = new ArrayList<>();
            int curr = end.getValue();
            while (p[curr] != -1) {
                ans.add(graph.findVertex(curr));
                curr = p[curr];
            }
            ans.add(graph.findVertex(curr));
            for (int i = 0; i < ans.size() / 2; i++) {
                int mirrorIndex = ans.size() - 1 - i;
//                if (i >= mirrorIndex)
//                    break;
                Graph.Vertex<Integer> temp = ans.get(i);
                ans.set(i, ans.get(mirrorIndex));
                ans.set(mirrorIndex, temp);
            }
            return ans;
        }
    }

    class EdgeInfo {
        final Integer weight;
        final Integer bandwidth;

        public EdgeInfo(Integer weight, Integer bandwidth) {
            this.weight = weight;
            this.bandwidth = bandwidth;
        }

        public Integer getWeight() {
            return weight;
        }

        public Integer getBandwidth() {
            return bandwidth;
        }

    }

    class Pair implements Comparable<Pair> {
        final Graph.Vertex<Integer> vertex;
        final long distance;

        public Pair(Graph.Vertex<Integer> vertex, long distance) {
            this.vertex = vertex;
            this.distance = distance;
        }

        public Graph.Vertex<Integer> getVertex() {
            return vertex;
        }


        public long getDistance() {
            return distance;
        }

        @Override
        public int compareTo(Pair o) {
            long diff = o.distance - this.distance;
            if (diff > 0)
                return 1;
            if (diff == 0)
                return 0;
            return -1;
        }
    }
}

class AdjacencyMatrixGraph<V, E> implements Graph<V, E> {
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
        if (from == null || to == null)
            return null;
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
     * @return cycle in the graph, if exists, otherwise empty list
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
     * custom dfs to find cycles in the graph
     * @param x              - current vertex we consider
     * @param from           - vertex from which we came to x
     * @param p              - array of parents of vertices, p[i] - vertex from which we came to i
     * @param color          - color of a vertex.
     *                       p[i] = 0, if vertex was not visited
     *                       p[i] = 1, if visited, but we are travelling through its children
     *                       p[i] = 2, if dfs visited the vertex i and its children (and its children,and its children...)
     * @param cycleContainer - if there will be a cycle, the method will fill the container with vertices
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

interface Graph<V, E> {
    /**
     * adds a vertex to the graph
     * @param value - value corresponding to a new vertex
     * @return reference to vertex with the corresponding value
     */
    Graph.Vertex<V> addVertex(V value);

    /**
     * removes the vertex v references to
     * @param v - reference to the vertex to remove
     */
    void removeVertex(Graph.Vertex<V> v);

    /**
     * adds an edge to the graph. Here edge is an ordered pair {from, to, w}
     * @param from - vertex from which the edge goes outside
     * @param to - vertex to which the edge goes to
     * @param weight - weight of the edge
     * @return - reference to corresponding edge
     */
    Graph.Edge<V, E> addEdge(Graph.Vertex<V> from, Graph.Vertex<V> to, E weight);

    /**
     * removes the edge e references to
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
     * @param toValue - value of vertex to which the same edge goes to
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
         * @return hashcode of a vertex
         */
        @Override
        public int hashCode() {
            return this.value.hashCode();
        }
    }

    /**
     * class representing an edge in a graph
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
            //TODO: place of possible errors
            String hashString = this.from + "" + this.to + "" + this.weight;
            return hashString.hashCode();
        }
    }
}
