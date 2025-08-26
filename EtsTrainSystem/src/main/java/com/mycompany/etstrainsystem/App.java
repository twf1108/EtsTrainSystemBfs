package com.mycompany.etstrainsystem;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.*;

public class App extends Application {
    
    private Pane graphPane;
    private Map<String, NodeView> nodes = new HashMap<>();
    private List<EdgeView> edges = new ArrayList<>();
    private Random random = new Random();
    private TextArea reachabilityResultArea;
    private TextArea allRoutesArea;
    private double globalRadius = 25; 
    private static final double MIN_NODE_DISTANCE = 80; 
    
    @Override
    public void start(Stage primaryStage) {
        
        BorderPane root = new BorderPane();
        
        
        graphPane = new Pane();
        graphPane.setStyle("-fx-background-color: white; -fx-border-color: lightgray;");
        graphPane.setPrefSize(900, 600);
        
        
        VBox controlPanel = createControlPanel();
        
        root.setCenter(graphPane);
        root.setRight(controlPanel);
        
        initializePresetGraph();
        
        Scene scene = new Scene(root, 1200, 650);
        primaryStage.setTitle("Graph Node Editor - Reachability Analysis");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private VBox createControlPanel() {
        VBox controlPanel = new VBox(10);
        controlPanel.setPadding(new Insets(10));
        controlPanel.setPrefWidth(280);
        controlPanel.setStyle("-fx-background-color: #f0f0f0;");

        Label nodeLabel = new Label("Node Operations:");
        nodeLabel.setStyle("-fx-font-weight: bold;");
        
        TextField nodeNameField = new TextField();
        nodeNameField.setPromptText("Enter node name");
        
        Button addNodeBtn = new Button("Add Node");
        addNodeBtn.setPrefWidth(180);
        
        Button deleteNodeBtn = new Button("Delete Node");
        deleteNodeBtn.setPrefWidth(180);

        Label edgeLabel = new Label("Edge Operations:");
        edgeLabel.setStyle("-fx-font-weight: bold;");
        
        TextField node1Field = new TextField();
        node1Field.setPromptText("Node 1 name");
        
        TextField node2Field = new TextField();
        node2Field.setPromptText("Node 2 name");
        
        Button addEdgeBtn = new Button("Add Edge");
        addEdgeBtn.setPrefWidth(180);
        
        Button deleteEdgeBtn = new Button("Delete Edge");
        deleteEdgeBtn.setPrefWidth(180);

        Label reachabilityLabel = new Label("Reachability Analysis:");
        reachabilityLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #0066cc; -fx-font-size: 14px;");
        
        TextField initialStateField = new TextField();
        initialStateField.setPromptText("Enter initial state (start node)");
        
        Button findAllReachableBtn = new Button("Find All Reachable");
        findAllReachableBtn.setPrefWidth(180);
        findAllReachableBtn.setStyle("-fx-background-color: #0066cc; -fx-text-fill: white;");
        
        reachabilityResultArea = new TextArea();
        reachabilityResultArea.setEditable(false);
        reachabilityResultArea.setPrefHeight(100);
        reachabilityResultArea.setPromptText("Reachability summary will be displayed here");
        
        Label routesLabel = new Label("All Routes Detail:");
        routesLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #cc6600;");
        
        allRoutesArea = new TextArea();
        allRoutesArea.setEditable(false);
        allRoutesArea.setPrefHeight(150);
        allRoutesArea.setPromptText("Detailed routes will be displayed here");
        allRoutesArea.setStyle("-fx-font-family: 'Courier New', monospace;");
        
        Button clearHighlightsBtn = new Button("Clear Highlights");
        clearHighlightsBtn.setPrefWidth(180);
        clearHighlightsBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        
        Button clearAllBtn = new Button("Clear All");
        clearAllBtn.setPrefWidth(180);
        clearAllBtn.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white;");
        
        Label nodesListLabel = new Label("Current Graph:");
        nodesListLabel.setStyle("-fx-font-weight: bold;");
        
        TextArea nodesListArea = new TextArea();
        nodesListArea.setEditable(false);
        nodesListArea.setPrefHeight(120);
        nodesListArea.setPromptText("Graph structure will be displayed here");
        
        addNodeBtn.setOnAction(e -> {
            String name = nodeNameField.getText().trim();
            if (!name.isEmpty()) {
                createNode(name);
                nodeNameField.clear();
                updateNodesList(nodesListArea);
            } else {
                showAlert("Please enter node name");
            }
        });
        
        deleteNodeBtn.setOnAction(e -> {
            String name = nodeNameField.getText().trim();
            if (!name.isEmpty()) {
                deleteNode(name);
                nodeNameField.clear();
                updateNodesList(nodesListArea);
            } else {
                showAlert("Please enter node name to delete");
            }
        });
        
        addEdgeBtn.setOnAction(e -> {
            String node1 = node1Field.getText().trim();
            String node2 = node2Field.getText().trim();
            if (!node1.isEmpty() && !node2.isEmpty()) {
                createEdge(node1, node2);
                node1Field.clear();
                node2Field.clear();
                updateNodesList(nodesListArea);
            } else {
                showAlert("Please enter two node names");
            }
        });
        
        deleteEdgeBtn.setOnAction(e -> {
            String node1 = node1Field.getText().trim();
            String node2 = node2Field.getText().trim();
            if (!node1.isEmpty() && !node2.isEmpty()) {
                deleteEdge(node1, node2);
                node1Field.clear();
                node2Field.clear();
                updateNodesList(nodesListArea);
            } else {
                showAlert("Please enter two node names");
            }
        });
        
        findAllReachableBtn.setOnAction(e -> {
            String initialState = initialStateField.getText().trim();
            if (!initialState.isEmpty()) {
                findAllReachableNodes(initialState);
            } else {
                showAlert("Please enter initial state");
            }
        });
        
        clearHighlightsBtn.setOnAction(e -> {
            clearPathHighlight();
            reachabilityResultArea.clear();
            allRoutesArea.clear();
        });
        
        clearAllBtn.setOnAction(e -> {
            clearAll();
            updateNodesList(nodesListArea);
            reachabilityResultArea.clear();
            allRoutesArea.clear();
        });
        
        updateNodesList(nodesListArea);
        
        controlPanel.getChildren().addAll(
            nodeLabel,
            nodeNameField,
            addNodeBtn,
            deleteNodeBtn,
            new Separator(),
            edgeLabel,
            node1Field,
            node2Field,
            addEdgeBtn,
            deleteEdgeBtn,
            new Separator(),
            reachabilityLabel,
            initialStateField,
            findAllReachableBtn,
            reachabilityResultArea,
            new Separator(),
            routesLabel,
            allRoutesArea,
            new Separator(),
            clearHighlightsBtn,
            clearAllBtn,
            new Separator(),
            nodesListLabel,
            nodesListArea
        );
        
        return controlPanel;
    }
    
    private void findAllReachableNodes(String initialState) {
        if (!nodes.containsKey(initialState)) {
            showAlert("Initial state '" + initialState + "' does not exist!");
            return;
        }
        
        clearPathHighlight();
        
        Map<String, List<String>> allPaths = findAllReachablePathsBFS(initialState);
        
        highlightReachableNodes(allPaths.keySet(), initialState);

        displayReachabilityResults(initialState, allPaths);
        displayAllRoutes(initialState, allPaths);
    }
    
    private Map<String, List<String>> findAllReachablePathsBFS(String start) {
        Map<String, List<String>> allPaths = new HashMap<>();
        Queue<String> queue = new LinkedList<>();
        Map<String, String> parent = new HashMap<>();
        Set<String> visited = new HashSet<>();
        
        queue.offer(start);
        visited.add(start);
        parent.put(start, null);
        allPaths.put(start, Arrays.asList(start));
        
        while (!queue.isEmpty()) {
            String current = queue.poll();
            List<String> neighbors = getNeighbors(current);
            
            for (String neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parent.put(neighbor, current);
                    queue.offer(neighbor);
                    
                    List<String> pathToNeighbor = reconstructPath(parent, start, neighbor);
                    allPaths.put(neighbor, pathToNeighbor);
                }
            }
        }
        
        return allPaths;
    }
    
