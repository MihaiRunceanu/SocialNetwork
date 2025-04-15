package com.example.guiex1.domain;

import java.time.LocalDateTime;


public class Prietenie extends Entity<Tuple<Long,Long>> {

    private LocalDateTime date;
    private StatusPrietenie status;

    public Prietenie() {
    }

    public Prietenie(Long idUser1, Long idUser2, LocalDateTime date) {
        this.date = date;
        setId(new Tuple<>(idUser1, idUser2));
        status = StatusPrietenie.PENDING;
    }

    public Prietenie(Long idUser1, Long idUser2) {
        status = StatusPrietenie.PENDING;
        setId(new Tuple<>(idUser1, idUser2));
        date = LocalDateTime.now();
    }

    public Prietenie(Long userId1, Long userId2, LocalDateTime date, StatusPrietenie status) {
        this.date = date;
        Tuple<Long,Long> id = new Tuple<>(userId1, userId2);
        this.setId(id);
        this.status = status;
    }

    /**
     *
     * @return the date when the friendship was created
     */
    public LocalDateTime getDate() {
        return date;
    }

    public StatusPrietenie getStatus() {
        return status;
    }
}
