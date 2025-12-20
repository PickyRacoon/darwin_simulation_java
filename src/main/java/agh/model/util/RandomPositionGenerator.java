package agh.model.util;

import agh.model.Boundary;
import agh.model.Vector2d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RandomPositionGenerator implements Iterable<Vector2d> {
    private final List<Vector2d> allPositions;
    private final int grassNum;

    public RandomPositionGenerator(Boundary boundaries, int grassNum) {
        this.grassNum = grassNum;
        Vector2d lowerLeft = boundaries.lowerLeft();
        Vector2d upperRight = boundaries.upperRight();
        this.allPositions = new ArrayList<>();
        for (int x = lowerLeft.getX(); x < upperRight.getX(); x++) {
            for (int y = lowerLeft.getY(); y < upperRight.getY(); y++) {
                allPositions.add(new Vector2d(x, y));
            }
        }
    }

    @Override
    public Iterator<Vector2d> iterator() {
        return new RandomPositionIterator(allPositions, grassNum);
    }
}

