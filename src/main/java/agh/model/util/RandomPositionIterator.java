package agh.model.util;

import agh.model.Vector2d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class RandomPositionIterator implements Iterator<Vector2d> {
    private final List<Vector2d> positions;
    private final int grassNum;
    private int currentPosition;
    private final Random rng;

    public RandomPositionIterator(List<Vector2d> allPositions, int grassNum) {
        this.grassNum = grassNum;
        this.currentPosition = 0;
        this.rng = new Random();
        this.positions = new ArrayList<>(allPositions);
    }

    @Override
    public boolean hasNext() {
        return currentPosition < grassNum;
    }

    @Override
    public Vector2d next() {
        int remainingCount = positions.size() - currentPosition;
        int randomOffset = rng.nextInt(remainingCount);
        int swapIndex = currentPosition + randomOffset;

        Vector2d randomPosition = positions.get(swapIndex);
        Vector2d temp = positions.get(currentPosition);
        positions.set(currentPosition, randomPosition);
        positions.set(swapIndex, temp);

        currentPosition++;

        return randomPosition;
    }
}

