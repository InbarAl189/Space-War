package com.example.spacewar;

public class PlayerCard {
    private int m_Score;
    private String m_Name;

    public PlayerCard(int score, String name) {
        this.m_Score = score;
        this.m_Name = name;
    }

    public int get_Score() {
        return m_Score;
    }

    public String get_Name() {
        return m_Name;
    }

    public void set_Score(int score) {
        this.m_Score = score;
    }

    public void set_Name(String name) {
        this.m_Name = name;
    }
}
