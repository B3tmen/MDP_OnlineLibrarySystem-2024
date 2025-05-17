package org.unibl.etf.service;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import org.unibl.etf.util.MyLogger;

import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;

public class LoadUtils {
    public static <T> void loadObservableListFromDatabase(Supplier<List<T>> serviceMethod, ObservableList<T> observableList) {
        LoadService<T> service = new LoadService<>(serviceMethod);

        service.setOnSucceeded(event -> {
            ObservableList<T> items = service.getValue();
            Platform.runLater(() -> {
                observableList.setAll(items);
                System.out.println(observableList);
                System.out.println("UI updated with " + observableList.size() + " items.");
            });
//            observableList.setAll(service.getValue());
//            System.out.println("Service succeeded. List updated with " + observableList.size() + " items.");
        });

        service.setOnFailed(event -> {
            Throwable e = service.getException();
            MyLogger.logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        });

        service.start(); // Start the service
    }
}
