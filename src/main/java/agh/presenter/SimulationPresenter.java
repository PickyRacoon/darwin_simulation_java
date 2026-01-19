package agh.presenter;

import agh.CSVLogger;
import agh.model.maps.Boundary;
import agh.simulation.Simulation;
import agh.simulation.SimulationConfig;
import agh.simulation.SimulationStatistics;
import agh.model.*;
import agh.model.animal.Animal;
import agh.model.animal.AnimalStatus;
import agh.model.maps.AbstractWorldMap;
import agh.model.maps.FarmingWorldMap;
import agh.model.maps.JungleWorldMap;
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
import java.util.concurrent.Future;

public class SimulationPresenter implements MapChangeListener, AnimalChangeListener {
    private static final int MAX_CANVAS_WIDTH = 500;
    private static final int MAX_CANVAS_HEIGHT = 500;

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

    // Staty zwierzaka
    @FXML
    private Label animalGenome;
    @FXML
    private Label activeGenomeIndex;
    @FXML
    private Label animalEnergy;
    @FXML
    private Label numPlantEaten;
    @FXML
    private Label numKids;
    @FXML
    private Label numDescendants;
    @FXML
    private Label daysSurvied;
    @FXML
    private Label diedOn;

    private double cellSize;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Future<?> simulationFuture;
    private Simulation simulation;
    private AbstractWorldMap worldMap;
    private Animal currentlyWatchedAnimal;
    private Vector2d lastClickedPosition = null;
    private int currentAnimalIndex = 0;

    @Override
    public void mapChanged(AbstractWorldMap map, String message) {
        Platform.runLater(() -> {
            this.drawMap();
            updateStats();
        });
    }

    public void createSimulation(SimulationConfig config, CSVLogger csvLogger) {
        this.worldMap = config.worldMap();
        this.worldMap.addObserver(this);
        this.simulation = new Simulation(config, csvLogger);
        drawMap();
        executorService.submit(simulation);
        simulationFuture = executorService.submit(simulation);
    }

    public void stopSimulation() {
        simulation.stopSimulation();
        if (simulationFuture != null && !simulationFuture.isDone()) {
            simulationFuture.cancel(true);
        }
        seeAnimalStats();
    }

    public void startSimulation() {
        simulation.startSimulation();
        executorService.submit(simulation);
        if (simulationFuture == null || simulationFuture.isDone()) {
            simulationFuture = executorService.submit(simulation);
        }
        turnOffAnimalStats();
    }

    public void seeAnimalStats() {
        mapCanvas.setOnMouseClicked(event -> {
            Vector2d positionClicked = canvasXYtoVector(event.getX(), event.getY());
            if (worldMap.isAnimalAt(positionClicked)) {
                List<Animal> animals = worldMap.getAnimalsAt(positionClicked);
                if (positionClicked.equals(lastClickedPosition)) {
                    currentAnimalIndex = (currentAnimalIndex + 1) % animals.size();
                } else {
                    currentAnimalIndex = 0;
                    lastClickedPosition = positionClicked;
                }
                Animal animal = animals.get(currentAnimalIndex);

                if (currentlyWatchedAnimal != null) {
                    currentlyWatchedAnimal.removeObserver(this);
                }
                
                animal.addObserver(this);
                currentlyWatchedAnimal = animal;
            }
        });
    }

    private void turnOffAnimalStats() {
        mapCanvas.setOnMouseClicked(null);
    }

    public Vector2d canvasXYtoVector(double x, double y) {
        return new Vector2d((int) ((x + cellSize) / cellSize - 2), (int) (-1 * (y - mapCanvas.getHeight()) / cellSize));
    }

    public void drawMap() {
        Boundary bounds = worldMap.getMapBoundary();
        Vector2d mapUR = bounds.upperRight();
        Vector2d mapLL = bounds.lowerLeft();
        if (mapUR.getY() > mapUR.getX()) {
            cellSize = (double) MAX_CANVAS_HEIGHT / (mapUR.getY() + 2);
        } else {
            cellSize = (double) MAX_CANVAS_WIDTH / (mapUR.getX() + 2);
        }

        mapCanvas.setHeight(cellSize * (mapUR.getY() + 2));
        mapCanvas.setWidth(cellSize * (mapUR.getX() + 2));
        double canvasHeight = mapCanvas.getHeight();
        double canvasWidth = mapCanvas.getWidth();
        GraphicsContext graphics = mapCanvas.getGraphicsContext2D();
        clearGrid();
        drawSpecialFields(graphics, canvasHeight);
        drawLines(graphics, canvasWidth, canvasHeight);
        drawMapIndex(mapLL, mapUR, graphics, canvasHeight);
        drawWorldElements(mapLL, mapUR, graphics, canvasHeight);
    }

