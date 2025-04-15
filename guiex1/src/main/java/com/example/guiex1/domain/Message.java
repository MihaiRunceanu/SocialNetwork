package com.example.guiex1.domain;

import java.time.LocalDateTime;
import java.util.List;

public class Message extends Entity<Long>{
    private Long id;
    private Utilizator from;
    private List<Utilizator> to;
    private LocalDateTime data;
    private Message reply;
    private String message;

    public Message(Utilizator from, String message, List<Utilizator> to, LocalDateTime data, Message reply) {
        this.from = from;
        this.message = message;
        this.to = to;
        this.data = data;
        this.reply = reply;
    }

    public Message() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Utilizator getFrom() {
        return from;
    }

    public void setFrom(Utilizator from) {
        this.from = from;
    }

    public List<Utilizator> getTo() {
        return to;
    }

    public void setTo(List<Utilizator> to) {
        this.to = to;
    }

    public Message getReply() {
        return reply;
    }

    public void setReply(Message reply) {
        this.reply = reply;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
