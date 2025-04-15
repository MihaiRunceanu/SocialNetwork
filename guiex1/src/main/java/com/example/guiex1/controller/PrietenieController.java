package com.example.guiex1.controller;

import com.example.guiex1.domain.Prietenie;
import com.example.guiex1.domain.StatusPrietenie;
import com.example.guiex1.domain.Tuple;
import com.example.guiex1.domain.Utilizator;
import com.example.guiex1.services.Service;
import com.example.guiex1.utils.events.Event;
import com.example.guiex1.utils.events.PrietenieEntityChangeEvent;
import com.example.guiex1.utils.observer.Observer;
import com.example.guiex1.utils.paging.Page;
import com.example.guiex1.utils.paging.Pageable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PrietenieController implements Observer<Event> {
    Service service;
    Utilizator mainUser;
    ObservableList<Utilizator> model = FXCollections.observableArrayList();

    @FXML
    TableView<Utilizator> tableView;
    @FXML
    TableColumn<Utilizator,String> tableColumnFirstName;
    @FXML
    TableColumn<Utilizator,String> tableColumnLastName;
    @FXML
    private Button buttonNext;
    @FXML
    private Button buttonPrevious;
    @FXML
    private Label labelPage;

    private int pageSize = 2;
    private int currentPage = 0;
    private int totalNumberOfElements = 0;

    private void initModel() {
//        Iterable<Tuple<Utilizator,String>> messages = mainUser.getFriends();
//        List<Utilizator> friendships = StreamSupport.stream(messages.spliterator(), false)
//                .filter(f -> StatusPrietenie.ACCEPTED.toString().equals(f.getRight())).map(f ->f.getLeft())
//                .collect(Collectors.toList());
//        model.setAll(friendships);

        Page<Utilizator> page = service.findAllOnPage(mainUser.getId(), new Pageable(currentPage, pageSize));
        // after delete, the number of pages might decrease
        int maxPage = (int) Math.ceil((double) page.getTotalNumberOfElements() / pageSize) - 1;
        if (maxPage == -1) {
            maxPage = 0;
        }
        if (currentPage > maxPage) {
            currentPage = maxPage;
            page = service.findAllOnPage(mainUser.getId(), new Pageable(currentPage, pageSize));
        }
        totalNumberOfElements = page.getTotalNumberOfElements();
        buttonPrevious.setDisable(currentPage == 0);
        buttonNext.setDisable((currentPage + 1) * pageSize >= totalNumberOfElements);
        List<Utilizator> userList = StreamSupport.stream(page.getElementsOnPage().spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(userList);
        labelPage.setText("Page " + (currentPage + 1) + " of " + (maxPage + 1));
    }

    @FXML
    private void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableView.setItems(model);
    }


    public void setPrietenieService(Service service, Utilizator mainUser) {
        this.service = service;
        this.mainUser = mainUser;
        service.addObserver(this);
        initModel();
    }

    @Override
    public void update(Event event) {
        this.mainUser = service.searchUser(this.mainUser.getId());
        if(event.getClass()== PrietenieEntityChangeEvent.class) {
            if(((PrietenieEntityChangeEvent) event).getData().getId().getRight().equals(mainUser.getId())) {
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "New ","Ai o cerere de prietenie noua");
            }
        }
        initModel();
    }

    public void handleRequests(ActionEvent event) {
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../views/requests-view.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Friend Requests");
            dialogStage.initModality(Modality.WINDOW_MODAL);

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            RequestsController friendRequestController = loader.getController();
            friendRequestController.setService(service, dialogStage, this.mainUser);

            dialogStage.show();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void handleRemove(ActionEvent event) {
        Utilizator user = (Utilizator) tableView.getSelectionModel().getSelectedItem();
        if (user!=null) {
            Tuple<Long,Long> idF = new Tuple<>(this.mainUser.getId(), user.getId());
            Prietenie deleted= service.deleteFriendship(idF);
            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION,"Delete Friendship","Userul a fost sters de la prieteni");
        }
        else MessageAlert.showErrorMessage(null, "Nu ati selectat nici un prieten");
    }

    public void handleAdd(ActionEvent event){
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../views/add-friend-view.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Friend");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            AddFriendController AddFriendController = loader.getController();
            AddFriendController.setService(service, this.mainUser, dialogStage);

            dialogStage.show();

        } catch ( IOException e) {
            e.printStackTrace();
        }
    }

    public void handleMessage(ActionEvent event){
        try{
            Utilizator friend = (Utilizator) tableView.getSelectionModel().getSelectedItem();
            if (friend != null) {

                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("../views/chat-view.fxml"));

                AnchorPane root = (AnchorPane) loader.load();
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Chat");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                Scene scene = new Scene(root);
                dialogStage.setScene(scene);

                ChatController chatController = loader.getController();
                chatController.setService(service, this.mainUser, friend);

                dialogStage.show();
            } else {
                MessageAlert.showErrorMessage(null, "Nu ati selectat nici un prieten!");
            }
        } catch ( IOException e) {
            e.printStackTrace();
        }
    }

    public void onNextPage(ActionEvent actionEvent) {
        currentPage ++;
        initModel();
    }

    public void onPreviousPage(ActionEvent actionEvent) {
        currentPage --;
        initModel();
    }

    public void handleUploadPfp() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        // Poți folosi Stage-ul principal sau un alt element pentru a deschide dialogul
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            service.updateImage(mainUser.getId(), selectedFile);
        }
    }

    public void handleOpenProfile(){
        if(tableView.getSelectionModel().getSelectedItem()!=null)
            try {
                // create a new stage for the popup dialog.
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("../views/profile-view.fxml"));

                AnchorPane root = (AnchorPane) loader.load();

                // Create the dialog Stage.
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Profile");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                //dialogStage.initOwner(primaryStage);
                Scene scene = new Scene(root);
                dialogStage.setScene(scene);

                ProfileController profileController = loader.getController();
                profileController.setService(service, tableView.getSelectionModel().getSelectedItem());

                dialogStage.show();

            } catch ( IOException e) {
                e.printStackTrace();
            }
        else {
            MessageAlert.showErrorMessage(null, "Nu ai selectat niciuni profil");
        }
    }

    public void handleOpenMyProfile(){
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../views/profile-view.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Profile");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            ProfileController profileController = loader.getController();
            profileController.setService(service, this.mainUser);

            dialogStage.show();

        } catch ( IOException e) {
            e.printStackTrace();
        }
    }
}
