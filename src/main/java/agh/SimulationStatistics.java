package agh;

import agh.model.animal.Genotype;

import java.util.List;

public record SimulationStatistics(int animalCount, int grassCount, int emptySquares, List<Integer> popularGenotype,
                                   double avgEnergy, double avgLifeSpan, double avgChildrenCount) {
}
