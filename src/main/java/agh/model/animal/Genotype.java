package agh.model.animal;

import agh.simulation.SimulationConfig;
import agh.model.util.RandomNumber;

import java.util.ArrayList;
import java.util.List;

public class Genotype {
    private static final int MIN_GENOM_NUMBER = 0;
    private static final int MAX_GENOM_NUMBER = 7;

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
        ParentPair parents = selectByEnergy(parent1, parent2);

        int splitIndex = calculateSplitIndex(parents.stronger(),
                parents.weaker(), config.genotypeLen());

        List<Integer> childGenes = mix(parents.stronger().getGenotype().getGenom(),
                parents.weaker().getGenotype().getGenom(), splitIndex, config.genotypeLen());

        Genotype childGenotype = new Genotype(childGenes, config);
        childGenotype.mutateGenom();
        return childGenotype;
    }

    public static ParentPair selectByEnergy(Animal a, Animal b) {
        if (a.getEnergy() >= b.getEnergy()) {
            return new ParentPair(a, b);
        }
        return new ParentPair(b, a);
    }

    public static int calculateSplitIndex(Animal stronger, Animal weaker, int genotypeLen) {
        double ratio = (double) stronger.getEnergy()
                / (stronger.getEnergy() + weaker.getEnergy());

        return (int) Math.round(genotypeLen * ratio);
    }

    public static List<Integer> mix(List<Integer> strongerGenes, List<Integer> weakerGenes, int splitIndex, int genotypeLen) {
        List<Integer> result = new ArrayList<>();
        boolean takeFromLeft = RandomNumber.getRandomNumberInRange(0, 1) == 0;

        if (takeFromLeft) {
            result.addAll(strongerGenes.subList(0, splitIndex));
            result.addAll(weakerGenes.subList(splitIndex, genotypeLen));
        } else {
            result.addAll(weakerGenes.subList(0, genotypeLen - splitIndex));
            result.addAll(strongerGenes.subList(genotypeLen - splitIndex, genotypeLen));
        }

        return result;
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
