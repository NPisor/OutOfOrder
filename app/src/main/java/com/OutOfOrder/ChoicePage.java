package com.OutOfOrder;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

public class ChoicePage extends AppCompatActivity{

    MediaPlayer menumusic;
    Handler handler;
    DatabaseHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_page);
        mydb = new DatabaseHelper(this);
        menumusic = MediaPlayer.create(ChoicePage.this, R.raw.menumusic);
        handler = new Handler();

        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.blink_anim);
        final ImageButton memorybutton = findViewById(R.id.memorybutton);             //Button with Intent to CardFlip game
        final ImageButton reflexbutton = findViewById(R.id.timeattackbutton);         //Button with Intent to Reflex Test game

        memorybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {                                                        //Stop menu music and transition to CardFlip game
                memorybutton.startAnimation(animation);
                handler.postDelayed(new Runnable() {
                    public void run() {
                        cardflip();
                        menumusic.stop();
                    }
                }, 200);
            }
        });
        reflexbutton.setOnClickListener(new View.OnClickListener() {                                //Stop music and proceed to Whack-a-Mole game
            @Override
            public void onClick(View v) {
                reflexbutton.startAnimation(animation);
                handler.postDelayed(new Runnable() {
                    public void run() {
                        reflexTest();
                        menumusic.stop();
                    }
                }, 200);
            }
        });
    }

    public void cardflip(){
        Intent intent = new Intent(this, cardFlipPage.class);                         //Function to be called to create CardFlip game instance
        startActivity(intent);
    }

    public void reflexTest(){
        Intent intent2 = new Intent(this, reflexGamePage.class);                      //Function to be called to create Reflex Test game
        startActivity(intent2);
    }

    @Override
    protected void onPause() {
        super.onPause();
        menumusic.stop();
    }

    @Override
    protected  void onResume(){
        super.onResume();
        menumusic.start();
    }
}
