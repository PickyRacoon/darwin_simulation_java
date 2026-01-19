import agh.simulation.SimulationConfig;
import agh.model.maps.JungleWorldMap;
import agh.model.maps.MapDirection;
import agh.model.animal.Animal;
import agh.model.Vector2d;
import agh.model.animal.Genotype;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AnimalTest {
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
    void animalMovesAndLosesEnergy() {
        Animal animal = new Animal(new Vector2d(2, 2), config);
        Vector2d oldPosition = animal.getPosition();
        int oldEnergy = animal.getEnergy();

        animal.move();

        assertNotEquals(oldPosition, animal.getPosition());
        assertEquals(oldEnergy - 5, animal.getEnergy());
    }

    @Test
    void animalEatsGrassAndEnergyIncreases() {
        Animal animal = new Animal(new Vector2d(0, 0), config);
        int oldEnergy = animal.getEnergy();

        animal.eat(2);

        assertEquals(oldEnergy + 2, animal.getEnergy());
    }

    @Test
    void animalCanBreedWhenEnoughEnergy() {
        Animal animal = new Animal(new Vector2d(1, 1), config);

        assertTrue(animal.canBreed());
    }

    @Test
    void animalBreedingReducesEnergy() {
        Animal animal = new Animal(new Vector2d(1, 1), config);
        int oldEnergy = animal.getEnergy();

        animal.breed();

        assertEquals(oldEnergy - 22, animal.getEnergy());
    }

    @Test
    void animalDiesCorrectly() {
        Animal animal = new Animal(new Vector2d(0, 0), config);
        assertTrue(animal.isAlive());

        animal.die();

        assertFalse(animal.isAlive());

        // powinien nie moc sie juz poruszac
        int energyBeforeMove = animal.getEnergy();
        Vector2d oldPosition = animal.getPosition();
        animal.move();
        assertEquals(energyBeforeMove, animal.getEnergy());
        assertEquals(oldPosition, animal.getPosition());
    }

    @Test
    void animalShouldMoveCorrectlyAfterThreeMoves() {
        Vector2d startPos = new Vector2d(0, 0);
        List<Integer> genes = List.of(1, 1, 1, 0, 0, 0, 0, 0, 0, 0);
        Genotype genotype = new Genotype(genes, config);

        Animal animal = new Animal(startPos, MapDirection.NORTH, genotype, config);

        animal.move();
        assertEquals(new Vector2d(1, 1), animal.getPosition());

        animal.move();
        assertEquals(new Vector2d(2, 1), animal.getPosition());

        animal.move();
        assertEquals(new Vector2d(3, 0), animal.getPosition());
    }
}
