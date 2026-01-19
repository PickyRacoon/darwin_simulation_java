package agh.model;
import agh.model.animal.AnimalStatus;

public interface AnimalChangeListener {
    void animalChanged(AnimalStatus animalStatus);
}
