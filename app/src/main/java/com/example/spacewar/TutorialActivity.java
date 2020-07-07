package com.example.spacewar;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
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
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class TutorialActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener {

    private final static int MARGIN_CIRCLE = 45;
    private final static int MARGIN_SIZE = 200;
    private Rect m_PlayerRect, m_EnemyRect, m_AsteroidRect, m_GiftRect, m_ShotsRect;
    ImageView m_Player, m_Enemy, m_Gift, m_Shots, m_Asteroid, m_Circle, m_BackgroundOne, m_BackgroundTwo;
    Button m_SkipBtn;
    TextView m_TutorialTv;
    FrameLayout m_FrameLayout;

    private boolean m_IsLearning,m_IsKnowToMove, m_IsKnowToKill, m_IsKnowToTakeGift, m_IsKnowToAvoidAsteroid, m_IsCountdown;
    private boolean m_IsStart = true, m_IsInIntent = true, m_IsExplode = false;
    private int m_GiftsCollectedCounter, m_AsteroidsPassedCounter, m_EnemiesKilledCounter, m_PlayerMovedCounter;
    private int m_XDelta, m_YDelta, m_LastAction, m_ScreenSizeX, m_ScreenSizeY, m_EnemyX, m_EnemyY, m_AsteroidX, m_AsteroidY, m_GiftX,m_GiftY;
    private Timer m_Timer = new Timer();
    private Handler m_Handler = new Handler();
    private CountDownTimer m_CountDownTimer = null;
    private CountDownTimer m_FinishTutorialCountDownTimer = null;


    //music and vibrate;
    private MusicService mServ;
    private HomeWatcher mHomeWatcher;
    private boolean m_IsBound = false;
    private boolean m_IsMusic, m_IsVibrate, m_IsSound;
    private Vibrator v;

    //shared preference
    private SharedPreferences isPassedTutorial;

    //animations
    private Animation m_AnimationBlink, m_CircleBlinkAnim, m_ExplodeAnim;
    private AnimationDrawable m_EnemyAnim, m_AsteroidAnim, m_ExplodeDrawableAnim;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        //Get screen size
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        m_ScreenSizeX = size.x;
        m_ScreenSizeY = size.y;

        //Find view by id
        m_FrameLayout = findViewById(R.id.frame_layout);
        m_TutorialTv = findViewById(R.id.tutorial_tv);
        m_Enemy = findViewById(R.id.enemy_imageView);
        m_Asteroid = findViewById(R.id.asteroid_imageView);
        m_Player = findViewById(R.id.player_imageView);
        m_Shots = findViewById(R.id.shot_imageView);
        m_Gift = findViewById(R.id.gift_imageView);
        m_Circle = findViewById(R.id.circle_imageView);
        m_BackgroundOne = findViewById(R.id.background_one);
        m_BackgroundTwo = findViewById(R.id.background_two);
        m_SkipBtn = findViewById(R.id.skip_btn);

        //Set listeners
        m_Player.setOnTouchListener(this);
        m_SkipBtn.setOnClickListener(this);
        m_FrameLayout.setOnTouchListener(this);


        //Set player info
        m_PlayerRect = new Rect(0,0,0,0);

        //Set Circle focus location
        m_Circle.setX(m_EnemyX - MARGIN_CIRCLE);
        m_Circle.setY(m_EnemyY - MARGIN_CIRCLE);

        //Set enemy location
        m_EnemyX = (int) Math.floor(Math.random() * (m_ScreenSizeX - m_Enemy.getWidth()));
        m_EnemyY = -200;
        m_Enemy.setX(m_EnemyX);
        m_Enemy.setY(m_EnemyY);
        m_EnemyRect = new Rect(m_EnemyX,m_EnemyY,m_EnemyX+m_Enemy.getWidth(),m_EnemyY+m_Enemy.getHeight());

        //Set asteroid location
        m_AsteroidX = (int) Math.floor(Math.random() * (m_ScreenSizeX - m_Asteroid.getWidth()));
        m_AsteroidY = -400;
        m_Asteroid.setX(m_AsteroidX);
        m_Asteroid.setY(m_AsteroidY);
        m_AsteroidRect = new Rect(m_AsteroidX,m_AsteroidY,m_AsteroidX+m_Asteroid.getWidth(),m_AsteroidY+m_Asteroid.getHeight());

        //set gift location
        m_GiftX = (int) Math.floor(Math.random() * (m_ScreenSizeX - m_Gift.getWidth()));
        m_GiftY = -200;
        m_Gift.setX(m_GiftX);
        m_Gift.setY(m_GiftY);
        m_GiftRect = new Rect(m_GiftX,m_GiftY,m_GiftX+m_Gift.getWidth(),m_GiftY+m_Gift.getHeight());

        //set shot info
        m_ShotsRect = new Rect(m_EnemyX,m_EnemyY,m_EnemyX+m_Enemy.getWidth(),m_EnemyY+m_Enemy.getHeight());

        //Moving background animation
        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(20000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
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
        animator.start();

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        //initialize animation
        m_AnimationBlink = AnimationUtils.loadAnimation(this,R.anim.hit_spaceship_blink);

        m_EnemyAnim = (AnimationDrawable) m_Enemy.getDrawable();
        m_EnemyAnim.start();

        m_ExplodeDrawableAnim = (AnimationDrawable) getResources().getDrawable(R.drawable.explode_enemy_anim);
        m_ExplodeAnim = AnimationUtils.loadAnimation(this,R.anim.hit_king_blink);
        m_ExplodeAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                m_IsExplode = true;
                m_Enemy.setImageDrawable(m_ExplodeDrawableAnim);
                m_ExplodeDrawableAnim.start();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                m_IsExplode = false;
                m_EnemyY = -300;
                m_EnemyX = (int) Math.floor(Math.random() * (m_ScreenSizeX - m_Enemy.getWidth()));
                m_Enemy.setImageDrawable(m_EnemyAnim);
                m_EnemyAnim.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        m_CircleBlinkAnim = AnimationUtils.loadAnimation(this,R.anim.blink);
        m_Circle.startAnimation(m_CircleBlinkAnim);



        m_AsteroidAnim = (AnimationDrawable) m_Asteroid.getDrawable();
        m_AsteroidAnim.start();



        //getExtras
        Intent intent = getIntent();
        m_IsMusic = intent.getBooleanExtra("Music",false);
        m_IsVibrate = intent.getBooleanExtra("Vibrate",false);
        m_IsSound = intent.getBooleanExtra("Sound",false);

        //Music background
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

        //set boolean
        m_IsLearning = true;
        m_IsKnowToMove = false;
        m_IsKnowToKill = false;
        m_IsKnowToTakeGift = false;
        m_IsKnowToAvoidAsteroid = false;
        m_IsCountdown = true;



        m_CountDownTimer = new CountDownTimer(4000,1000){

            @Override
            public void onTick(long millisUntilFinished) {
                if(millisUntilFinished < 4000 && millisUntilFinished > 3000 )
                    m_TutorialTv.setText("3");
                if(millisUntilFinished < 3000 && millisUntilFinished > 2000 )
                    m_TutorialTv.setText("2");
                if(millisUntilFinished < 2000 && millisUntilFinished > 1000 )
                    m_TutorialTv.setText("1");
                if(millisUntilFinished < 1000)
                    m_TutorialTv.setText(R.string.go);
            }

            @Override
            public void onFinish() {
                m_IsCountdown = false;
                m_GiftsCollectedCounter = 0;
                m_AsteroidsPassedCounter = 0;
                m_EnemiesKilledCounter = 0;
                m_PlayerMovedCounter = 0;
                m_TutorialTv.setText(getResources().getString(R.string.move_space) +"\n" + m_PlayerMovedCounter + "/4");

                m_Timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        m_Handler.post(new Runnable() {
                            @Override
                            public void run() {
                                locateCircle();
                                if(m_IsLearning) {
                                    createShots();
                                    if (m_IsKnowToMove)
                                        moveEnemies();
                                    if (m_IsKnowToKill)
                                        moveAsteroid();
                                    if (m_IsKnowToAvoidAsteroid)
                                        sendGift();
                                    if (m_IsKnowToTakeGift && m_IsStart) {
                                        m_Circle.setVisibility(View.GONE);
                                        m_Circle.clearAnimation();
                                        startGame();
                                    }
                                }
                            }
                        });
                    }
                }, 0, 20);
            }
        }.start();
    }

    private void locateCircle() {
        if (m_IsKnowToMove && !m_IsKnowToKill) {
            m_Circle.setVisibility(View.VISIBLE);
            m_Circle.setX(m_EnemyX - MARGIN_CIRCLE);
            m_Circle.setY(m_EnemyY - MARGIN_CIRCLE);
        }

        if (m_IsKnowToKill && !m_IsKnowToAvoidAsteroid) {
            m_Circle.setX(m_AsteroidX - MARGIN_CIRCLE);
            m_Circle.setY(m_AsteroidY - MARGIN_CIRCLE);
        }

        if (m_IsKnowToAvoidAsteroid && !m_IsKnowToTakeGift) {
            m_Circle.getLayoutParams().width = MARGIN_SIZE;
            m_Circle.getLayoutParams().height = MARGIN_SIZE;
            m_Circle.requestLayout();

            m_Circle.setX(m_GiftX - MARGIN_CIRCLE-5);
            m_Circle.setY(m_GiftY - MARGIN_CIRCLE-5);
        }

        if(!m_IsLearning)
        {
            m_Circle.setVisibility(View.GONE);
        }
    }

    private void startGame()
    {
        m_Timer.cancel();
        m_FinishTutorialCountDownTimer = new CountDownTimer(2000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(m_IsStart)
                {
                    m_SkipBtn.setVisibility(View.GONE);
                    m_Asteroid.setVisibility(View.GONE);
                    m_Enemy.setVisibility(View.GONE);
                    m_Shots.setVisibility(View.GONE);
                    m_Gift.setVisibility(View.GONE);
                    movePlayerUpWhileFinishLevel();
                    m_IsStart = false;
                }
            }

            @Override
            public void onFinish() {
                if(m_IsInIntent) {
                    m_IsInIntent=false;
                    Intent intent = new Intent(TutorialActivity.this, RunGameActivity.class);
                    intent.putExtra("Sound", m_IsSound);
                    intent.putExtra("Vibrate", m_IsVibrate);
                    intent.putExtra("Music", m_IsMusic);
                    setSharedPreference();
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    TutorialActivity.this.finish();
                    m_IsLearning = false;
                }
            }
        }.start();

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(m_IsStart) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    m_XDelta = (int) (m_Player.getX() - event.getRawX());
                    m_YDelta = (int) (m_Player.getY() - event.getRawY());
                    m_LastAction = MotionEvent.ACTION_DOWN;
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (event.getRawX() + m_XDelta > 0 && event.getRawX() + m_XDelta < m_ScreenSizeX - m_Player.getWidth())
                        m_Player.setX(event.getRawX() + m_XDelta);
                    if (event.getRawY() + m_YDelta > m_ScreenSizeY / 2 && event.getRawY() + m_YDelta < m_ScreenSizeY - m_Player.getHeight())
                        m_Player.setY(event.getRawY() + m_YDelta);

                    m_LastAction = MotionEvent.ACTION_MOVE;

                    m_PlayerRect.left = (int) m_Player.getX();
                    m_PlayerRect.top = (int) m_Player.getY();
                    m_PlayerRect.right = (int) m_Player.getX() + m_Player.getWidth();
                    m_PlayerRect.bottom = (int) m_Player.getY() + m_Player.getHeight();
                    break;

                case MotionEvent.ACTION_UP:
                    if (!m_IsKnowToMove && !m_IsCountdown) {
                        m_PlayerMovedCounter++;
                        m_TutorialTv.setText(getResources().getString(R.string.move_space) + "\n" + m_PlayerMovedCounter + "/4");

                        if (m_PlayerMovedCounter == 4) {
                            m_IsKnowToMove = true;
                            m_TutorialTv.setText(getResources().getString(R.string.kill_enemies) + "\n" + m_EnemiesKilledCounter + "/3");
                        }
                    }
                    break;

                default:
                    return false;
            }
        }
        return true;
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.skip_btn:
                vibrate();
                playSound(R.raw.click_electronic);
                Intent intent = new Intent(TutorialActivity.this, RunGameActivity.class);
                intent.putExtra("Sound", m_IsSound);
                intent.putExtra("Vibrate", m_IsVibrate);
                intent.putExtra("Music", m_IsMusic);
                setSharedPreference();
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                TutorialActivity.this.finish();
                m_IsLearning = false;
                m_Timer.cancel();
        }
    }

    private void createShots()
    {
        int ivX = (int)m_Shots.getX();
        int ivY = (int)m_Shots.getY();

        ivY -= m_ScreenSizeY / 40;
        if (ivY < 0 ) {
            ivY = (int)m_Player.getY() - m_Player.getHeight() + m_Shots.getHeight()/2;
            ivX = ((int)m_Player.getX() + m_Player.getWidth()/2) - m_Shots.getWidth()/2;
        }
        m_Shots.setX(ivX);
        m_Shots.setY(ivY);

        m_ShotsRect.left = ivX;
        m_ShotsRect.top = ivY;
        m_ShotsRect.right = ivX+m_Shots.getWidth();
        m_ShotsRect.bottom = ivY+m_Shots.getHeight();
    }


    private void sendGift() {

        checkIfTakeGifts();
        m_GiftY += m_ScreenSizeY/400;
        if(m_GiftY > m_ScreenSizeY)
        {
            m_GiftY = -40;
            m_GiftX = (int) Math.floor(Math.random() * (m_ScreenSizeX - m_Gift.getWidth()*2));
        }
        m_Gift.setX(m_GiftX);
        m_Gift.setY(m_GiftY);

        m_GiftRect.left = m_GiftX;
        m_GiftRect.top = m_GiftY;
        m_GiftRect.right = m_GiftX+m_Gift.getWidth();
        m_GiftRect.bottom = m_GiftY+m_Gift.getHeight();
    }

    private void moveEnemies()
    {
        checkIfHitEnemies();
        checkIfEnemyPlayerCollision();
        m_EnemyY += m_ScreenSizeY/500;
        if(m_EnemyY > m_ScreenSizeY)
        {

            m_EnemyY = -300;
            m_EnemyX = (int) Math.floor(Math.random() * (m_ScreenSizeX - m_Enemy.getWidth()*2));
        }
        m_Enemy.setX(m_EnemyX);
        m_Enemy.setY(m_EnemyY);

        m_EnemyRect.left = m_EnemyX;
        m_EnemyRect.top = m_EnemyY;
        m_EnemyRect.right = m_EnemyX+m_Enemy.getWidth();
        m_EnemyRect.bottom = m_EnemyY+m_Enemy.getHeight();

    }

    private void checkIfEnemyPlayerCollision()
    {
        if (Rect.intersects(m_PlayerRect,m_EnemyRect) && !m_IsExplode){
            vibrate();
            playSound(R.raw.explode);


            m_EnemyY = -300;
            m_EnemyX = (int) Math.floor(Math.random() * (m_ScreenSizeX - m_Enemy.getWidth()));
            m_Enemy.setX(m_EnemyX);
            m_Enemy.setY(m_EnemyY);

            m_EnemyRect.left = m_EnemyX;
            m_EnemyRect.top = m_EnemyY;
            m_EnemyRect.right = m_EnemyX+m_Enemy.getWidth();
            m_EnemyRect.bottom = m_EnemyY+m_Enemy.getHeight();

            Toast.makeText(TutorialActivity.this,getResources().getString(R.string.collision_alert_tutorial),Toast.LENGTH_SHORT).show();
            m_Player.startAnimation(m_AnimationBlink);

        }
    }

    private void moveAsteroid() {
        checkifAsteroidPlayerCollision();
        m_AsteroidY += m_ScreenSizeY/400;
        if(m_AsteroidY > m_ScreenSizeY)
        {
            m_AsteroidY = -40;
            m_AsteroidX = (int) Math.floor(Math.random() * (m_ScreenSizeX - m_Asteroid.getWidth()*2));

            if (!m_IsKnowToAvoidAsteroid) {
                m_AsteroidsPassedCounter++;
                m_TutorialTv.setText(getResources().getString(R.string.avoid_asteroids) + "\n" + m_AsteroidsPassedCounter + "/2");
                if (m_AsteroidsPassedCounter == 2) {
                    m_IsKnowToAvoidAsteroid = true;
                    m_TutorialTv.setText(getResources().getString(R.string.take_gifts) + "\n" + m_GiftsCollectedCounter + "/3");
                }
            }

        }
        m_Asteroid.setX(m_AsteroidX);
        m_Asteroid.setY(m_AsteroidY);

        m_AsteroidRect.left = m_AsteroidX;
        m_AsteroidRect.top = m_AsteroidY;
        m_AsteroidRect.right = m_AsteroidX+m_Asteroid.getWidth();
        m_AsteroidRect.bottom = m_AsteroidY+m_Asteroid.getHeight();

    }

    private void checkifAsteroidPlayerCollision()
    {
        if (Rect.intersects(m_PlayerRect,m_AsteroidRect)){
            vibrate();
            playSound(R.raw.explode);

            m_AsteroidY = -40;
            m_AsteroidX = (int) Math.floor(Math.random() * (m_ScreenSizeX - m_Asteroid.getWidth()));
            m_Asteroid.setX(m_AsteroidX);
            m_Asteroid.setY(m_AsteroidY);

            m_AsteroidRect.left = m_AsteroidX;
            m_AsteroidRect.top = m_AsteroidY;
            m_AsteroidRect.right = m_AsteroidX+m_Asteroid.getWidth();
            m_AsteroidRect.bottom = m_AsteroidY+m_Asteroid.getHeight();

            Toast.makeText(TutorialActivity.this,getResources().getString(R.string.collision_alert_tutorial),Toast.LENGTH_SHORT).show();
            m_Player.startAnimation(m_AnimationBlink);
        }
    }

    private void checkIfHitEnemies() {
        if (Rect.intersects(m_ShotsRect, m_EnemyRect) && !m_IsExplode) {
            playSound(R.raw.crash_sound);
            m_Enemy.startAnimation(m_ExplodeAnim);

            m_Enemy.setX(m_EnemyX);
            m_Enemy.setY(m_EnemyY);

            m_Shots.setX((int) m_Player.getY() - m_Player.getHeight() + m_Shots.getHeight() / 2);
            m_Shots.setY(((int) m_Player.getX() + m_Player.getWidth() / 2) - m_Shots.getWidth() / 2);

            if (!m_IsKnowToKill) {
                m_EnemiesKilledCounter++;
                m_TutorialTv.setText(getResources().getString(R.string.kill_enemies) + "\n" + m_EnemiesKilledCounter + "/3");
                if (m_EnemiesKilledCounter == 3) {
                    m_IsKnowToKill = true;
                    m_TutorialTv.setText(getResources().getString(R.string.avoid_asteroids)+"\n" + m_AsteroidsPassedCounter + "/2");
                }
            }
        }
    }

    private void checkIfTakeGifts()
    {

        if(Rect.intersects(m_GiftRect,m_PlayerRect))
        {
            playSound(R.raw.gift_sound);
            m_GiftY = ((int) Math.floor(Math.random() * (1000 - 100))) * -1;
            m_GiftX = (int) Math.floor(Math.random() * (m_ScreenSizeX - m_Gift.getWidth()));

            m_Gift.setX(m_GiftX);
            m_Gift.setY(m_GiftY);

            if(!m_IsKnowToTakeGift) {
                m_GiftsCollectedCounter++;
                m_TutorialTv.setText(getResources().getString(R.string.take_gifts) + "\n" + m_GiftsCollectedCounter + "/3");

                if(m_GiftsCollectedCounter == 3) {
                    m_IsKnowToTakeGift = true;
                    m_TutorialTv.setText(getResources().getString(R.string.finish_tutorial));
                }
            }
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

                        m_Player.setY(m_Player.getY()-m_ScreenSizeY/150);
                        if(m_Player.getY() < -200)
                        {
                            isUp[0] = false;

                            m_Player.setY(m_ScreenSizeY-500);
                            m_Player.setX(m_ScreenSizeX/2 - m_Player.getWidth()/2);
                            m_Player.setY(m_Player.getY()-m_ScreenSizeY/150);

                        }
                        if(!isUp[0] && m_Player.getY() < m_ScreenSizeY-200-m_Player.getHeight())
                        {   t.cancel(); }
                    }
                });
            }}, 0, 20);

    }



    private ServiceConnection Scon = new ServiceConnection(){

        public void onServiceConnected(ComponentName name, IBinder
                binder) {
            mServ = ((MusicService.ServiceBinder)binder).getService();
            if(!m_IsMusic && mServ != null)
            {
                mServ.stopMusic();
            }
            else
            {
                mServ.changeMusic(R.raw.app_music_2);
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };

    void doBindService(){
        bindService(new Intent(this,MusicService.class),
                Scon, Context.BIND_AUTO_CREATE);
        m_IsBound = true;
    }

    void doUnbindService()
    {
        if(m_IsBound)
        {
            unbindService(Scon);
            m_IsBound = false;
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



    protected void onPause() {
        super.onPause();

        try {
            m_IsLearning = false;
            m_Timer.cancel();
            m_Timer = null;

        } catch (Exception e) { }

        PowerManager pm = (PowerManager)
                getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = false;
        if (pm != null) {
            isScreenOn = pm.isScreenOn();
        }

        if (!isScreenOn) {
            if (mServ != null && m_IsMusic) {
                mServ.pauseMusic();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        m_IsLearning = true;
        m_Timer = new Timer();
        m_Timer.schedule(new TimerTask() {
            @Override
            public void run() {
                m_Handler.post(new Runnable() {
                    @Override
                    public void run() {
                        locateCircle();
                        if(m_IsLearning) {
                            createShots();
                            if (m_IsKnowToMove)
                                moveEnemies();
                            if (m_IsKnowToKill)
                                moveAsteroid();
                            if (m_IsKnowToAvoidAsteroid)
                                sendGift();
                            if (m_IsKnowToTakeGift && m_IsStart) {
                                m_Circle.setVisibility(View.GONE);
                                m_Circle.clearAnimation();
                                startGame();
                            }
                        }
                    }
                });
            }
        }, 0, 20);

        //music
        if (mServ != null&& m_IsMusic) {
            mServ.resumeMusic();
        }
    }

    private void setSharedPreference() {
        isPassedTutorial = getSharedPreferences("tutorial", Context.MODE_PRIVATE);
        SharedPreferences.Editor editorPassedTutorial = isPassedTutorial.edit();
        editorPassedTutorial.putBoolean("tutorial",true);
        editorPassedTutorial.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //music
        doUnbindService();
        Intent music = new Intent();
        music.setClass(this,MusicService.class);
        stopService(music);

        if(m_CountDownTimer!=null)
            m_CountDownTimer.cancel();
    }

    private void vibrate() {
        if(m_IsVibrate)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                v.vibrate(500);
            }
        }
    }

    private void playSound(int sound) {
        if(m_IsSound){
            MediaPlayer pressSound = MediaPlayer.create(TutorialActivity.this, sound);
            pressSound.setVolume(30,30);
            pressSound.start();
        }
    }
}