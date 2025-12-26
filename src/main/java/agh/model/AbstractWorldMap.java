package agh.model;

import agh.model.animal.Animal;
import agh.model.util.ConsoleMapVisualizer;
import agh.model.util.MultiValueHashMap;
import agh.model.util.RandomPositionGenerator;

import java.util.*;

public abstract class AbstractWorldMap {
    protected final GrassGenerator grassGenerator = new GrassGenerator();
    private final MultiValueHashMap<Vector2d, Animal> animals = new MultiValueHashMap<>();
    private final HashMap<Vector2d, Grass> grasses = new HashMap<>();
    protected final Boundary mapBoundary;
    private final List<MapChangeListener> observers = new ArrayList<>();
    private final ConsoleMapVisualizer cmv = new ConsoleMapVisualizer(this); // temp dla testów
    protected final int initNumGrass;
    private int emptySquares;

    public AbstractWorldMap(int width, int height, int numGrass) {
        this.mapBoundary = new Boundary(new Vector2d(0, 0), new Vector2d(width - 1, height - 1));
        this.emptySquares = width * height;
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

    // to do - jak wolne miejsce to trza odjac emptySquare--
    public void placeAnimal(Animal animal) {
        Vector2d position = animal.getPosition();
        if (!isAnimalAt(position) && !isGrassAt(position)) {
            emptySquares--;
        }
        animals.put(animal.getPosition(), animal);
        this.mapChanged("%s was placed at %s".formatted(animal, animal.getPosition()));
    }

    public void removeAnimal(Animal animal) {
        Vector2d position = animal.getPosition();
        List<Animal> list = animals.get(position);
        if (list != null) {
            list.remove(animal);
            if (list.isEmpty()) {
                animals.remove(animal.getPosition());
                if (!isGrassAt(position)) {
                    emptySquares++;
                }
            }
        }
        mapChanged("Animal removed");
    }

    public void placeGrass(Grass grass) {
        Vector2d position = grass.getPosition();
        if (!grasses.containsKey(position) && !isAnimalAt(position)) {
            emptySquares--;
        }
        grasses.put(position, grass);

        mapChanged("Grass was placed at %s".formatted(grass.getPosition()));
    }

    public void removeGrass(Grass grass) {
        Vector2d position = grass.getPosition();
        if (grasses.remove(position) != null) {
            if (!isAnimalAt(position)) {
                emptySquares++;
            }
        }
        mapChanged("Grass removed");
    }

    public List<Vector2d> getPositionsWithoutGrass(List<Vector2d> allPositions) {
        allPositions.removeAll(this.getGrassPositions());
        return allPositions;
    }

    public void moveAllAnimals() {
        // obejscie zeby nie zminiac listy po ktorej iterujemy
        List<Animal> allAnimals = new ArrayList<>();

        // wszystkie zwierzeta w jednej liscie
        for (List<Animal> list : animals.values()) {
            allAnimals.addAll(list);
        }

        for (Animal animal : allAnimals) {
            Vector2d oldPos = animal.getPosition();

            // usuniecie ze starej pozycji
            List<Animal> list = animals.get(oldPos);
            if (list != null) {
                list.remove(animal);
                if (animals.get(oldPos).isEmpty()) {
                    animals.remove(oldPos);
                    emptySquares++;
                }
            }

            animal.move();

            // zeby zgodnie z mapa pelz
            Vector2d newPos = wrapPosition(animal.getPosition());
            animal.setPosition(newPos);

            // dodanie animalka do nowej pozycji
            if (!animals.containsKey(newPos)) {
                emptySquares--;
            }
            animals.put(newPos, animal);

            mapChanged("%s moved from %s to %s"
                    .formatted(animal, oldPos, newPos));
        }
    }

    public boolean inBounds(Vector2d position) {
        return position.follows(mapBoundary.lowerLeft()) && position.precedes(mapBoundary.upperRight());
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

    public List<WorldElement> objectAt(Vector2d position) {
        if (isAnimalAt(position)) {
            return List.copyOf(animals.get(position));
        }
        return List.of(grasses.get(position));
    }
    public List<Animal> getAnimalsAt(Vector2d position) {
        return animals.containsKey(position)
                ? List.copyOf(animals.get(position))
                : List.of();
    }

    public List<Animal> getAllAnimals() {
        return animals.values().stream()
                .flatMap(List::stream)
                .toList();
    }

    public boolean isOccupied(Vector2d position) {
        return isAnimalAt(position) || isGrassAt(position);
    }

    public int getEmptySquares() {
        return emptySquares;
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

    @Override
    public String toString() {
        return cmv.draw(getMapBoundary().lowerLeft(), getMapBoundary().upperRight());
    }

}
