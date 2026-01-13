package agh.model.util;

import agh.model.maps.AbstractWorldMap;
import agh.model.Vector2d;
import agh.model.animal.Animal;

import java.util.List;

public class ConsoleMapVisualizer {
    private static final String EMPTY_CELL = " ";
    private static final String FRAME_SEGMENT = "-";
    private static final String CELL_SEGMENT = "|";
    private final AbstractWorldMap map;

    /**
     * Initializes the MapVisualizer with an instance of map to visualize.
     *
     * @param map
     */
    public ConsoleMapVisualizer(AbstractWorldMap map) {
        this.map = map;
    }

    /**
     * Convert selected region of the map into a string. It is assumed that the
     * indices of the map will have no more than two characters (including the
     * sign).
     *
     * @param lowerLeft  The lower left corner of the region that is drawn.
     * @param upperRight The upper right corner of the region that is drawn.
     * @return String representation of the selected region of the map.
     */
    public String draw(Vector2d lowerLeft, Vector2d upperRight) {
        StringBuilder builder = new StringBuilder();
        for (int i = upperRight.getY() + 1; i >= lowerLeft.getY() - 1; i--) {
            if (i == upperRight.getY() + 1) {
                builder.append(drawHeader(lowerLeft, upperRight));
            }
            builder.append(String.format("%3d: ", i));
            for (int j = lowerLeft.getX(); j <= upperRight.getX() + 1; j++) {
                if (i < lowerLeft.getY() || i > upperRight.getY()) {
                    builder.append(drawFrame(j <= upperRight.getX()));
                } else {
                    builder.append(CELL_SEGMENT);
                    if (j <= upperRight.getX()) {
                        builder.append(drawObject(new Vector2d(j, i)));
                    }
                }
            }
            builder.append(System.lineSeparator());
        }
        return builder.toString();
    }

    private String drawFrame(boolean innerSegment) {
        if (innerSegment) {
            return FRAME_SEGMENT + FRAME_SEGMENT;
        } else {
            return FRAME_SEGMENT;
        }
    }

    private String drawHeader(Vector2d lowerLeft, Vector2d upperRight) {
        StringBuilder builder = new StringBuilder();
        builder.append(" y\\x ");
        for (int j = lowerLeft.getX(); j < upperRight.getX() + 1; j++) {
            builder.append(String.format("%2d", j));
        }
        builder.append(System.lineSeparator());
        return builder.toString();
    }

    private String drawObject(Vector2d currentPosition) {
        if (this.map.isOccupied(currentPosition)) {
            if (this.map.isAnimalAt(currentPosition)) {
                 List<Animal> animals= this.map.getAnimalsAt(currentPosition);
                 if (animals.size() == 1) {
                     return animals.getFirst().toString();
                 } else {
                     return String.valueOf(animals.size());
                 }
            }
            if (this.map.isGrassAt(currentPosition)){
                return "*";
            }
            // znowu to jest okropnie napisane ale chciałem móc widzieć jak jest więcej zwierząt
            // i tak cała ta klasa to placeholder do testów :P
        }
        return EMPTY_CELL;
    }
}

