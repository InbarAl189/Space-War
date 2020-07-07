package com.example.spacewar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private int m_VideoCurrPosition;
    private boolean mIsBound = false;
    private boolean[] checkedItems;
    private Animation upToDown,downToUp;
    private Vibrator v;
    private VideoView m_VideoView;
    private MediaPlayer m_MediaPlayer;
    private HomeWatcher mHomeWatcher;
    Button m_PlayButton;
    Button m_HighScoreButton;
    LinearLayout bottomLayout, logoLayout;
    GifImageView m_spaceshipGif;

    boolean isSoundCheck = true, isVibrateCheck = true, isMusicCheck = true, mIsMusic, mIsVibrate, mIsSound, isPassedTutorial;
    private SharedPreferences isPassedTutorialSP;
    private MusicService mServ;
    private ServiceConnection Scon = new ServiceConnection(){

        public void onServiceConnected(ComponentName name, IBinder
                binder) {
            mServ = ((MusicService.ServiceBinder)binder).getService();
            if(!mIsMusic && mServ != null)
            {
                mServ.stopMusic();
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };

    void doBindService(){
        bindService(new Intent(this,MusicService.class),
                Scon,Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService()
    {
        if(mIsBound)
        {
            unbindService(Scon);
            mIsBound = false;
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        m_PlayButton = findViewById(R.id.play_game_btn);
        m_PlayButton.setOnClickListener(this);
        m_HighScoreButton = findViewById(R.id.high_score_btn);
        m_HighScoreButton.setOnClickListener(this);

        Intent intent = getIntent();
        mIsMusic = intent.getBooleanExtra("Music",true);
        mIsVibrate = intent.getBooleanExtra("Vibrate",true);
        mIsSound = intent.getBooleanExtra("Sound",true);
        isMusicCheck = mIsMusic;
        isVibrateCheck = mIsVibrate;
        isSoundCheck = mIsSound;

        checkedItems = new boolean[]{mIsSound, mIsMusic, mIsVibrate};



        m_VideoView = (VideoView) findViewById(R.id.videoView);
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.main_vid_back_sound_off);
        m_VideoView.setVideoURI(uri);
        m_VideoView.start();

        m_VideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                              @Override
                                              public void onPrepared(MediaPlayer mp) {
                                                  m_MediaPlayer = mp;
                                                  m_MediaPlayer.setLooping(true);
                                                  m_MediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);

                                                  if(m_VideoCurrPosition != 0) {
                                                      m_MediaPlayer.seekTo(m_VideoCurrPosition);
                                                      m_MediaPlayer.start();
                                                  }
                                              }
                                          }
        );
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

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        //Shared preference - tutorial
        isPassedTutorialSP = getSharedPreferences("tutorial", Context.MODE_PRIVATE);
        isPassedTutorial = isPassedTutorialSP.getBoolean("tutorial",false);

        //Animated spaceship git
        m_spaceshipGif = findViewById(R.id.space_ship_gif_view);
        ObjectAnimator animation = ObjectAnimator.ofFloat(m_spaceshipGif, "translationY", -100f);
        animation.setDuration(2000);
        animation.setRepeatMode(ValueAnimator.REVERSE);
        animation.setRepeatCount(ValueAnimator.INFINITE);
        animation.start();

        //Animation layout
        bottomLayout = (LinearLayout) findViewById(R.id.bottom_layout);
        logoLayout = (LinearLayout) findViewById(R.id.logo_layout);
        upToDown = AnimationUtils.loadAnimation(this,R.anim.up_to_down);
        downToUp = AnimationUtils.loadAnimation(this,R.anim.down_to_up);
        bottomLayout.setAnimation(downToUp);
        logoLayout.setAnimation(upToDown);


    }

    @Override
    protected void onPause() {
        super.onPause();
        m_VideoCurrPosition = m_MediaPlayer.getCurrentPosition();
        m_VideoView.pause();

        PowerManager pm = (PowerManager)
                getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = false;
        if (pm != null) {
            isScreenOn = pm.isScreenOn();
        }

        if (!isScreenOn) {
            if (mServ != null) {
                mServ.pauseMusic();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        m_VideoView.start();

        //music
        if (mServ != null && mIsMusic) {
            mServ.resumeMusic();
            if(isMusicCheck)
                mServ.changeMusic(R.raw.main_music);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        m_MediaPlayer.release();
        m_MediaPlayer = null;
        //music
        doUnbindService();
        Intent music = new Intent();
        music.setClass(this,MusicService.class);
        stopService(music);
    }

    @Override
    public void onClick(View v) {
        setSoundAndVibrateOnClick();

        switch (v.getId())
        {

            case R.id.high_score_btn:
                Intent intent = new Intent(MainActivity.this, HighScoreActivity.class);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out);
                intent.putExtra("Music",isMusicCheck);
                intent.putExtra("CallIntent","Main");
                startActivity(intent);
                break;
            case R.id.play_game_btn:
                if(!isPassedTutorial)
                    openTutorialIntent();
                else
                    openRunGameIntent();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }

    private void openRunGameIntent() {
        Intent intent1 = new Intent(MainActivity.this, RunGameActivity.class);
        overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in);
        intent1.putExtra("Sound",isSoundCheck);
        intent1.putExtra("Vibrate",isVibrateCheck);
        intent1.putExtra("Music",isMusicCheck);
        startActivity(intent1);
    }

    private void openTutorialIntent() {
        Intent intent1 = new Intent(MainActivity.this, TutorialActivity.class);
        overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in);
        intent1.putExtra("Sound",isSoundCheck);
        intent1.putExtra("Vibrate",isVibrateCheck);
        intent1.putExtra("Music",isMusicCheck);
        startActivity(intent1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_menu,menu);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_first);// set drawable icon
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        setSoundAndVibrateOnClick();

        switch (item.getItemId()) {
            case R.id.setting:
                createAlertDialogForSettings();
                return true;
            case R.id.tutorial:
                openTutorialIntent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createAlertDialogForSettings() {

        LayoutInflater inflater = getLayoutInflater();
        View settingsLayout = inflater.inflate(R.layout.settings_dialog, null);

        AlertDialog.Builder settingsBuilder = new AlertDialog.Builder(MainActivity.this);
        settingsBuilder.setCustomTitle(settingsLayout);




        // add a checkbox list
        settingsBuilder.setMultiChoiceItems(R.array.settings_array, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                // user checked or unchecked a box
                setSoundAndVibrateOnClick();

                switch(which)
                {
                    case 0: //Sound
                        isSoundCheck = isChecked;
                        break;
                    case 1://Music
                        isMusicCheck = isChecked;
                        if(isMusicCheck) {
                            mServ.startMusic();
                        }
                        else {
                            mServ.stopMusic();
                        }
                        break;
                    case 2://Vibtate
                        isVibrateCheck = isChecked;
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value");
                }
            }
        });

        // add OK and Cancel buttons
        settingsBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = settingsBuilder.create();
        dialog.show();
    }

    void setSoundAndVibrateOnClick()
    {
        if(isSoundCheck)
        {
            MediaPlayer pressSound = MediaPlayer.create(MainActivity.this, R.raw.click_electronic);
            pressSound.setVolume(30,30);
            pressSound.start();
        }
        if(isVibrateCheck)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                v.vibrate(500);
            }
        }
    }
}
