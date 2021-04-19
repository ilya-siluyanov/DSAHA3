package task2;


import task1.Graph;

import java.util.List;
import java.util.Scanner;

public class Checker {
    public static void main(String[] args) {
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
