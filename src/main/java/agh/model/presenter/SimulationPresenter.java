package agh.model.presenter;

import agh.Simulation;
import agh.SimulationConfig;
import agh.model.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimulationPresenter implements MapChangeListener {
    private static final int CELL_WIDTH = 30;
    private static final int CELL_HEIGHT = 30;

    @FXML
    private Stage stage;
    @FXML
    private Canvas mapCanvas;
    @FXML
    private Button stopSimulation;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Simulation simulation;
    private AbstractWorldMap worldMap;


    @Override
    public void mapChanged(AbstractWorldMap map, String message) {
        Platform.runLater(() -> {
            this.drawMap();
        });
    }


    public void createSimulation(SimulationConfig config) {
        this.worldMap = config.worldMap();
        this.worldMap.addObserver(this);
        this.simulation = new Simulation(config);
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
        Vector2d mapLL = bounds.lowerLeft();
        Vector2d mapUR = bounds.upperRight();
        Vector2d mapSize = mapUR.subtract(mapLL);
        mapCanvas.setHeight(CELL_HEIGHT * (mapSize.getY() + 2));
        mapCanvas.setWidth(CELL_WIDTH * (mapSize.getX() + 2));
        double canvasHeight = mapCanvas.getHeight();
        double canvasWidth = mapCanvas.getWidth();
        clearGrid();

        GraphicsContext graphics = mapCanvas.getGraphicsContext2D();
        graphics.setStroke(Color.BLACK);
        graphics.setLineWidth(1);

        for (int x = 0; x < canvasWidth + 3; x += CELL_WIDTH) {
            graphics.strokeLine(x, 0, x, canvasHeight);
        }
        for (int y = 0; y < canvasHeight + 3; y += CELL_HEIGHT) {
            graphics.strokeLine(0, y, canvasWidth, y);
        }

        configureFont(graphics, 10, Color.BLUE);


        for (int y = mapLL.getY(); y <= mapUR.getY(); y++) {
            graphics.strokeText(String.valueOf(y), CELL_WIDTH / 2, canvasHeight - (y * CELL_HEIGHT) - (CELL_HEIGHT / 2));
        }

        for (int x = mapLL.getX(); x <= mapUR.getX(); x++) {
            graphics.strokeText(String.valueOf(x), CELL_WIDTH * 1.5 + x * CELL_WIDTH, CELL_HEIGHT / 2);
        }

        graphics.strokeText("y/x", CELL_WIDTH / 2, CELL_HEIGHT / 2);

        for (int x = mapLL.getX(); x <= mapUR.getX(); x++) {
            for (int y = mapLL.getY(); y <= mapUR.getY(); y++) {
                Vector2d position = new Vector2d(x, y);
                if (worldMap.isOccupied(position)) {
                    graphics.strokeText(worldMap.objectAt(position).toString(),
                            x * CELL_WIDTH + CELL_WIDTH * 1.5,
                            canvasHeight - (y * CELL_HEIGHT) - (CELL_HEIGHT / 2));
                }
            }
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
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
