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
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import com.wajahatkarim3.easyflipview.EasyFlipView;
import java.util.Collections;
import java.util.ArrayList;

public class cardFlipPage extends AppCompatActivity {

    final ImageView[] cards = new ImageView[16];                                                    //Array of Card Objects
    final int[] cardFaces = new int[8];                                                             //Array of Card Drawables
    ArrayList<Integer> cardValues = new ArrayList();                                                //Array List of Values corresponding to Drawables (to randomize)
    final EasyFlipView[] card = new EasyFlipView[16];                                               //Card objects of EasyFlipView classifier
    boolean clicked = false;                                                                        //Clicked variable used to separate first and second card choices
    Integer[] cardChoice = new Integer[2];
    Integer[] cardPicked = new Integer[2];
    boolean firstMove = true;
    Handler handler = new Handler();                                                                //Handler used for one second delay to return unmatched card to previous state
    int mistakes = 0;                                                                               //Incorrect matches kept to pass to Database Object
    int correctPairs = 0;                                                                           //Correct Pairs used to determine if all cards on the board have been matched
    MediaPlayer miss, success, bgmusic, cardflip;                                                   //MediaPlayer objects used to create Incorrect Match, Correct Match, Card Flip SFX and BG Music
    CountDownTimer cdtimer;
    String user;
    Chronometer timer;                                                                              //Chronometer used as timer to get completion time
    DatabaseHelper myDB;
    int completion_time = 0;                                                                        //Value of seconds passed since game started for entry into database


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_flip_page);
        user = PreferenceManager.getDefaultSharedPreferences
                (this).getString("username", "defaultStringIfNothingFound");         //Get SharedPreferences value "username"
        myDB = new DatabaseHelper(this);
        timer = findViewById(R.id.timer);                                                           //Timer used to add 1 second delay after flipping incorrect matches to allow cards to flip back
        timer.start();
        timer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                completion_time ++;
            }
        });
        miss = MediaPlayer.create(cardFlipPage.this, R.raw.whistle);
        success = MediaPlayer.create(cardFlipPage.this, R.raw.success);
        bgmusic = MediaPlayer.create(cardFlipPage.this, R.raw.cardflipbg);
        cardflip = MediaPlayer.create(cardFlipPage.this, R.raw.cardflip);
        bgmusic.setLooping(true);                                                                   //Start BGMusic on game instance creation and set to loop after finish
        bgmusic.start();


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
        cards[10] = findViewById(R.id.card11Front);
        cards[11] = findViewById(R.id.card12Front);
        cards[12] = findViewById(R.id.card13Front);
        cards[13] = findViewById(R.id.card14Front);
        cards[14] = findViewById(R.id.card15Front);
        cards[15] = findViewById(R.id.card16Front);
        setListeners();                                                                             //Call function setListeners() to add default onClickListeners with attached code to all cards[] objects

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
        card[10] = findViewById(R.id.card11);
        card[11] = findViewById(R.id.card12);
        card[12] = findViewById(R.id.card13);
        card[13] = findViewById(R.id.card14);
        card[14] = findViewById(R.id.card15);
        card[15] = findViewById(R.id.card16);

    //-------------------------Assign Card Face Drawables to CardFaces Array for use in for() loop-----------------------------------------
        cardFaces[0] = R.drawable.heartcard;
        cardFaces[1] = R.drawable.starcard;
        cardFaces[2] = R.drawable.xcard;
        cardFaces[3] = R.drawable.arrowcard;
        cardFaces[4] = R.drawable.diamondcard;
        cardFaces[5] = R.drawable.lightningcard;
        cardFaces[6] = R.drawable.mooncard;
        cardFaces[7] = R.drawable.trianglecard;

    //--------------------------Assign integer values to CardFaces in RandPlace() function--------------------------------------------------
        cardValues.add(0);
        cardValues.add(0);
        cardValues.add(1);
        cardValues.add(1);
        cardValues.add(2);
        cardValues.add(2);
        cardValues.add(3);
        cardValues.add(3);
        cardValues.add(4);
        cardValues.add(4);
        cardValues.add(5);
        cardValues.add(5);
        cardValues.add(6);
        cardValues.add(6);
        cardValues.add(7);
        cardValues.add(7);

    //Create board placement through RNG each time the page is created and closed again----------------------------------------------------
        randPlace();
    }


    public void randPlace() {
        for (int x = 0; x <= 15; x++) {                                                             //Iterate 16 times through the for loop, accounting for each of the 16 cards[] objects
            Collections.shuffle(cardValues);                                                        //Shuffle all items in cardValues array to place in pairs
            int y = cardValues.get(0);                                                              //Get the first element in the cardValues array each iteration of the for() loop
            cards[x].setImageResource(cardFaces[y]);                                                //Set the card images to the cardFaces objects of the y value obtained in previous line
            cards[x].setTag(y);                                                                     //Create a tag for each cards[] object for comparison outside the function randPlace()
            card[x].setTag(x);
            cardValues.remove(0);                                                             //Remove the first element in the cardValues array after each iteration to eliminate more than two copies of each card
            cards[x].setPressed(false);
        }
    }

    @Override

    public void onBackPressed(){                                                                    //Function used to add Android Back Button functionality
        bgmusic.stop();
        Intent intent = new Intent(this, ChoicePage.class);                           //Start new intent to return to Choice Page, stopping BG Music
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void setListeners(){
        for(int x = 0; x <= 15; x++){                                                               //Set onClickListeners to each cards[] object during each iteration
            final int finalX = x;
            cards[x].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firstMove = false;                                                              //Let the application know that by default the first move has not been made at app start
                    card[finalX].flipTheView();                                                     //Flip the card as a result of a click
                    cardflip.start();
                    card[finalX].setFlipEnabled(false);
                    cards[finalX].setClickable(false);
                    checkForMatches(finalX);                                                        //Run function checkForMatches() on any given two cards chosen in succession
                }
            });
        }
    }

    public void checkForMatches(final int x){                                                       //Take in value "x", which is the current card being clicked
        final Integer holdCard = (Integer)cards[x].getTag();                                        //Create a "placeholder" that takes the face value of the clicked card
        final Integer cardPos = (Integer)card[x].getTag();                                          //Create a "placeholder" that takes in the location of the card on the board
        TextView scoreBox = findViewById(R.id.scoreBox);
        Chronometer timer = findViewById(R.id.timer);

        if(clicked == false){                                                                       //If a card has not been clicked, set the values of holdCard and cardPos to the current card to compare with
            cardChoice[0] = holdCard;
            cardPicked[0] = cardPos;
            clicked = true;                                                                         //Set variable "clicked" to true to initialize second card choice state
        }
        else if(clicked == true){
            cardChoice[1] = holdCard;                                                               //If clicked is true, take in values of holdCard and cardPos as second card
            cardPicked[1] = cardPos;
            for(int count = 0; count <= 15; count++){                                               //Make all cards unflippable and unclickable during match checking
                card[count].setFlipEnabled(false);
                cards[count].setClickable(false);
            }
            if(cardChoice[0].equals(cardChoice[1])){                                                //After second choice is made, check for matching pairs
                int card1 = cardPicked[0];
                int card2 = cardPicked[1];
                correctPairs +=1;                                                                   //Add value to correct pairs to determine when all cards have been matched
                success.start();                                                                    //Play success jingle on second correct match
                if(correctPairs == 8){
                    timer.stop();                                                                   //Stop timer after final match has been made
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            endGame();
                        }
                    }, 1000);
                }
                for(int count = 0; count <= 15; count++){
                    if((count == card1) || (count == card2)){                                       //Make all cards except for matching pair clickable and flippable again
                        continue;
                    }
                    card[count].setFlipEnabled(true);
                    cards[count].setClickable(true);
                }
            }
            if(!cardChoice[0].equals(cardChoice[1])){                                               //If cards do not match, wait 1 second and return cards to unflipped state
                final int card1 = cardPicked[0];
                final int card2 = cardPicked[1];
                scoreBox.setText("Mistakes: " + (mistakes+=1));                                     //Add value to and display number of mistakes for incorrect card matches
                miss.start();                                                                       //Play miss sound effect

                handler.postDelayed(new Runnable() {
                    public void run() {
                        for(int count = 0; count <= 15; count++){                                   //Set all cards flippable and clickable after wrong match
                            card[count].setFlipEnabled(true);
                            cards[count].setClickable(true);
                        }
                        card[card1].flipTheView();                                                  //Return cards to unflipped state upon incorrect match
                        card[card2].flipTheView();
                        cardflip.start();
                    }
                }, 1000);
            }
            clicked = false;                                                                        //Set variable "clicked" to false to return the next choice to first choice position
        }
    }

    public void endGame(){
        cdtimer = new CountDownTimer(8000, 500) {
            int x = 0;
            @Override
            public void onTick(long l) {                                                            //Start fanfare card flipping upon completion
                card[x].setFlipEnabled(true);
                card[x].flipTheView();
                cardflip.start();
                card[x].setFlipEnabled(false);
                x++;
            }

            @Override
            public void onFinish() {
                bgmusic.stop();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        returnToChoice();                                                           //Return to ChoicePage upon game completion
                    }
                }, 1000);
            }
        }.start();
        myDB.insertData(user, 0, mistakes, completion_time);                            //Add data to database
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

    public void returnToChoice(){
        Intent intent = new Intent(this, ChoicePage.class);                           //Function to be called to return to Choice Page
        startActivity(intent);
    }
}
