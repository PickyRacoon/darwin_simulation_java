package agh.model.animal;

import agh.model.RandomNumber;

import java.util.ArrayList;
import java.util.List;

public class Genotype {
    private static final int MIN_GENOM_NUMBER = 0;
    private static final int MAX_GENOM_NUMBER = 7;
    private static final int MIN_MUTATION_LEVEL = 1;     // parametr
    private static final int MAX_MUTATION_LEVEL = 4;     // parametr

    private static final int genomLength = 10;     // parametr
    private final List<Integer> genom;
    private int actievGenomIndex = 0;

    public Genotype() {
        this.genom = generateRandomGenom();
    }

    public Genotype(List<Integer> genom) {
        this.genom = genom;
    }

    // random genom dla poczatkowych zwierzakow
    private List<Integer> generateRandomGenom() {
        List<Integer> genom = new ArrayList<>();
        for (int i = 0; i < genomLength; i++) {
            genom.add(RandomNumber.getRandomNumberInRange(MIN_GENOM_NUMBER, MAX_GENOM_NUMBER));
        }
        return genom;
    }

    public int getActievGenomIndex() {
        return actievGenomIndex;
    }

    public void nextGenomIndex() {
        actievGenomIndex = (actievGenomIndex + 1)  % genomLength;
    }

    public static Genotype crossGenotype(Animal parent1, Animal parent2) {
        List<Integer> childGenes = new ArrayList<>();

        Animal strongerParent, weakerParent;
        if (parent1.getEnergy() >= parent2.getEnergy()) {
            strongerParent = parent1;
            weakerParent = parent2;
        } else {
            strongerParent = parent2;
            weakerParent = parent1;
        }

        List<Integer> strongerGenes = strongerParent.getGenotype().getGenom();
        List<Integer> weakerGenes = weakerParent.getGenotype().getGenom();

        // udział genow silniejszego rodzica
        double ratio = (double) strongerParent.getEnergy() / (strongerParent.getEnergy() + weakerParent.getEnergy());
        int splitIndex = (int) Math.round(genomLength * ratio);

        boolean takeFromLeft = RandomNumber.getRandomNumberInRange(0, 1) == 0;

        if (takeFromLeft) {
            // lewa czesc silniejszego + prawa czesc slabszego
            childGenes.addAll(strongerGenes.subList(0, splitIndex));
            childGenes.addAll(weakerGenes.subList(splitIndex, genomLength));
        } else {
            // prawa czesc silniejszego + lewa czesc slabszego
            childGenes.addAll(weakerGenes.subList(0, genomLength - splitIndex));
            childGenes.addAll(strongerGenes.subList(genomLength - splitIndex, genomLength));
        }

        Genotype childGenotype = new Genotype(childGenes);
        childGenotype.mutateGenom();
        return childGenotype;
    }

    private void mutateGenom() {
        int mutationLevel = RandomNumber.getRandomNumberInRange(MIN_MUTATION_LEVEL, MAX_MUTATION_LEVEL);
        for (int i = 0; i < mutationLevel; i++) {
            int index = RandomNumber.getRandomNumberInRange(0, genomLength-1);
            int newGen = RandomNumber.getRandomNumberInRange(MIN_GENOM_NUMBER, MAX_GENOM_NUMBER);
            genom.set(index, newGen);
        }
    }

    public List<Integer> getGenom() {
        return genom;
    }
}
