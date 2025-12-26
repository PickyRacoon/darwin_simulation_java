package agh.model;

import java.util.List;

public class JungleWorldMap extends AbstractWorldMap{
    private final Boundary jungle;

    public JungleWorldMap(int width, int height, int numGrass) {
        super(width, height, numGrass);
        this.jungle = createJungle();
        generateGrass(numGrass);
    }

    private Boundary createJungle() {
        int jungleHeight = (int) (getHeight() * 0.2);
        int jungleStart = (getHeight() - jungleHeight) / 2;
        int jungleEnd = jungleStart + jungleHeight - 1;
        return new Boundary(new Vector2d(0, jungleStart), new Vector2d(getWidth() - 1, jungleEnd));
    }

    @Override
    protected List<Vector2d> getJunglePositions() {
        return jungle.getAllPositions();
    }

    public Boundary getJungleBoundary() {
        return jungle;
    }

}
