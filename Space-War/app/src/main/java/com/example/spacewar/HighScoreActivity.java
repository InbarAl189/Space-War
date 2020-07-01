package com.example.spacewar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.List;

public class HighScoreActivity extends AppCompatActivity {
    VideoView m_VideoView;
    MediaPlayer m_MediaPlayer;
    int m_VideoCurrPosition;
    RecyclerView m_RecyclerView;
    LinearLayout m_HighScoreLayout;
    TextView m_HighScoreTv;
    Animation upToDown,downToUp;
    private MusicService mServ;
    HomeWatcher mHomeWatcher;
    private boolean mIsBound = false;
    private boolean mIsMusic;
    private String mCallIntent;
    private HighScoreManager mHighScoreManager;

    List<PlayerCard> top10Players;

    private ServiceConnection Scon = new ServiceConnection(){

        public void onServiceConnected(ComponentName name, IBinder
                binder) {
            mServ = ((MusicService.ServiceBinder)binder).getService();
            if(!mIsMusic && mServ != null)
            {
                mServ.pauseMusic();
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
        setContentView(R.layout.activity_highscore);

        //Animation start activity
        m_HighScoreLayout = findViewById(R.id.high_score_layout);
        m_HighScoreTv = findViewById(R.id.high_score_tv);

        upToDown = AnimationUtils.loadAnimation(this,R.anim.up_to_down);
        downToUp = AnimationUtils.loadAnimation(this,R.anim.down_to_up);
        m_HighScoreLayout.setAnimation(downToUp);
        m_HighScoreTv.setAnimation(upToDown);

        //Put video on background
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

        //getExtras
        Intent intent = getIntent();
        mIsMusic = intent.getBooleanExtra("Music",false);
        mCallIntent = intent.getStringExtra("CallIntent");

        m_RecyclerView = findViewById(R.id.recycler_view);
        m_RecyclerView.setHasFixedSize(true);
        m_RecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mHighScoreManager = HighScoreManager.getInstance(HighScoreActivity.this);
        top10Players = mHighScoreManager.getTop10PlayersList(HighScoreActivity.this);

        PlayerAdapter playerAdapter = new PlayerAdapter(top10Players);
        m_RecyclerView.setAdapter(playerAdapter);

        }

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
            if (mServ != null && mIsMusic) {
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
}
