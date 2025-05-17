package org.unibl.etf.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;

public class FxmlViewManager {
    private String fxmlPath;
    private String title;
    private Object controllerClass;

    public FxmlViewManager(String fxmlPath, String title, Object... controllerClass) {
        this.fxmlPath = fxmlPath;
        this.title = title;

        if(controllerClass.length > 0){
            this.controllerClass = controllerClass[0];
        }
        else{
            this.controllerClass = null;
        }
    }

    public void showView(){
        Parent root = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        if(controllerClass != null){
            loader.setController(controllerClass);
        }

        try{
            root = loader.load();
        } catch (IOException e){
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }

        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle(title);
        stage.getIcons().add(new Image(getClass().getResource(ImagePaths.APP_ICON).toExternalForm()));

        stage.show();
    }
}
