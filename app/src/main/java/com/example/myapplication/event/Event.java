package com.example.myapplication.event;
import java.util.EventObject;

public class Event extends EventObject {
    private Object data;
    private String comment;

    public Event(Object source, Object data,String s) {
        super(source);
        this.data = data;
        this.comment = s;
    }
    public Object getData() {
        return this.data;
    }

    public String getComment() {
        return this.comment;
    }
}