    private void highlightReachableNodes(Set<String> reachableNodes, String initialState) {
        NodeView initialNode = nodes.get(initialState);
        if (initialNode != null) {
            initialNode.circle.setFill(Color.ORANGE);
            initialNode.circle.setStroke(Color.DARKORANGE);
            initialNode.circle.setStrokeWidth(4);
        }
        
        for (String nodeName : reachableNodes) {
            if (!nodeName.equals(initialState)) {
                NodeView node = nodes.get(nodeName);
                if (node != null) {
                    node.circle.setFill(Color.LIGHTGREEN);
                    node.circle.setStroke(Color.DARKGREEN);
                    node.circle.setStrokeWidth(3);
                }
            }
        }
        
        for (String nodeName : nodes.keySet()) {
            if (!reachableNodes.contains(nodeName)) {
                NodeView node = nodes.get(nodeName);
                if (node != null) {
                    node.circle.setFill(Color.LIGHTCORAL);
                    node.circle.setStroke(Color.DARKRED);
                    node.circle.setStrokeWidth(2);
                }
            }
        }
    }
    
    private void displayReachabilityResults(String initialState, Map<String, List<String>> allPaths) {
        StringBuilder result = new StringBuilder();
        result.append("FROM: ").append(initialState).append("\n");
        
        Map<String, List<String>> destinationPaths = new HashMap<>(allPaths);
        destinationPaths.remove(initialState);
        
        if (destinationPaths.isEmpty()) {
            result.append("❌ No reachable destinations\n");
            result.append("This node is isolated.");
        } else {
            result.append("✅ Reachable destinations: ").append(destinationPaths.size()).append("\n\n");
            
            List<String> sortedDestinations = new ArrayList<>(destinationPaths.keySet());
            Collections.sort(sortedDestinations);
            
            for (String destination : sortedDestinations) {
                List<String> path = destinationPaths.get(destination);
                result.append("🎯 ").append(destination);
                result.append(" (").append(path.size() - 1).append(" steps)\n");
            }
        }
        
        Set<String> unreachableNodes = new HashSet<>(nodes.keySet());
        unreachableNodes.removeAll(allPaths.keySet());
        if (!unreachableNodes.isEmpty()) {
            result.append("\n❌ Unreachable: ");
            result.append(String.join(", ", unreachableNodes));
        }
        
        reachabilityResultArea.setText(result.toString());
    }
    
