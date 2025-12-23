package agh;


public class World {

    public static void main(String[] args) {
        Simulation simulation = new Simulation(10, 10, 2, 0);
        for (int day = 0; day < 100; day++) {
            simulation.run();
        }
    }
}
