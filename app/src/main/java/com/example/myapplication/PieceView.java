package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.example.myapplication.event.Event;
import com.example.myapplication.event.EventListener;
import com.example.myapplication.event.EventNotifier;

@SuppressLint("AppCompatCustomView")
public class PieceView extends ImageView {

    int row;
    int column;
    boolean clickable = false;
    private EventNotifier notifier = new EventNotifier();

    public PieceView(Context context, int r, int c) {
        super(context);
        this.row = r;
        this.column = c;
    }

    public PieceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PieceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PieceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(clickable) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                this.setColorFilter(Color.GRAY, PorterDuff.Mode.DARKEN);
                notifier.diffuserEvent(new Event(this, new int[]{this.row, this.column}, "set"));
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                return true;
            }
        }
        return false;
    }

    public void lock(){
        this.clickable = false;
    }

    public void unlock(){
        this.clickable = true;
    }

    public void addEventListener(EventListener listener) {
        notifier.addEventListener(listener);
    }

    public void removeEventListener(EventListener listener) {
        notifier.removeEventListener(listener);
    }
}
