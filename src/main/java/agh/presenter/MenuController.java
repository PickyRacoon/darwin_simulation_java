package agh.presenter;

import agh.CSVLogger;
import agh.simulation.FarmingStatistics;
import agh.simulation.SimulationConfig;
import agh.model.maps.AbstractWorldMap;
import agh.model.maps.FarmingWorldMap;
import agh.model.maps.JungleWorldMap;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class MenuController {
    @FXML
    private Spinner<Integer> grassEnergy;
    @FXML
    private CheckBox simulationVariation;
    @FXML
    private Spinner<Integer> genotypeLen;
    @FXML
    private Spinner<Integer> maxNumMutations;
    @FXML
    private Spinner<Integer> minNumMutations;
    @FXML
    private Spinner<Integer> animalEnergyUsedToBreed;
    @FXML
    private Spinner<Integer> animalMinBreedEnergy;
    @FXML
    private Spinner<Integer> animalLooseEnergy;
    @FXML
    private Spinner<Integer> animalStartEnergy;
    @FXML
    private Spinner<Integer> numDailyGrass;
    @FXML
    private Spinner<Integer> mapWidth;
    @FXML
    private Spinner<Integer> mapHeight;
    @FXML
    private Spinner<Integer> numAnimals;
    @FXML
    private Spinner<Integer> numGrass;
    @FXML
    private VBox farmingBox;
    @FXML
    private Spinner<Integer> minEnergyToCultivate;
    @FXML
    private Spinner<Integer> daysLandIsFertile;
    @FXML
    private Spinner<Integer> numMeals;
    @FXML
    private Spinner<Integer> fertileLandValue;

    private Stage stage;
    private CSVLogger csvLogger;

    public void setStage(Stage stage) {
        this.stage = stage;

        stage.setOnCloseRequest(event -> {
            System.out.println("Exiting application");
            Platform.exit();       // konczy cale gui
            System.exit(0);     // konczy jvm
        });
    }

    @FXML
    private void onCultivatingToggle() {
        boolean enabled = simulationVariation.isSelected();
        farmingBox.setVisible(enabled);
        farmingBox.setManaged(enabled);
    }

    @FXML
    public void onStartSimulation() throws IOException {
        // nowy stage dla kazdej symulacji
        Stage simulationStage = new Stage();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("simulation.fxml"));
        BorderPane root = loader.load();

        SimulationPresenter presenter = loader.getController();
        presenter.setStage(simulationStage);

        File folder = new File("sim_logs");
        if (!folder.exists()) {
            folder.mkdir();
        }

        String fileName = "simulation_" + System.currentTimeMillis() + ".csv";
        File file = new File(folder, fileName);
        csvLogger = new CSVLogger(file);

        SimulationConfig config = getSimulationConfig();

        presenter.createSimulation(config, csvLogger);

        simulationStage.setOnCloseRequest(event -> {
            presenter.stopSimulation();
            if (csvLogger != null) {
                csvLogger.close();
            }
            System.out.println("Simulation closed");
        });

        configureSimulationStage(simulationStage, root);
        simulationStage.show();
    }

    private SimulationConfig getSimulationConfig() {
        FarmingStatistics farmingStatistics = null;
        if (simulationVariation.isSelected()) {
            farmingStatistics = new FarmingStatistics(
                    minEnergyToCultivate.getValue(),
                    daysLandIsFertile.getValue(),
                    numMeals.getValue(),
                    fertileLandValue.getValue()
            );
        }

        AbstractWorldMap worldMap;
        if (!simulationVariation.isSelected()) {
            worldMap = new JungleWorldMap(mapWidth.getValue(), mapHeight.getValue(), numGrass.getValue());
        } else {
            worldMap = new FarmingWorldMap(mapWidth.getValue(), mapHeight.getValue(), numGrass.getValue(), farmingStatistics);
        }

        return new SimulationConfig(
                worldMap,
                numAnimals.getValue(),
                numGrass.getValue(),
                grassEnergy.getValue(),
                numDailyGrass.getValue(),
                animalStartEnergy.getValue(),
                animalLooseEnergy.getValue(),
                animalMinBreedEnergy.getValue(),
                animalEnergyUsedToBreed.getValue(),
                minNumMutations.getValue(),
                maxNumMutations.getValue(),
                genotypeLen.getValue()
        );
    }

    private void configureSimulationStage(Stage stage, BorderPane viewRoot) {
        var scene = new Scene(viewRoot);
        stage.setScene(scene);

        stage.setTitle("Symulacja");
        stage.minWidthProperty().bind(viewRoot.minWidthProperty());
        stage.minHeightProperty().bind(viewRoot.minHeightProperty());
    }
}
