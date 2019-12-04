package com.OutOfOrder;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;

public class CreateUser extends AppCompatActivity {

    ImageButton nextButton;
    Handler handler;
    MediaPlayer menumusic;
    EditText name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        nextButton = findViewById(R.id.nextbutton);
        name = findViewById(R.id.NameInput);                                              //EditText object to get info entered by user
        menumusic = MediaPlayer.create(CreateUser.this, R.raw.menumusic);
        menumusic.setLooping(true);
        menumusic.start();
        handler = new Handler();
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.blink_anim);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextButton.startAnimation(animation);
                handler.postDelayed(new Runnable() {
                    public void run() {
                        choicePage();
                        menumusic.stop();
                    }
                }, 200);
            }
        });
    }

    public void choicePage(){
        Intent intent = new Intent(this, ChoicePage.class);
        PreferenceManager.getDefaultSharedPreferences
                (this).edit().putString("username", name.getText().toString()).apply();  //Shared preferences to take in user input for entry into database
        startActivity(intent);
    }
}
