package com.example.spacewar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.TextView;

import org.w3c.dom.Text;

public class BufferActivity extends AppCompatActivity {
    Animation textAnim;
    TextView loadingTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buffer);
        loadingTv = findViewById(R.id.loading_tv);
        textAnim = AnimationUtils.loadAnimation(this,R.anim.blink);
        loadingTv.setAnimation(textAnim);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(BufferActivity.this, MainActivity.class);
                finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out);
                startActivity(intent);

            }
        }, 4000);

    }
}
