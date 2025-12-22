package agh;

import agh.model.*;
import agh.model.animal.Animal;
import agh.model.util.ConsoleMapVisualizer;
import agh.model.util.RandomPositionGenerator;

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
        initWorld();


    }

    private void initWorld() {
        worldMap.addObserver(consoleDisplay);
        placeGrass();
        placeAnimals();
    }

    private void deleteDeadAnimals() {}

    private void moveAnimals() {}

}
