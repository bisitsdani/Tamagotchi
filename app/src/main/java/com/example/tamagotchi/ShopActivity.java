package com.example.tamagotchi;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ShopActivity extends AppCompatActivity {

    private int money;
    private SharedPreferences prefs;
    private Button hungerButton;
    private Button gameButton;
    private TextView moneyShop;

    private int hungerLevel;
    private int gameLevel;
    private long hungerPrice;
    private long gamePrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        loadAll();
    }
    public void hungerBuy(View v){
        if(money >= hungerPrice){
            prefs = getApplicationContext().getSharedPreferences("Tamagotchi", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            hungerPrice = prefs.getLong("hungerPrice", 500);
            editor.putInt("MONEY", (int)(money-hungerPrice));

            hungerPrice += hungerPrice*0.65;
            editor.putLong("hungerPrice", hungerPrice);

            hungerLevel++;
            editor.putInt("hungerLevel", hungerLevel);
            editor.apply();
            sendMsg("Sikeres vásárlás!");
            loadAll();
        }
        else{
            sendMsg("Nincs elég pénzed!");
        }
    }
    public void gameBuy(View v){
        if(money >= gamePrice){
            prefs = getApplicationContext().getSharedPreferences("Tamagotchi", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            gamePrice = prefs.getLong("gamePrice", 500);
            editor.putInt("MONEY", (int)(money-gamePrice));

            gamePrice += gamePrice*0.65;
            editor.putLong("gamePrice", gamePrice);

            gameLevel++;
            editor.putInt("gameLevel", gameLevel);
            editor.apply();
            sendMsg("Sikeres vásárlás!");
            loadAll();
        }
        else{
            sendMsg("Nincs elég pénzed!");
        }
    }
    public void sendMsg(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
    public void loadAll(){
        prefs = getApplicationContext().getSharedPreferences("Tamagotchi", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        hungerLevel = prefs.getInt("hungerLevel", 1);
        gameLevel = prefs.getInt("gameLevel", 1);
        money = prefs.getInt("MONEY", 50);

        if(hungerLevel == 1 && gameLevel == 1){
            hungerPrice = 100;
            gamePrice = 100;
            editor.putInt("hungerLevel", 1);
            editor.putInt("gameLevel", 1);
            editor.putLong("hungerPrice", hungerPrice);
            editor.putLong("gamePrice", gamePrice);
            editor.apply();
        }
        else{
            hungerPrice = prefs.getLong("hungerPrice", 550);
            gamePrice = prefs.getLong("gamePrice", 550);
        }
        hungerButton = findViewById(R.id.hungerShop);
        gameButton = findViewById(R.id.gameShop);
        moneyShop = findViewById(R.id.moneyShop);
        moneyShop.setText("" + prefs.getInt("MONEY", 50));
        hungerButton.setText(prefs.getLong("hungerPrice", 510) + " $");
        gameButton.setText(gamePrice + " $");
    }

}
