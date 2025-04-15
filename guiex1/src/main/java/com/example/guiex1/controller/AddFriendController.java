package com.example.guiex1.controller;

import com.example.guiex1.domain.Prietenie;
import com.example.guiex1.domain.Tuple;
import com.example.guiex1.domain.Utilizator;
import com.example.guiex1.domain.validators.ValidationException;
import com.example.guiex1.services.Service;
import com.example.guiex1.utils.events.Event;
import com.example.guiex1.utils.observer.Observer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddFriendController implements Observer<Event> {

    private Service service;
    private Utilizator user;
    private Stage dialogStage;

    @FXML
    private TextField textFirstName;

    @FXML
    private TextField textLastName;

    @FXML
    private void initialize() {}

    @Override
    public void update(Event event) {

    }

    public void setService(Service service, Utilizator user, Stage stage) {
        this.service = service;
        this.user = user;
        dialogStage = stage;
    }

    public void handleAddF(ActionEvent actionEvent) {
        String firstNameText= textFirstName.getText();
        String lastNameText= textLastName.getText();

        Utilizator friend = service.searchName(firstNameText,lastNameText);
        try {
            if (friend == null) {
                MessageAlert.showErrorMessage(null, "Nu exista aceasta persoana");
            } else {
                Long id1 = user.getId();
                Long id2 = friend.getId();
                service.AdaugaPrietenie(new Prietenie(id1, id2));

                dialogStage.close();
            }
        }catch (ValidationException e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
    }
}
