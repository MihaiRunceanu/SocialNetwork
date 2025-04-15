package com.example.guiex1.repository.dbrepo;

import com.example.guiex1.domain.Prietenie;
import com.example.guiex1.domain.StatusPrietenie;
import com.example.guiex1.domain.Tuple;
import com.example.guiex1.domain.Utilizator;
import com.example.guiex1.domain.validators.PrietenieValidator;
import com.example.guiex1.domain.validators.Validator;
import com.example.guiex1.repository.Repository;
import com.example.guiex1.utils.paging.Page;
import com.example.guiex1.utils.paging.Pageable;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class PrietenieDbRepository implements Repository<Tuple<Long, Long>, Prietenie> {

    private final String url;
    private final String username;
    private final String password;
    private final Validator<Prietenie> validator;

    public PrietenieDbRepository(String url,String username,String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        validator = new PrietenieValidator();
    }


    @Override
    public Optional<Prietenie> findOne(Tuple<Long,Long> id) {
        Prietenie friendship;
        Long id1 = id.getLeft();
        Long id2 = id.getRight();
        try(Connection connection = DriverManager.getConnection(url, username, password);
            ResultSet resultSet = connection.createStatement().executeQuery(String.format("select * from friendships where id1 = '%d' and id2 = '%d' or id1='%d' and id2='%d'", id1,id2,id2,id1))) {
            if(resultSet.next()){
                friendship = createFriendFromResultSet(resultSet);
                return Optional.ofNullable(friendship);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private Prietenie createFriendFromResultSet(ResultSet resultSet) {
        try {
            Long id1 = resultSet.getLong("id1");
            Long id2 = resultSet.getLong("id2");
            Timestamp date = resultSet.getTimestamp("f_date");
            StatusPrietenie status = StatusPrietenie.valueOf(resultSet.getString("status"));
            LocalDateTime localDateTime = date.toLocalDateTime();
            Prietenie friendship = new Prietenie(id1, id2, localDateTime, status);
            return friendship;
        } catch (SQLException e) {
            return null;
        }
    }



    @Override
    public Iterable<Prietenie> findAll() {
        Set<Prietenie> friendShips = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long user1  = resultSet.getLong("User1");
                Long user2 = resultSet.getLong("User2");
                LocalDateTime date = resultSet.getTimestamp("DataPrietenie").toLocalDateTime();
                StatusPrietenie status = StatusPrietenie.valueOf(resultSet.getString("Status"));

                Prietenie friends = new Prietenie(user1, user2, date);
                friends.setId(new Tuple<>(user1,user2));
                friendShips.add(friends);
            }
            return friendShips;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendShips;
    }

    public Iterable<Prietenie> findAll(Long id){
        Set<Prietenie> friendships = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships WHERE (id1 = ? or id2 = ?) AND status = 'ACCEPTED'");
        ) {
            statement.setLong(1,id);
            statement.setLong(2,id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Prietenie prietenie = createFriendFromResultSet(resultSet);
                friendships.add(prietenie);
            }
            return friendships;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }


    @Override
    public Optional<Prietenie> save(Prietenie entity, String s) {
        String sql = "insert into friendships (id1,id2,f_date,status) values (?, ?, ?, ?)";
        validator.validate(entity);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, entity.getId().getLeft());
            ps.setLong(2, entity.getId().getRight());
            ps.setTimestamp(3,Timestamp.valueOf(entity.getDate()));
            ps.setString(4,entity.getStatus().toString());

            ps.executeUpdate();
        } catch (SQLException e) {
            return Optional.ofNullable(entity);
        }
        return Optional.empty();
    }


    @Override
    public Optional<Prietenie> delete(Tuple<Long,Long> id) {
        Long id1 = id.getLeft();
        Long id2 = id.getRight();
        String sql = "delete from friendships where id1 = ? and id2 = ? or id1 = ? and id2 = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            Optional<Prietenie> friendship = findOne(id);
            if(!friendship.isEmpty()) {
                ps.setLong(1, id1);
                ps.setLong(2, id2);
                ps.setLong(3, id2);
                ps.setLong(4, id1);
                ps.executeUpdate();
            }
            return friendship;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Prietenie> update(Prietenie prietenie) { // maybe modified ?
        if(prietenie == null)
            throw new IllegalArgumentException("entity must be not null!");
        validator.validate(prietenie);
        Long id1 = prietenie.getId().getLeft();
        Long id2 = prietenie.getId().getRight();
        StatusPrietenie status = prietenie.getStatus();
        String sql = "update friendships set status = ? where id1 = ? and id2 = ? or id1 = ? and id2 = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status.toString());
            ps.setLong(2, id1);
            ps.setLong(3, id2);
            ps.setLong(4,id2);
            ps.setLong(5,id1);

            if( ps.executeUpdate() > 0 )
                return Optional.empty();
            return Optional.ofNullable(prietenie);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Page<Prietenie> findAllOnPage(Long id, Pageable pageable){
        List<Prietenie> friendsOnPage = new ArrayList<>();

        String sql = "select * from friendships where (id1 = ? or id2 = ?) and status = ? limit ? offset ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
             ps.setLong(1, id);
             ps.setLong(2, id);
             ps.setString(3, StatusPrietenie.ACCEPTED.toString());
             ps.setInt(4, pageable.getPageSize());
             ps.setInt(5, pageable.getPageNumber() * pageable.getPageSize());

            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Prietenie friends = createFriendFromResultSet(resultSet);
                friendsOnPage.add(friends);
            }

            return new Page<Prietenie>(friendsOnPage, count(connection, id));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private int count(Connection connection, Long id) throws SQLException {
        String sql = "select count(*) as count from friendships where id1 = ? or id2 = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.setLong(2, id);
            try (ResultSet result = statement.executeQuery()) {
                int totalNumberOfFriends = 0;
                if (result.next()) {
                    totalNumberOfFriends = result.getInt("count");
                }
                return totalNumberOfFriends;
            }
        }
    }
}