    private void drawHealthBar(GraphicsContext graphics, Animal animal, int x, int y, double canvasHeight) {
        double barWidth = cellSize;
        double barHeight = cellSize / 4;
        double energyLevel = Math.max(0, Math.min(1, (double) animal.getEnergy() / 100));
        double barFill = barWidth * energyLevel;

        double barX = x * cellSize + cellSize;
        double barY = canvasHeight - y * cellSize - cellSize;

        Color color;
        if (energyLevel > 0.6) color = Color.GREENYELLOW;
        else if  (energyLevel > 0.36 ) color = Color.YELLOW;
        else color = Color.RED;

        graphics.setFill(color);
        graphics.fillRect(barX, barY, barFill, barHeight);

        graphics.setStroke(Color.BLACK);
        graphics.strokeRect(barX, barY, barWidth, barHeight);
    }

    private void drawWorldElements(Vector2d mapLL, Vector2d mapUR, GraphicsContext graphics, double canvasHeight) {
        for (int x = mapLL.getX(); x <= mapUR.getX(); x++) {
            for (int y = mapLL.getY(); y <= mapUR.getY(); y++) {
                Vector2d position = new Vector2d(x, y);

                if (worldMap.isAnimalAt(position)) {
                    List<Animal> animals = worldMap.getAnimalsAt(position);

                    if (animals.size() == 1) {
                        drawHealthBar(graphics, animals.getFirst(), x, y, canvasHeight);
                    }

                    drawAnimalsAt(position, graphics, canvasHeight);
                    continue;
                }

                if (worldMap.isGrassAt(position)) {
                    drawGrass(position, graphics, canvasHeight);
                }
            }
        }
    }


    private boolean isMostPopularGenotype(Animal animal) {
        SimulationStatistics stats = simulation.getSimulationStatistics();
        return animal.getGenotype().getGenom().equals(stats.popularGenotype());
    }

    private void drawAnimalsAt(Vector2d position, GraphicsContext graphics, double canvasHeight) {
        List<Animal> animals = worldMap.getAnimalsAt(position);
        int limit = Math.min(2, animals.size());

        for (int i = 0; i < limit; i++) {
            Animal animal = animals.get(i);

            if (isMostPopularGenotype(animal)) {
                configureFont(graphics, (int) cellSize / 4, Color.RED);
            } else {
                configureFont(graphics, (int) cellSize / 4, Color.BLACK);
            }

            graphics.strokeText(animal.toString(), position.getX() * cellSize + cellSize * 1.5,
                    canvasHeight - (position.getY() * cellSize) - (cellSize / 2) + i * 12);
        }
    }

    private void drawGrass(Vector2d position, GraphicsContext graphics, double canvasHeight) {
        configureFont(graphics, (int) cellSize / 2, Color.GREEN);

        graphics.strokeText(worldMap.getGrassAt(position).toString(), position.getX() * cellSize + cellSize * 1.5,
                canvasHeight - (position.getY() * cellSize) - (cellSize / 2));
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

    private void drawSpecialFields(GraphicsContext graphics, double canvasHeight) {
        if (worldMap instanceof JungleWorldMap jungleWorldMap) {
            for (Vector2d pos : jungleWorldMap.getJungleBoundary().getAllPositions()) {
                double x = (pos.getX() + 1) * cellSize;
                double y = canvasHeight - pos.getY() * cellSize - cellSize;

                graphics.setFill(Color.LIGHTGREEN);
                graphics.fillRect(x, y, cellSize, cellSize);
            }
        } else if (worldMap instanceof FarmingWorldMap farmingWorldMap) {
            for (Vector2d pos : farmingWorldMap.getJunglePositions()) {
                double x = (pos.getX() + 1) * cellSize;
                double y = canvasHeight - pos.getY() * cellSize - cellSize;

                graphics.setFill(Color.LIGHTGREEN);
                graphics.fillRect(x, y, cellSize, cellSize);
            }
        }
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

    @Override
    public void animalChanged(AnimalStatus animalStatus) {
        Platform.runLater(() -> {
        animalGenome.setText("Genome: " + animalStatus.genom().toString());
        activeGenomeIndex.setText("Genome index: " + animalStatus.activeGenomIndex());
        animalEnergy.setText("Energy: " + animalStatus.energy());
        numPlantEaten.setText("Number of plants eaten: " + animalStatus.numPlantsEaten());
        numKids.setText("Number of kids: " + animalStatus.numKids());
        numDescendants.setText("Number of grandchildren: " + animalStatus.numDescendants());
        daysSurvied.setText("Days Survived: " + animalStatus.daysSurvived());
        diedOn.setText("Died on: " + animalStatus.diedOnDay());
        });
    }
}
