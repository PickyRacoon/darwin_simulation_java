import agh.model.MapDirection;
import agh.model.animal.Animal;
import agh.model.Vector2d;
import agh.model.animal.Genotype;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AnimalTest {
    @Test
    void animalMovesAndLosesEnergy() {
        Animal animal = new Animal(new Vector2d(2, 2));
        Vector2d oldPosition = animal.getPosition();
        int oldEnergy = animal.getEnergy();

        animal.move();

        assertNotEquals(oldPosition, animal.getPosition());
        assertEquals(oldEnergy - 5, animal.getEnergy());
    }

    @Test
    void animalEatsGrassAndEnergyIncreases() {
        Animal animal = new Animal(new Vector2d(0, 0));
        int oldEnergy = animal.getEnergy();

        animal.eat(2);

        assertEquals(oldEnergy + 2, animal.getEnergy());
    }

    @Test
    void animalCanBreedWhenEnoughEnergy() {
        Animal animal = new Animal(new Vector2d(1, 1));

        assertTrue(animal.canBreed());
    }

    @Test
    void animalBreedingReducesEnergy() {
        Animal animal = new Animal(new Vector2d(1, 1));
        int oldEnergy = animal.getEnergy();

        animal.breed();

        assertEquals(oldEnergy - 22, animal.getEnergy());
    }

    @Test
    void animalDiesCorrectly() {
        Animal animal = new Animal(new Vector2d(0, 0));
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
        List<Integer> genes = List.of(1, 5, 0, 0, 0, 0, 0, 0, 0, 0);
        Genotype genotype = new Genotype(genes);

        Animal animal = new Animal(startPos, MapDirection.NORTH, genotype);

        animal.move();
        assertEquals(new Vector2d(1, 1), animal.getPosition());

        animal.move();
        assertEquals(new Vector2d(0, 1), animal.getPosition());

        animal.move();
        assertEquals(new Vector2d(-1, 1), animal.getPosition());
    }
}
