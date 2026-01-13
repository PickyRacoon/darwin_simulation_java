package agh.model.grass;

import agh.model.Vector2d;
import agh.model.WorldElement;

public class Grass implements WorldElement {
    private final Vector2d position;
    protected int numMeals;

    public Grass(Vector2d position) {
        this.position = position;
        this.numMeals = 1;
    }

    @Override
    public Vector2d getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "*";
    }

    public void eat() {
        numMeals--;
    }

    public boolean isEaten() {
        return numMeals < 1;
    }
}

