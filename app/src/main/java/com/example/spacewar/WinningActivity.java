package com.example.spacewar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WinningActivity extends AppCompatActivity implements View.OnClickListener {

    EditText m_NameEt;
    Button m_OkBtn;
    TextView m_PlayerScore;
    TextView m_ScrollingText;

    HighScoreManager m_HighScoreManager;

    String score;
    LinearLayout m_TxtLayout;

    private boolean mIsMusic,mIsVibrate,mIsSound;
    private Vibrator v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winning);

        m_TxtLayout = findViewById(R.id.txt_layout);
        Animation layoutScaleUp = AnimationUtils.loadAnimation(this,R.anim.scale_up);
        m_TxtLayout.setAnimation(layoutScaleUp);

        m_ScrollingText = findViewById(R.id.scrolling_text);
        m_ScrollingText.setSelected(true);

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        m_HighScoreManager = HighScoreManager.getInstance(WinningActivity.this);

        //get Extras
        Intent intent = getIntent();
        score = String.valueOf(intent.getIntExtra("Score",0));
        mIsMusic = intent.getBooleanExtra("Music",false);
        mIsVibrate = intent.getBooleanExtra("Vibrate",false);
        mIsSound = intent.getBooleanExtra("Sound",false);

        playSound(R.raw.victory);

        m_NameEt = findViewById(R.id.name_edit_text);
        m_OkBtn = findViewById(R.id.ok_btn);
        m_PlayerScore = findViewById(R.id.score_text_view);

        m_OkBtn.setOnClickListener(this);
        m_PlayerScore.setText(score);
    }

    private void vibrate() {
        if(mIsVibrate)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                v.vibrate(500);
            }
        }
    }

    private void playSound(int sound) {
        if(mIsSound){
            MediaPlayer pressSound = MediaPlayer.create(WinningActivity.this, sound);
            pressSound.setVolume(30,30);
            pressSound.start();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.ok_btn: {
                checkIfNeedToInitializeScoreOnSP();
                playSound(R.raw.click_electronic);
                vibrate();
            }
        }
    }

    private void checkIfNeedToInitializeScoreOnSP() {
        if(!m_NameEt.getText().toString().isEmpty()) {
            int currScore = Integer.parseInt(m_PlayerScore.getText().toString());
            String name = m_NameEt.getText().toString();

            m_HighScoreManager.CheckAndInitializeIfNeedToAddToList(currScore,name);

            Intent intent = new Intent(WinningActivity.this, HighScoreActivity.class);
            intent.putExtra("Music", mIsMusic);
            intent.putExtra("CallIntent","Winning");
            startActivity(intent);
            this.finish();
        }
        else
        {
            Toast.makeText(WinningActivity.this, getResources().getString(R.string.enter_your_name_msg), Toast.LENGTH_LONG).show();
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

}
