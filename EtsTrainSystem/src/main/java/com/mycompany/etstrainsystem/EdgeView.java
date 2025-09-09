package com.mycompany.etstrainsystem;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class EdgeView {
    public String node1Name, node2Name;
    public Line line;
    
    public EdgeView(String node1Name, String node2Name, double x1, double y1, double x2, double y2) {
        this.node1Name = node1Name;
        this.node2Name = node2Name;
        
        line = new Line(x1, y1, x2, y2);
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(2);
    }
    
    public void addToPane(Pane pane) {
        pane.getChildren().add(line);
    }
    
    public void removeFromPane(Pane pane) {
        pane.getChildren().remove(line);
    }
    
    public boolean connectsNode(String nodeName) {
        return node1Name.equals(nodeName) || node2Name.equals(nodeName);
    }
    
    public boolean connects(String n1, String n2) {
        return (node1Name.equals(n1) && node2Name.equals(n2)) ||
               (node1Name.equals(n2) && node2Name.equals(n1));
    }
}
