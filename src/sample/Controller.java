package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.File;

public class Controller {
    public Button traziDugme;
    public Button prekiniDugme;
    public TextField pretragaPolja;
    public ListView<String> Lista;

    @FXML
    public void initialize() {
        prekiniDugme.setDisable(true);
        traziDugme.disabledProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue)
                prekiniDugme.setDisable(true);
            else
                prekiniDugme.setDisable(false);
        });
    }

    private class Pretraga implements Runnable {
        private File korijen;

        public Pretraga(String home) {
            this.korijen = new File(home);
        }

        @Override
        public void run() {
            Pretrazi(korijen, korijen);
        }

        public void Pretrazi(File korijen, File trenutni) {
            if (!traziDugme.isDisabled())
                Thread.currentThread().interrupt();
            if (trenutni.isDirectory()) {
                File[] listFiles = trenutni.listFiles();
                if (listFiles == null)
                    return;
                for (File file : listFiles) {
                    if (file.isDirectory()) {
                        Pretrazi(korijen, file);
                    }
                    if (file.isFile()) {
                        if (file.getName().contains(pretragaPolja.getText()))
                            Platform.runLater(()-> Lista.getItems().add(file.getAbsolutePath()));
                    }
                }
            }
            if (korijen.getAbsolutePath().equals(trenutni.getAbsolutePath()))
                traziDugme.setDisable(false);
        }
    }

    public void Trazi(ActionEvent actionEvent) {
        traziDugme.setDisable(true);
        Lista.getSelectionModel().clearSelection();
        Lista.getItems().clear();
        String home = System.getProperty("user.home");
        Pretraga pretraga = new Pretraga(home);
        Thread thread = new Thread(pretraga);
        thread.start();
    }

    public void Prekini(ActionEvent actionEvent) {
        traziDugme.setDisable(false);
    }
}