package agh.model.util;

import agh.model.MapChangeListener;
import agh.model.maps.AbstractWorldMap;

public class ConsoleMapDisplay implements MapChangeListener {

    public void mapChanged(AbstractWorldMap map, String message) {
        System.out.println(message);
        System.out.println(map);
    };
}
