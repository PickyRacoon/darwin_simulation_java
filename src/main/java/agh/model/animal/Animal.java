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
        this.direction = MapDirection.generateRandomDirection();
        this.genotype = new Genotype();
        this.position = position;
    }

    public void move() {
        genotype.nextGenomIndex();
        direction.rotation(genotype.getActievGenomIndex());
        energy -= ENERGY_AFTER_DAY;
        daysAlive += 1;
        position = position.add(direction.toUnitVector());
        // jeszcze ze zjada tylko trza najpierw mape ogarnac
    }

    public void breed() {
        energy -= BREED_MIN_ENERGY;
        numberOfBreedings += 1;
    }

    public void eat(int grassEnergy) {
        energy += grassEnergy;
        // jezeli zakladamy ze mamy limit energii
        if (energy > 100) {
            energy = 100;
        }
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

    @Override
    public Vector2d getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return direction.toString();
    }
 }


