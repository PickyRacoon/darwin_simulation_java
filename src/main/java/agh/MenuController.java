package agh;

import agh.model.AbstractWorldMap;
import agh.model.JungleWorldMap;
import agh.model.presenter.SimulationPresenter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

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
    private Stage stage;


    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void onStartSimulation() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("simulation.fxml"));
        BorderPane root = loader.load();
        SimulationPresenter presenter = loader.getController();
        presenter.setStage(stage);

        AbstractWorldMap worldMap;
        if (!simulationVariation.isSelected()) {
            worldMap = new JungleWorldMap(mapWidth.getValue(), mapHeight.getValue());
        } else {
            worldMap = new JungleWorldMap(mapWidth.getValue(), mapHeight.getValue()); // tutaj będzie druga mapa
        }
        SimulationConfig config = new SimulationConfig(worldMap, numAnimals.getValue(), numGrass.getValue(), grassEnergy.getValue(),
                numDailyGrass.getValue(), animalStartEnergy.getValue(), animalLooseEnergy.getValue(), animalMinBreedEnergy.getValue(),
                animalEnergyUsedToBreed.getValue(), minNumMutations.getValue(), maxNumMutations.getValue(), genotypeLen.getValue());

        presenter.createSimulation(config);

        configureSimulationStage(stage, root);
        stage.show();
    }


    private void configureSimulationStage(Stage stage, BorderPane viewRoot) {
        var scene = new Scene(viewRoot);
        stage.setScene(scene);

        stage.setTitle("Menu");
        stage.minWidthProperty().bind(viewRoot.minWidthProperty());
        stage.minHeightProperty().bind(viewRoot.minHeightProperty());
    }
}
