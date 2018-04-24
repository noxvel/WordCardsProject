package com.vseznaikastas.wordcards;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


/**
 * Created by admin on 15.06.13.
 */
public class MainActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    public void callFlipActivity(View v){
        Intent intent = new Intent(this, FlipWordCards.class);
        startActivity(intent);

    }
    public void callAddActivity(View v){
        Intent intent = new Intent(this, AddWords.class);
        startActivity(intent);

    }

}
