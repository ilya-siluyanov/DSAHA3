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
        int w = Integer.parseInt(scanner.next());
        AdjacencyMatrixGraph<Integer, EdgeInfo> graph = new AdjacencyMatrixGraph<>();
        for (int i = 0; i < m; i++) {
            int from = Integer.parseInt(scanner.next());
            int to = Integer.parseInt(scanner.next());
            int weight = Integer.parseInt(scanner.next());
            int bandwidth = Integer.parseInt(scanner.next());
            if (bandwidth < w)
                continue;
            Graph.Vertex<Integer> vertexFrom = graph.findVertex(from);
            Graph.Vertex<Integer> vertexTo = graph.findVertex(to);
            if (vertexFrom == null)
                vertexFrom = graph.addVertex(from);
            if (vertexTo == null)
                vertexTo = graph.addVertex(to);
            graph.addEdge(vertexFrom, vertexTo, new EdgeInfo(weight, bandwidth));
        }
        Graph.Vertex<Integer> start = graph.findVertex(Integer.parseInt(scanner.next()));
        Graph.Vertex<Integer> end = graph.findVertex(Integer.parseInt(scanner.next()));
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
            for (Graph.Vertex<Integer> v : path) {
                System.out.print(v.getValue() + " ");
            }
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

        if (d[end.getValue()] == INF) {
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
                Graph.Vertex<Integer> temp = ans.get(i);
                ans.set(i, ans.get(ans.size() - 1 - i));
                ans.set(ans.size() - 1 - i, temp);
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
