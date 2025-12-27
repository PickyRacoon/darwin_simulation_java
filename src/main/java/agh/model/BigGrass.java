package agh.model;

public class BigGrass extends Grass {
    public BigGrass(Vector2d position) {
        super(position);
        this.numMeals = 5;
    }

    @Override
    public String toString() {
        return String.valueOf(numMeals);
    }
}
