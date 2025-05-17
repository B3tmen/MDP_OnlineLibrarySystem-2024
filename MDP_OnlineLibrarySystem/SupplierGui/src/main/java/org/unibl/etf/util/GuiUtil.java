package org.unibl.etf.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class GuiUtil {

	public static Optional<ButtonType> showAlert(AlertType alertType, String title, String header, String content, ButtonType... buttonTypes) {
		Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
		if(buttonTypes.length > 0) {
			alert.getButtonTypes().setAll(buttonTypes);
		}

        return alert.showAndWait();
	}

	public static void showScene(String fxmlPath, String title) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(GuiUtil.class.getResource(fxmlPath));
		Parent root = fxmlLoader.load();
		Scene scene = new Scene(root);

		Stage stage = new Stage();
		stage.setTitle(title);
		stage.setScene(scene);
		stage.show();
	}
}
