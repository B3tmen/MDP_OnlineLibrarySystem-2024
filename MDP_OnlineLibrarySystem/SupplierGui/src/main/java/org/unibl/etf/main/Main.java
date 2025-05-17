package org.unibl.etf.main;

import javafx.application.Application;
import javafx.stage.Stage;
import org.unibl.etf.util.ConfigReader;
import org.unibl.etf.util.FxmlPaths;
import org.unibl.etf.util.FxmlViewManager;

public class Main extends Application{

    public static void main(String[] args) {
        System.setProperty("java.security.policy", ConfigReader.getInstance().getRMIClientPolicy());

        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FxmlViewManager viewManager = new FxmlViewManager(FxmlPaths.SUPPLIER_MAIN_VIEW, "Supplier");
        viewManager.showView();
    }
}
