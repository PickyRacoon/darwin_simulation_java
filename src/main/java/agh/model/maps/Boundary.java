package agh.model.maps;

import agh.model.Vector2d;

import java.util.ArrayList;
import java.util.List;

public record Boundary(Vector2d lowerLeft, Vector2d upperRight) {
    public List<Vector2d> getAllPositions(){
        Vector2d lowerLeft = this.lowerLeft();
        Vector2d upperRight = this.upperRight();
        List<Vector2d> allPositions = new ArrayList<>();
        for (int x = lowerLeft.getX(); x <= upperRight.getX(); x++) {
            for (int y = lowerLeft.getY(); y <= upperRight.getY(); y++) {
                allPositions.add(new Vector2d(x, y));
            }
        }
        return allPositions;
    }
}
