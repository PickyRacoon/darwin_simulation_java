import agh.model.Vector2d;
import agh.model.animal.Animal;
import agh.model.grass.Grass;
import agh.model.maps.Boundary;
import agh.model.maps.JungleWorldMap;
import agh.model.util.RandomNumber;
import agh.simulation.SimulationConfig;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JungleWorldMapTest {
    // w praktyce to też testy dla AbstractWorldMap

    SimulationConfig config = new SimulationConfig(
            new JungleWorldMap(10, 10, 2),
            2,
            2,
            5,
            5,
            100,
            5,
            55,
            22,
            1,
            4,
            10
    );

    @Test
    void jungleBoundaryCreatedCorrectly() {
        JungleWorldMap jungleWorldMap = new JungleWorldMap(10, 10, 0);
        Boundary expectedBoundary = new Boundary(new Vector2d(0, 4), new Vector2d(9, 5));

        assertEquals(expectedBoundary, jungleWorldMap.getJungleBoundary());
    }

    @Test
    void plantsFollowEightyTwentyRule() {
        int totalGrassInJungle = 0;
        int totalGrassOutside = 0;
        int testCount = 1000;

        for (int i = 0; i < testCount; i++) {
            // Duża mapa, żeby dżungla miała dużo wolnego miejsca
            // Dla mapy 50x50: dżungla = 50 * (50*0.2) = 50 * 10 = 500 pól
            // Generujemy 50 roślin, więc dżungla się nie zapełni
            JungleWorldMap map = new JungleWorldMap(50, 50, 50);

            Boundary jungleBoundary = map.getJungleBoundary();

            for (Vector2d position : map.getGrassPositions()) {
                if (position.follows(jungleBoundary.lowerLeft()) &&
                        position.precedes(jungleBoundary.upperRight())) {
                    totalGrassInJungle++;
                } else {
                    totalGrassOutside++;
                }
            }
        }

        int totalGrass = totalGrassInJungle + totalGrassOutside;
        double percentageInJungle = (totalGrassInJungle * 100.0) / totalGrass;

        // Z marginesem błędu 5%
        assertTrue(percentageInJungle >= 75 && percentageInJungle <= 85);
    }

    @Test
    void animalIsPlacedOnMap() {
        JungleWorldMap map = new JungleWorldMap(10, 10, 0);
        Vector2d position = new Vector2d(5, 5);
        Animal animal = new Animal(position, config);

        map.placeAnimal(animal);

        assertTrue(map.isAnimalAt(position));
        assertEquals(1, map.getAnimalsAt(position).size());
    }

    @Test
    void multipleAnimalsCanBeOnSamePosition() {
        JungleWorldMap map = new JungleWorldMap(10, 10, 0);
        Vector2d position = new Vector2d(5, 5);
        Animal animal1 = new Animal(position, config);
        Animal animal2 = new Animal(position, config);
        Animal animal3 = new Animal(position, config);

        map.placeAnimal(animal1);
        map.placeAnimal(animal2);
        map.placeAnimal(animal3);

        List<Animal> animals = map.getAnimalsAt(position);
        assertEquals(3, animals.size());
        assertTrue(animals.contains(animal1));
        assertTrue(animals.contains(animal2));
        assertTrue(animals.contains(animal3));
    }

    @Test
    void animalIsRemovedFromMap() {
        JungleWorldMap map = new JungleWorldMap(10, 10, 0);
        Vector2d position = new Vector2d(3, 7);
        Animal animal = new Animal(position, config);

        map.placeAnimal(animal);
        assertTrue(map.isAnimalAt(position));

        map.removeAnimal(animal);
        assertFalse(map.isAnimalAt(position));
        assertEquals(0, map.getAnimalsAt(position).size());
    }

    @Test
    void grassIsPlacedOnMap() {
        JungleWorldMap map = new JungleWorldMap(10, 10, 0);
        Vector2d position = new Vector2d(3, 3);
        Grass grass = new Grass(position);

        map.placeGrass(grass);

        assertTrue(map.isGrassAt(position));
        assertEquals(grass, map.getGrassAt(position));
    }

    @Test
    void grassIsRemovedFromMap() {
        JungleWorldMap map = new JungleWorldMap(10, 10, 0);
        Vector2d position = new Vector2d(6, 6);
        Grass grass = new Grass(position);

        map.placeGrass(grass);
        assertTrue(map.isGrassAt(position));

        map.removeGrass(grass);
        assertFalse(map.isGrassAt(position));
        assertNull(map.getGrassAt(position));
    }

    @Test
    void generateGrassCreatesCorrectAmount() {
        JungleWorldMap map = new JungleWorldMap(10, 10, 0);
        int numGrass = 10;

        map.generateGrass(numGrass);

        assertEquals(numGrass, map.getGrassPositions().size());
    }
}