package com.example.spacewar;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class RunGameActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener{

    // Static vars
    private static final int MAX_ENEMY_LVL_ONE = 20;
    private static final int MAX_ENEMY_LVL_TWO = 50;
    private static final int MAX_ENEMY_LVL_THREE = 60;

    // Screen size
    private int m_ScreenSizeX;
    private int m_ScreenSizeY;

    // Xml vars
    ImageView m_BackgroundOne;
    ImageView m_BackgroundTwo;
    ImageView m_BackgroundFade;
    LinearLayout m_SettingsLayout;
    ImageView m_PlayerIv;
    ImageView m_AsteroidOneIv;
    ImageView m_AsteroidTwoIv;
    ImageView m_AsteroidThreeIv;
    ImageView m_EnemyOneIv;
    ImageView m_EnemyTwoIv;
    ImageView m_EnemyThreeIv;
    ImageView m_EnemyFourIv;
    ImageView m_EnemyFiveIv;
    ImageView m_EnemySixIv;
    ImageView m_EnemyKingIv;
    ImageView m_EnemyKingShotIv;
    ImageView m_GiftIv;
    ImageView m_ShotsIv;
    Button m_HomeButton;
    Button m_PauseButton;
    Button m_PlayButton;
    ImageView m_HeartOneIv;
    ImageView m_HeartTwoIv;
    ImageView m_HeartThreeIv;
    ImageView m_BrokenHeartIv;
    TextView m_ScoreTv;
    TextView m_LevelTv;

    // Animations vars
    private ValueAnimator m_BackgroundMoveAnim;
    private AlphaAnimation m_BackgroundFadeAnim;
    private Animation m_AnimationSpaceShipBlink;
    private Animation m_ExplodeAnim;
    private Animation m_AnimationKingBlink;
    private Animation m_AnimationEnemyBlink;
    private Animation m_AnimationFadeInOut;
    private Animation m_AnimationKingUpToDown;
    private AnimationDrawable m_EnemyOneAnim;
    private AnimationDrawable m_EnemyExplodeDrawableAnim;
    private AnimationDrawable m_KingExplodeDrawableAnim;
    private AnimationDrawable m_EnemyTwoAnim;
    private AnimationDrawable m_EnemyThreeAnim;
    private AnimationDrawable m_EnemyFourAnim;
    private AnimationDrawable m_EnemyFiveAnim;
    private AnimationDrawable m_EnemySixAnim;
    private AnimationDrawable m_AsteroidOneAnim;
    private AnimationDrawable m_AsteroidTwoAnim;
    private AnimationDrawable m_AsteroidThreeAnim;
    private AnimationDrawable m_KingAnim;

    //boolean for king enemy
    private boolean m_IsKingMoveLeft = true;
    private boolean m_IsKingDead = false;
    private boolean m_KingIsAbove = true;
    private boolean m_KingAnimationNotStart = true;

    //members for onTouch listener
    FrameLayout m_FrameLayout;
    private int m_XDelta;
    private int m_YDelta;
    private int m_LastAction;

    private boolean isStart = true;
    private boolean isPause = false;
    private boolean isMove = false;
    private boolean isPlay = true;
    private boolean isInIntent = true;

    //music and vibrate
    Vibrator v;
    private MusicService mServ;
    HomeWatcher mHomeWatcher;
    private boolean mIsBound = false;
    private boolean mIsMusic, mIsVibrate, mIsSound;

    // Timer and handler
    private Timer m_Timer = new Timer();
    private Handler m_Handler = new Handler();
    private CountDownTimer m_CountDownTimer = null;
    private CountDownTimer m_FinishGameCountDownTimer = null;

    private Game m_Game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_game);

        //find views by id
        m_SettingsLayout = findViewById(R.id.linear_settings_layout);
        m_FrameLayout = findViewById(R.id.frame_layout);
        m_PlayerIv = findViewById(R.id.player_imageView);
        m_AsteroidOneIv = findViewById(R.id.asteroid_one_imageView);
        m_AsteroidTwoIv = findViewById(R.id.asteroid_two_imageView);
        m_AsteroidThreeIv = findViewById(R.id.asteroid_three_imageView);
        m_EnemyOneIv = findViewById(R.id.enemy_one_imageView);
        m_EnemyTwoIv = findViewById(R.id.enemy_two_imageView);
        m_EnemyThreeIv = findViewById(R.id.enemy_three_imageView);
        m_EnemyFourIv = findViewById(R.id.enemy_four_imageView);
        m_EnemyFiveIv = findViewById(R.id.enemy_five_imageView);
        m_EnemySixIv = findViewById(R.id.enemy_six_imageView);
        m_EnemyKingIv = findViewById(R.id.enemy_king_imageView);
        m_EnemyKingShotIv = findViewById(R.id.enemy_king_shot_one);
        m_GiftIv = findViewById(R.id.gift_imageView);
        m_ShotsIv = findViewById(R.id.shot_imageView);
        m_HomeButton = findViewById(R.id.home_btn);
        m_PauseButton = findViewById(R.id.pause_btn);
        m_PlayButton = findViewById(R.id.play_btn);
        m_HeartOneIv = findViewById(R.id.heart_one);
        m_HeartTwoIv = findViewById(R.id.heart_two);
        m_HeartThreeIv = findViewById(R.id.heart_three);
        m_BrokenHeartIv = findViewById(R.id.broken_heart);
        m_ScoreTv = findViewById(R.id.score_tv);
        m_LevelTv = findViewById(R.id.next_level_tv);
        m_BackgroundOne = findViewById(R.id.background_one);
        m_BackgroundTwo = findViewById(R.id.background_two);
        m_BackgroundFade = findViewById(R.id.background_fade_out);

        m_Game = new Game();

        //set imageViews location
        initializeNextLevel();

        WindowManager wm = getWindowManager();
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);

        m_ScreenSizeX = size.x;
        m_ScreenSizeY = size.y;

        m_ScoreTv.setText(getResources().getString(R.string.score) + " 0");

        //Set listeners
        m_PlayerIv.setOnTouchListener(this);
        m_PauseButton.setOnClickListener(this);
        m_PlayButton.setOnClickListener(this);
        m_HomeButton.setOnClickListener(this);
        m_FrameLayout.setOnTouchListener(this);

        //Moving background animation
        m_BackgroundMoveAnim = ValueAnimator.ofFloat(0.0f, 1.0f);
        m_BackgroundMoveAnim.setRepeatCount(ValueAnimator.INFINITE);
        m_BackgroundMoveAnim.setInterpolator(new LinearInterpolator());
        m_BackgroundMoveAnim.setDuration(20000);
        m_BackgroundMoveAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                final float height = m_BackgroundOne.getHeight();
                final float translationY = height * progress;
                m_BackgroundOne.setTranslationY(translationY);
                m_BackgroundTwo.setTranslationY(translationY - height);
                m_BackgroundTwo.setTranslationY(translationY);
                m_BackgroundOne.setTranslationY(translationY - height);
            }
        });
        m_BackgroundMoveAnim.start();


        //initialize animation
        m_LevelTv.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        m_AnimationSpaceShipBlink = AnimationUtils.loadAnimation(this, R.anim.hit_spaceship_blink);
        m_AnimationKingBlink = AnimationUtils.loadAnimation(this, R.anim.hit_king_blink);
        m_AnimationEnemyBlink = AnimationUtils.loadAnimation(this, R.anim.hit_enemy_blink);
        m_AnimationFadeInOut = AnimationUtils.loadAnimation(this, R.anim.fade_in_out);
        m_AnimationKingUpToDown = AnimationUtils.loadAnimation(this, R.anim.up_to_down_king_enemy);

        m_KingExplodeDrawableAnim = (AnimationDrawable) getResources().getDrawable(R.drawable.explode_king_anim);
        m_EnemyExplodeDrawableAnim = (AnimationDrawable) getResources().getDrawable(R.drawable.explode_enemy_anim);
        m_ExplodeAnim = AnimationUtils.loadAnimation(this, R.anim.hit_king_blink);
        m_ExplodeAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                onStartExplodeAnim();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                onEndExplodeAnim();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        m_EnemyOneAnim = (AnimationDrawable) m_EnemyOneIv.getDrawable();
        m_EnemyTwoAnim = (AnimationDrawable) m_EnemyTwoIv.getDrawable();
        m_EnemyThreeAnim = (AnimationDrawable) m_EnemyThreeIv.getDrawable();
        m_EnemyFourAnim = (AnimationDrawable) m_EnemyFourIv.getDrawable();
        m_EnemyFiveAnim = (AnimationDrawable) m_EnemyFiveIv.getDrawable();
        m_EnemySixAnim = (AnimationDrawable) m_EnemySixIv.getDrawable();
        m_KingAnim = (AnimationDrawable) m_EnemyKingIv.getDrawable();

        m_AsteroidOneAnim = (AnimationDrawable) m_AsteroidOneIv.getDrawable();
        m_AsteroidTwoAnim = (AnimationDrawable) m_AsteroidTwoIv.getDrawable();
        m_AsteroidThreeAnim = (AnimationDrawable) m_AsteroidThreeIv.getDrawable();

        startGameItemsAnimations();


        //Initialize fade in-out animation
        m_BackgroundFadeAnim = new AlphaAnimation(1.0f, 0.3f);
        m_BackgroundFadeAnim.setDuration(2000);
        m_BackgroundFadeAnim.setRepeatCount(0);
        m_BackgroundFadeAnim.setRepeatMode(Animation.REVERSE);

        //getExtras
        Intent intent = getIntent();
        mIsMusic = intent.getBooleanExtra("Music", false);
        mIsVibrate = intent.getBooleanExtra("Vibrate", false);
        mIsSound = intent.getBooleanExtra("Sound", false);

        //set music and vibrate
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        doBindService();
        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        startService(music);

        mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                if (mServ != null) {
                    mServ.pauseMusic();
                }
            }

            @Override
            public void onHomeLongPressed() {
                if (mServ != null) {
                    mServ.pauseMusic();
                }
            }
        });
        mHomeWatcher.startWatch();

        m_CountDownTimer = new CountDownTimer(5000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished < 5000 && millisUntilFinished > 4000)
                    m_LevelTv.setText("3");
                if (millisUntilFinished < 4000 && millisUntilFinished > 3000)
                    m_LevelTv.setText("2");
                if (millisUntilFinished < 3000 && millisUntilFinished > 2000)
                    m_LevelTv.setText("1");
                if (millisUntilFinished < 2000 && millisUntilFinished > 1000)
                    m_LevelTv.setText(R.string.go);
                if (millisUntilFinished < 1000)
                    m_LevelTv.setText(getResources().getString(R.string.level_one));
            }

            @Override
            public void onFinish() {
                m_LevelTv.setVisibility(View.GONE);
                m_HomeButton.setVisibility(View.VISIBLE);
                m_PauseButton.setVisibility(View.VISIBLE);
                m_ShotsIv.setVisibility(View.VISIBLE);
                m_Timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        m_Handler.post(new Runnable() {
                            @Override
                            public void run() {
                                runGame();
                            }
                        });
                    }
                }, 0, 20);

            }
        }.start();
    }


    private void startGameItemsAnimations() {
        m_EnemyOneAnim.start();
        m_EnemyTwoAnim.start();
        m_EnemyThreeAnim.start();
        m_EnemyFourAnim.start();
        m_EnemyFiveAnim.start();
        m_EnemySixAnim.start();
        m_KingAnim.start();

        m_AsteroidOneAnim.start();
        m_AsteroidTwoAnim.start();
        m_AsteroidThreeAnim.start();
    }

    private void stopGameItemsAnimations() {
        m_EnemyOneAnim.stop();
        m_EnemyTwoAnim.stop();
        m_EnemyThreeAnim.stop();
        m_EnemyFourAnim.stop();
        m_EnemyFiveAnim.stop();
        m_EnemySixAnim.stop();
        m_KingAnim.stop();

        m_AsteroidOneAnim.stop();
        m_AsteroidTwoAnim.stop();
        m_AsteroidThreeAnim.stop();
    }

    private void initializeNextLevel() {
        m_Game.set_EnemiesCounter(0);
        m_Game.get_Player().setRect(0, 0, 0, 0);
        m_Game.get_Shot().setRect((int) m_EnemyOneIv.getX(),
                (int) m_EnemyOneIv.getY(),
                (int) m_EnemyOneIv.getX() + m_EnemyOneIv.getWidth(),
                (int) m_EnemyOneIv.getY() + m_EnemyOneIv.getHeight());

        setEnemyState(m_EnemyOneIv, m_Game.get_Enemy_one(), -200);
        setEnemyState(m_EnemyTwoIv, m_Game.get_Enemy_two(), -400);
        setEnemyState(m_EnemyThreeIv, m_Game.get_Enemy_three(), -550);
        setEnemyState(m_GiftIv, m_Game.get_Gift(), -200);

        if (m_Game.get_CurrLevel() > 1) {
            //Transition animation between levels
            m_BackgroundOne.setVisibility(View.GONE);
            m_BackgroundTwo.setVisibility(View.GONE);
            m_LevelTv.setVisibility(View.GONE);


            //Set enemies state
            setEnemyState(m_EnemyFourIv, m_Game.get_Enemy_four(), -700);
            setEnemyState(m_EnemyFiveIv, m_Game.get_Enemy_five(), -600);
            setAsteroidState(m_AsteroidOneIv, m_Game.get_Asteroid_One(), -400);
            setAsteroidState(m_AsteroidTwoIv, m_Game.get_Asteroid_Two(), -600);


            m_BackgroundFadeAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    m_BackgroundFade.setVisibility(View.VISIBLE);
                    m_ShotsIv.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    m_ShotsIv.setVisibility(View.VISIBLE);
                    m_BackgroundFade.setVisibility(View.GONE);
                    m_BackgroundOne.setImageResource(R.drawable.stage_two_pattern);
                    m_BackgroundTwo.setImageResource(R.drawable.stage_two_pattern);
                    m_LevelTv.setText(getResources().getString(R.string.level_two));
                    m_LevelTv.startAnimation(m_AnimationSpaceShipBlink);

                    m_BackgroundOne.setVisibility(View.VISIBLE);
                    m_BackgroundTwo.setVisibility(View.VISIBLE);
                    m_LevelTv.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            m_BackgroundFade.startAnimation(m_BackgroundFadeAnim);
            m_BackgroundMoveAnim.start();

        }

        if (m_Game.get_CurrLevel() > 2) {

            setEnemyState(m_EnemySixIv, m_Game.get_Enemy_six(), -1000);
            setAsteroidState(m_AsteroidThreeIv, m_Game.get_Asteroid_Three(), -500);

            //Change Background and set animation
            m_BackgroundOne.setVisibility(View.GONE);
            m_BackgroundTwo.setVisibility(View.GONE);
            m_LevelTv.setVisibility(View.GONE);

            m_BackgroundFadeAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    m_BackgroundFade.setVisibility(View.VISIBLE);
                    m_ShotsIv.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    m_BackgroundFade.setVisibility(View.GONE);
                    m_ShotsIv.setVisibility(View.VISIBLE);
                    m_BackgroundOne.setImageResource(R.drawable.stage_three_pattern);
                    m_BackgroundTwo.setImageResource(R.drawable.stage_three_pattern);
                    m_LevelTv.setText(getResources().getString(R.string.level_three));
                    m_LevelTv.startAnimation(m_AnimationSpaceShipBlink);

                    m_BackgroundOne.setVisibility(View.VISIBLE);
                    m_BackgroundTwo.setVisibility(View.VISIBLE);
                    m_LevelTv.setVisibility(View.VISIBLE);

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            m_BackgroundFade.startAnimation(m_BackgroundFadeAnim);
            m_BackgroundMoveAnim.start();


        }
        if (m_Game.get_CurrLevel() > 3) {
            setKingState(m_EnemyKingIv, m_Game.get_Enemy_king(), 600);
            m_EnemyKingShotIv.setVisibility(View.VISIBLE);
            m_EnemyOneIv.setVisibility(View.GONE);
            m_EnemyTwoIv.setVisibility(View.GONE);
            m_EnemyThreeIv.setVisibility(View.GONE);
            m_EnemyFourIv.setVisibility(View.GONE);
            m_EnemyFiveIv.setVisibility(View.GONE);
            m_EnemySixIv.setVisibility(View.GONE);
            m_GiftIv.setVisibility(View.GONE);
            m_AsteroidThreeIv.setVisibility(View.GONE);

            //Change Background and set animation
            m_BackgroundOne.setVisibility(View.GONE);
            m_BackgroundTwo.setVisibility(View.GONE);
            m_LevelTv.setVisibility(View.GONE);//todo

            m_BackgroundFadeAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    m_BackgroundFade.setVisibility(View.VISIBLE);
                    m_ShotsIv.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    m_BackgroundFade.setVisibility(View.GONE);
                    m_ShotsIv.setVisibility(View.VISIBLE);
                    m_BackgroundOne.setImageResource(R.drawable.king_pattern);
                    m_BackgroundTwo.setImageResource(R.drawable.king_pattern);
                    m_LevelTv.setText(getResources().getString(R.string.level_king));
                    m_LevelTv.startAnimation(m_AnimationSpaceShipBlink);

                    m_BackgroundOne.setVisibility(View.VISIBLE);
                    m_BackgroundTwo.setVisibility(View.VISIBLE);
                    m_LevelTv.setVisibility(View.VISIBLE);


                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            m_BackgroundFade.startAnimation(m_BackgroundFadeAnim);
            m_BackgroundMoveAnim.start();
        }
    }

    private void setAsteroidState(ImageView asteroidIv, NonValueEnemyItem asteroid, int dist) {
        asteroidIv.setVisibility(View.VISIBLE);
        asteroidIv.setY((int) Math.floor(Math.random() * (m_ScreenSizeY - asteroidIv.getHeight() * 2)));
        if (asteroid.getDirection() == 1) {
            asteroidIv.setX(dist);
        } else {
            asteroidIv.setX(dist * -1 + m_ScreenSizeX);
        }
        asteroid.setRect((int) asteroidIv.getX(),
                (int) asteroidIv.getY(),
                (int) asteroidIv.getX() + asteroidIv.getWidth(),
                (int) asteroidIv.getY() + asteroidIv.getHeight());
    }

    private void setKingState(ImageView enemyIv, ValueEnemyItem enemy, int dist) {
        enemyIv.setX((int) m_ScreenSizeX / 2 - enemyIv.getWidth() / 2);
        enemyIv.setY(dist);
        enemy.setRect(0, 0, 0, 0);
    }


    private void setEnemyState(ImageView enemyIv, ValueEnemyItem enemy, int dist) {
        enemyIv.setVisibility(View.VISIBLE);
        enemyIv.setX((int) Math.floor(Math.random() * (m_ScreenSizeX - enemyIv.getWidth() * 2)));
        enemyIv.setY(dist);
        enemy.setRect((int) enemyIv.getX(),
                (int) enemyIv.getY(),
                (int) enemyIv.getX() + enemyIv.getWidth(),
                (int) enemyIv.getY() + enemyIv.getHeight());
    }


    private void runGame() {
        createShots();
        if (m_Game.get_CurrLevel() <= 3) {
            moveEnemies(m_EnemyOneIv, m_Game.get_Enemy_one());
            moveEnemies(m_EnemyTwoIv, m_Game.get_Enemy_two());
            moveEnemies(m_EnemyThreeIv, m_Game.get_Enemy_three());
            sendGift();


            if (m_Game.get_CurrLevel() > 1) {
                sendAsteroid(m_AsteroidOneIv, m_Game.get_Asteroid_One());
                sendAsteroid(m_AsteroidTwoIv, m_Game.get_Asteroid_Two());
                moveEnemies(m_EnemyFourIv, m_Game.get_Enemy_four());
                moveEnemies(m_EnemyFiveIv, m_Game.get_Enemy_five());
            }

            if (m_Game.get_CurrLevel() > 2) {
                moveEnemies(m_EnemySixIv, m_Game.get_Enemy_six());
                sendAsteroid(m_AsteroidThreeIv, m_Game.get_Asteroid_Three());
            }
        } else {
            if (m_Game.get_CurrLevel() > 3) {
                moveKing(m_EnemyKingIv, m_Game.get_Enemy_king());
                createKingShots(m_EnemyKingShotIv, m_Game.get_KingShotOne());

                sendAsteroid(m_AsteroidOneIv, m_Game.get_Asteroid_One());
                sendAsteroid(m_AsteroidTwoIv, m_Game.get_Asteroid_Two());
            }
        }
        checkIfLevelEnd();

    }

    private void checkIfLevelEnd() {
        if (isPlay) {
            switch (m_Game.get_CurrLevel()) {
                case 1:
                    if (m_Game.get_EnemiesCounter() >= MAX_ENEMY_LVL_ONE) {
                        movePlayerUpWhileFinishLevel();
                        m_Game.set_CurrLevel(m_Game.get_CurrLevel() + 1);
                        initializeNextLevel();
                        handelOnPauseBtnClick();
                        if (mIsMusic)
                            mServ.changeMusic(R.raw.stage_two_music);
                        break;
                    }
                case 2:
                    if (m_Game.get_EnemiesCounter() >= MAX_ENEMY_LVL_TWO) {
                        movePlayerUpWhileFinishLevel();
                        m_Game.set_CurrLevel(m_Game.get_CurrLevel() + 1);
                        initializeNextLevel();
                        handelOnPauseBtnClick();
                        if (mIsMusic)
                            mServ.changeMusic(R.raw.stage_three_music);
                        break;
                    }
                case 3:
                    if (m_Game.get_EnemiesCounter() >= MAX_ENEMY_LVL_THREE) {
                        movePlayerUpWhileFinishLevel();
                        m_Game.set_CurrLevel(m_Game.get_CurrLevel() + 1);
                        initializeNextLevel();
                        handelOnPauseBtnClick();
                        if (mIsMusic)
                            mServ.changeMusic(R.raw.king_sound);
                        break;
                    }
                case 4:
                    if (m_IsKingDead) {
                        m_Timer.cancel();
                        m_FinishGameCountDownTimer = new CountDownTimer(2400, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                if (isStart) {
                                    m_AsteroidOneIv.setVisibility(View.GONE);
                                    m_AsteroidTwoIv.setVisibility(View.GONE);
                                    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                    m_EnemyKingIv.setImageDrawable(m_KingExplodeDrawableAnim);
                                    m_KingExplodeDrawableAnim.start();
                                    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                    m_ShotsIv.setVisibility(View.GONE);
                                    m_EnemyKingShotIv.setVisibility(View.GONE);
                                    movePlayerUpWhileFinishLevel();
                                    isStart = false;
                                }
                            }

                            @Override
                            public void onFinish() {
                                if (isInIntent) {
                                    m_EnemyOneIv.setVisibility(View.GONE);
                                    isInIntent = false;
                                    Intent intent = new Intent(RunGameActivity.this, WinningActivity.class);
                                    intent.putExtra("Score", m_Game.m_Player.getScore());
                                    intent.putExtra("Sound", mIsSound);
                                    intent.putExtra("Vibrate", mIsVibrate);
                                    intent.putExtra("Music", mIsMusic);
                                    mServ.changeMusic(R.raw.main_music);
                                    mServ.pauseMusic();
                                    startActivity(intent);
                                    RunGameActivity.this.finish();
                                }
                            }
                        }.start();
                    }
            }
        } else {
            isInIntent = false;
            Intent intent = new Intent(RunGameActivity.this, GameOverActivity.class);
            intent.putExtra("Score", m_Game.m_Player.getScore());
            intent.putExtra("Sound", mIsSound);
            intent.putExtra("Vibrate", mIsVibrate);
            intent.putExtra("Music", mIsMusic);
            mServ.changeMusic(R.raw.main_music);
            mServ.pauseMusic();
            startActivity(intent);
            this.finish();
        }
    }

    private void movePlayerUpWhileFinishLevel() {
        final Boolean[] isUp = {true};
        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                m_Handler.post(new Runnable() {
                    @Override
                    public void run() {
                        m_PlayButton.setVisibility(View.INVISIBLE);
                        m_HomeButton.setVisibility(View.INVISIBLE);
                        m_PlayerIv.setY(m_PlayerIv.getY() - m_ScreenSizeY / 150);
                        if (m_PlayerIv.getY() < -200) {
                            isUp[0] = false;
                            if (!m_IsKingDead) {
                                m_PlayerIv.setY(m_ScreenSizeY - 500);
                                m_PlayerIv.setX(m_ScreenSizeX / 2 - m_PlayerIv.getWidth() / 2);
                                m_PlayerIv.setY(m_PlayerIv.getY() - m_ScreenSizeY / 150);
                            } else {
                                t.cancel();
                            }
                        }
                        if (!isUp[0] && m_PlayerIv.getY() < m_ScreenSizeY - 200 - m_PlayerIv.getHeight()) {
                            t.cancel();
                            m_PlayButton.setVisibility(View.VISIBLE);
                            m_HomeButton.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        }, 0, 20);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!isPause) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    m_XDelta = (int) (m_PlayerIv.getX() - event.getRawX());
                    m_YDelta = (int) (m_PlayerIv.getY() - event.getRawY());
                    m_LastAction = MotionEvent.ACTION_DOWN;
                    break;

                case MotionEvent.ACTION_MOVE:
                    isMove = true;
                    if (event.getRawX() + m_XDelta > 0 && event.getRawX() + m_XDelta < m_ScreenSizeX - m_PlayerIv.getWidth())
                        m_PlayerIv.setX(event.getRawX() + m_XDelta);
                    if (event.getRawY() + m_YDelta > m_ScreenSizeY / 2 && event.getRawY() + m_YDelta < m_ScreenSizeY - m_PlayerIv.getHeight() - m_SettingsLayout.getHeight() - 60)
                        m_PlayerIv.setY(event.getRawY() + m_YDelta);

                    m_LastAction = MotionEvent.ACTION_MOVE;
                    m_Game.get_Player().setRect((int) m_PlayerIv.getY(), (int) m_PlayerIv.getX(), (int) m_PlayerIv.getY() + m_PlayerIv.getHeight(), (int) m_PlayerIv.getX() + m_PlayerIv.getWidth());
                    break;

                case MotionEvent.ACTION_UP:
                    isMove = false;
                    break;

                default:
                    return false;
            }
        } else {
            isMove = false;
        }
        return true;
    }

    public void onClick(View v) {
        if (!isMove) {
            switch (v.getId()) {
                case R.id.pause_btn:
                case R.id.play_btn:
                    handelOnPauseBtnClick();
                    playSound(R.raw.click_electronic);
                    vibrate();
                    break;
                case R.id.home_btn:
                    handelOnHomeBtnClick();
                    playSound(R.raw.click_electronic);
                    vibrate();
                    break;
            }
        }
    }

    private void handelOnHomeBtnClick() {
        m_HomeButton.setEnabled(false);

        if (isPause == false) {
            m_Timer.cancel();
            m_Timer = null;
            stopGameItemsAnimations();

            LayoutInflater inflater = getLayoutInflater();
            View alertLayout = inflater.inflate(R.layout.exit_dialog, null);

            AlertDialog.Builder alert = new AlertDialog.Builder(RunGameActivity.this);
            alert.setView(alertLayout);
            alert.setCancelable(false);

            alert.setPositiveButton(getResources().getString(R.string.yes_str), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(RunGameActivity.this, MainActivity.class);
                    isInIntent = false;
                    intent.putExtra("Score", m_Game.m_Player.getScore());
                    intent.putExtra("Sound", mIsSound);
                    intent.putExtra("Vibrate", mIsVibrate);
                    intent.putExtra("Music", mIsMusic);
                    startActivity(intent);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    finish();
                }

            });

            alert.setNegativeButton(getResources().getString(R.string.no_str), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    m_Timer = new Timer();
                    m_Timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            m_Handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    runGame();
                                }
                            });
                        }
                    }, 0, 20);
                    startGameItemsAnimations();
                }

            });

            AlertDialog dialog = alert.create();
            dialog.show();
        }
        m_HomeButton.setEnabled(true);
    }

    private void handelOnPauseBtnClick() {
        if (isStart) {
            if (isPause == false) {
                m_EnemyKingShotIv.setVisibility(View.GONE);
                m_ShotsIv.setVisibility(View.GONE);
                m_LevelTv.setVisibility(View.VISIBLE);
                isPause = true;
                m_PauseButton.setVisibility(View.INVISIBLE);
                m_PlayButton.setVisibility(View.VISIBLE);
                m_Timer.cancel();
                m_Timer = null;
                m_BackgroundMoveAnim.pause();
                stopGameItemsAnimations();

            } else {
                isPause = false;
                m_ShotsIv.setVisibility(View.VISIBLE);
                if (m_Game.get_CurrLevel() > 3)
                    m_EnemyKingShotIv.setVisibility(View.VISIBLE);
                m_PlayButton.setVisibility(View.INVISIBLE);
                m_PauseButton.setVisibility(View.VISIBLE);
                m_LevelTv.setVisibility(View.GONE);
                m_BackgroundMoveAnim.start();
                startGameItemsAnimations();
                if (m_Game.get_CurrLevel() > 3 && m_KingAnimationNotStart) {
                    m_KingAnimationNotStart = false;
                    m_EnemyKingIv.setVisibility(View.VISIBLE);

                    m_AnimationKingUpToDown.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            m_EnemyKingShotIv.setVisibility(View.GONE);

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            m_KingIsAbove = false;
                            m_EnemyKingShotIv.setVisibility(View.VISIBLE);
                            m_Game.get_Enemy_king().setRect((int) m_EnemyKingIv.getX(),
                                    (int) m_EnemyKingIv.getY(),
                                    (int) m_EnemyKingIv.getX() + m_EnemyKingIv.getWidth(),
                                    (int) m_EnemyKingIv.getY() + m_EnemyKingIv.getHeight());
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    m_EnemyKingIv.startAnimation(m_AnimationKingUpToDown);
                }

                m_Timer = new Timer();
                m_Timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        m_Handler.post(new Runnable() {
                            @Override
                            public void run() {
                                runGame();
                            }
                        });
                    }
                }, 0, 20);

                PowerManager pm = (PowerManager)
                        getSystemService(Context.POWER_SERVICE);
                boolean isScreenOn = false;
                if (pm != null) {
                    isScreenOn = pm.isScreenOn();
                }

                if (!isScreenOn) {
                    if (mServ != null) {
                        if (isPause)
                            mServ.pauseMusic();
                        else
                            mServ.resumeMusic();
                    }
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //pause music
        PowerManager pm = (PowerManager)
                getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = false;
        if (pm != null) {
            isScreenOn = pm.isScreenOn();
        }

        if (!isScreenOn) {
            if (mServ != null && mIsMusic) {
                mServ.pauseMusic();
            }
        }

        if (isPause == false) {

            try {
                isPause = true;

                m_Timer.cancel();
                m_Timer = null;

                stopGameItemsAnimations();

                m_PauseButton.setBackgroundResource(R.drawable.ic_play);

            } catch (Exception e) {
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //music
        if (mServ != null && mIsMusic) {
            mServ.resumeMusic();
        }

        startGameItemsAnimations();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //music
        doUnbindService();
        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        stopService(music);

        if (m_CountDownTimer != null)
            m_CountDownTimer.cancel();
        if (m_FinishGameCountDownTimer != null)
            m_FinishGameCountDownTimer.cancel();
    }

    private void createShots() {
        int ivX = (int) m_ShotsIv.getX();
        int ivY = (int) m_ShotsIv.getY();

        ivY -= m_ScreenSizeY / m_Game.get_Shot().getSpeed();
        if (ivY < 0) {
            ivY = (int) m_PlayerIv.getY() - m_PlayerIv.getHeight() + m_ShotsIv.getHeight() / 2;
            ivX = ((int) m_PlayerIv.getX() + m_PlayerIv.getWidth() / 2) - m_ShotsIv.getWidth() / 2;
        }
        m_ShotsIv.setX(ivX);
        m_ShotsIv.setY(ivY);

        m_Game.get_Shot().setRect(ivY, ivX, ivY + m_ShotsIv.getHeight(), ivX + m_ShotsIv.getWidth());
    }

    private void createKingShots(ImageView kingShotIV, GameItem kingShot) {
        if (!m_KingIsAbove) {
            int ivX = (int) kingShotIV.getX();
            int ivY = (int) kingShotIV.getY();

            ivY += m_ScreenSizeY / kingShot.getSpeed();
            if (ivY > m_ScreenSizeY) {
                ivX = (int) Math.floor(Math.random() * (kingShotIV.getX() + m_EnemyKingIv.getWidth() - kingShotIV.getWidth()));
                ivY = 600;
            }
            kingShotIV.setX(ivX);
            kingShotIV.setY(ivY);

            kingShot.setRect(ivY, ivX, ivY + kingShotIV.getHeight(), ivX + kingShotIV.getWidth());
        }
    }


    private void sendGift() {

        checkIfTakeGifts();
        m_GiftIv.setY(m_GiftIv.getY() + m_ScreenSizeY / m_Game.get_Gift().getSpeed());
        if (m_GiftIv.getY() > m_ScreenSizeY) {
            m_Game.get_Gift().setSpeed(800, 100);
            m_GiftIv.setY(-40);
            m_GiftIv.setX((int) Math.floor(Math.random() * (m_ScreenSizeX - m_GiftIv.getWidth() * 2)));
        }

        m_Game.get_Gift().setRect((int) m_GiftIv.getY(),
                (int) m_GiftIv.getX(),
                (int) m_GiftIv.getY() + m_GiftIv.getHeight(),
                (int) m_GiftIv.getX() + m_GiftIv.getWidth());
    }

    private void sendAsteroid(ImageView asteroidIV, NonValueEnemyItem asteroid) {

        checkIfPlayerAsteroidCollision(asteroidIV, asteroid);
        asteroidIV.setX(asteroidIV.getX() + m_ScreenSizeX / asteroid.getSpeed() * asteroid.getDirection());
        asteroidIV.setY(asteroidIV.getY() + m_ScreenSizeY / 700);
        if (asteroid.getDirection() == 1) {
            if (asteroidIV.getX() > m_ScreenSizeX || asteroidIV.getY() > m_ScreenSizeY) {
                asteroid.setSpeed(1000, 400);
                asteroid.setDirection();
                setAsteroidState(asteroidIV, asteroid, 1);
            }
        } else {
            if (asteroidIV.getX() < 0 || asteroidIV.getY() > m_ScreenSizeY) {
                asteroid.setDirection();
                setAsteroidState(asteroidIV, asteroid, 1);
            }
        }

        asteroid.setRect((int) asteroidIV.getY(),
                (int) asteroidIV.getX(),
                (int) asteroidIV.getY() + asteroidIV.getHeight(),
                (int) asteroidIV.getX() + asteroidIV.getWidth());
    }

    private void moveEnemies(ImageView enemyIV, ValueEnemyItem enemy) {
        checkIfHitEnemies(enemyIV, enemy);
        checkIfPlayerEnemyCollision(enemyIV, enemy);
        enemyIV.setY(enemyIV.getY() + m_ScreenSizeY / enemy.getSpeed());
        if (enemyIV.getY() > m_ScreenSizeY) {
            enemy.setSpeed(1000, 300);
            m_Game.set_EnemiesCounter(m_Game.get_EnemiesCounter() + 1);
            enemyIV.setY(-300);
            enemyIV.setX((int) Math.floor(Math.random() * (m_ScreenSizeX - enemyIV.getWidth() * 2)));
        }

        enemy.setRect((int) enemyIV.getY(),
                (int) enemyIV.getX(),
                (int) enemyIV.getY() + enemyIV.getHeight(),
                (int) enemyIV.getX() + enemyIV.getWidth());
    }

    private void moveKing(ImageView enemyIV, ValueEnemyItem enemy) {
        checkIfHitKing(enemyIV, enemy);
        checkIfPlayerEnemyShotsCollision(m_EnemyKingShotIv, m_Game.get_KingShotOne());

        if (m_IsKingMoveLeft) {
            enemyIV.setX(enemyIV.getX() - m_ScreenSizeX / enemy.getSpeed());
            if (enemyIV.getX() < 0) {
                m_IsKingMoveLeft = false;
            }
        } else {
            enemyIV.setX(enemyIV.getX() + m_ScreenSizeX / enemy.getSpeed());
            if (enemyIV.getX() + enemyIV.getWidth() > m_ScreenSizeX) {
                m_IsKingMoveLeft = true;
            }
        }

        if (!m_KingIsAbove) {
            enemy.setRect((int) enemyIV.getY(),
                    (int) enemyIV.getX(),
                    (int) enemyIV.getY() + enemyIV.getHeight(),
                    (int) enemyIV.getX() + enemyIV.getWidth());
        }
    }

    private void checkIfHitKing(ImageView enemyIV, ValueEnemyItem enemy) {
        if (Rect.intersects(m_Game.get_Shot().getRect(), enemy.getRect())) {

            playSound(R.raw.crash_sound);
            enemyIV.startAnimation(m_AnimationKingBlink);

            m_Game.hitTheKing();
            if (m_Game.getEnemyKingLives() <= 0) {
                m_IsKingDead = true;
                enemyIV.setVisibility(View.GONE);
                m_Game.get_Player().setScore(m_Game.get_Player().getScore() + enemy.getValue());
                m_ScoreTv.setText(getResources().getString(R.string.score) + m_Game.get_Player().getScore());
            }

            m_ShotsIv.setX((int) m_PlayerIv.getY() - m_PlayerIv.getHeight() + m_ShotsIv.getHeight() / 2);
            m_ShotsIv.setY(((int) m_PlayerIv.getX() + m_PlayerIv.getWidth() / 2) - m_ShotsIv.getWidth() / 2);
        }
    }

    private void checkIfHitEnemies(ImageView enemyIV, ValueEnemyItem enemy) {
        if (Rect.intersects(m_Game.get_Shot().getRect(), enemy.getRect()) && !enemy.isExplode() && enemy.isIconOriginal()) {

            playSound(R.raw.crash_sound);

            m_Game.set_EnemiesCounter(m_Game.get_EnemiesCounter() + 1);

            startExplodeAnim(enemyIV, enemy);
            enemyIV.startAnimation(m_ExplodeAnim);

            m_ShotsIv.setX((int) m_PlayerIv.getY() - m_PlayerIv.getHeight() + m_ShotsIv.getHeight() / 2);
            m_ShotsIv.setY(((int) m_PlayerIv.getX() + m_PlayerIv.getWidth() / 2) - m_ShotsIv.getWidth() / 2);

            m_Game.get_Player().setScore(m_Game.get_Player().getScore() + enemy.getValue());
            m_ScoreTv.setText(getResources().getString(R.string.score) + m_Game.get_Player().getScore());
        }
    }

    private void checkIfTakeGifts() {

        if (Rect.intersects(m_Game.get_Gift().getRect(), m_Game.get_Player().getRect())) {
            playSound(R.raw.gift_sound);
            m_Game.get_Gift().setSpeed(800, 100);
            m_GiftIv.setX((int) Math.floor(Math.random() * (m_ScreenSizeX - m_GiftIv.getWidth())));
            m_GiftIv.setY(((int) Math.floor(Math.random() * (1000 - 100))) * -1);

            m_Game.get_Player().setScore(m_Game.get_Player().getScore() + m_Game.get_Gift().getValue());
            m_ScoreTv.setText(getResources().getString(R.string.score) + m_Game.get_Player().getScore());
        }
    }

    private void checkIfPlayerAsteroidCollision(ImageView asteroidIV, NonValueEnemyItem asteroid) {
        if (Rect.intersects(m_Game.get_Player().getRect(), asteroid.getRect())) {
            m_Game.get_Player().hit();

            asteroid.setSpeed(1000, 400);
            asteroidIV.setX(-100);
            asteroidIV.setY((int) Math.floor(Math.random() * (m_ScreenSizeY - asteroidIV.getHeight()) - m_ScreenSizeY / 4));
            m_ShotsIv.setX((int) m_PlayerIv.getY() - m_PlayerIv.getHeight() + m_ShotsIv.getHeight() / 2);
            m_ShotsIv.setY(((int) m_PlayerIv.getX() + m_PlayerIv.getWidth() / 2) - m_ShotsIv.getWidth() / 2);

            collision();

        }
    }

    private void collision() {
        if (m_Game.get_Player().getLives() == 0) {
            m_HeartOneIv.setImageResource(R.drawable.ic_hearts_gray);

            Intent intent = new Intent(RunGameActivity.this, GameOverActivity.class);
            intent.putExtra("Score", m_Game.m_Player.getScore());
            intent.putExtra("Sound", mIsSound);
            intent.putExtra("Vibrate", mIsVibrate);
            intent.putExtra("Music", mIsMusic);
            mServ.changeMusic(R.raw.main_music);
            mServ.pauseMusic();
            overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_out);
            isInIntent = false;
            startActivity(intent);
            this.finish();
        }

        if (m_Game.get_Player().getLives() == 1) {
            m_HeartTwoIv.setImageResource(R.drawable.ic_hearts_gray);
        }
        if (m_Game.get_Player().getLives() == 2) {
            m_HeartThreeIv.setImageResource(R.drawable.ic_hearts_gray);
        }
        playSound(R.raw.explode);
        //Broken heart animation
        m_BrokenHeartIv.startAnimation(m_AnimationFadeInOut);
        m_BrokenHeartIv.setVisibility(View.INVISIBLE);
        m_PlayerIv.startAnimation(m_AnimationSpaceShipBlink);
        vibrate();
    }


    private void checkIfPlayerEnemyShotsCollision(ImageView kingShotIV, GameItem kingShot) {
        if (Rect.intersects(m_Game.get_Player().getRect(), kingShot.getRect())) {
            m_Game.get_Player().hit();

            kingShotIV.setX((int) Math.floor(Math.random() * (kingShotIV.getX() + m_EnemyKingIv.getWidth() - kingShotIV.getWidth())));
            kingShotIV.setY(600);

            collision();

        }
    }

    private void checkIfPlayerEnemyCollision(ImageView enemyIV, ValueEnemyItem enemy) {
        if (Rect.intersects(m_Game.get_Player().getRect(), enemy.getRect()) && !enemy.isExplode() && enemy.isIconOriginal()) {
            m_Game.get_Player().hit();
            enemy.setSpeed(1000, 300);
            enemyIV.setX((int) Math.floor(Math.random() * (m_ScreenSizeX - enemyIV.getWidth())));
            enemyIV.setY(-300);
            m_ShotsIv.setX((int) m_PlayerIv.getY() - m_PlayerIv.getHeight() + m_ShotsIv.getHeight() / 2);
            m_ShotsIv.setY(((int) m_PlayerIv.getX() + m_PlayerIv.getWidth() / 2) - m_ShotsIv.getWidth() / 2);

            collision();

        }
    }


    private ServiceConnection Scon = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder
                binder) {
            mServ = ((MusicService.ServiceBinder) binder).getService();
            if (!mIsMusic && mServ != null) {
                mServ.stopMusic();
            } else {
                if (mIsMusic) {
                    mServ.changeMusic(R.raw.stage_one_music);
                }
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };

    void doBindService() {
        bindService(new Intent(this, MusicService.class),
                Scon, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            unbindService(Scon);
            mIsBound = false;
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void vibrate() {
        if (mIsVibrate) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                v.vibrate(500);
            }
        }
    }

    private void playSound(int sound) {
        if (mIsSound) {
            MediaPlayer pressSound = MediaPlayer.create(RunGameActivity.this, sound);
            pressSound.setVolume(30, 30);
            pressSound.start();
        }
    }

    private void startExplodeAnim(ImageView enemyIV, ValueEnemyItem enemy) {
        enemy.setSpeed(1000, 300);
        enemy.setExplode(true);
    }

    private void onStartExplodeAnim() {
        if (m_Game.get_Enemy_one().isExplode()) {
            m_EnemyOneIv.setImageDrawable(m_EnemyExplodeDrawableAnim);
            m_EnemyExplodeDrawableAnim.start();
            m_Game.get_Enemy_one().setExplode(false);
            m_Game.get_Enemy_one().setIconOriginal(false);
        }

        if (m_Game.get_Enemy_two().isExplode()) {
            m_EnemyTwoIv.setImageDrawable(m_EnemyExplodeDrawableAnim);
            m_EnemyExplodeDrawableAnim.start();
            m_Game.get_Enemy_two().setExplode(false);
            m_Game.get_Enemy_two().setIconOriginal(false);
        }

        if (m_Game.get_Enemy_three().isExplode()) {
            m_EnemyThreeIv.setImageDrawable(m_EnemyExplodeDrawableAnim);
            m_EnemyExplodeDrawableAnim.start();
            m_Game.get_Enemy_three().setExplode(false);
            m_Game.get_Enemy_three().setIconOriginal(false);
        }

        if (m_Game.get_Enemy_four().isExplode()) {
            m_EnemyFourIv.setImageDrawable(m_EnemyExplodeDrawableAnim);
            m_EnemyExplodeDrawableAnim.start();
            m_Game.get_Enemy_four().setExplode(false);
            m_Game.get_Enemy_four().setIconOriginal(false);
        }

        if (m_Game.get_Enemy_five().isExplode()) {
            m_EnemyFiveIv.setImageDrawable(m_EnemyExplodeDrawableAnim);
            m_EnemyExplodeDrawableAnim.start();
            m_Game.get_Enemy_five().setExplode(false);
            m_Game.get_Enemy_five().setIconOriginal(false);
        }

        if (m_Game.get_Enemy_six().isExplode()) {
            m_EnemySixIv.setImageDrawable(m_EnemyExplodeDrawableAnim);
            m_EnemyExplodeDrawableAnim.start();
            m_Game.get_Enemy_six().setExplode(false);
            m_Game.get_Enemy_six().setIconOriginal(false);
        }
    }


    private void onEndExplodeAnim() {

        if (!m_Game.get_Enemy_one().isIconOriginal()) {
            m_EnemyOneIv.setX((int) Math.floor(Math.random() * (m_ScreenSizeX - m_EnemyOneIv.getWidth())));
            m_EnemyOneIv.setY(-300);

            m_EnemyOneIv.setImageDrawable(m_EnemyOneAnim);
            m_EnemyOneAnim.start();
            m_Game.get_Enemy_one().setIconOriginal(true);
        }
        if (!m_Game.get_Enemy_two().isIconOriginal()) {
            m_EnemyTwoIv.setX((int) Math.floor(Math.random() * (m_ScreenSizeX - m_EnemyTwoIv.getWidth())));
            m_EnemyTwoIv.setY(-300);

            m_EnemyTwoIv.setImageDrawable(m_EnemyTwoAnim);
            m_EnemyTwoAnim.start();
            m_Game.get_Enemy_two().setIconOriginal(true);
        }
        if (!m_Game.get_Enemy_three().isIconOriginal()) {
            m_EnemyThreeIv.setX((int) Math.floor(Math.random() * (m_ScreenSizeX - m_EnemyThreeIv.getWidth())));
            m_EnemyThreeIv.setY(-300);

            m_EnemyThreeIv.setImageDrawable(m_EnemyThreeAnim);
            m_EnemyThreeAnim.start();
            m_Game.get_Enemy_three().setIconOriginal(true);
        }
        if (!m_Game.get_Enemy_four().isIconOriginal()) {
            m_EnemyFourIv.setX((int) Math.floor(Math.random() * (m_ScreenSizeX - m_EnemyFourIv.getWidth())));
            m_EnemyFourIv.setY(-300);

            m_EnemyFourIv.setImageDrawable(m_EnemyFourAnim);
            m_EnemyFourAnim.start();
            m_Game.get_Enemy_four().setIconOriginal(true);
        }

        if (!m_Game.get_Enemy_five().isIconOriginal()) {
            m_EnemyFiveIv.setX((int) Math.floor(Math.random() * (m_ScreenSizeX - m_EnemyFiveIv.getWidth())));
            m_EnemyFiveIv.setY(-300);

            m_EnemyFiveIv.setImageDrawable(m_EnemyFiveAnim);
            m_EnemyFiveAnim.start();
            m_Game.get_Enemy_five().setIconOriginal(true);
        }
        if (!m_Game.get_Enemy_six().isIconOriginal()) {
            m_EnemySixIv.setX((int) Math.floor(Math.random() * (m_ScreenSizeX - m_EnemySixIv.getWidth())));
            m_EnemySixIv.setY(-300);

            m_EnemySixIv.setImageDrawable(m_EnemySixAnim);
            m_EnemySixAnim.start();
            m_Game.get_Enemy_six().setIconOriginal(true);
        }
    }
}
