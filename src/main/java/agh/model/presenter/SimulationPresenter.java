package agh.model.presenter;

import agh.Simulation;
import agh.SimulationConfig;
import agh.SimulationStatistics;
import agh.model.*;
import agh.model.animal.Animal;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimulationPresenter implements MapChangeListener {
    private static final int MAX_CANVAS_WIDTH = 500;
    private static final int MAX_CANVAS_HEIGHT = 500;
    private static final int MIN_CELL_WIDTH = 15;
    private static final int MIN_CELL_HEIGHT = 15;
    private static final int CELL_WIDTH = 15;
    private static final int CELL_HEIGHT = 15;

    @FXML
    private Stage stage;
    @FXML
    private Canvas mapCanvas;
    @FXML
    private Button stopSimulation;
    @FXML
    private Label animalCountLabel;
    @FXML
    private Label grassCountLabel;
    @FXML
    private Label emptySquaresLabel;
    @FXML
    private Label avgEnergyLabel;
    @FXML
    private Label avgLifeSpanLabel;
    @FXML
    private Label avgChildrenLabel;
    @FXML
    private Label popularGenotypeLabel;


    private double cellSize;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Simulation simulation;
    private AbstractWorldMap worldMap;


    @Override
    public void mapChanged(AbstractWorldMap map, String message) {
        Platform.runLater(() -> {
            this.drawMap();
            updateStats();
        });
    }

    public void createSimulation(SimulationConfig config) {
        this.worldMap = config.worldMap();
        this.worldMap.addObserver(this);
        this.simulation = new Simulation(config);
        drawMap();
        executorService.submit(simulation);
    }

    public void stopSimulation() {
        simulation.stopSimulation();
    }

    public void startSimulation() {
        simulation.startSimulation();
        executorService.submit(simulation);
    }

    public void drawMap() {
        Boundary bounds = worldMap.getMapBoundary();
        Vector2d mapUR = bounds.upperRight();
        Vector2d mapLL = bounds.lowerLeft();
        if (mapUR.getY() > mapUR.getX()) {
            cellSize = (double) MAX_CANVAS_HEIGHT / (mapUR.getY()+2);
        } else {
            cellSize = (double) MAX_CANVAS_WIDTH / (mapUR.getX()+2);
        }

        mapCanvas.setHeight(cellSize * (mapUR.getY() + 2));
        mapCanvas.setWidth(cellSize * (mapUR.getX() + 2));
        double canvasHeight = mapCanvas.getHeight();
        double canvasWidth = mapCanvas.getWidth();
        GraphicsContext graphics = mapCanvas.getGraphicsContext2D();
        clearGrid();

        drawLines(graphics, canvasWidth, canvasHeight);
        drawMapIndex(mapLL, mapUR, graphics, canvasHeight);
        drawWorldElements(mapLL, mapUR, graphics, canvasHeight);
    }

    private void drawWorldElements(Vector2d mapLL, Vector2d mapUR, GraphicsContext graphics, double canvasHeight) {
        for (int x = mapLL.getX(); x <= mapUR.getX(); x++) {
            for (int y = mapLL.getY(); y <= mapUR.getY(); y++) {
                Vector2d position = new Vector2d(x, y);
                if (worldMap.isOccupied(position)) {
                    graphics.strokeText(parseWorldElementToString(position, graphics),
                            x * cellSize + cellSize * 1.5,
                            canvasHeight - (y * cellSize) - (cellSize / 2));
                }
            }
        }
    }

    private String parseWorldElementToString(Vector2d position, GraphicsContext graphics) {
        if (worldMap.isAnimalAt(position)) {
            configureFont(graphics, (int) cellSize/4, Color.BLACK);
            List<Animal> animals = worldMap.getAnimalsAt(position);
            if (animals.size() > 1) {
                return animals.getFirst().toString() + "\n" + animals.getFirst().toString();
            } else {return animals.getFirst().toString();}
        }
        if (worldMap.isGrassAt(position)) {
            configureFont(graphics, (int) cellSize/2, Color.GREEN);
            return worldMap.getGrassAt(position).toString();
        }
        return null;
    }

    private void drawMapIndex(Vector2d mapLL, Vector2d mapUR, GraphicsContext graphics, double canvasHeight) {
        configureFont(graphics, 10, Color.BLACK);
        for (int y = mapLL.getY(); y <= mapUR.getY(); y++) {
            graphics.strokeText(String.valueOf(y), cellSize / 2, canvasHeight - (y * cellSize) - (cellSize / 2));
        }
        for (int x = mapLL.getX(); x <= mapUR.getX(); x++) {
            graphics.strokeText(String.valueOf(x), cellSize * 1.5 + x * cellSize, cellSize / 2);
        }
        graphics.strokeText("y/x", cellSize / 2, cellSize / 2);
    }

    private void drawLines(GraphicsContext graphics, double canvasWidth, double canvasHeight) {

        graphics.setStroke(Color.BLACK);
        graphics.setLineWidth(1);

        for (double x = 0; x < canvasWidth + 3; x += cellSize) {
            graphics.strokeLine(x, 0, x, canvasHeight);
        }
        for (double y = 0; y < canvasHeight + 3; y += cellSize) {
            graphics.strokeLine(0, y, canvasWidth, y);
        }
    }

    private void clearGrid() {
        GraphicsContext graphics = mapCanvas.getGraphicsContext2D();
        graphics.setFill(Color.WHITE);
        graphics.fillRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
    }

    private void configureFont(GraphicsContext graphics, int size, Color color) {
        graphics.setTextAlign(TextAlignment.CENTER);
        graphics.setTextBaseline(VPos.CENTER);
        graphics.setFont(new Font("Arial", size));
        graphics.setFill(color);
        graphics.setStroke(color);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void updateStats() {
        SimulationStatistics stats = simulation.getSimulationStatistics();

        animalCountLabel.setText("Number of living animals: " + stats.animalCount());
        grassCountLabel.setText("Number of grasses: " + stats.grassCount());
        emptySquaresLabel.setText("Number of empty squares: " + stats.emptySquares());
        avgEnergyLabel.setText(String.format("Average energy value for living animals: %.2f", stats.avgEnergy()));
        avgLifeSpanLabel.setText(String.format("Average life span: %.2f", stats.avgLifeSpan()));
        avgChildrenLabel.setText(String.format("Average number of children for living animals: %.2f", stats.avgChildrenCount()));
        popularGenotypeLabel.setText("The most popular genotype: " + stats.popularGenotype());
    }
}
