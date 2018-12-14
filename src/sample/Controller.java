package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.File;

public class Controller {
    public Button traziButton;
    public TextField pretragaField;
    public ListView<String> listaPutanja;
    public Button prekiniButton;

    @FXML
    public void initialize() {
        prekiniButton.setDisable(true);
        Thread prekiniThread = new Thread(() -> {
            if (traziButton.isDisable())
                prekiniButton.setDisable(false);
        });
        prekiniThread.start();
    }

    private class Pretraga implements Runnable {
        private File korijen;

        public Pretraga(String home) {
            this.korijen = new File(home);
        }

        @Override
        public void run() {
            if (korijen.isDirectory()) {
                File[] listFiles = korijen.listFiles();
                if (listFiles == null)
                    return;
                for (File file : listFiles) {
                    if (file.isDirectory()) {
                        Pretraga pretraga = new Pretraga(file.getAbsolutePath());
                        Platform.runLater(pretraga);
                    }
                    if (file.isFile()) {
                        if (file.getName().contains(pretragaField.getText()))
                            listaPutanja.getItems().add(file.getAbsolutePath());
                    }
                }
            }
        }
    }

    public void traziClick(ActionEvent actionEvent) throws InterruptedException {
        traziButton.setDisable(true);
        listaPutanja.getSelectionModel().clearSelection();
        listaPutanja.getItems().clear();
        String home = System.getProperty("user.home");
        Pretraga pretraga = new Pretraga(home);
        Thread thread = new Thread(pretraga);
        thread.start();
        /*Thread pomocnaThread = new Thread(() -> {
            if (!thread.isAlive())
                traziButton.setDisable(false);
        });
        pomocnaThread.start();*/
    }
}