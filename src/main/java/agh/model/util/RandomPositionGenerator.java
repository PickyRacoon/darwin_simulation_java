package agh.model.util;

import agh.model.Boundary;
import agh.model.Vector2d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RandomPositionGenerator implements Iterable<Vector2d> {
    private final List<Vector2d> allPositions;
    private final int itemNum;
    private final boolean unique;

    public RandomPositionGenerator(Boundary boundaries, int itemNum, boolean unique) {
        this.itemNum = itemNum;
        this.unique = unique;
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
        if (this.unique) {
            return new UniqueRandomPositionIterator(allPositions, itemNum);
        } else {
            return new RandomPositionIterator(allPositions, itemNum);
        }
    }
}

