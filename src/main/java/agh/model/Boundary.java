package agh.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
