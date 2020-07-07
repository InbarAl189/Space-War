package com.example.spacewar;

public class Player extends GameItem {
    private int score, lives;

    public Player() {
        super(0);
        score = 10;
        lives = 3;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public int getSpeed() {
        return super.getSpeed();
    }

    public int getLives() {
        return lives;
    }

    public void hit() {
        lives--;
    }
}
