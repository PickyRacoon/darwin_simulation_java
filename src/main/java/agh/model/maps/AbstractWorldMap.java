package agh.model.maps;

import agh.model.*;
import agh.model.animal.Animal;
import agh.model.grass.Grass;
import agh.model.grass.GrassGenerator;
import agh.model.util.MultiValueHashMap;
import agh.model.util.RandomPositionGenerator;

import java.util.*;

public abstract class AbstractWorldMap {
    protected final GrassGenerator grassGenerator = new GrassGenerator();
    private final MultiValueHashMap<Vector2d, Animal> animals = new MultiValueHashMap<>();
    private final HashMap<Vector2d, Grass> grasses = new HashMap<>();
    protected final Boundary mapBoundary;
    private final List<MapChangeListener> observers = new ArrayList<>();
    protected final int initNumGrass;
    private final int total;

    public AbstractWorldMap(int width, int height, int numGrass) {
        this.mapBoundary = new Boundary(new Vector2d(0, 0), new Vector2d(width - 1, height - 1));
        this.total = width * height;
        this.initNumGrass = numGrass;
    }

    public void generateGrass(int numGrass) {
        List<Vector2d> allJunglePositions = this.getPositionsWithoutGrass(this.getJunglePositions());
        List<Vector2d> allNotJunglePositions = this.getPositionsWithoutGrass(mapBoundary.getAllPositions());
        allNotJunglePositions.removeAll(allJunglePositions);
        RandomPositionGenerator randomJunglePositions = grassGenerator.generateJungle(allJunglePositions, numGrass);
        for (Vector2d position: randomJunglePositions) {
            this.placeGrass(createJungleGrass(position));
            numGrass--;
        }
        RandomPositionGenerator randomNotJunglePositions = grassGenerator.generateGrass(allNotJunglePositions, numGrass);
        for (Vector2d position: randomNotJunglePositions) {
            this.placeGrass(new Grass(position));
        }
    }

    protected Grass createJungleGrass(Vector2d position) {
        return new Grass(position);
    }

    protected abstract List<Vector2d> getJunglePositions();

    public void placeAnimal(Animal animal) {
        synchronized (animals) {
            Vector2d position = animal.getPosition();
            animals.put(position, animal);
        }
        this.mapChanged("%s was placed at %s".formatted(animal, animal.getPosition()));
    }

    public void removeAnimal(Animal animal) {
        synchronized (animals) {
            Vector2d position = animal.getPosition();
            List<Animal> list = animals.get(position);
            if (list != null) {
                list.remove(animal);
                if (list.isEmpty()) {
                    animals.remove(animal.getPosition());
                }
            }
        }

        mapChanged("Animal removed");
    }

    public void placeGrass(Grass grass) {
        synchronized (grasses) {
            Vector2d position = grass.getPosition();
            grasses.put(position, grass);
        }
        mapChanged("Grass was placed at %s".formatted(grass.getPosition()));
    }

    public void removeGrass(Grass grass) {
        synchronized (grasses) {
            Vector2d position = grass.getPosition();
            grasses.remove(position);
        }
        mapChanged("Grass removed");
    }

    public List<Vector2d> getPositionsWithoutGrass(List<Vector2d> allPositions) {
        ArrayList<Vector2d> positions = new ArrayList<>(allPositions);
        positions.removeAll(this.getGrassPositions());
        return positions;
    }

    public Vector2d wrapPosition(Vector2d position) {
        int x = position.getX();
        int y = position.getY();

        int minY = mapBoundary.lowerLeft().getY();
        int maxY = mapBoundary.upperRight().getY();
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

    public int getEmptySquares() {
        Set<Vector2d> occupied = new HashSet<>();
        synchronized (animals) {
            occupied.addAll(animals.keySet());
        }
        synchronized (grasses) {
            occupied.addAll(grasses.keySet());
        }

        return total - occupied.size();
    }

    public List<Animal> getAnimalsAt(Vector2d position) {
        List<Animal> copy;
        synchronized (animals) {
            copy = animals.containsKey(position)
                    ? List.copyOf(animals.get(position))
                    : List.of();
        }
        return copy;
    }

    public List<Animal> getAllAnimals() {
        List<List<Animal>> valuesCopy;
        synchronized (animals) {
            valuesCopy = new ArrayList<>(animals.values());
        }

        return valuesCopy.stream()
                .flatMap(List::stream)
                .toList();
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

    public Grass getGrassAt(Vector2d position) {
        return grasses.get(position);
    }

    public Set<Vector2d> getGrassPositions() {
        return grasses.keySet();
    }

    public Set<Vector2d> getAnimalPositions() {
        return animals.keySet();
    }

    public int getHeight() {
        return mapBoundary.upperRight().getY() + 1;
    }

    public int getWidth() {
        return mapBoundary.upperRight().getX() + 1;
    }

    public Boundary getMapBoundary() {
        return new Boundary(mapBoundary.lowerLeft(), mapBoundary.upperRight());
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
