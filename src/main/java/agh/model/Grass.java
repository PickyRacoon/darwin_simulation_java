package agh.model;

public class Grass implements WorldElement {
    private final Vector2d position;
    private int numMeals = 1;

    public Grass(Vector2d position) {
        this.position = position;
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

