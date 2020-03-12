package com.example.myapplication;

import com.example.myapplication.event.Event;
import com.example.myapplication.event.EventListener;

public class Controller implements EventListener {

    Modele modele;

    public Controller(Modele m){
        this.modele = m;
    }

    @Override
    public void actionADeclancher(Event evt) {
        switch(evt.getComment()){
            case "set" :
                int[] tab = (int[]) evt.getData();
                modele.setPosition(tab[0], tab[1]);
                break;
            case "initialize":
                modele.initializeTab();
                break;
            case "random":
                modele.randomizeTab();
                break;
        }
    }
}
