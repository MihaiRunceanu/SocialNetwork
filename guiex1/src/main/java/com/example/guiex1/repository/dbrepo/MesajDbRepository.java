package com.example.guiex1.repository.dbrepo;

import com.example.guiex1.domain.*;
import com.example.guiex1.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class MesajDbRepository implements Repository<Long, Message> {
    private final String url;
    private final String username;
    private final String password;

    public MesajDbRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Optional<Message> findOne(Long id) {
        Message msg;
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM messages WHERE id = ?")){
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                msg = createMessageFromResultSet(resultSet);
                return Optional.ofNullable(msg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private Message createMessageFromResultSet(ResultSet resultSet) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statementU1 = connection.prepareStatement("SELECT * FROM users WHERE id = ?");
             PreparedStatement statementU2 = connection.prepareStatement("SELECT * FROM users WHERE id = ?");)
        {
            statementU1.setLong(1, resultSet.getLong("from_user"));
            statementU2.setLong(1, resultSet.getLong("to_user"));
            ResultSet resultSetU1 = statementU1.executeQuery();
            ResultSet resultSetU2 = statementU2.executeQuery();
            Long id = resultSet.getLong("id");

            Utilizator u1 = null;
            Utilizator u2 = null;

            if(resultSetU1.next()) {
                u1 = new Utilizator(resultSetU1.getString("first_name"), resultSetU1.getString("last_name"));
                u1.setId(resultSetU1.getLong("id"));
            }
            if(resultSetU2.next()) {
                u2 = new Utilizator(resultSetU2.getString("first_name"), resultSetU2.getString("last_name"));
                u2.setId(resultSetU2.getLong("id"));
            }
            Message replyMsg = new Message();
            Long id2 = resultSet.getLong("to_user");
            Timestamp date = resultSet.getTimestamp("date");
            LocalDateTime localDateTime = date.toLocalDateTime();
            Long reply = resultSet.getLong("reply");
            if(resultSet.wasNull())
                reply = null;
            else{
                replyMsg.setId(reply);
            }
            String text = resultSet.getString("message");
            Message msg = new Message(u1, text, List.of(u2),localDateTime,reply==null ? null : replyMsg);
            msg.setId(id);
            return msg;
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public Iterable<Message> findAll() {
        return null;
    }

    @Override
    public Optional<Message> save(Message entity, String s) {
        entity.getTo().forEach(user->{
            String sql = "insert into messages (from_user, to_user, message, date, reply) values (?, ? ,? ,? ,?)";
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement ps = connection.prepareStatement(sql)) {

                ps.setLong(1, entity.getFrom().getId());
                ps.setLong(2, user.getId());
                ps.setString(3, entity.getMessage());
                ps.setTimestamp(4, java.sql.Timestamp.valueOf(entity.getData()));
                if(entity.getReply()!=null){
                    ps.setLong(5, entity.getReply().getId());
                }
                else{
                    ps.setNull(5, Types.INTEGER);
                }
                ps.executeUpdate();
            } catch (SQLException e) {
                //e.printStackTrace();
            }
        });
        return Optional.empty();
    }

    @Override
    public Optional<Message> delete(Long aLong) {
        return Optional.empty();
    }

    @Override
    public Optional<Message> update(Message entity) {
        return Optional.empty();
    }

    public Message findChat(Long id1, Long id2) throws SQLException {
        Set<Message> messages = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM messages WHERE (from_user = ? AND to_user = ? OR from_user = ? AND to_user = ?) AND reply IS NULL");) {
            statement.setLong(1, id1);
            statement.setLong(2, id2);
            statement.setLong(3, id2);
            statement.setLong(4, id1);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Message message = createMessageFromResultSet(resultSet);
                Message next = findReplier(message.getId());
                while(next!=null){
                    next.setReply(message);
                    message = next;
                    next = findReplier(message.getId());
                }
                return message;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Message findReplier(Long id){
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM messages WHERE reply = ?"))
        {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return createMessageFromResultSet(resultSet);
            }
            return null;
        }
        catch (SQLException e) {
            return null;
        }
    }

    public Iterable<Message> findConvo(Long id1,Long id2) {
        Set<Message> messages = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM messages WHERE from_user = ? AND to_user = ? OR from_user = ? AND to_user = ?");) {
            statement.setLong(1, id1);
            statement.setLong(2, id2);
            statement.setLong(3, id2);
            statement.setLong(4, id1);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                Message message = createMessageFromResultSet(resultSet);
                if(message.getReply()!=null){
                    Message reply = findOne(message.getReply().getId()).get();
                    message.setReply(reply);
                }
                messages.add(message);
            }
            return messages;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
}
