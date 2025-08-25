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
    private TextArea pathResultArea;
    private double globalRadius = 25; 
    
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
        
        Scene scene = new Scene(root, 1150, 650);
        primaryStage.setTitle("Graph Node Editor");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private VBox createControlPanel() {
        VBox controlPanel = new VBox(10);
        controlPanel.setPadding(new Insets(10));
        controlPanel.setPrefWidth(200);
        controlPanel.setStyle("-fx-background-color: #f0f0f0;");
        
        
        Label nodeLabel = new Label("Node Operations:");
        nodeLabel.setStyle("-fx-font-weight: bold;");
        
        TextField nodeNameField = new TextField();
        nodeNameField.setPromptText("Enter node name");
        
        Button addNodeBtn = new Button("Add Node");
        addNodeBtn.setPrefWidth(150);
        addNodeBtn.setOnAction(e -> {
            String name = nodeNameField.getText().trim();
            if (!name.isEmpty()) {
                createNode(name);
                nodeNameField.clear();
            } else {
                showAlert("Please enter node name");
            }
        });
        
        Button deleteNodeBtn = new Button("Delete Node");
        deleteNodeBtn.setPrefWidth(150);
        deleteNodeBtn.setOnAction(e -> {
            String name = nodeNameField.getText().trim();
            if (!name.isEmpty()) {
                deleteNode(name);
                nodeNameField.clear();
            } else {
                showAlert("Please enter node name to delete");
            }
        });
        
        Label edgeLabel = new Label("Edge Operations:");
        edgeLabel.setStyle("-fx-font-weight: bold;");
        
        TextField node1Field = new TextField();
        node1Field.setPromptText("Node 1 name");
        
        TextField node2Field = new TextField();
        node2Field.setPromptText("Node 2 name");
        
        Button addEdgeBtn = new Button("Add Edge");
        addEdgeBtn.setPrefWidth(150);
        addEdgeBtn.setOnAction(e -> {
            String node1 = node1Field.getText().trim();
            String node2 = node2Field.getText().trim();
            if (!node1.isEmpty() && !node2.isEmpty()) {
                createEdge(node1, node2);
                node1Field.clear();
                node2Field.clear();
            } else {
                showAlert("Please enter two node names");
            }
        });
        
        Button deleteEdgeBtn = new Button("Delete Edge");
        deleteEdgeBtn.setPrefWidth(150);
        deleteEdgeBtn.setOnAction(e -> {
            String node1 = node1Field.getText().trim();
            String node2 = node2Field.getText().trim();
            if (!node1.isEmpty() && !node2.isEmpty()) {
                deleteEdge(node1, node2);
                node1Field.clear();
                node2Field.clear();
            } else {
                showAlert("Please enter two node names");
            }
        });
        
        Label pathLabel = new Label("BFS Path Finding:");
        pathLabel.setStyle("-fx-font-weight: bold;");
        
        TextField startNodeField = new TextField();
        startNodeField.setPromptText("Start node");
        
        TextField endNodeField = new TextField();
        endNodeField.setPromptText("End node");
        
        Button findPathBtn = new Button("Find Path");
        findPathBtn.setPrefWidth(150);
        findPathBtn.setOnAction(e -> {
            String start = startNodeField.getText().trim();
            String end = endNodeField.getText().trim();
            if (!start.isEmpty() && !end.isEmpty()) {
                findAndDisplayPath(start, end);
            } else {
                showAlert("Please enter start and end nodes");
            }
        });
        
        Button clearPathBtn = new Button("Clear Path");
        clearPathBtn.setPrefWidth(150);
        clearPathBtn.setOnAction(e -> clearPathHighlight());
        
        pathResultArea = new TextArea();
        pathResultArea.setEditable(false);
        pathResultArea.setPrefHeight(80);
        pathResultArea.setPromptText("Path result will be displayed here");
        
        Button clearAllBtn = new Button("Clear All");
        clearAllBtn.setPrefWidth(150);
        clearAllBtn.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white;");
        clearAllBtn.setOnAction(e -> clearAll());
        
        Label nodesListLabel = new Label("Current Nodes:");
        nodesListLabel.setStyle("-fx-font-weight: bold;");
        
        TextArea nodesListArea = new TextArea();
        nodesListArea.setEditable(false);
        nodesListArea.setPrefHeight(100);
        nodesListArea.setPromptText("Node list will be displayed here");
        
        updateNodesList(nodesListArea);
        
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
        
        clearPathBtn.setOnAction(e -> {
            clearPathHighlight();
            pathResultArea.clear();
        });
        
        clearAllBtn.setOnAction(e -> {
            clearAll();
            updateNodesList(nodesListArea);
            pathResultArea.clear();
        });
        
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
            pathLabel,
            startNodeField,
            endNodeField,
            findPathBtn,
            clearPathBtn,
            pathResultArea,
            new Separator(),
            clearAllBtn,
            new Separator(),
            nodesListLabel,
            nodesListArea
        );
        
        return controlPanel;
    }
    
    private void findAndDisplayPath(String startNode, String endNode) {
        if (!nodes.containsKey(startNode)) {
            showAlert("Start node '" + startNode + "' does not exist!");
            return;
        }
        
        if (!nodes.containsKey(endNode)) {
            showAlert("End node '" + endNode + "' does not exist!");
            return;
        }
        
        List<String> path = findPathBFS(startNode, endNode);
        
        clearPathHighlight();
        
        if (path == null || path.isEmpty()) {
            showAlert("No path found from '" + startNode + "' to '" + endNode + "'!");
            pathResultArea.setText("No path found from " + startNode + " to " + endNode);
            return;
        }
        
        highlightPath(path);
        
        String pathStr = String.join(" -> ", path);
        pathResultArea.setText("Path found: " + pathStr + "\nPath length: " + (path.size() - 1) + " edges");
        
        System.out.println("BFS Path from " + startNode + " to " + endNode + ": " + pathStr);
    }
    
    private List<String> findPathBFS(String start, String end) {
        if (start.equals(end)) {
            return Arrays.asList(start);
        }
        
        Queue<String> queue = new LinkedList<>();
        Map<String, String> parent = new HashMap<>();
        Set<String> visited = new HashSet<>();
        
        queue.offer(start);
        visited.add(start);
        parent.put(start, null);
        
        while (!queue.isEmpty()) {
            String current = queue.poll();
            
            List<String> neighbors = getNeighbors(current);
            
            for (String neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parent.put(neighbor, current);
                    queue.offer(neighbor);
                    
                    if (neighbor.equals(end)) {
                        return reconstructPath(parent, start, end);
                    }
                }
            }
        }
        
        return null; 
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
    
    private void highlightPath(List<String> path) {
        for (String nodeName : path) {
            NodeView node = nodes.get(nodeName);
            if (node != null) {
                node.circle.setFill(Color.LIGHTGREEN);
                node.circle.setStroke(Color.DARKGREEN);
                node.circle.setStrokeWidth(3);
            }
        }

        for (int i = 0; i < path.size() - 1; i++) {
            String node1 = path.get(i);
            String node2 = path.get(i + 1);
            
            for (EdgeView edge : edges) {
                if (edge.connects(node1, node2)) {
                    edge.line.setStroke(Color.RED);
                    edge.line.setStrokeWidth(4);
                    break;
                }
            }
        }
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
            sb.append("No nodes");
        } else {
            sb.append("Nodes: ").append(String.join(", ", nodes.keySet()));
        }
        
        if (!edges.isEmpty()) {
            sb.append("\n\nEdges: ");
            for (EdgeView edge : edges) {
                sb.append("\n").append(edge.node1Name).append(" - ").append(edge.node2Name);
            }
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
            redistributeNodes();
        }
        
        double x, y;
        boolean validPosition = false;
        int attempts = 0;
        double margin = globalRadius + 15; 
        
        do {
            x = margin + random.nextDouble() * (graphPane.getPrefWidth() - 2 * margin);
            y = margin + random.nextDouble() * (graphPane.getPrefHeight() - 2 * margin);
            validPosition = isValidPosition(x, y, globalRadius * 2.2); 
            attempts++;
        } while (!validPosition && attempts < 100); 
        
        NodeView nodeView = new NodeView(name, x, y, globalRadius);
        nodeView.addToPane(graphPane);
        nodes.put(name, nodeView);
        
        System.out.println("Created node: " + name + " at position (" + x + ", " + y + ")");
    }
    
    private boolean isValidPosition(double x, double y, double minDistance) {
        for (NodeView existingNode : nodes.values()) {
            double distance = Math.sqrt(Math.pow(x - existingNode.x, 2) + Math.pow(y - existingNode.y, 2));
            if (distance < minDistance) {
                return false;
            }
        }
        return true;
    }
    
    private void redistributeNodes() {
        if (nodes.isEmpty()) return;
        
        List<NodeView> nodeList = new ArrayList<>(nodes.values());
        double margin = globalRadius + 15;
        
        int cols = (int) Math.ceil(Math.sqrt(nodeList.size()));
        double spacingX = (graphPane.getPrefWidth() - 2 * margin) / Math.max(1, cols - 1);
        double spacingY = (graphPane.getPrefHeight() - 2 * margin) / Math.max(1, cols - 1);
        
        for (int i = 0; i < nodeList.size(); i++) {
            NodeView node = nodeList.get(i);
            
            if (isPresetNode(node.name)) continue;
            
            int row = i / cols;
            int col = i % cols;
            
            double newX = margin + col * spacingX;
            double newY = margin + row * spacingY;
            
            node.updatePosition(newX, newY);
        }

        updateAllEdgePositions();
    }
    
    private boolean isPresetNode(String nodeName) {
        String[] presetNodes = {"Johor", "Kuala Lumpur", "Kedah", "Pahang", "Penang"};
        for (String preset : presetNodes) {
            if (preset.equals(nodeName)) {
                return true;
            }
        }
        return false;
    }
    
    private void updateAllEdgePositions() {
        for (EdgeView edge : edges) {
            NodeView n1 = nodes.get(edge.node1Name);
            NodeView n2 = nodes.get(edge.node2Name);
            if (n1 != null && n2 != null) {
                edge.updatePosition(n1.x, n1.y, n2.x, n2.y);
            }
        }
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
            {200, 350}  
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