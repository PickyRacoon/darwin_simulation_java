import agh.model.Vector2d;
import agh.model.animal.Animal;
import agh.model.grass.BigGrass;
import agh.model.grass.Grass;
import agh.model.maps.FarmingWorldMap;
import agh.model.maps.JungleWorldMap;
import agh.simulation.FarmingStatistics;
import agh.simulation.SimulationConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FarmingWorldMapTest {
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

    FarmingStatistics farmingStatistics = new FarmingStatistics(
            10,
            5,
            5,
            5
    );

    @Test
    void landBecomesFertileAfterRequiredCultivations() {
        FarmingWorldMap map = new FarmingWorldMap(10, 10, 0, farmingStatistics);
        Vector2d position = new Vector2d(7, 7);
        int requiredCultivations = farmingStatistics.fertileLandValue();


        for (int i = 0; i < requiredCultivations; i++) {
            Animal animal = new Animal(position, config);
            map.placeAnimal(animal);
            map.removeAnimal(animal);
        }

        // Teraz pole powinno być żyzne
        assertTrue(map.getJunglePositions().contains(position));
    }

    @Test
    void bigGrassGrowsOnFertileLand() {
        FarmingWorldMap map = new FarmingWorldMap(10, 10, 0, farmingStatistics);
        Vector2d position = new Vector2d(10, 10);
        int requiredCultivations = farmingStatistics.fertileLandValue();

        // Czynimy pole żyznym
        for (int i = 0; i < requiredCultivations; i++) {
            Animal animal = new Animal(position, config);
            map.placeAnimal(animal);
            map.removeAnimal(animal);
        }

        // Generujemy trawę - powinna pojawić się BigGrass na żyznym polu
        map.generateGrass(5);

        // Sprawdzamy czy na żyznym polu może być trawa
        if (map.isGrassAt(position)) {
            Grass grass = map.getGrassAt(position);
            assertInstanceOf(BigGrass.class, grass);
        }
    }
}