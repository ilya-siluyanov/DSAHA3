package task3;

import task1.Graph;
import task2.AdjacencyMatrixGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

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
        if (path != null) { //if there is a path from start to end
            long vertices = path.size();
            long length = 0;
            long bandwidth = INF;
            //calculate length and bandwidth of a path
            for (int i = 0; i < vertices - 1; i++) {
                Graph.Edge<Integer, EdgeInfo> edge = graph.findEdge(path.get(i).getValue(), path.get(i + 1).getValue());
                length += edge.getWeight().getWeight(); //length of a path is sum of edges connecting edges v[i] and v[i+1], 0<=i<=path.size()-1
                bandwidth = min(bandwidth, edge.getWeight().getBandwidth()); //bandwidth of a path is the minimum bandwidth on the path
            }

            System.out.println(vertices + " " + length + " " + bandwidth + " ");
            for (int i = 0; i < path.size() - 1; i++) {
                Graph.Vertex<Integer> v = path.get(i);
                System.out.print(v.getValue() + " ");
            }
            System.out.println(path.get(path.size() - 1).getValue());
        } else { //otherwise
            System.out.println("IMPOSSIBLE");
        }
    }

    /**
     * Dijkstra algorithm implementation
     * @param n - number of vertices in a graph
     * @param graph - given graph
     * @param start - start vertex
     * @param end - end vertex
     * @return - shortest path from start to end if present, otherwise null
     */
    public List<Graph.Vertex<Integer>> dijkstraAlgorithm(int n, AdjacencyMatrixGraph<Integer, EdgeInfo> graph, Graph.Vertex<Integer> start, Graph.Vertex<Integer> end) {
        //d[i] - the shortest path from start to vertex i, inf if there is no such a path from start to i
        long[] d = new long[n + 1];
        //p[i] - parent of vertex i on the shortest path, -1 if there is no parent of i
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
            //check adjacent vertices
            for (Graph.Edge<Integer, EdgeInfo> outEdge : graph.edgesFrom(currCase.getVertex())) {
                //if the shortest path can be updated, update it
                if (d[currVertex.getValue()] + outEdge.getWeight().getWeight() < d[outEdge.getTo().getValue()]) {
                    //update length of the shortest path
                    d[outEdge.getTo().getValue()] = d[currVertex.getValue()] + outEdge.getWeight().getWeight();
                    //update parent of vertex on the shortest path
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
                Graph.Vertex<Integer> temp = ans.get(i);
                ans.set(i, ans.get(mirrorIndex));
                ans.set(mirrorIndex, temp);
            }
            return ans;
        }
    }

    /**
     * wrapper for a graph edge that stores both weight and bandwidth
     */
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

    /**
     * wrapper for an element in priority queue containing a vertex and distance from start to the vertex
     * A pair x is less than y if x.distance < y.distance, greater than if x.distance > y.distance (equality does not
     * matter for the priority queue in Dijkstra algorithm)
     */
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