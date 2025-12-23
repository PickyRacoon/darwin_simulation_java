package agh;

import agh.model.JungleWorldMap;
import agh.model.presenter.SimulationPresenter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuController {
    private Stage stage;
    @FXML
    private Spinner<Integer> mapWidth;
    @FXML
    private Spinner<Integer> mapHeigth;
    @FXML
    private Spinner<Integer> numAnimals;
    @FXML
    private Spinner<Integer> numGrass;


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

        presenter.createSimulation(new SimulationConfig(new JungleWorldMap(mapWidth.getValue(), mapHeigth.getValue()), numAnimals.getValue(), numGrass.getValue()));

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
