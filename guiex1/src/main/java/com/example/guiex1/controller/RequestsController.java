package com.example.guiex1.controller;

import com.example.guiex1.domain.Prietenie;
import com.example.guiex1.domain.StatusPrietenie;
import com.example.guiex1.domain.Tuple;
import com.example.guiex1.domain.Utilizator;
import com.example.guiex1.domain.validators.ValidationException;
import com.example.guiex1.services.Service;
import com.example.guiex1.utils.events.Event;
import com.example.guiex1.utils.observer.Observer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class RequestsController implements Observer<Event> {
    Service service;
    ObservableList<Utilizator> model = FXCollections.observableArrayList();

    Stage dialogStage;
    Utilizator utilizator;

    @FXML
    TableView<Utilizator> tableView;

    @FXML
    TableColumn<Utilizator, String> FirstName;

    @FXML
    TableColumn<Utilizator, String> LastName;

    @FXML
    TableColumn<Utilizator, String> Date;

    public void setService(Service service, Stage stage, Utilizator u) {
        this.service = service;
        this.dialogStage = stage;
        this.utilizator = u;
        service.addObserver(this);
        initModel();
    }

    private void initModel() {
        Iterable<Tuple<Utilizator,String>> messages = utilizator.getFriends();
        List<Utilizator> users = StreamSupport.stream(messages.spliterator(), false)
                .filter(u->StatusPrietenie.PENDING.toString().equals(u.getRight())).map(u->u.getLeft())
                .collect(Collectors.toList());
        model.setAll(users);
        tableView.setItems(null);
        tableView.setItems(model);
    }

    @FXML
    public void initialize() {
        FirstName.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        LastName.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));
        Date.setCellValueFactory(cellData-> {
                    Utilizator user = cellData.getValue();
                    Tuple<Long,Long> idF = new Tuple<>(utilizator.getId(),user.getId());
                    Prietenie friend = service.searchFriendship(idF);
                    return new SimpleStringProperty(friend.getDate().toString());
                }
        );
        tableView.setItems(model);
    }

    @Override
    public void update(Event utilizatorEntityChangeEvent) {
        this.utilizator = service.searchUser(this.utilizator.getId());
        initModel();
    }

    public void handleDecline(){
        Utilizator user=(Utilizator) tableView.getSelectionModel().getSelectedItem();
        if(user!=null) {
            Tuple<Long, Long> idF = new Tuple<>(utilizator.getId(), user.getId());
            service.deleteFriendship(idF);
        }
        else{
            MessageAlert.showErrorMessage(null,"Nimic selectat de dat decline");
        }
    }

    public void handleAccept(ActionEvent actionEvent){
        Utilizator user=(Utilizator) tableView.getSelectionModel().getSelectedItem();
        if(user!=null) {
            try {
                Tuple<Long, Long> idF = new Tuple<>(utilizator.getId(), user.getId());
                Prietenie p = new Prietenie(utilizator.getId(),user.getId());
                service.AdaugaPrietenie(p);
            }
            catch(ValidationException e){
                MessageAlert.showErrorMessage(null,e.getMessage());
            }
        }
        else{
            MessageAlert.showErrorMessage(null,"Nimic selectat de dat accept");
        }
    }
}
