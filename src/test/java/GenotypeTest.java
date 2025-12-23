import agh.SimulationConfig;
import agh.model.JungleWorldMap;
import agh.model.Vector2d;
import agh.model.animal.Animal;
import agh.model.animal.Genotype;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GenotypeTest {
    SimulationConfig config = new SimulationConfig(
            new JungleWorldMap(10, 10),
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
    void genotypeShouldHaveCorrectLength() {
        Genotype genotype = new Genotype(config);

        assertEquals(config.genotypeLen(), genotype.getGenom().size());
    }

    @Test
    void genesShouldBeInCorrectRange() {
        Genotype genotype = new Genotype(config);

        for (int gene : genotype.getGenom()) {
            assertTrue(gene >= 0 && gene <= 7);
        }
    }

    @Test
    void correctActiveGenomIndex() {
        Genotype genotype = new Genotype(config);

        genotype.nextGenomIndex();

        assertEquals(1, genotype.getActievGenomIndex());
    }

    @Test
    void activeGenomIndexShouldWrapAround() {
        Genotype genotype = new Genotype(config);

        for (int i = 0; i < config.genotypeLen(); i++) {
            genotype.nextGenomIndex();
        }

        assertEquals(0, genotype.getActievGenomIndex());
    }

    @Test
    void crossGenotypeShouldCreateValidChildGenotype() {
        Animal parent1 = new Animal(new Vector2d(0, 0), config);
        Animal parent2 = new Animal(new Vector2d(1, 1), config);

        Genotype child = Genotype.crossGenotype(parent1, parent2, config);

        assertEquals(config.genotypeLen(), child.getGenom().size());

        for (int gene : child.getGenom()) {
            assertTrue(gene >= 0 && gene <= 7);
        }
    }

    @Test
    void mutationChangesSomeGenesCorrectly() {
        List<Integer> genes = List.of(0,0,0,0,0,0,0,0,0,0);
        Genotype genotype = new Genotype(genes, config);

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
