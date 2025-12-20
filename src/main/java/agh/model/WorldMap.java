package agh.model;

import agh.model.animal.Animal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorldMap {
    private final HashMap<Vector2d, Animal> animals = new HashMap<>();
    private final HashMap<Vector2d, Grass> grasses = new HashMap<>();
    private final Boundary boundary;
    private final Boundary jungle;
    private final List<MapChangeListener> observers = new ArrayList<>();
    private int emptySquares;

    public WorldMap(int width, int height) {
        this.boundary = new Boundary(new Vector2d(0, 0), new Vector2d(width - 1, height - 1));
        this.jungle = createJungle();
        this.emptySquares = width * height;
    }

    // to do - jak wolne miejsce to trza odjac emptySquare--
    public void placeAnimal(Animal animal) {
        if (!isAnimalAt(animal.getPosition())) {
            emptySquares--;
        }
        ;
        animals.put(animal.getPosition(), animal);
        this.mapChanged("%s was placed at %s".formatted(animal, animal.getPosition()));
    }

    public void removeAnimal(Animal animal) {
        animals.remove(animal.getPosition());
    }

    public void placeGrass(Grass grass) {
        emptySquares--;
        grasses.put(grass.getPosition(), grass);
    }

    public void removeGrass(Grass grass) {
    }

    private Boundary createJungle() {
        int jungleHeight = (int) (getHeight() * 0.2);
        int jungleStart = (getHeight() - jungleHeight) / 2;
        int jungleEnd = jungleStart + jungleHeight - 1;
        return new Boundary(new Vector2d(0, jungleStart), new Vector2d(getWidth() - 1, jungleEnd));
    }

    public boolean inBounds(Vector2d position) {
        return position.follows(boundary.lowerLeft()) && position.precedes(boundary.upperRight());
    }

    public Vector2d moveWhenOutOfBoundary(Vector2d position) {
        int x = position.getX();
        int y = position.getY();
        int width = getWidth();

        if (y < boundary.lowerLeft().getY() || y > boundary.upperRight().getY()) {
            return null;
        }

        if (x < boundary.lowerLeft().getX()) {
            x = width - 1;
        }

        if (x >= width) {
            x = 0;
        }

        return new Vector2d(x, y);
    }

    public WorldElement objectAt(Vector2d position) {
        if (isAnimalAt(position)) {
            return animals.get(position);
        }
        return grasses.get(position);
    }

    public boolean isOccupied(Vector2d position) {
        return isAnimalAt(position) || isGrassAt(position);
    }

    public boolean isAnimalAt(Vector2d position) {
        return animals.containsKey(position);
    }

    public boolean isGrassAt(Vector2d position) {
        return grasses.containsKey(position);
    }

    public int getHeight() {
        return boundary.upperRight().getY() + 1;
    }

    public int getWidth() {
        return boundary.upperRight().getX() + 1;
    }

    public Boundary getMapBoundary() {
        return boundary;
    }

    public Boundary getJungleBoundary() {
        return jungle;
    }

    public void addObserver(MapChangeListener observer) {
        observers.add(observer);
    }

    public void removeObserver(MapChangeListener observer) {
        observers.remove(observer);
    }

    private void mapChanged(String message) {
        for (MapChangeListener observer : observers) {
            observer.mapChanged(this);
        }
    }

}
