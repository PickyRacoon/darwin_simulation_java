package agh;

import agh.model.AbstractWorldMap;
import agh.model.JungleWorldMap;

public record SimulationConfig(AbstractWorldMap worldMap, int numAnimals, int numGrass) {
}
