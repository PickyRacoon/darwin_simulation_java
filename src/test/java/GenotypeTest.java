import agh.model.Vector2d;
import agh.model.animal.Animal;
import agh.model.animal.Genotype;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GenotypeTest {
    @Test
    void genotypeShouldHaveCorrectLength() {
        Genotype genotype = new Genotype();

        assertEquals(10, genotype.getGenom().size());
    }

    @Test
    void genesShouldBeInCorrectRange() {
        Genotype genotype = new Genotype();

        for (int gene : genotype.getGenom()) {
            assertTrue(gene >= 0 && gene <= 7);
        }
    }

    @Test
    void activeGenomIndexShouldWrapAround() {
        Genotype genotype = new Genotype();

        for (int i = 0; i < 10; i++) {
            genotype.nextGenomIndex();
        }

        assertEquals(0, genotype.getActievGenomIndex());
    }

    @Test
    void crossGenotypeShouldCreateValidChildGenotype() {
        Animal parent1 = new Animal(new Vector2d(0, 0));
        Animal parent2 = new Animal(new Vector2d(1, 1));

        Genotype child = Genotype.crossGenotype(parent1, parent2);

        assertEquals(10, child.getGenom().size());

        for (int gene : child.getGenom()) {
            assertTrue(gene >= 0 && gene <= 7);
        }
    }

    @Test
    void mutationChangesSomeGenesCorrectly() {
        List<Integer> genes = List.of(0,0,0,0,0,0,0,0,0,0);
        Genotype genotype = new Genotype(genes);

        boolean changed = false;

        genotype.mutateGenom();
        if (!genotype.getGenom().equals(genes)) {
            changed = true;
        }

        assertTrue(changed);

        for (int gene : genotype.getGenom()) {
            assertTrue(gene >= 0 && gene <= 7);
        }
    }
}
