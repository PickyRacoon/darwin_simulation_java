package agh.model;

import agh.model.maps.AbstractWorldMap;

public interface MapChangeListener {
    void mapChanged(AbstractWorldMap map, String message);
}
