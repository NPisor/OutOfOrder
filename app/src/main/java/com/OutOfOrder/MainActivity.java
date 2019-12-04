package com.OutOfOrder;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    MediaPlayer titlemusic;                                                                         //Create MediaPlayer object for title music
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);                //Create AudioManager to adjust MediaPlayer volume levels
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, 10, 0);
        handler = new Handler();
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.blink_anim);
        titlemusic = MediaPlayer.create(this, R.raw.titlemusic);
        titlemusic.setLooping(true);                                                                //Play title music and set to loop
        titlemusic.start();
        final ImageButton startButton = findViewById(R.id.startbutton);               //Start button runs Intent to ChoicePage
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startButton.startAnimation(animation);                                              //Start button onClick animation(pulse)
                handler.postDelayed(new Runnable() {
                    public void run() {
                        CreateUser();                                                               //Stop title music and proceed to CreateUser page
                        titlemusic.stop();
                    }
                }, 200);
            }
        });
    }

    public void CreateUser(){
        Intent intent = new Intent(this, CreateUser.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        titlemusic.stop();
    }

    @Override
    protected  void onResume(){
        super.onResume();
        titlemusic.start();
    }

}
