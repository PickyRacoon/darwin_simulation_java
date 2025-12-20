package agh.model.util;

import agh.model.Vector2d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class RandomPositionIterator implements Iterator<Vector2d> {
    private final List<Vector2d> positions;
    private final int grassNum;
    private int alreadyGenerated;
    private final Random rng;

    public RandomPositionIterator(List<Vector2d> allPositions, int grassNum) {
        this.grassNum = grassNum;
        this.alreadyGenerated = 0;
        this.rng = new Random();
        this.positions = new ArrayList<>(allPositions);
    }

    @Override
    public boolean hasNext() {
        return alreadyGenerated < grassNum;
    }

    @Override
    public Vector2d next() {
        alreadyGenerated++;
        return positions.get(rng.nextInt(positions.size() - 1));
    }
}

