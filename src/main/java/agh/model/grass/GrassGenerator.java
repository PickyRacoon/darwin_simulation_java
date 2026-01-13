package agh.model.grass;

import agh.model.RandomNumber;
import agh.model.Vector2d;
import agh.model.util.RandomPositionGenerator;
import java.util.List;

public class GrassGenerator {
    public RandomPositionGenerator generateGrass(List<Vector2d> allPossiblePositions, int numGrass) {
        return new RandomPositionGenerator(allPossiblePositions, numGrass, true);
    }

    public RandomPositionGenerator generateJungle(List<Vector2d> junglePositions, int initNumGrass) {
        int inJungleNum = 0;
        for (int i = 0; i < initNumGrass; i++) {
            int randomNum = RandomNumber.getRandomNumberInRange(0, 100);
            if (randomNum > 20) {
                inJungleNum++;
            }
        }
        return new RandomPositionGenerator(junglePositions, inJungleNum, true);
    }
}
