package agh.model.grass;

import agh.model.Vector2d;

public class BigGrass extends Grass {
    public BigGrass(Vector2d position, int numMeals) {
        super(position);
        this.numMeals = numMeals;
    }

    @Override
    public String toString() {
        return String.valueOf(numMeals);
    }
}
