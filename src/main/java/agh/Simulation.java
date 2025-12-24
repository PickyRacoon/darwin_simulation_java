package agh;

import agh.model.*;
import agh.model.animal.Animal;
import agh.model.animal.Genotype;
import agh.model.util.ConsoleMapVisualizer;
import agh.model.util.RandomPositionGenerator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Simulation implements Runnable {
    private final SimulationConfig config;
    private final AbstractWorldMap worldMap;
    private boolean isStopped = false;
    //private final ConsoleMapDisplay consoleDisplay = new ConsoleMapDisplay();

    public Simulation(SimulationConfig config) {
        this.config = config;
        this.worldMap = config.worldMap();
        this.initWorld();
    }

    private void placeAnimals(int count) {
        RandomPositionGenerator randomPositions = new RandomPositionGenerator(worldMap.getMapBoundary(), count, false);
        for (Vector2d position : randomPositions) {
            worldMap.placeAnimal(new Animal(position, config));
        }
    }

    private void placeGrass(int count) {
        GrassGenerator grassGenerator = new GrassGenerator(count);
        // grassGenerator.createJungle(worldMap);
        // TODO trzeba naprawić

    }

    @Override
    public void run() {
        while (!isStopped) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            deleteDeadAnimals();
            moveAnimals();
            eat();
            procreate();
            growNewPlants(config.numDailyGrass());
        }
    }

    public void stopSimulation() {
        isStopped = true;
    }

    public void startSimulation() {
        isStopped = false;
    }

    private void initWorld() {
        // worldMap.addObserver(consoleDisplay); // potrzebne tylko do testów w konsoli
        placeGrass(config.numGrass());
        placeAnimals(config.numAnimals());
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

    private void eat() {
        List<Vector2d> grassPositions = new ArrayList<>(worldMap.getGrassPositions());

        for (Vector2d position : grassPositions) {
            Grass grass = worldMap.getGrassAt(position);
            if (grass == null) continue;

            List<Animal> animalsAtPosition = worldMap.getAnimalsAt(position);
            if (animalsAtPosition.isEmpty()) continue;

            // zasady z Q&A
            Animal eater = idealAnimalToEatByQA(animalsAtPosition);

            if (eater != null) {
                eater.eat(config.grassEnergy());
                worldMap.removeGrass(grass);
            }
        }
    }

    private Animal idealAnimalToEatByQA(List<Animal> animals) {
        if (animals.isEmpty()) return null;

        // .max() zwraca optionalint dlatego musze orelse
        int maxEnergy = animals.stream().mapToInt(Animal::getEnergy).max().orElse(Integer.MIN_VALUE);
        List<Animal> candidates = animals.stream()
                .filter(animal -> animal.getEnergy() == maxEnergy)
                .collect(Collectors.toList());

        int maxAge = candidates.stream().mapToInt(Animal::getDaysAlive).max().orElse(Integer.MIN_VALUE);
        candidates = candidates.stream()
                .filter(animal -> animal.getDaysAlive() == maxAge)
                .collect(Collectors.toList());

        int maxChildren = candidates.stream().mapToInt(Animal::getNumberOfBreedings).max().orElse(Integer.MIN_VALUE);
        candidates = candidates.stream()
                .filter(animal -> animal.getNumberOfBreedings() == maxChildren)
                .collect(Collectors.toList());

        return candidates.get(new Random().nextInt(candidates.size()));
    }

    private void procreate() {
        List<Vector2d> animalPositions = new ArrayList<>(worldMap.getAnimalPositions());

        for (Vector2d position : animalPositions) {
            List<Animal> animalsAtPosition = worldMap.getAnimalsAt(position);
            if (animalsAtPosition.isEmpty() || animalsAtPosition.size() < 2) continue;

            List<Animal> parents = animalsAtPosition.stream()
                    .filter(Animal::canBreed)
                    .collect(Collectors.toList());

            if (parents.size() < 2) continue;

            parents.sort(Comparator.comparingInt(Animal::getEnergy).reversed());
            Animal parent1 = parents.get(0);
            Animal parent2 = parents.get(1);

            Genotype childGenotype = Genotype.crossGenotype(parent1, parent2, config);
            Animal child = new Animal(position, childGenotype, config);

            parent1.breed();
            parent2.breed();

            worldMap.placeAnimal(child);
        }
    }

    private void growNewPlants(int numOfNewGrasses) {
        RandomPositionGenerator randomPositions = new RandomPositionGenerator(worldMap.getMapBoundary(), numOfNewGrasses, true);
        for (Vector2d position : randomPositions) {
            if (worldMap.getGrassAt(position) == null) {
                worldMap.placeGrass(new Grass(position));
            }
        }
    }
}
