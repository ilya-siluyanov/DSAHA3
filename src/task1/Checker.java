package task1;

import java.util.Scanner;

public class Checker {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Graph<String, Integer> graph = new AdjacencyMatrixGraph<>();
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
            }
        }
    }
}
