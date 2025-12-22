package agh.model;

import agh.model.util.ConsoleMapVisualizer;

public class ConsoleMapDisplay implements MapChangeListener {

    public void mapChanged(AbstractWorldMap map, String message) {
        System.out.println(message);
        System.out.println(map);
    };
}
