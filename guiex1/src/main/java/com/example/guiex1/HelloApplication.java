package com.example.guiex1;

import com.example.guiex1.controller.LogInController;
import com.example.guiex1.domain.Message;
import com.example.guiex1.domain.Prietenie;
import com.example.guiex1.domain.Tuple;
import com.example.guiex1.domain.Utilizator;
import com.example.guiex1.domain.validators.UtilizatorValidator;
import com.example.guiex1.repository.Repository;
import com.example.guiex1.repository.dbrepo.MesajDbRepository;
import com.example.guiex1.repository.dbrepo.PrietenieDbRepository;
import com.example.guiex1.repository.dbrepo.UtilizatorDbRepository;
import com.example.guiex1.services.Service;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;


public class HelloApplication extends Application {

    Repository<Long, Utilizator> utilizatorRepository;
    Repository<Tuple<Long, Long>, Prietenie> prietenieRepository;
    Service service;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
//        String fileN = ApplicationContext.getPROPERTIES().getProperty("data.tasks.messageTask");
//        messageTaskRepository = new InFileMessageTaskRepository
//                (fileN, new MessageTaskValidator());
//        messageTaskService = new MessageTaskService(messageTaskRepository);
        //messageTaskService.getAll().forEach(System.out::println);

        System.out.println("Reading data from file");
        String username="postgres";
        String pasword="1234";
        String url="jdbc:postgresql://localhost:5432/socialnetwork";
        UtilizatorDbRepository utilizatorRepository =
                new UtilizatorDbRepository(url,username, pasword,  new UtilizatorValidator());

        PrietenieDbRepository prietenieRepository =
                new PrietenieDbRepository(url,username, pasword);

        MesajDbRepository mesajeRepository = new MesajDbRepository(url,username, pasword);

        //utilizatorRepository.findAll().forEach(x-> System.out.println(x));
        service =new Service(utilizatorRepository, prietenieRepository, mesajeRepository);
        initView(primaryStage);
        primaryStage.setWidth(800);
        primaryStage.show();


    }

    private void initView(Stage primaryStage) throws IOException {

       // FXMLLoader fxmlLoader = new FXMLLoader();
        //fxmlLoader.setLocation(getClass().getResource("com/example/guiex1/views/utilizator-view.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("views/log-in-view.fxml"));

        AnchorPane userLayout = fxmlLoader.load();
        primaryStage.setScene(new Scene(userLayout));

        LogInController userController = fxmlLoader.getController();
        userController.setService(service);

    }
}