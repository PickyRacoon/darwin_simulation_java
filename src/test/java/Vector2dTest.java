import agh.model.Vector2d;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Vector2dTest {
    private Vector2d vecPosPos = new Vector2d(2, 4);
    private Vector2d vecNegNeg = new Vector2d(-4, -5);
    private Vector2d vecPosNeg1 = new Vector2d(7, -9);
    private Vector2d vecPosNeg2 = new Vector2d(7, -9);
    private Vector2d vecZeroZero = new Vector2d(0, 0);

    @Test
    void equalsMethod() {
        assertTrue(vecPosNeg1.equals(vecPosNeg2));
        assertFalse(vecZeroZero.equals(vecNegNeg));
        assertFalse(vecZeroZero.equals(null));
        assertTrue(vecNegNeg.equals(vecNegNeg));
        assertFalse(vecPosPos.equals("wektor"));
    }

    @Test
    void toStringMethod() {
        assertEquals("(7,-9)", vecPosNeg1.toString());
    }

    @Test
    void precederMethod() {
        assertTrue(vecNegNeg.precedes(vecPosPos));
        assertFalse(vecZeroZero.precedes(vecPosNeg2));
        assertFalse(vecPosPos.precedes(vecNegNeg));
    }

    @Test
    void followsMethod() {
        assertTrue(vecPosPos.follows(vecNegNeg));
        assertFalse(vecZeroZero.follows(vecPosNeg2));
        assertFalse(vecNegNeg.follows(vecPosPos));
    }

    @Test
    void addMethod() {
        assertEquals(new Vector2d(-2, -1), vecPosPos.add(vecNegNeg));
    }

    @Test
    void substractMethod() {
        assertEquals(new Vector2d(6, 9), vecPosPos.subtract(vecNegNeg));
    }

    @Test
    void oppositeMethod() {
        assertEquals(new Vector2d(-7, 9), vecPosNeg1.opposite());
        assertEquals(new Vector2d(0, 0), vecZeroZero.opposite());
    }

    @Test
    void upperRightMethod() {
        assertEquals(new Vector2d(7, 0), vecZeroZero.upperRight(vecPosNeg1));
    }

    @Test
    void lowerLeftMethod() {
        assertEquals(new Vector2d(0, -9), vecPosNeg1.lowerLeft(vecZeroZero));
    }

    @Test
    void hashCodeMethod() {
        assertEquals(vecPosNeg1.hashCode(), vecPosNeg2.hashCode());
        assertNotEquals(vecNegNeg.hashCode(), vecPosPos.hashCode());
    }
}
