package agh.model.util;

import agh.model.Vector2d;

import java.util.Iterator;
import java.util.List;

public class RandomPositionGenerator implements Iterable<Vector2d> {
    private final List<Vector2d> allPositions;
    private final int itemNum;
    private final boolean unique;

    public RandomPositionGenerator(List<Vector2d> allPositions, int itemNum, boolean unique) {
        this.itemNum = itemNum;
        this.unique = unique;
        this.allPositions = allPositions;
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

