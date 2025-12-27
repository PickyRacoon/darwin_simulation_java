package agh.model.animal;

import java.util.List;

public record AnimalStatus(List<Integer> genom, int activeGenomIndex, int energy, int numPlantsEaten, int numKids, int numDescendants,
                           int daysSurvived, int diedOnDay) {
}
