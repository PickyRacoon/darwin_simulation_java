package agh.model.animal;

import agh.model.MapDirection;
import agh.model.Vector2d;
import agh.model.WorldElement;

import java.util.UUID;


public class Animal implements WorldElement {
    private static final int BREED_MIN_ENERGY = 77;     // parametr
    private static final int ENERGY_USED_TO_BREED = 22;     // parametr
    private static final int ENERGY_AFTER_DAY = 5;       // parametr

    private final Genotype genotype;
    private final UUID animalId = UUID.randomUUID();

    private Vector2d position;
    private MapDirection direction;
    private int energy = 97;    // parametr na poczatek
    private int daysAlive = 0;
    private boolean isAlive = true;
    private int numberOfBreedings = 0;

    // konstruktor na staetowego animala
    public Animal(Vector2d position) {
        this.genotype = new Genotype();
        this.direction = MapDirection.generateRandomDirection();
        this.position = position;
    }

    // konstruktor dla potomkow
    public Animal(Vector2d position, Genotype genotype) {
        this.position = position;
        this.genotype = genotype;
        this.direction = MapDirection.generateRandomDirection();
    }

    // konstruktor dla testow
    public Animal(Vector2d position, MapDirection direction, Genotype genotype) {
        this.position = position;
        this.genotype = genotype;
        this.direction = direction;
    }

    public void move() {
        if (!isAlive) {
            return;
        }
        int gene = genotype.currentGenomValue();
        direction = direction.rotation(gene);
        position = position.add(direction.toUnitVector());
        energy -= ENERGY_AFTER_DAY;
        daysAlive += 1;
        if (energy <= 0) {
            die();
        }
        genotype.nextGenomIndex();
    }

    public void breed() {
        energy -= ENERGY_USED_TO_BREED;
        numberOfBreedings += 1;
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
        return energy >= BREED_MIN_ENERGY;
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

    public boolean isAlive() {
        return isAlive;
    }

    @Override
    public Vector2d getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return direction.toString();
    }
 }


