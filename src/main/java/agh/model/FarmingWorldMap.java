package agh.model;

import agh.model.animal.Animal;

import java.util.HashMap;
import java.util.List;

public class FarmingWorldMap extends AbstractWorldMap {
    private final HashMap<Vector2d, Integer> landBeingCultivated = new HashMap<>();
    private final HashMap<Vector2d, Integer> fertileLand = new HashMap<>();
    private final int fertileLandValue = 3;
    private int minEnergyToCultivate = 20; // param
    private int daysLandIsFertile = 5; // param

    public FarmingWorldMap(int width, int height, int numGrass) {
        super(width, height, numGrass);
        generateGrass(numGrass);
    }

    @Override
    public void generateGrass(int numGrass) {
        for (Vector2d position : fertileLand.keySet()) {
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
        return new BigGrass(position);
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
        if (landBeingCultivated.get(position) > fertileLandValue) {
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
