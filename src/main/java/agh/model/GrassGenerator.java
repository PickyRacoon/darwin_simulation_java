package agh.model;

import agh.model.util.RandomPositionGenerator;

public class GrassGenerator {
    private int numberOfGrasses; // parametr więc chyba nie final

    public GrassGenerator(int numberOfGrasses) {
        this.numberOfGrasses = numberOfGrasses;
    }

    public void createJungle(JungleWorldMap worldMap) {
        // 80% procent szans ze grass wyrosnie w jungle
        // można później rozdzielić tą metodę na pare mniejszych
        Boundary jungleBoundary = worldMap.getJungleBoundary();
        Boundary mapBoundary = worldMap.getMapBoundary();

        int inJungleNum = 0;
        int notInJungleNum = 0;

        for (int i = 0; i < numberOfGrasses; i++) {
            int randomNum = RandomNumber.getRandomNumberInRange(0, 100);
            if (randomNum > 20) {
                inJungleNum++;
            } else {
                notInJungleNum++;
            }
        }


        RandomPositionGenerator randomJunglePositions = new RandomPositionGenerator(jungleBoundary, inJungleNum, true);
        for (Vector2d position : randomJunglePositions) {
            worldMap.placeGrass(new Grass(position));
            inJungleNum--;
        }

        if (inJungleNum > 0 ) {notInJungleNum += inJungleNum;}
        // kurde tu jest chyba za troche namieszane, chodzi o to żeby jakby zingorować to że istnieje jungle
        // i wygenerować losowo trawe mapy bez jungle, a potem rozdzielić w pół i wrzucić górą połowę nad jungle

        int jungleHeight = jungleBoundary.upperRight().getY() - jungleBoundary.lowerLeft().getY();
        int worldHeightWithoutJungle = worldMap.getHeight() - jungleHeight - 1;
        Boundary mapWithoutJungleBoundaries = new Boundary(mapBoundary.lowerLeft(), new Vector2d(worldMap.getWidth()-1, worldHeightWithoutJungle-1));
        RandomPositionGenerator randomNonJunglePositions = new RandomPositionGenerator(mapWithoutJungleBoundaries, notInJungleNum, true);
        for (Vector2d position : randomNonJunglePositions) {
            if (position.follows(jungleBoundary.lowerLeft())) {
                position = position.add(new Vector2d(0, jungleHeight+1));
            }
            worldMap.placeGrass(new Grass(position));
        }

    }

}
