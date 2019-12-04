package com.OutOfOrder;

/* Credit to wajahatkarim3 on GitHub for EasyFlipView library */

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.wajahatkarim3.easyflipview.EasyFlipView;
import java.util.Random;

public class reflexGamePage extends AppCompatActivity {

    final ImageView[] cards = new ImageView[10];                                                    //Array of Card Objects
    final EasyFlipView[] card = new EasyFlipView[10];                                               //Card objects of EasyFlipView classifier
    Random rand = new Random();
    Handler handler = new Handler();                                                                ///Handler used to create 2 second delay between mole movement
    boolean clicked = false;                                                                        //Clicked variable used to determine whether Start Button has been clicked and should stop or reset timer
    CountDownTimer cdtimer, cdtimer2;                                                               //Countdown timer used to run moleRandomMovement() and endGame()
    final int newmole = R.drawable.holefilled;                                                      //Drawable object to return mole card picture to default before new flip
    MediaPlayer pop, molewhack, bgmusic, cardflip;                                                  //MediaPlayer Object for Mole Flip, Mole Whack, Card Flip SFX and BG Music
    int goodHits;                                                                                   //Goodhits variable used to determine how many successful Mole Hits
    DatabaseHelper myDb;                                                                            //Database class called to enter info into database myDB
    String user;                                                                                    //String object to take in SharedPreference "username" to add to database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reflex_game_page);
        final ImageButton startbutton = findViewById(R.id.startbutton);                //Start Button used to start Countdown Timer object
        final int start = R.drawable.reflexstart;
        final int stop = R.drawable.reflexstop;
        myDb = new DatabaseHelper(this);
        user = PreferenceManager.getDefaultSharedPreferences
                (this).getString("username", "defaultStringIfNothingFound");         //Get SharedPreference "username" and set to String user for entry into database
        pop = MediaPlayer.create(reflexGamePage.this, R.raw.popsound);
        molewhack = MediaPlayer.create(reflexGamePage.this, R.raw.molewhack);
        bgmusic = MediaPlayer.create(reflexGamePage.this, R.raw.popweasel);
        cardflip = MediaPlayer.create(reflexGamePage.this,R.raw.cardflip);
        bgmusic.setVolume(2,2);
        bgmusic.setLooping(true);                                                                   //Start BGMusic upon instance creation and set to looping
        bgmusic.start();
        goodHits = 0;                                                                               //Set Mole Hits to 0 upon each instance creation

        //------------------------ Assign Card Objects and Set Up Click Listeners using setListeners() Function-----------------------------
        cards[0] = findViewById(R.id.card1Front);
        cards[1] = findViewById(R.id.card2Front);
        cards[2] = findViewById(R.id.card3Front);
        cards[3] = findViewById(R.id.card4Front);
        cards[4] = findViewById(R.id.card5Front);
        cards[5] = findViewById(R.id.card6Front);
        cards[6] = findViewById(R.id.card7Front);
        cards[7] = findViewById(R.id.card8Front);
        cards[8] = findViewById(R.id.card9Front);
        cards[9] = findViewById(R.id.card10Front);
        setListeners();

        //-------------------------Assign EasyFlipView Objects based on Card Objects----------------------------------------------------------
        card[0] = findViewById(R.id.card1);
        card[1] = findViewById(R.id.card2);
        card[2] = findViewById(R.id.card3);
        card[3] = findViewById(R.id.card4);
        card[4] = findViewById(R.id.card5);
        card[5] = findViewById(R.id.card6);
        card[6] = findViewById(R.id.card7);
        card[7] = findViewById(R.id.card8);
        card[8] = findViewById(R.id.card9);
        card[9] = findViewById(R.id.card10);

        setFlipandClick();                                                                          //Iterate through card[] and cards[] objects to make them unflippable and unclickable

        startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clicked == false){
                    startbutton.setImageResource(stop);                                             //Cycle through Start and Stop button on each click
                    cdtimer = new CountDownTimer(30000, 2000) {
                        public void onFinish() {
                            endGame();
                            goodHits = 0;                                                           //Reset number of hits to 0 for new game after completion
                            startbutton.setImageResource(start);
                            myDb.insertData(user, goodHits, 0, 0);                //Insert data after game is complete
                        }

                        public void onTick(long millisUntilFinished) {                              //Move moles with moleRandomMovement() every 2 seconds
                            moleRandomMovement();
                            for(int x = 0; x <= 9; x++){
                                cards[x].setImageResource(newmole);                                 //Return moles to default image state upon flip
                                card[x].setClickable(false);                                        //Make all cards other than active flipped mole(s) unclickable
                            }
                        }
                    }.start();
                    clicked = true;                                                                 //Set start button to clicked
                }
                else if(clicked == true){
                    startbutton.setImageResource(start);                                            //Return button to Start from Stop and set clicked to false
                    clicked = false;
                    cdtimer.cancel();                                                               //Cancel timer on Stop button click
                }
            }
        });
    }

    @Override

    public void onBackPressed(){                                                                    //On Android Back Button press, stop current running timer, BGMusic, and start Choice Page intent
        if(clicked == true){
            cdtimer.cancel();
        }
        bgmusic.stop();
        Intent intent = new Intent(this, ChoicePage.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);                    //On Android Back Button press, use slide animation to return to ChoicePage
    }

    public void setFlipandClick(){                                                                  //Function used to set all card[] and cards[] objects to unclickable and unflippable
        for(int x = 0; x <= 9; x++){
            card[x].setClickable(false);
            cards[x].setClickable(false);
            card[x].setFlipEnabled(false);
        }
    }

    public void setListeners(){
        for(int x = 0; x <= 9; x++){                                                                //Set onClickListeners to each cards[] object during each iteration
            final int finalX = x;
            final int molehit = R.drawable.molehit;
            cards[x].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cards[finalX].setImageResource(molehit);                                        //Change image to WhackedMole after successful hit
                    molewhack.start();                                                              //Play hit SFX upon hit
                    goodHits += 1;                                                                  //Add value to goodHits upon successful hit
                }
            });
        }
    }

    public void moleRandomMovement(){
        final int random = rand.nextInt(8);                                                  //Choose random card to flip and display mole to hit
        int random2 = rand.nextInt(8);
        if(random2 == random){                                                                      //Check that second mole is not same randomly chosen number to prevent one card flipping back
            random2 = rand.nextInt(8);
        }
        if(goodHits >= 5){                                                                          //Flip two cards at a time after 5 successful hits
            card[random2].setFlipEnabled(true);
            card[random2].flipTheView();
            cards[random2].setClickable(true);
        }
        card[random].setFlipEnabled(true);                                                          //Make only active mole card clickable
        card[random].flipTheView();
        cardflip.start();
        cards[random].setClickable(true);
        pop.start();                                                                                //Play popup sound effect after card flip
        final int finalNewMole = random2;
        handler.postDelayed(new Runnable() {
            public void run() {
                if(goodHits >= 5){                                                                  //Ensure second card returns to unflipped state
                    card[finalNewMole].flipTheView();
                }
                card[random].flipTheView();                                                         //Return card to unflipped state after 1 second delay
                cardflip.start();
                setFlipandClick();
            }
        }, 1000);
    }

    public void endGame(){
        cdtimer2 = new CountDownTimer(4000,400) {                      //Start fanfare upon game completion by flipping all cards in succession, then restarting
            int x = 0;
            @Override
            public void onTick(long millisUntilFinished) {
                card[x].setFlipEnabled(true);
                card[x].flipTheView();
                cardflip.start();
                card[x].setFlipEnabled(false);
                x++;
            }

            @Override
            public void onFinish() {                                                                //Return all flipped cards to unflipped state(game start) and enter score into database
                handler.postDelayed(new Runnable() {
                    public void run() {
                        for(int x = 0; x<=9; x++){
                            card[x].setFlipEnabled(true);
                            card[x].flipTheView();
                            cardflip.start();
                            card[x].setFlipEnabled(false);
                        }
                        myDb.insertData(user, goodHits, 0,0);
                    }
                }, 1000);
            }
        }.start();
        clicked = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        bgmusic.stop();
    }

    @Override
    protected  void onResume(){
        super.onResume();
        bgmusic.start();
    }
}
