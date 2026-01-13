package agh;

import agh.presenter.MenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class SimulationApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        showMenu(stage);
    }

    private void showMenu(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("menu.fxml"));
        BorderPane root = loader.load();
        MenuController controller = loader.getController();
        controller.setStage(stage);
        configureMenuStage(stage, root);
        stage.show();
    }

    private void configureMenuStage(Stage stage, BorderPane viewRoot) {
        var scene = new Scene(viewRoot);
        stage.setScene(scene);

        stage.setTitle("Menu");
        stage.minWidthProperty().bind(viewRoot.minWidthProperty());
        stage.minHeightProperty().bind(viewRoot.minHeightProperty());
    }

}
