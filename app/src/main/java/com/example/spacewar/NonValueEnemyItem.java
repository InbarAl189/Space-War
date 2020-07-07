package com.example.spacewar;

import java.util.Random;

public class NonValueEnemyItem extends GameItem {
    private int direction, random;
    private Random r;

    public NonValueEnemyItem(int direction,int speed)
    {
        super(speed);
        r = new Random();
        setDirection();
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection() {
        random = r.nextInt(2);
        if(random == 1)
            direction = 1;
        else
            direction = -1;
    }
}
