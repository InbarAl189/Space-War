package com.example.spacewar;

public class Game {

    // Static vars
    private static final int GIFT_VALUE = 15;
    private static final int ENEMY_ONE_VALUE = 20;
    private static final int ENEMY_TWO_VALUE = 20;
    private static final int ENEMY_THREE_VALUE = 20;
    private static final int ENEMY_FOUR_VALUE = 30;
    private static final int ENEMY_FIVE_VALUE = 40;
    private static final int ENEMY_SIX_VALUE = 50;
    private static final int ENEMY_KING_VALUE = 1000;
    private  int m_enemyKingLives = 70;


    // Objects
    Player m_Player;
    ValueEnemyItem m_Enemy_one;
    ValueEnemyItem m_Enemy_two;
    ValueEnemyItem m_Enemy_three;
    ValueEnemyItem m_Enemy_four;
    ValueEnemyItem m_Enemy_five;
    ValueEnemyItem m_Enemy_six;
    ValueEnemyItem m_Enemy_king;
    GameItem m_KingShotOne;
    GameItem m_KingShotTwo;
    GameItem m_KingShotThree;
    NonValueEnemyItem m_Asteroid_one;
    NonValueEnemyItem m_Asteroid_two;
    NonValueEnemyItem m_Asteroid_three;
    ValueEnemyItem m_Gift;
    GameItem m_Shot;

    int m_CurrLevel;
    int m_EnemiesCounter;

    public Game() {

        //initialize objects
        m_Player = new Player();
        m_Shot = new GameItem(20);
        m_KingShotOne = new GameItem(80);
        m_KingShotTwo = new GameItem(80);
        m_KingShotThree = new GameItem(80);
        m_Gift = new ValueEnemyItem(GIFT_VALUE,400);
        m_Asteroid_one = new NonValueEnemyItem(1,1000);
        m_Asteroid_two = new NonValueEnemyItem(-1,900);
        m_Asteroid_three = new NonValueEnemyItem(1,800);
        m_Enemy_one = new ValueEnemyItem(ENEMY_ONE_VALUE,700);
        m_Enemy_two = new ValueEnemyItem(ENEMY_TWO_VALUE,550);
        m_Enemy_three = new ValueEnemyItem(ENEMY_THREE_VALUE,600);
        m_Enemy_four = new ValueEnemyItem(ENEMY_FOUR_VALUE,450);
        m_Enemy_five = new ValueEnemyItem(ENEMY_FIVE_VALUE,400);
        m_Enemy_six = new ValueEnemyItem(ENEMY_SIX_VALUE,300);
        m_Enemy_king = new ValueEnemyItem(ENEMY_KING_VALUE,1000);

        m_CurrLevel = 1;
    }

    public Player get_Player() {
        return m_Player;
    }

    public ValueEnemyItem get_Enemy_one() {
        return m_Enemy_one;
    }

    public ValueEnemyItem get_Enemy_two() {
        return m_Enemy_two;
    }

    public ValueEnemyItem get_Enemy_three() {
        return m_Enemy_three;
    }

    public ValueEnemyItem get_Enemy_four() {
        return m_Enemy_four;
    }

    public ValueEnemyItem get_Enemy_five() {
        return m_Enemy_five;
    }

    public ValueEnemyItem get_Enemy_six() {
        return m_Enemy_six;
    }

    public ValueEnemyItem get_Enemy_king() {
        return m_Enemy_king;
    }

    public ValueEnemyItem get_Gift() {
        return m_Gift;
    }

    public GameItem get_Shot() {
        return m_Shot;
    }

    public GameItem get_KingShotOne() {
        return m_KingShotOne;
    }

    public GameItem get_KingShotTwo() {
        return m_KingShotTwo;
    }

    public GameItem get_KingShotThree() {
        return m_KingShotThree;
    }

    public int get_CurrLevel() {
        return m_CurrLevel;
    }

    public void set_CurrLevel(int m_CurrLevel) {
        this.m_CurrLevel = m_CurrLevel;
    }

    public int get_EnemiesCounter() {
        return m_EnemiesCounter;
    }

    public void set_EnemiesCounter(int m_EnemiesCounter) {
        this.m_EnemiesCounter = m_EnemiesCounter;
    }

    public NonValueEnemyItem get_Asteroid_One() {
        return m_Asteroid_one;
    }

    public NonValueEnemyItem get_Asteroid_Two() {
        return m_Asteroid_two;
    }

    public NonValueEnemyItem get_Asteroid_Three() {
        return m_Asteroid_three;
    }

    public void hitTheKing() {m_enemyKingLives--;}

    public int getEnemyKingLives() {return m_enemyKingLives;}
}
