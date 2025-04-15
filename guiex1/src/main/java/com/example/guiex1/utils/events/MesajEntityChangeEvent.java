package com.example.guiex1.utils.events;

import com.example.guiex1.domain.Message;

public class MesajEntityChangeEvent implements Event{
    private ChangeEventType type;
    private Message data, oldData;

    public MesajEntityChangeEvent(ChangeEventType type, Message data) {
        this.type = type;
        this.data = data;
    }
    public MesajEntityChangeEvent(ChangeEventType type, Message data, Message oldData) {
        this.type = type;
        this.data = data;
        this.oldData=oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public Message getData() {
        return data;
    }

    public Message getOldData() {
        return oldData;
    }
}
