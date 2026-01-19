package agh.model.animal;

import agh.simulation.SimulationConfig;
import agh.model.AnimalChangeListener;
import agh.model.maps.MapDirection;
import agh.model.Vector2d;
import agh.model.WorldElement;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class Animal implements WorldElement {
    private final SimulationConfig config;
    private final Genotype genotype;

    private Vector2d position;
    private MapDirection direction;
    private int energy;
    private int daysAlive = 0;
    private boolean isAlive = true;
    private int numberOfBreedings = 0;
    private int numberOfGrassEaten;
    private Animal parent1;
    private Animal parent2;
    private int numDescendants;
    private int daysSurvived = 0;
    private int diedOnDay;
    private final List<AnimalChangeListener> observers = new ArrayList<>();

    // konstruktor na staetowego animala
    public Animal(Vector2d position, SimulationConfig config) {
        this.genotype = new Genotype(config);
        this.direction = MapDirection.generateRandomDirection();
        this.position = position;
        this.config = config;
        this.energy = config.animalStartEnergy();
    }

    // konstruktor dla potomkow
    public Animal(Vector2d position, Genotype genotype, Animal parent1, Animal parent2, SimulationConfig config) {
        this.position = position;
        this.genotype = genotype;
        this.parent1 = parent1;
        this.parent2 = parent2;
        this.config = config;
        this.energy = config.animalEnergyUsedToBreed() * 2;
        this.direction = MapDirection.generateRandomDirection();
    }

    // konstruktor dla testow
    public Animal(Vector2d position, MapDirection direction, Genotype genotype, SimulationConfig config) {
        this.config = config;
        this.position = position;
        this.genotype = genotype;
        this.direction = direction;
        this.energy = config.animalStartEnergy();
    }

    public void move() {
        if (!isAlive) {
            return;
        }
        int gene = genotype.currentGenomValue();
        direction = direction.rotation(gene);
        position = position.add(direction.toUnitVector());
        energy -= config.animalLooseEnergy();
        daysAlive += 1;
        if (energy <= 0) {
            energy = 0;
            die();
        }
        genotype.nextGenomIndex();
        animalChanged();
    }

    public void breed() {
        energy -= config.animalEnergyUsedToBreed();
        numberOfBreedings++;
        if (parent1 != null) {
            parent1.incrementDescendantsNum();
        }
        if (parent2 != null) {
            parent2.incrementDescendantsNum();
        }
        animalChanged();
    }

    public void incrementDescendantsNum() {
        numDescendants++;
    }

    public void eat(int grassEnergy) {
        energy += grassEnergy;
        numberOfGrassEaten++;
        animalChanged();
        // jezeli zakladamy ze mamy limit energii
//        if (energy > 100) {
//            energy = 100;
//        }
    }

    public void die() {
        isAlive = false;
    }

    public boolean canBreed() {
        return energy >= config.animalMinBreedEnergy();
    }

    public int getEnergy() {
        return energy;
    }

    public Genotype getGenotype() {
        return genotype;
    }

    public MapDirection getDirection() {
        return direction;
    }

    public int getDaysAlive() {
        return daysAlive;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public int getNumberOfBreedings() {
        return numberOfBreedings;
    }

    public void setPosition(Vector2d position) {
        this.position = position;
    }

    @Override
    public Vector2d getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "ʕ•ᴥ•ʔ";
    }

    public void setDiedOnDay(int diedOnDay) {
        this.diedOnDay = diedOnDay;
        animalChanged();
    }

    public void incrementDaysSurvived() {
        this.daysSurvived++;
        animalChanged();
    }

    private AnimalStatus getAnimalStatus() {
        return new AnimalStatus(
                genotype.getGenom(),
                genotype.getActievGenomIndex(),
                energy,
                numberOfGrassEaten,
                numberOfBreedings,
                numDescendants,
                daysSurvived,
                diedOnDay);
    }

    private void animalChanged(){
        for (AnimalChangeListener observer: observers) {
            observer.animalChanged(getAnimalStatus());
        }
    }

    public void addObserver(AnimalChangeListener observer) {
        observers.add(observer);
        animalChanged();
    }

    public void removeObserver(AnimalChangeListener observer) {
        observers.remove(observer);
    }
}


