import agh.Simulation;
import agh.SimulationConfig;
import agh.model.AbstractWorldMap;
import agh.model.JungleWorldMap;
import agh.model.animal.Animal;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimulationTest {

    @Test
    void testSimulationWithoutAnimals() {
        SimulationConfig config = new SimulationConfig(
                new JungleWorldMap(10, 10, 2),
                0,
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

        AbstractWorldMap map = config.worldMap();
        Simulation sim = new Simulation(config);

        List<Animal> animals = map.getAllAnimals();
        assertEquals(0, animals.size());

        sim.growNewPlants(3);

        assertTrue(map.getGrassPositions().size() >= 3);

        sim.deleteDeadAnimals();
        sim.moveAnimals();
        sim.eat();
        sim.procreate();

        assertEquals(0, map.getAllAnimals().size());
    }
}
