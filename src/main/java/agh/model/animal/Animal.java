package agh.model.animal;

import agh.SimulationConfig;
import agh.model.MapDirection;
import agh.model.Vector2d;
import agh.model.WorldElement;

import java.util.UUID;


public class Animal implements WorldElement {
    private final SimulationConfig config;
    private final Genotype genotype;
    private final UUID animalId = UUID.randomUUID();

    private Vector2d position;
    private MapDirection direction;
    private int energy;
    private int daysAlive = 0;
    private boolean isAlive = true;
    private int numberOfBreedings = 0;

    // konstruktor na staetowego animala
    public Animal(Vector2d position, SimulationConfig config) {
        this.genotype = new Genotype(config);
        this.direction = MapDirection.generateRandomDirection();
        this.position = position;
        this.config = config;
        this.energy = config.animalStartEnergy();
    }

    // konstruktor dla potomkow
    public Animal(Vector2d position, Genotype genotype, SimulationConfig config) {
        this.position = position;
        this.genotype = genotype;
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
            energy = 0
            die();
        }
        genotype.nextGenomIndex();
    }

    public void breed() {
        energy -= config.animalEnergyUsedToBreed();
        numberOfBreedings++;
    }

    public void eat(int grassEnergy) {
        energy += grassEnergy;
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

    public  int getNumberOfBreedings() {
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
 }


