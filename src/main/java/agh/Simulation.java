package agh;

import agh.model.*;
import agh.model.animal.Animal;
import agh.model.util.ConsoleMapVisualizer;
import agh.model.util.RandomPositionGenerator;

import java.util.Random;

public class Simulation {
    private final int mapWidth;
    private final int mapHeight;
    private final int numAnimals;
    private final int numGrass;
    private final WorldMap worldMap;

    public Simulation(int mapWidth, int mapHeight, int numAnimals, int numGrass) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.numAnimals = numAnimals;
        this.numGrass = numGrass;
        this.worldMap = new WorldMap(mapWidth, mapHeight);
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
//        RandomPositionGenerator randomPositions = new RandomPositionGenerator(worldMap.getMapBoundary(), numGrass, true);
//        for (Vector2d position: randomPositions) {
//            worldMap.placeGrass(new Grass(position));
//        }
    }

    public void run() {
        placeGrass();
        //placeAnimals();

        // temporary do testów
        ConsoleMapVisualizer cmv = new ConsoleMapVisualizer(worldMap);
        System.out.println(cmv.draw(worldMap.getMapBoundary().lowerLeft(), worldMap.getMapBoundary().upperRight()));
    }

    private void initWorld() {

    }

    private void deleteDeadAnimals() {}

    private void moveAnimals() {}

}
