package agh;

import agh.model.*;
import agh.model.animal.Animal;
import agh.model.animal.Genotype;
import agh.model.util.ConsoleMapVisualizer;
import agh.model.util.RandomPositionGenerator;

import java.util.*;
import java.util.stream.Collectors;

public class Simulation implements Runnable {
    private final SimulationConfig config;
    private final AbstractWorldMap worldMap;
    private boolean isStopped = false;
    //private final ConsoleMapDisplay consoleDisplay = new ConsoleMapDisplay();
    private final List<Animal> allDeadAnimals = new ArrayList<>();

    public Simulation(SimulationConfig config) {
        this.config = config;
        this.worldMap = config.worldMap();
        this.initWorld();
    }

    private void placeAnimals(int count) {
        Boundary mapBoundary = worldMap.getMapBoundary();
        RandomPositionGenerator randomPositions = new RandomPositionGenerator(mapBoundary.getAllPositions(), count, false);
        for (Vector2d position : randomPositions) {
            worldMap.placeAnimal(new Animal(position, config));
        }
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

    public void initWorld() {
        // worldMap.addObserver(consoleDisplay); // potrzebne tylko do testów w konsoli
        placeAnimals(config.numAnimals());
    }

    public void deleteDeadAnimals() {
        List<Animal> deadAnimals = worldMap.getAllAnimals().stream()
                .filter(animal -> !animal.isAlive())
                .toList(); // aby nie modyfikować mapy w trakcie iteracji

        for (Animal animal : deadAnimals) {
            allDeadAnimals.add(animal);
            worldMap.removeAnimal(animal);
        }
    }

    public void moveAnimals() {
        List<Animal> allAnimals = new ArrayList<>(worldMap.getAllAnimals());

        for (Animal animal : allAnimals) {
            worldMap.removeAnimal(animal);

            animal.move();

            Vector2d newPos = worldMap.wrapPosition(animal.getPosition());
            animal.setPosition(newPos);

            worldMap.placeAnimal(animal);
        }
    }

    public void eat() {
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

    public Animal idealAnimalToEatByQA(List<Animal> animals) {
        if (animals.isEmpty()) return null;

        Comparator<Animal> comparator = Comparator
                .comparingInt(Animal::getEnergy).reversed()
                .thenComparingInt(Animal::getDaysAlive).reversed()
                .thenComparingInt(Animal::getNumberOfBreedings).reversed();

        Animal bestAnimal = animals.stream().max(comparator).orElse(null);

        if (bestAnimal == null) {
            return null;
        }

        List<Animal> allBestAnimals = animals.stream()
                .filter(animal -> comparator.compare(animal, bestAnimal) == 0)
                .toList();

        return allBestAnimals.get(new Random().nextInt(allBestAnimals.size()));
    }

    public void procreate() {
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

    public void growNewPlants(int numOfNewGrasses) {
        GrassGenerator grassGenerator = new GrassGenerator();
        grassGenerator.placeNewGrass(worldMap, numOfNewGrasses);
    }

    public SimulationStatistics getSimulationStatistics() {
        List<Animal> allAnimals = worldMap.getAllAnimals();
        int animalsCount = allAnimals.size();
        int grassCount = worldMap.getGrassPositions().size();
        int emptySquares = worldMap.getEmptySquares();

        double avgEnergy = allAnimals.stream()
                .mapToInt(Animal::getEnergy)
                .average()
                .orElse(0.0);

        double avgLifeSpan = allDeadAnimals.stream()
                .mapToInt(Animal::getDaysAlive)
                .average()
                .orElse(0.0);

        double avgChildrenCount = allAnimals.stream()
                .mapToInt(Animal::getNumberOfBreedings)
                .average()
                .orElse(0.0);

        Map<Genotype, Long> genCount = allAnimals.stream()
                .collect(Collectors.groupingBy(
                        Animal::getGenotype,
                        Collectors.counting()
                ));

        List<Integer> mostPopular = genCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> entry.getKey().getGenom())
                .orElse(List.of());

        return new SimulationStatistics(
                animalsCount,
                grassCount,
                emptySquares,
                mostPopular,
                avgEnergy,
                avgLifeSpan,
                avgChildrenCount
        );
    }
}
