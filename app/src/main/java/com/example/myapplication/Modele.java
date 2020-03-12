package com.example.myapplication;

import com.example.myapplication.event.Event;
import com.example.myapplication.event.EventNotifier;
import com.example.myapplication.event.EventListener;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;

public class Modele {

    private EventNotifier notifier = new EventNotifier();
    String[][] tabImage;
    int MAX;
    int[] position1 = new int[2];
    int[] position2 = new int[2];
    int nmove;

    public Modele(int MAX) {
        this.MAX = MAX;
        this.tabImage = new String[MAX][MAX];
        this.position1[0] = -1;
        this.position1[1] = -1;
        this.position2[0] = -1;
        this.position2[1] = -1;
        this.nmove = 0;
    }

    //Create the tab au image's names
    public void initializeTab(){
        for (int i = 0; i < MAX; i++) {
            for (int j = 0; j < MAX; j++) {
                this.tabImage[i][j] = i+"_"+j;
            }
        }
        //notifier.diffuserEvent(new Event(this, tabImage, "update"));
    }

    //Randomize the current tab
    public void randomizeTab() {
        Random random = new Random();

        for (int i = tabImage.length - 1; i > 0; i--) {
            for (int j = tabImage[i].length - 1; j > 0; j--) {
                int m = random.nextInt(i + 1);
                int n = random.nextInt(j + 1);

                String temp = tabImage[i][j];
                tabImage[i][j] = tabImage[m][n];
                tabImage[m][n] = temp;
            }
        }

        for (int i = tabImage.length - 1; i > 0; i--) {
            for (int j = tabImage[i].length - 1; j > 0; j--) {
                int m = random.nextInt(i + 1);
                int n = random.nextInt(j + 1);

                String temp = tabImage[i][j];
                tabImage[i][j] = tabImage[m][n];
                tabImage[m][n] = temp;
            }
        }

        notifier.diffuserEvent(new Event(this, this.tabImage, "update"));
        //notifier.diffuserEvent(new Event(this, this.ncout, "update"));
    }

    //Save the position of clicked piece. On the second clicked piece, a switch between them occur
    public void setPosition(int r, int c){
        if(position1[0] < 0 || position1[1] < 0){
            this.position1[0] = r;
            this.position1[1] = c;
        } else {
            this.position2[0] = r;
            this.position2[1] = c;
            switchCase();
        }
    }

    //Increment move by 1
    public void addMove(){
        this.nmove++;
        notifier.diffuserEvent(new Event(this, this.nmove, "move"));
    }

    //Exchange 2 piece of the puzzle
    public void switchCase(){
        if(position1[0] >= 0 && position1[1] >= 0 && position2[0] >= 0 && position2[1] >= 0 ){

            if(position1[0] != position2[0] || position1[1] != position2[1]) this.addMove();

            String s = tabImage[position1[0]][position1[1]];
            tabImage[position1[0]][position1[1]] = tabImage[position2[0]][position2[1]];
            tabImage[position2[0]][position2[1]] = s;
            ArrayList<String> data = new ArrayList<>();

            data.add(position1[0]+"");
            data.add(position1[1]+"");
            data.add(tabImage[position1[0]][position1[1]]);
            data.add(position2[0]+"");
            data.add(position2[1]+"");
            data.add(tabImage[position2[0]][position2[1]]);

            //notifier.diffuserEvent(new Event(this, this.tabImage, "update"));
            notifier.diffuserEvent(new Event(this, data, "switch"));

            if(testWin()){
                notifier.diffuserEvent(new Event(this, this.tabImage, "win"));
            }
            this.position1[0] = -1;
            this.position1[1] = -1;
            this.position2[0] = -1;
            this.position2[1] = -1;
        }
    }

    //Test with the current pattern of piece if we win the game
    public boolean testWin(){
        boolean win = true;
        for (int i = 0; i < MAX; i++) {
            for (int j = 0; j < MAX; j++) {
                if(!this.tabImage[i][j].equals(i+"_"+j)){
                    win = false;
                    //System.out.println(this.tabImage[i][j]);
                    //System.out.println(i+""+j);
                }
            }
        }
        System.out.println(win);
        return win;
    }

    public void addEventListener(EventListener listener) {
        notifier.addEventListener(listener);
    }

    public void removeEventListener(EventListener listener) {
        notifier.removeEventListener(listener);
    }


}
