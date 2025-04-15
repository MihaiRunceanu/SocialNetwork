package com.example.guiex1.utils.events;

import com.example.guiex1.domain.Prietenie;
import com.example.guiex1.domain.Utilizator;

public class PrietenieEntityChangeEvent implements Event{
    private ChangeEventType type;
    private Prietenie data, oldData;

    public PrietenieEntityChangeEvent(ChangeEventType type, Prietenie data) {
        this.type = type;
        this.data = data;
    }
    public PrietenieEntityChangeEvent(ChangeEventType type, Prietenie data, Prietenie oldData) {
        this.type = type;
        this.data = data;
        this.oldData=oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public Prietenie getData() {
        return data;
    }

    public Prietenie getOldData() {
        return oldData;
    }
}
