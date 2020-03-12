package com.example.myapplication.event;

import java.util.ArrayList;

public class EventNotifier {

    private ArrayList<EventListener> listenerList = new ArrayList<>();

    public void addEventListener(EventListener listener) {
        listenerList.add(listener);
    }
    public void removeEventListener(EventListener listener) {
        listenerList.remove(listener);
    }
    public void diffuserEvent(Event evt) {
        int n = listenerList.size();
        Object[] listeners = new Object[n];
        for (int i = 0; i < n; i++) {
            listeners[i] = listenerList.get(i);
        }
        for (int i = 0; i < listeners.length; i++) {
            //if (listeners[i] == EventListener.class) {
            ((EventListener) listeners[i]).actionADeclancher(evt);
            //}
        }
    }
}
