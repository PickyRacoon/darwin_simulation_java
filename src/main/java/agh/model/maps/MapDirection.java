package agh.model.maps;

import agh.model.util.RandomNumber;
import agh.model.Vector2d;

public enum MapDirection {
    NORTH,
    NORTHEAST,
    EAST,
    SOUTHEAST,
    SOUTH,
    SOUTHWEST,
    WEST,
    NORTHWEST;

    @Override
    public String toString() {
        return switch (this) {
            case NORTH -> "N";
            case NORTHEAST -> "NE";
            case EAST -> "E";
            case SOUTHEAST -> "SE";
            case SOUTH -> "S";
            case SOUTHWEST -> "SW";
            case WEST -> "W";
            case NORTHWEST -> "NW";
        };
    }

    public Vector2d toUnitVector() {
        return switch (this) {
            case NORTH -> new Vector2d(0, 1);
            case NORTHEAST -> new Vector2d(1, 1);
            case EAST -> new Vector2d(1, 0);
            case SOUTHEAST -> new Vector2d(1, -1);
            case SOUTH -> new Vector2d(0, -1);
            case SOUTHWEST -> new Vector2d(-1, -1);
            case WEST -> new Vector2d(-1, 0);
            case NORTHWEST -> new Vector2d(-1, 1);
        };
    }

    public static MapDirection generateRandomDirection() {
        MapDirection[] directions = MapDirection.values();
        return directions[RandomNumber.getRandomNumberInRange(0, directions.length-1)];
    }

    public MapDirection rotation(int rotationNumber) {
        int size = MapDirection.values().length;
        int newIndex = (this.ordinal() + rotationNumber) % size;
        return MapDirection.values()[newIndex];
    }
}
