import agh.model.maps.MapDirection;
import agh.model.Vector2d;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class
MapDirectionTest {
    @Test
    void toUnitVectorReturnsCorrectVector() {
        assertEquals(new Vector2d(0, 1), MapDirection.NORTH.toUnitVector());
        assertEquals(new Vector2d(1, 1), MapDirection.NORTHEAST.toUnitVector());
        assertEquals(new Vector2d(1, 0), MapDirection.EAST.toUnitVector());
        assertEquals(new Vector2d(1, -1), MapDirection.SOUTHEAST.toUnitVector());
        assertEquals(new Vector2d(0, -1), MapDirection.SOUTH.toUnitVector());
        assertEquals(new Vector2d(-1, -1), MapDirection.SOUTHWEST.toUnitVector());
        assertEquals(new Vector2d(-1, 0), MapDirection.WEST.toUnitVector());
        assertEquals(new Vector2d(-1, 1), MapDirection.NORTHWEST.toUnitVector());
    }

    @Test
    void rotationReturnsCorrectDirection() {
        assertEquals(MapDirection.NORTH, MapDirection.NORTH.rotation(0));
        assertEquals(MapDirection.NORTHEAST, MapDirection.NORTH.rotation(1));
        assertEquals(MapDirection.EAST, MapDirection.NORTH.rotation(2));
        assertEquals(MapDirection.NORTH, MapDirection.NORTHWEST.rotation(1));
        assertEquals(MapDirection.NORTHWEST, MapDirection.NORTHWEST.rotation(0));
    }
}
