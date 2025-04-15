package com.example.guiex1.services;

import com.example.guiex1.controller.PrietenieController;
import com.example.guiex1.domain.*;
import com.example.guiex1.domain.validators.ValidationException;
import com.example.guiex1.repository.Repository;
import com.example.guiex1.repository.dbrepo.MesajDbRepository;
import com.example.guiex1.repository.dbrepo.PrietenieDbRepository;
import com.example.guiex1.repository.dbrepo.UtilizatorDbRepository;
import com.example.guiex1.utils.events.*;
import com.example.guiex1.utils.observer.Observable;
import com.example.guiex1.utils.observer.Observer;
import com.example.guiex1.utils.paging.Page;
import com.example.guiex1.utils.paging.Pageable;
import javafx.scene.image.Image;

import java.io.File;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Service implements Observable<Event> {
    UtilizatorDbRepository userRepo;
    PrietenieDbRepository friendRepo;
    MesajDbRepository messageRepo;

    List<Observer<Event>> observers = new ArrayList<>();


    @Override
    public void addObserver(Observer<Event> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<Event> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(Event t) {
        observers.forEach(e -> e.update(t));
    }

    public Service(UtilizatorDbRepository userRepository, PrietenieDbRepository friendRepository, MesajDbRepository messageRepo)
    {
        this.userRepo = userRepository;
        this.friendRepo = friendRepository;
        this.messageRepo = messageRepo;
    }

    public Utilizator AdaugaUser(String firstName, String lastName, String password) throws ValidationException
    {
        Utilizator user = new Utilizator(firstName, lastName);
        if(userRepo.save(user,password).isEmpty()){
            UtilizatorEntityChangeEvent event = new UtilizatorEntityChangeEvent(ChangeEventType.ADD, user);
            return null;
        }
        UtilizatorEntityChangeEvent event = new UtilizatorEntityChangeEvent(ChangeEventType.ADD, user);
        notifyObservers(event);
        return user;
    }

    public void StergeUser(long idUser) throws ValidationException
    {
        Optional<Utilizator> deletedUser = userRepo.delete(idUser);
        if(deletedUser.isPresent())
            notifyObservers(new UtilizatorEntityChangeEvent(ChangeEventType.DELETE, deletedUser.get()));
        else
            throw new ValidationException("Utilizatorul cu acest id nu exista!");

    }

    public Prietenie AdaugaPrietenie(Prietenie p) throws ValidationException
    {
        String s = "1";
        Prietenie friendship = friendRepo.findOne(p.getId()).orElse(null);
        if(friendship != null && friendship.getStatus().equals(StatusPrietenie.PENDING) && p.getId().getLeft().equals(friendship.getId().getRight())){
            friendRepo.update(new Prietenie(friendship.getId().getLeft(),friendship.getId().getRight(),friendship.getDate(), StatusPrietenie.ACCEPTED));
            UtilizatorEntityChangeEvent event = new UtilizatorEntityChangeEvent(ChangeEventType.UPDATE, null);
            notifyObservers(event);
            return null;
        }
        if(friendRepo.save(p,s).isEmpty()){
            PrietenieEntityChangeEvent event = new PrietenieEntityChangeEvent(ChangeEventType.FRIEND, p);
            notifyObservers(event);
            return null;
        }
        return p;
    }

    public String getHashedPasswordFromDB(Long userID) {
        String sql = "SELECT password FROM passwords WHERE user_id = ?";
        String hashedPassword = null;

        // Conectează-te la baza de date
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "1234");
             PreparedStatement ps = connection.prepareStatement(sql)) {

            // Setează parametrii pentru interogare
            ps.setLong(1, userID);

            // Execută interogarea
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    hashedPassword = rs.getString("password");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return hashedPassword; // Returnează parola hash-uită (sau null dacă nu există)
    }

//    public void adaugaPrietenieId_Nume(Long idUser1,String firstName,String lastName)
//    {
//        Optional<Utilizator> searchedUser = Optional.empty();
//        for(Utilizator u : userRepo.findAll())
//            if(u.getFirstName().equalsIgnoreCase(firstName) && u.getLastName().equalsIgnoreCase(lastName))
//                searchedUser = Optional.of(u);
//
//        if(searchedUser.isPresent())
//            AdaugaPrietenie(idUser1,searchedUser.get().getId());
//        else
//            throw  new ValidationException("Nu exista utilizator cu acest nume - prenume");
//
//    }

    public Prietenie deleteFriendship(Tuple<Long,Long> id){
        Optional<Prietenie> friendship=friendRepo.delete(id);
        if (friendship.isPresent()) {
            notifyObservers(new UtilizatorEntityChangeEvent(ChangeEventType.DELETE, null));
            return friendship.get();
        }
        return null;
    }

    public Iterable<Utilizator> GetUseri()
    {
        return (Iterable<Utilizator>) userRepo.findAll();
    }

    public Iterable<Prietenie> GetPrietenii()
    {
        return (Iterable<Prietenie>) friendRepo.findAll();
    }

//    public ArrayList<ArrayList<Long>> GetComunitati()
//    {
//
//        Graph grafComunitati = new Graph(userRepo.findLength());
//
//        grafComunitati.addVertices(userRepo.findAll());
//
//        friendRepo.findAll().forEach(friendship->
//        {
//            long stanga = friendship.getId().getLeft();
//            long dreapta = friendship.getId().getRight();
//
//            grafComunitati.addEdge(stanga,dreapta);
//        });
//
//        return grafComunitati.connectedComponents();
//    }
//
//    public ArrayList<Long> ComunitateSociabila()
//    {
//        int longestListPos = 0;
//        int maxElements = 0;
//
//        int currentPos = -1;
//        for(var list : GetComunitati())
//        {
//            currentPos++;
//            if (list.size() > maxElements)
//            {
//                maxElements = list.size();
//                longestListPos = currentPos;
//            }
//        }
//
//        return GetComunitati().get(longestListPos);
//    }

    public Iterable<Message> getConvo(Long id1, Long id2){
        return messageRepo.findConvo(id1,id2);
    }

    public List<Utilizator> findAllFriends(Long userId) {
        List<Utilizator> friends = new ArrayList<>();


        return friends;
    }

    public Iterable<Utilizator> getAllUsers(){
        return userRepo.findAll();
    }

    public Utilizator searchUser(Long id){
        return userRepo.findOne(id).orElse(null);
    }

    public Prietenie searchFriendship(Tuple<Long,Long> id){
        return friendRepo.findOne(id).orElse(null);
    }

    public Utilizator searchName(String firstName, String lastName){
        List<Utilizator> list = StreamSupport.stream(userRepo.findAll().spliterator(),false)
                .filter(u->u.getFirstName().equals(firstName)&&u.getLastName().equals(lastName))
                .collect(Collectors.toList());
        if(list.isEmpty()){
            return null;
        }
        else return list.get(0);
    }

    public Message addMessage(Message m){
        if(messageRepo.save(m,"1").isEmpty()){
            MesajEntityChangeEvent event = new MesajEntityChangeEvent(ChangeEventType.ADD, m);
            notifyObservers(event);
            return null;
        }
        return m;
    }

    public Page<Utilizator> findAllOnPage(Long id, Pageable pageable) {
        Page<Prietenie> page = friendRepo.findAllOnPage(id, pageable);
        return new Page<Utilizator>(StreamSupport.stream(page.getElementsOnPage().spliterator(),false)
                .map(f->f.getId().getLeft().equals(id) ? userRepo.findOne(f.getId().getRight()).get() : userRepo.findOne(f.getId().getLeft()).get())
                .collect(Collectors.toList()),page.getTotalNumberOfElements());
    }

    public List<Utilizator> getFriends(Long id){
        return StreamSupport.stream(friendRepo.findAll(id).spliterator(),false)
                .map(u->u.getId().getRight()==id ? userRepo.findOne(u.getId().getLeft()).get() : userRepo.findOne(u.getId().getRight()).get())
                .collect(Collectors.toList());
    }

    public void updateImage(Long idUser, File file){
        this.userRepo.updateProfilePicture(idUser,file);
        notifyObservers(new UtilizatorEntityChangeEvent(ChangeEventType.UPDATE, null));
    }

    public Image getImage(Long idUser){
        return this.userRepo.getImageFromDatabase(idUser);
    }
}
