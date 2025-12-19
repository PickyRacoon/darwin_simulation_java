package agh.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Animal {
    private static final int BREED_MIN_ENERGY = 77;     // parametr
    private static final int ENERGY_USED_TO_BREED = 22;     // parametr

    private final Genotype genotype;

    private Vector2d position;
    private MapDirection direction;
    private int energy = 97;    // parametr na poczatek


    public Animal() {
        this.direction = generateRandomDirection();
        this.genotype = new Genotype();
    }

    private MapDirection generateRandomDirection() {
        MapDirection[] directions = MapDirection.values();
        return directions[RandomNumber.getRandomNumberInRange(0, directions.length-1)];
    }

    public void eat(int plantEnergy) {
        this.energy += plantEnergy;
        if (this.energy > 100) {
            this.energy = 100;
        }
    }

    public boolean canBreed() {
        return this.energy >= BREED_MIN_ENERGY;
    }

    public int getEnergy() {
        return this.energy;
    }

    public Genotype getGenotype() {
        return this.genotype;
    }

    public MapDirection getDirection() {
        return this.direction;
    }

    public Vector2d getPosition() {
        return this.position;
    }
 }


