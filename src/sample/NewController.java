package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

public class NewController {

    public TextField postanskiBroj;
    public TextField Grad;
    public TextField Adresa;
    public TextField imeIPrezime;
    public Button Potvrda;
    private boolean dobarPostanskiBroj = false;
    private boolean[] dobroPolje;


    private boolean validnoPolje(String unos) {
        return (unos.length() >= 1 && unos.length() <= 30 && Character.isUpperCase(unos.charAt(0)));
    }

    private void provjerUnosa(TextField tekstualnoPolje, Function<String, Boolean> validacija, int n) {
        tekstualnoPolje.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue.equals("")) {
                tekstualnoPolje.getStyleClass().removeAll("poljeNijeIspravno", "poljeIspravno");
                tekstualnoPolje.getStyleClass().add("poljeNeutralno");
                dobroPolje[n] = false;
            } else {
                if (validacija.apply(newValue)) {
                    tekstualnoPolje.getStyleClass().removeAll("poljeNijeIspravno", "poljeNeutralno");
                    tekstualnoPolje.getStyleClass().add("poljeIspravno");
                    dobroPolje[n] = true;
                } else {
                    tekstualnoPolje.getStyleClass().removeAll("poljeIspravno", "poljeNeutralno");
                    tekstualnoPolje.getStyleClass().add("poljeNijeIspravno");
                    dobroPolje[n] = false;
                }
            }
        });
    }

    private void urlProvjera() {
        String adresa = "http://c9.etf.unsa.ba/proba/postanskiBroj.php?postanskiBroj=";
        try {
            URL url = new URL(adresa + postanskiBroj.getText());
            BufferedReader ulaz = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
            String content = "", line = null;
            while ((line = ulaz.readLine()) != null)
                content = content + line;
            if (content.equals("OK")) {
                dobarPostanskiBroj = true;
                Platform.runLater(() -> {
                    postanskiBroj.getStyleClass().removeAll("poljeNijeIspravno", "poljeNeutralno");
                    postanskiBroj.getStyleClass().add("poljeIspravno");
                });
            } else {
                dobarPostanskiBroj = false;
                Platform.runLater(() -> {
                    postanskiBroj.getStyleClass().removeAll("poljeIspravno", "poljeNeutralno");
                    postanskiBroj.getStyleClass().add("poljeNijeIspravno");
                });
            }
        }catch (MalformedURLException ignore) {
            System.out.println("String " + adresa + postanskiBroj.getText() + "ne predstavlja validan URL");
        }catch (IOException ignore) {
            System.out.println("Greška pri kreiranju ulaznog toka");
        }
    }

    @FXML
    public void initialize() {
        dobroPolje = new boolean[3];
        provjerUnosa(imeIPrezime, this::validnoPolje, 0);
        provjerUnosa(Adresa, this::validnoPolje, 1);
        provjerUnosa(Grad, this::validnoPolje, 2);
        postanskiBroj.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (postanskiBroj.getText().length() == 0) {
                    postanskiBroj.getStyleClass().removeAll("poljeNijeIspravno", "poljeIspravno");
                    postanskiBroj.getStyleClass().add("poljeNeutralno");
                    dobarPostanskiBroj = false;
                    return;
                }
                Thread thread = new Thread(this::urlProvjera);
                thread.start();
            }
        });
    }

    public void Potvrda(ActionEvent actionEvent) {
        if (dobarPostanskiBroj && dobroPolje[0] && dobroPolje[1] && dobroPolje[2]) {
            Alert info = new Alert(Alert.AlertType.CONFIRMATION);
            info.setTitle("Uspjeh");
            info.setHeaderText("Ime i prezime: " + imeIPrezime.getText() + "\nAdresa: " + Adresa.getText() + "\nGrad: " + Grad.getText() + "\nPoštanski broj: " + postanskiBroj.getText());
            info.showAndWait();
            if (info.getResult() == ButtonType.OK) {
                Stage stage = (Stage) Potvrda.getScene().getWindow();
                stage.close();
            }
        } else {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Greška");
            String greska = "";
            if (!dobroPolje[0]) {
                if (imeIPrezime.getText().length() == 0) greska += "Unesite ime i prezime./n";
                else greska += "Neispravno ime i prezime./n";
            }
            if (!dobroPolje[1]) {
                if (Adresa.getText().length() == 0) greska += "Unesite adresu.\n";
                else greska += "Neispravna adresa.\n";
            }
            if (!dobroPolje[2]) {
                if (Grad.getText().length() == 0) greska += "Unesite grad.\n";
                else greska += "Neispravna grad.\n";
            }
            if (!dobarPostanskiBroj) {
                if (postanskiBroj.getText().length() == 0) greska += "Unesite postanski broj.";
                else greska += "Poštanski broj " + postanskiBroj.getText() + " nije validan.";
            }
            error.setHeaderText(greska);
            error.show();
        }
    }
}