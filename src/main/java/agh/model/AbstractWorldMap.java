package agh.model;

import agh.model.animal.Animal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class AbstractWorldMap {
    private final HashMap<Vector2d, Animal> animals = new HashMap<>();
    private final HashMap<Vector2d, Grass> grasses = new HashMap<>();
    private final Boundary boundary;
    private final List<MapChangeListener> observers = new ArrayList<>();
    private int emptySquares;

    public AbstractWorldMap(int width, int height) {
        this.boundary = new Boundary(new Vector2d(0, 0), new Vector2d(width - 1, height - 1));
        this.emptySquares = width * height;
    }

    // to do - jak wolne miejsce to trza odjac emptySquare--
    public void placeAnimal(Animal animal) {
        if (!isAnimalAt(animal.getPosition())) {
            emptySquares--;
        }
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

    public boolean inBounds(Vector2d position) {
        return position.follows(boundary.lowerLeft()) && position.precedes(boundary.upperRight());
    }

    public Vector2d wrapPosition(Vector2d position) {
        int x = position.getX();
        int y = position.getY();

        int minY = boundary.lowerLeft().getY();
        int maxY = boundary.upperRight().getY();
        int width = getWidth();

        if (y < minY) y = minY;
        if (y > maxY) y = maxY;

        if (x < 0) {
            x = width - 1;
        } else if (x >= width) {
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
        return new Boundary(boundary.lowerLeft(), boundary.upperRight());
    }

    public void addObserver(MapChangeListener observer) {
        observers.add(observer);
    }

    public void removeObserver(MapChangeListener observer) {
        observers.remove(observer);
    }

    private void mapChanged(String message) {
        for (MapChangeListener observer : observers) {
            observer.mapChanged(this, message);
        }
    }

}
