package agh;

import agh.model.AbstractWorldMap;

public record SimulationConfig(AbstractWorldMap worldMap, int numAnimals, int numGrass, int grassEnergy, int numDailyGrass,
                               int animalStartEnergy, int animalLooseEnergy,
                               int animalMinBreedEnergy, int animalEnergyUsedToBreed,
                               int minNumMutations, int maxNumMutations, int genotypeLen) {
}
