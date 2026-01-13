package agh.model.animal;

import agh.simulation.SimulationConfig;
import agh.model.RandomNumber;

import java.util.ArrayList;
import java.util.List;

public class Genotype {
    private static final int MIN_GENOM_NUMBER = 0;
    private static final int MAX_GENOM_NUMBER = 7;

    //private static final int genomLength = 10;
    private final SimulationConfig config;
    private final List<Integer> genom;
    private int actievGenomIndex = 0;

    public Genotype(SimulationConfig config) {
        this.config = config;
        this.genom = generateRandomGenom();
    }

    public Genotype(List<Integer> genom, SimulationConfig config) {
        this.config = config;
        this.genom = new ArrayList<>(genom);
    }

    // random genom dla poczatkowych zwierzakow
    private List<Integer> generateRandomGenom() {
        List<Integer> genom = new ArrayList<>();
        for (int i = 0; i < config.genotypeLen(); i++) {
            genom.add(RandomNumber.getRandomNumberInRange(MIN_GENOM_NUMBER, MAX_GENOM_NUMBER));
        }
        return genom;
    }

    public int currentGenomValue() {
        return genom.get(actievGenomIndex);
    }

    public int getActievGenomIndex() {
        return actievGenomIndex;
    }

    public void nextGenomIndex() {
        this.actievGenomIndex = (actievGenomIndex + 1)  % config.genotypeLen();
    }

    public static Genotype crossGenotype(Animal parent1, Animal parent2, SimulationConfig config) {
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
        int splitIndex = (int) Math.round(config.genotypeLen() * ratio);

        boolean takeFromLeft = RandomNumber.getRandomNumberInRange(0, 1) == 0;

        if (takeFromLeft) {
            // lewa czesc silniejszego + prawa czesc slabszego
            childGenes.addAll(strongerGenes.subList(0, splitIndex));
            childGenes.addAll(weakerGenes.subList(splitIndex, config.genotypeLen()));
        } else {
            // prawa czesc silniejszego + lewa czesc slabszego
            childGenes.addAll(weakerGenes.subList(0, config.genotypeLen() - splitIndex));
            childGenes.addAll(strongerGenes.subList(config.genotypeLen() - splitIndex, config.genotypeLen()));
        }

        Genotype childGenotype = new Genotype(childGenes, config);
        childGenotype.mutateGenom();
        return childGenotype;
    }

    public void mutateGenom() {
        int mutationLevel = RandomNumber.getRandomNumberInRange(config.minNumMutations(), config.maxNumMutations());
        for (int i = 0; i < mutationLevel; i++) {
            int index = RandomNumber.getRandomNumberInRange(0, config.genotypeLen()-1);
            int newGen = RandomNumber.getRandomNumberInRange(MIN_GENOM_NUMBER, MAX_GENOM_NUMBER);
            genom.set(index, newGen);
        }
    }

    public List<Integer> getGenom() {
        return genom == null ? List.of() : List.copyOf(genom);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Genotype genotype = (Genotype) o;
        return genom.equals(genotype.genom);
    }

    @Override
    public int hashCode() {
        return genom.hashCode();
    }

}
