package com.example.guiex1.controller;

import com.example.guiex1.domain.Password;
import com.example.guiex1.domain.Utilizator;
import com.example.guiex1.services.Service;
import com.example.guiex1.utils.events.Event;
import com.example.guiex1.utils.observer.Observer;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.io.IOException;

import static com.example.guiex1.utils.Hashing.hashPassword;

public class LogInController{

    private Service service;

    @FXML
    private TextField txtFirstName;

    @FXML
    private TextField txtLastName;

    @FXML
    private TextField txtPassword;

    public void setService(Service service){
        this.service = service;
    }

    public void handleLogIn(ActionEvent event) throws IOException {
        String firstName = txtFirstName.getText();
        String lastName = txtLastName.getText();
        String password = txtPassword.getText();

        if (firstName.isEmpty() || lastName.isEmpty() || password.isEmpty()){
            MessageAlert.showErrorMessage(null, "Campuri goale!");
        }else {
            try {
                Utilizator user = service.searchName(firstName, lastName);
                if (user == null) {
                    MessageAlert.showErrorMessage(null, "Utilizatorul nu este înregistrat!");
                } else {
                    if(service.getHashedPasswordFromDB(user.getId()).equals(hashPassword(password)))
                        try {
                            String hashedInputPassword = hashPassword(password);
                            try {
                                // Încarcă view-ul pentru prieteni
                                FXMLLoader loader = new FXMLLoader();
                                loader.setLocation(getClass().getResource("../views/friendsview.fxml"));

                                AnchorPane root = loader.load();

                                // Creează un dialog Stage.
                                Stage dialogStage = new Stage();
                                dialogStage.setTitle("Friends");
                                dialogStage.initModality(Modality.WINDOW_MODAL);

                                Scene scene = new Scene(root);
                                dialogStage.setScene(scene);

                                // Setează serviciul și utilizatorul în controller
                                PrietenieController friendController = loader.getController();
                                friendController.setPrietenieService(service, user);

                                dialogStage.show();

                            } catch (IOException e) {
                                MessageAlert.showErrorMessage(null, "Eroare la încărcarea ferestrei Friends!");
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            MessageAlert.showErrorMessage(null, "Eroare la hashing-ul parolei!");
                            e.printStackTrace();
                        }
                    else{
                        MessageAlert.showErrorMessage(null, "Parola incorecta!");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void handleSignUp(ActionEvent event) throws IOException{
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../views/sign-in-view.fxml"));

            AnchorPane friendshipLayout = fxmlLoader.load();
            Stage secondaryStage = new Stage();
            secondaryStage.setScene(new Scene(friendshipLayout));

            SignUpController signUpController = fxmlLoader.getController();
            signUpController.setService(service, secondaryStage);

            secondaryStage.show();
        } catch ( IOException e){
            e.printStackTrace();
        }
    }
}
