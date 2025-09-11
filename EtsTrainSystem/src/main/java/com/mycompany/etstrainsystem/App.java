package com.mycompany.etstrainsystem;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.util.concurrent.CountDownLatch;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class App extends Application {
    
    private Pane graphPane;
    private Map<String, NodeView> nodes = new HashMap<>();
    private List<EdgeView> edges = new ArrayList<>();
    private Random random = new Random();
    private double globalRadius = 25; 
    private static final double MIN_NODE_DISTANCE = 80; 
    private Scanner scanner = new Scanner(System.in);
    private Stage primaryStage;
    private Scene scene;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        graphPane = new Pane();
        graphPane.setStyle("-fx-background-color: white; -fx-border-color: lightgray;");
        graphPane.setPrefSize(900, 600);
        
        initializePresetGraph();
        
        scene = new Scene(graphPane, 900, 600);
        primaryStage.setTitle("Graph Visualization Window");
        primaryStage.setScene(scene);
        
        primaryStage.setOnCloseRequest((WindowEvent event) -> {
            event.consume();
            primaryStage.hide();
        });
        
        Platform.setImplicitExit(false);
        
        CompletableFuture.runAsync(this::startCLI);
    }
    
    private void startCLI() {
        System.out.println("Graph Node Editor - CLI Mode");
        printCurrentGraph();
        
        while (true) {
            System.out.println("\n" + "-".repeat(50));
            System.out.println("Available commands (enter number):");
            System.out.println("1. Add a new node");
            System.out.println("2. Delete a node");
            System.out.println("3. Add an edge");
            System.out.println("4. Delete an edge");
            System.out.println("5. Display current graph");
            System.out.println("6. Find all reachable nodes");
            System.out.println("7. Exit program");
            System.out.println("-".repeat(50));
            System.out.print("Enter command number: ");
            
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;
            
            try {
                switch (input) {
                    case "1":
                        System.out.print("Enter node name: ");
                        String nodeName = scanner.nextLine().trim();
                        CountDownLatch latch = new CountDownLatch(1);
                        Platform.runLater(() -> {
                            createNode(nodeName);
                            latch.countDown(); // Signal completion
                        });
                        try {
                            latch.await(); // Wait for completion
                        } catch (InterruptedException e) {
                            System.out.println("Operation interrupted");
                        }
                        break;
                        
                    case "2":
                        System.out.print("Enter node name to delete: ");
                        String nodeToDelete = scanner.nextLine().trim();
                        CountDownLatch deleteLatch = new CountDownLatch(1);
                        Platform.runLater(() -> {
                            deleteNode(nodeToDelete);
                            deleteLatch.countDown();
                        });
                        try {
                            deleteLatch.await();
                        } catch (InterruptedException e) {
                            System.out.println("Operation interrupted");
                        }
                        break;
                        
                    case "3":
                        System.out.print("Enter first node name: ");
                        String node1 = scanner.nextLine().trim();
                        System.out.print("Enter second node name: ");
                        String node2 = scanner.nextLine().trim();
                        CountDownLatch edgeLatch = new CountDownLatch(1);
                        Platform.runLater(() -> {
                            createEdge(node1, node2);
                            edgeLatch.countDown();
                        });
                        try {
                            edgeLatch.await();
                        } catch (InterruptedException e) {
                            System.out.println("Operation interrupted");
                        }
                        break;
                        
                    case "4":
                        System.out.print("Enter first node name: ");
                        String edgeNode1 = scanner.nextLine().trim();
                        System.out.print("Enter second node name: ");
                        String edgeNode2 = scanner.nextLine().trim();
                        CountDownLatch deleteEdgeLatch = new CountDownLatch(1);
                        Platform.runLater(() -> {
                            deleteEdge(edgeNode1, edgeNode2);
                            deleteEdgeLatch.countDown();
                        });
                        try {
                            deleteEdgeLatch.await();
                        } catch (InterruptedException e) {
                            System.out.println("Operation interrupted");
                        }
                        break;
                        
                    case "5":
                        Platform.runLater(() -> {
                            if (!primaryStage.isShowing()) {
                                primaryStage.show();
                            }
                            primaryStage.toFront();
                        });
                        printCurrentGraph();
                        break;
                        
                    case "6":
                        System.out.print("Enter starting node: ");
                        String startNode = scanner.nextLine().trim();
                        findAllReachableNodes(startNode);
                        break;
                        
                    case "7":
                        System.out.println("Goodbye!");
                        Platform.runLater(() -> {
                            primaryStage.close();
                            Platform.exit();
                        });
                        return;
                        
                    default:
                        System.out.println("Invalid command number: " + input);
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    
    private void findAllReachableNodes(String initialState) {
        if (!nodes.containsKey(initialState)) {
            System.out.println("Initial state '" + initialState + "' does not exist!");
            return;
        }

        List<String> reachableNodesInOrder = findAllReachableNodesBFS(initialState);

        StringBuilder output = new StringBuilder("Reachable : " + initialState + " -> ");

        reachableNodesInOrder.remove(initialState);

        output.append(String.join(" -> ", reachableNodesInOrder));

        System.out.println(output.toString());
    }

    private List<String> findAllReachableNodesBFS(String start) {
        List<String> reachableNodesInOrder = new ArrayList<>();
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        queue.offer(start);
        visited.add(start);
        reachableNodesInOrder.add(start);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            List<String> neighbors = getNeighbors(current);

            for (String neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.offer(neighbor);
                    reachableNodesInOrder.add(neighbor);
                }
            }
        }

        return reachableNodesInOrder;
    }
    
    private List<String> getNeighbors(String nodeName) {
        List<String> neighbors = new ArrayList<>();
        
        for (EdgeView edge : edges) {
            if (edge.node1Name.equals(nodeName)) {
                neighbors.add(edge.node2Name);
            } else if (edge.node2Name.equals(nodeName)) {
                neighbors.add(edge.node1Name);
            }
        }
        
        return neighbors;
    }
    
    
    private void printCurrentGraph() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("CURRENT GRAPH STATUS");
        System.out.println("=".repeat(50));
        
        if (nodes.isEmpty()) {
            System.out.println("No nodes in graph");
        } else {
            System.out.println("Nodes (" + nodes.size() + "): " + String.join(", ", nodes.keySet()));
        }
        
        if (!edges.isEmpty()) {
            System.out.println("\nEdges (" + edges.size() + "):");
            for (EdgeView edge : edges) {
                System.out.println("  " + edge.node1Name + " <-> " + edge.node2Name);
            }
        } else {
            System.out.println("\nNo edges in graph");
        }
    }

    private void createNode(String name) {
        if (nodes.containsKey(name)) {
            System.out.println("Node '" + name + "' already exists!");
            return;
        }
        
        Text tempText = new Text(name);
        double textWidth = tempText.getBoundsInLocal().getWidth();
        double requiredRadius = Math.max(25, textWidth / 2 + 10);
        
        if (requiredRadius > globalRadius) {
            globalRadius = requiredRadius;
            updateAllNodeRadii();
        }

        double x, y;
        boolean validPosition = false;
        int attempts = 0;
        double margin = globalRadius + 20;

        do {
            x = margin + random.nextDouble() * (graphPane.getPrefWidth() - 2 * margin);
            y = margin + random.nextDouble() * (graphPane.getPrefHeight() - 2 * margin);
            validPosition = isValidNodePosition(x, y);
            attempts++;
        } while (!validPosition && attempts < 200);

        if (!validPosition) {
            double[] gridPosition = findGridPosition();
            x = gridPosition[0];
            y = gridPosition[1];
        }
        
        NodeView nodeView = new NodeView(name, x, y, globalRadius);
        nodeView.addToPane(graphPane);
        nodes.put(name, nodeView);
        
        System.out.println("Created node: " + name);
    }
    
    private boolean isValidNodePosition(double x, double y) {
        for (NodeView existingNode : nodes.values()) {
            double distance = Math.sqrt(Math.pow(x - existingNode.x, 2) + Math.pow(y - existingNode.y, 2));
            if (distance < MIN_NODE_DISTANCE) {
                return false;
            }
        }
        
        for (EdgeView edge : edges) {
            double distanceToEdge = distanceFromPointToLine(x, y, 
                edge.line.getStartX(), edge.line.getStartY(),
                edge.line.getEndX(), edge.line.getEndY());
            if (distanceToEdge < globalRadius + 10) {
                return false;
            }
        }
        
        return true;
    }
    
    private double distanceFromPointToLine(double px, double py, double x1, double y1, double x2, double y2) {
        double A = px - x1;
        double B = py - y1;
        double C = x2 - x1;
        double D = y2 - y1;
        
        double dot = A * C + B * D;
        double lenSq = C * C + D * D;
        
        if (lenSq == 0) {
            return Math.sqrt(A * A + B * B);
        }
        
        double param = dot / lenSq;
        
        double xx, yy;
        if (param < 0) {
            xx = x1;
            yy = y1;
        } else if (param > 1) {
            xx = x2;
            yy = y2;
        } else {
            xx = x1 + param * C;
            yy = y1 + param * D;
        }
        
        double dx = px - xx;
        double dy = py - yy;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    private double[] findGridPosition() {
        double margin = globalRadius + 20;
        double spacing = MIN_NODE_DISTANCE;
        int cols = (int)((graphPane.getPrefWidth() - 2 * margin) / spacing);
        int rows = (int)((graphPane.getPrefHeight() - 2 * margin) / spacing);
        
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                double x = margin + col * spacing;
                double y = margin + row * spacing;
                
                if (isValidNodePosition(x, y)) {
                    return new double[]{x, y};
                }
            }
        }

        double x = margin + random.nextDouble() * (graphPane.getPrefWidth() - 2 * margin);
        double y = margin + random.nextDouble() * (graphPane.getPrefHeight() - 2 * margin);
        return new double[]{x, y};
    }
    
    private void deleteNode(String name) {
        NodeView nodeView = nodes.get(name);
        if (nodeView == null) {
            System.out.println("Node '" + name + "' does not exist!");
            return;
        }
        
        edges.removeIf(edge -> {
            if (edge.connectsNode(name)) {
                edge.removeFromPane(graphPane);
                return true;
            }
            return false;
        });
        
        nodeView.removeFromPane(graphPane);
        nodes.remove(name);
        
        System.out.println("Deleted node: " + name);
    }
    
    private void createEdge(String node1Name, String node2Name) {
        if (!nodes.containsKey(node1Name)) {
            System.out.println("Node '" + node1Name + "' does not exist!");
            return;
        }
        
        if (!nodes.containsKey(node2Name)) {
            System.out.println("Node '" + node2Name + "' does not exist!");
            return;
        }
        
        if (node1Name.equals(node2Name)) {
            System.out.println("Cannot create edge from node to itself!");
            return;
        }
        
        for (EdgeView edge : edges) {
            if (edge.connects(node1Name, node2Name)) {
                System.out.println("Edge '" + node1Name + " - " + node2Name + "' already exists!");
                return;
            }
        }
        
        NodeView n1 = nodes.get(node1Name);
        NodeView n2 = nodes.get(node2Name);
        
        EdgeView edgeView = new EdgeView(node1Name, node2Name, n1.x, n1.y, n2.x, n2.y);
        edgeView.addToPane(graphPane);
        edges.add(edgeView);

        for (NodeView node : nodes.values()) {
            node.circle.toFront();
            node.label.toFront();
        }
        
        System.out.println("Created edge: " + node1Name + " - " + node2Name);
    }
    
    private void deleteEdge(String node1Name, String node2Name) {
        EdgeView toRemove = null;
        for (EdgeView edge : edges) {
            if (edge.connects(node1Name, node2Name)) {
                toRemove = edge;
                break;
            }
        }
        
        if (toRemove == null) {
            System.out.println("Edge '" + node1Name + " - " + node2Name + "' does not exist!");
            return;
        }
        
        toRemove.removeFromPane(graphPane);
        edges.remove(toRemove);
        
        System.out.println("Deleted edge: " + node1Name + " - " + node2Name);
    }
    
    private void updateAllNodeRadii() {
        for (NodeView node : nodes.values()) {
            node.updateRadius(globalRadius);
        }
    }
    
    private void initializePresetGraph() {
        String[] presetNodes = {"Ipoh", "Kampar", "Batu Gajah", "Kuala Lumpur", "Seremban"};
        double maxRequiredRadius = 25; 
        
        for (String nodeName : presetNodes) {
            Text tempText = new Text(nodeName);
            double textWidth = tempText.getBoundsInLocal().getWidth();
            double requiredRadius = Math.max(25, textWidth / 2 + 10);
            maxRequiredRadius = Math.max(maxRequiredRadius, requiredRadius);
        }
        
        globalRadius = maxRequiredRadius;

        double[][] positions = {
            {450, 500}, 
            {450, 300}, 
            {200, 150}, 
            {700, 150}, 
            {200, 400}  
        };
        
        for (int i = 0; i < presetNodes.length; i++) {
            String name = presetNodes[i];
            double x = positions[i][0];
            double y = positions[i][1];
            
            NodeView nodeView = new NodeView(name, x, y, globalRadius);
            nodeView.addToPane(graphPane);
            nodes.put(name, nodeView);
        }
        
        createPresetEdge("Ipoh", "Kampar");
        createPresetEdge("Ipoh", "Seremban");
        createPresetEdge("Batu Gajah", "Kuala Lumpur");
        createPresetEdge("Batu Gajah", "Ipoh");
        
        System.out.println("Initialized preset graph with " + presetNodes.length + " nodes and 4 edges");
    }
    
    private void createPresetEdge(String node1Name, String node2Name) {
        NodeView n1 = nodes.get(node1Name);
        NodeView n2 = nodes.get(node2Name);
        
        EdgeView edgeView = new EdgeView(node1Name, node2Name, n1.x, n1.y, n2.x, n2.y);
        edgeView.addToPane(graphPane);
        edges.add(edgeView);
        
        for (NodeView node : nodes.values()) {
            node.circle.toFront();
            node.label.toFront();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}