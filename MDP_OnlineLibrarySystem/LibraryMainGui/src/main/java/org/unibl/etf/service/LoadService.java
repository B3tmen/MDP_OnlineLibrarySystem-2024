package org.unibl.etf.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.List;
import java.util.function.Supplier;

public class LoadService<T> extends Service<ObservableList<T>> {
    private final Supplier<List<T>> loadOperation;

    public LoadService(Supplier<List<T>> databaseOperation) {
        this.loadOperation = databaseOperation;
    }

    @Override
    protected Task<ObservableList<T>> createTask() {
        return new Task<>() {
            @Override
            protected ObservableList<T> call() throws Exception {
                List<T> results = loadOperation.get();
                return FXCollections.observableArrayList(results);
            }
        };
    }
}