    private void displayAllRoutes(String initialState, Map<String, List<String>> allPaths) {
        StringBuilder routes = new StringBuilder();
        routes.append("DETAILED ROUTES FROM: ").append(initialState).append("\n");
        routes.append("═══════════════════════════════════════════════\n\n");
        
        Map<String, List<String>> destinationPaths = new HashMap<>(allPaths);
        destinationPaths.remove(initialState);
        
        if (destinationPaths.isEmpty()) {
            routes.append("No routes available - node is isolated.\n");
        } else {
            List<String> sortedDestinations = new ArrayList<>(destinationPaths.keySet());
            Collections.sort(sortedDestinations);
            
            int routeNumber = 1;
            for (String destination : sortedDestinations) {
                List<String> path = destinationPaths.get(destination);
                routes.append("Route ").append(routeNumber++).append(": ");
                routes.append(initialState).append(" → ").append(destination).append("\n");
                routes.append("Path:  ").append(String.join(" → ", path)).append("\n");
                routes.append("Steps: ").append(path.size() - 1).append(" edges\n");
                routes.append("─────────────────────────────────────────\n");
            }
            
            routes.append("\nTotal Routes: ").append(destinationPaths.size());
        }
        
        allRoutesArea.setText(routes.toString());

        System.out.println("\n" + routes.toString());
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
    
    private List<String> reconstructPath(Map<String, String> parent, String start, String end) {
        List<String> path = new ArrayList<>();
        String current = end;
        
        while (current != null) {
            path.add(current);
            current = parent.get(current);
        }
        
        Collections.reverse(path);
        return path;
    }
    
    private void clearPathHighlight() {
        for (NodeView node : nodes.values()) {
            node.circle.setFill(Color.LIGHTBLUE);
            node.circle.setStroke(Color.BLACK);
            node.circle.setStrokeWidth(2);
        }

        for (EdgeView edge : edges) {
            edge.line.setStroke(Color.BLACK);
            edge.line.setStrokeWidth(2);
        }
    }
    
    private void updateNodesList(TextArea textArea) {
        StringBuilder sb = new StringBuilder();
        if (nodes.isEmpty()) {
            sb.append("No nodes in graph");
        } else {
            sb.append("Nodes (").append(nodes.size()).append("): ");
            sb.append(String.join(", ", nodes.keySet()));
        }
        
        if (!edges.isEmpty()) {
            sb.append("\n\nEdges (").append(edges.size()).append("):");
            for (EdgeView edge : edges) {
                sb.append("\n  ").append(edge.node1Name).append(" ↔ ").append(edge.node2Name);
            }
        } else {
            sb.append("\n\nNo edges in graph");
        }
        
        textArea.setText(sb.toString());
    }

    private void createNode(String name) {
        if (nodes.containsKey(name)) {
            showAlert("Node '" + name + "' already exists!");
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
        
        System.out.println("Created node: " + name + " at position (" + x + ", " + y + ")");
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
            showAlert("Node '" + name + "' does not exist!");
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
            showAlert("Node '" + node1Name + "' does not exist!");
            return;
        }
        
        if (!nodes.containsKey(node2Name)) {
            showAlert("Node '" + node2Name + "' does not exist!");
            return;
        }
        
        if (node1Name.equals(node2Name)) {
            showAlert("Cannot create edge from node to itself!");
            return;
        }
        
        for (EdgeView edge : edges) {
            if (edge.connects(node1Name, node2Name)) {
                showAlert("Edge '" + node1Name + " - " + node2Name + "' already exists!");
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
            showAlert("Edge '" + node1Name + " - " + node2Name + "' does not exist!");
            return;
        }
        
        toRemove.removeFromPane(graphPane);
        edges.remove(toRemove);
        
        System.out.println("Deleted edge: " + node1Name + " - " + node2Name);
    }
    
    private void clearAll() {
        for (EdgeView edge : edges) {
            edge.removeFromPane(graphPane);
        }
        edges.clear();
        
        for (NodeView node : nodes.values()) {
            node.removeFromPane(graphPane);
        }
        nodes.clear();
        
        globalRadius = 25;
        
        System.out.println("Cleared all nodes and edges");
    }
    
    private void updateAllNodeRadii() {
        for (NodeView node : nodes.values()) {
            node.updateRadius(globalRadius);
        }
        System.out.println("Updated all node radii to: " + globalRadius);
    }
    
    private void initializePresetGraph() {
        String[] presetNodes = {"Johor", "Kuala Lumpur", "Kedah", "Pahang", "Penang"};
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
        
        createPresetEdge("Johor", "Kuala Lumpur");
        createPresetEdge("Johor", "Penang");
        createPresetEdge("Kedah", "Pahang");
        createPresetEdge("Kedah", "Johor");
        
        System.out.println("Initialized preset graph with nodes and edges (global radius: " + globalRadius + ")");
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
    
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}