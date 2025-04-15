package com.example.guiex1.controller;

import com.example.guiex1.domain.Password;
import com.example.guiex1.domain.Utilizator;
import com.example.guiex1.services.Service;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SignUpController {
    private Service service;
    private Stage stage;

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField password;

    public void setService(Service service, Stage stage) {
        this.service = service;
        this.stage = stage;
    }

    public void handleRegister(ActionEvent event) {
        String firstName = this.firstName.getText();
        String lastName = this.lastName.getText();
        String password = this.password.getText();

        if (firstName.isEmpty() || lastName.isEmpty() || password.isEmpty()) {
            MessageAlert.showErrorMessage(null, "Campuri goale!");
        } else {
            service.AdaugaUser(firstName, lastName, password);
            stage.close();
        }

    }
}
