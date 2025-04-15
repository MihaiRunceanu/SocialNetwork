package com.example.guiex1.controller;

import com.example.guiex1.domain.Utilizator;
import com.example.guiex1.services.Service;
import com.example.guiex1.utils.events.Event;
import com.example.guiex1.utils.observer.Observer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class ProfileController implements Observer<Event> {

    Service service;
    Utilizator user;

    @FXML
    private ImageView imageView;
    @FXML
    private Label labelNume;
    @FXML
    private Label labelFriends;

    public void setService(Service service, Utilizator user) {
        this.service = service;
        this.user = user;

        service.addObserver(this);
        labelNume.setText(user.getFirstName() + " " + user.getLastName());
        Integer friends = this.service.getFriends(user.getId()).size();
        labelFriends.setText(String.valueOf(friends));
        Image image = service.getImage(user.getId());
        if(image != null) {
            imageView.setImage(image);
        }
        else {
            imageView.setImage(new Image("file:" + "C:\\Mihai\\47ba71f457434319819ac4a7cbd9988e.jpg"));
        }
    }

    @Override
    public void update(Event e) {
        Integer friends = this.service.getFriends(user.getId()).size();
        labelFriends.setText(String.valueOf(friends));
        Image image = service.getImage(user.getId());
        if(image != null) {
            imageView.setImage(image);
        }
        else {
            imageView.setImage(new Image("file:" + "C:\\Mihai\\47ba71f457434319819ac4a7cbd9988e.jpg"));        }
    }
    }

