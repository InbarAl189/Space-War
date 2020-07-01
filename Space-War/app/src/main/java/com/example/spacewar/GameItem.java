package com.example.spacewar;

import android.graphics.Rect;

import java.util.Random;

public class GameItem {
    private int speed;
    private Rect rect;
    private Random r;

    public GameItem(int speed) {
        this.speed = speed;
        r = new Random();
        rect = new Rect();
    }

    Rect getRect(){
        return rect;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int max,int min) {
        speed = r.nextInt(max-min)+min;
    }

    public void setRect(int top, int left, int bottom, int right){
        rect.top = top;
        rect.left = left;
        rect.bottom=bottom;
        rect.right=right;
    }
}
