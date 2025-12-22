package agh.model;

import agh.model.animal.Animal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JungleWorldMap extends AbstractWorldMap{
    private final Boundary jungle;

    public JungleWorldMap(int width, int height) {
        super(width, height);
        this.jungle = createJungle();
    }

    private Boundary createJungle() {
        int jungleHeight = (int) (getHeight() * 0.2);
        int jungleStart = (getHeight() - jungleHeight) / 2;
        int jungleEnd = jungleStart + jungleHeight - 1;
        return new Boundary(new Vector2d(0, jungleStart), new Vector2d(getWidth() - 1, jungleEnd));
    }

    public Boundary getJungleBoundary() {
        return jungle;
    }

}
