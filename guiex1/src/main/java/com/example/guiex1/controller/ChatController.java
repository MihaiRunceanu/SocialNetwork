package com.example.guiex1.controller;

import com.example.guiex1.domain.Message;
import com.example.guiex1.domain.StatusPrietenie;
import com.example.guiex1.domain.Tuple;
import com.example.guiex1.domain.Utilizator;
import com.example.guiex1.services.Service;
import com.example.guiex1.utils.events.Event;
import com.example.guiex1.utils.events.MesajEntityChangeEvent;
import com.example.guiex1.utils.observer.Observer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ChatController implements Observer<Event> {
    private Service service;
    private ObservableList<Message> model = FXCollections.observableArrayList();
    private Utilizator mainUser;
    private Utilizator friend;

    @FXML
    private TableView<Message> tableView;
    @FXML
    private TableColumn<Message,String> tableColumnMessage;
    @FXML
    private TableColumn<Message,String> tableColumnDate;
    @FXML
    private TableColumn<Message,String> tableColumnReply;
    @FXML
    private TextField textFieldMessage;

    private void initModel() {
        Iterable<Message> messages = service.getConvo(mainUser.getId(),friend.getId());
        List<Message> sorted = StreamSupport.stream(messages.spliterator(),false)
                .sorted(Comparator.comparing(Message::getData))
                .collect(Collectors.toList());
        model.setAll(sorted);
    }

    @FXML
    private void initialize() {
        tableColumnMessage.setCellValueFactory(new PropertyValueFactory<Message, String>("message"));
        tableColumnReply.setCellValueFactory(cellData -> {
            Message reply = cellData.getValue().getReply();
            return new SimpleStringProperty(reply != null ? reply.getMessage() : "No Reply");
        });
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<Message, String>("data"));

        // Set row factory to color rows based on condition
        tableView.setRowFactory(tv -> new TableRow<Message>() {
            @Override
            protected void updateItem(Message item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setStyle(""); // Reset style for empty or null rows
                } else {
                    // Check condition and set row color
                    if (item.getFrom().getId().equals(mainUser.getId())) {
                        setStyle("-fx-background-color: #95f080;"); // Color row green if from the same user
                    } else {
                        setStyle("-fx-background-color: cyan;"); // Default style
                    }
                }
            }
        });

        tableView.setItems(model); // Set items for the TableView
    }

    public void setService(Service service, Utilizator mainUser, Utilizator friend) {
        this.service = service;
        this.mainUser = mainUser;
        this.friend = friend;
        service.addObserver(this);
        initModel();
    }

    @Override
    public void update(Event event) {
        if(event.getClass()== MesajEntityChangeEvent.class){
            Message msg = (Message) ((MesajEntityChangeEvent) event).getData();
            initModel();
        }
    }

    public void handleSendMessage(){
        Message reply=(Message) tableView.getSelectionModel().getSelectedItem();
        String text = textFieldMessage.getText();
        Message msg = new Message(mainUser, text, List.of(friend), LocalDateTime.now(),reply);
        service.addMessage(msg);
        textFieldMessage.clear();
    }
}
