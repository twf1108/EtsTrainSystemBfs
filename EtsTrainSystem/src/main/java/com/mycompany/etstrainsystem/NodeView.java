package com.mycompany.etstrainsystem;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class NodeView {
    public String name;
    public Circle circle;
    public Text label;
    public double x, y;
    
    public NodeView(String name, double x, double y, double radius) {
        this.name = name;
        this.x = x;
        this.y = y;

        label = new Text(name);
        label.setFill(Color.BLACK);

        circle = new Circle(x, y, radius);
        circle.setFill(Color.LIGHTBLUE);
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(2);

        double textWidth = label.getBoundsInLocal().getWidth();
        label.setX(x - textWidth / 2);
        label.setY(y + 5);
    }

    public void updateRadius(double newRadius) {
        circle.setRadius(newRadius);
    }
    
    public void addToPane(Pane pane) {
        pane.getChildren().addAll(circle, label);
    }
    
    public void removeFromPane(Pane pane) {
        pane.getChildren().removeAll(circle, label);
    }
    
    public void updatePosition(double newX, double newY) {
        this.x = newX;
        this.y = newY;
        circle.setCenterX(newX);
        circle.setCenterY(newY);

        double textWidth = label.getBoundsInLocal().getWidth();
        label.setX(newX - textWidth / 2);
        label.setY(newY + 5);
    }
}
