package agh;

import agh.model.*;
import agh.model.animal.Animal;
import agh.model.util.ConsoleMapVisualizer;
import agh.model.util.RandomPositionGenerator;

import java.util.ArrayList;
import java.util.List;

public class Simulation {
    private final int mapWidth;
    private final int mapHeight;
    private final int numAnimals;
    private final int numGrass;
    private final JungleWorldMap worldMap;
    private final ConsoleMapDisplay consoleDisplay = new ConsoleMapDisplay();

    public Simulation(int mapWidth, int mapHeight, int numAnimals, int numGrass) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.numAnimals = numAnimals;
        this.numGrass = numGrass;
        this.worldMap = new JungleWorldMap(mapWidth, mapHeight);
        this.initWorld();
    }

    private void placeAnimals() {
        RandomPositionGenerator randomPositions = new RandomPositionGenerator(worldMap.getMapBoundary(), numAnimals, false);
        for (Vector2d position: randomPositions) {
            worldMap.placeAnimal(new Animal(position));
        }
    }

    private void placeGrass() {
        GrassGenerator grassGenerator = new GrassGenerator(numGrass);
        grassGenerator.createJungle(worldMap);
    }

    public void run() {
        deleteDeadAnimals();
        moveAnimals();
        eat();
        procreate();
        growNewPlants();
    }

    private void initWorld() {
        worldMap.addObserver(consoleDisplay);
        placeGrass();
        placeAnimals();
    }

    private void deleteDeadAnimals() {
        List<Animal> deadAnimals = worldMap.getAllAnimals().stream()
                                            .filter(animal -> !animal.isAlive())
                                            .toList(); // aby nie modyfikować mapy w trakcie iteracji

        for (Animal animal : deadAnimals) {
            worldMap.removeAnimal(animal);
        }
    }

    private void moveAnimals() {
        List<Animal> allAnimals = new ArrayList<>(worldMap.getAllAnimals());

        for (Animal animal : allAnimals) {
            worldMap.removeAnimal(animal);

            animal.move();

            Vector2d newPos = worldMap.wrapPosition(animal.getPosition());
            animal.setPosition(newPos);

            worldMap.placeAnimal(animal);
        }
    }

    private void eat() {}

    private void procreate() {}

    private void growNewPlants() {}

}
