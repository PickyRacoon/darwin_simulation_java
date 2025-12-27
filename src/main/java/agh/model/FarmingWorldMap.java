package agh.model;

import agh.FarmingStatistics;
import agh.model.animal.Animal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FarmingWorldMap extends AbstractWorldMap {
    private final HashMap<Vector2d, Integer> landBeingCultivated = new HashMap<>();
    private final HashMap<Vector2d, Integer> fertileLand = new HashMap<>();
    private final FarmingStatistics farmingStatistics;
    private final int fertileLandValue;
    private final int minEnergyToCultivate;
    private final int daysLandIsFertile;

    public FarmingWorldMap(int width, int height, int numGrass, FarmingStatistics farmingStatistics) {
        super(width, height, numGrass);
        this.farmingStatistics = farmingStatistics;
        this.fertileLandValue = farmingStatistics.fertileLandValue();
        this.minEnergyToCultivate = farmingStatistics.minEnergyToCultivate();
        this.daysLandIsFertile = farmingStatistics.daysLandIsFertile();
        generateGrass(numGrass);
    }

    @Override
    public void generateGrass(int numGrass) {
        List<Vector2d> fertilePositions = new ArrayList<>(fertileLand.keySet());

        for (Vector2d position : fertilePositions) {
            int daysFertile = fertileLand.get(position);
            if (daysFertile < 1) {
                fertileLand.remove(position);
            } else {
                fertileLand.put(position, daysFertile - 1);
            }
        }
        super.generateGrass(numGrass);
    }

    @Override
    protected Grass createJungleGrass(Vector2d position) {
        return new BigGrass(position, farmingStatistics.numMeals());
    }

    @Override
    protected List<Vector2d> getJunglePositions() {
        return this.fertileLand.keySet().stream().toList();
    }

    private void cultivateLand(Vector2d position) {
        if (fertileLand.containsKey(position)) {
            return;
        }
        if (!landBeingCultivated.containsKey(position)) {
            landBeingCultivated.put(position, 1);
        } else {
            landBeingCultivated.put(position, landBeingCultivated.get(position) + 1);
        }
        if (landBeingCultivated.get(position) >= fertileLandValue) {
            fertileLand.put(position, daysLandIsFertile);
            landBeingCultivated.remove(position);
        }
    }

    @Override
    public void placeAnimal(Animal animal) {
        if (animal.getEnergy() >= minEnergyToCultivate) {
            cultivateLand(animal.getPosition());
        }
        super.placeAnimal(animal);
    }

}
