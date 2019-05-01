package com.example.tamagotchi;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity {
    private static final String MONEY = "MONEY";
    private static final String GAME = "GAME";
    private static final String HUNGER = "HUNGER";
    public static final String BROADCAST_ACTION = "update";

    private TextView money;
    private ProgressBar hungerProg;
    private ProgressBar gameProg;
    private ImageView coin;
    private boolean service = false;
    MyBroadCastReceiver myBroadCastReceiver;

    private SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getApplicationContext().getSharedPreferences("Tamagotchi", MODE_PRIVATE);
        setContentView(R.layout.activity_main);
        loadAll();
        saveAll();
        startTamagotchiService();
        registerMyReceiver();
    }
    public void switchToShop(View v){
        saveAll();
        Intent intent = new Intent(this, ShopActivity.class);
        startActivity(intent);
    }
    public void startTamagotchiService() {
        if(!service) {
            try
            {
                myBroadCastReceiver = new MyBroadCastReceiver();
                Intent intent = new Intent(this, TamagotchiService.class);
                startService(intent);
                service = true;
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
    public void game(View v) {
        gameProg.setProgress(gameProg.getProgress() + prefs.getInt("gameLevel", 1));
        saveAll();
    }
    public void feed(View v) {
        hungerProg.setProgress(hungerProg.getProgress() + prefs.getInt("hungerLevel", 1));
        changeMoney(-10);
    }
    public void moneyUp(View v) {
        changeMoney(10);

        ObjectAnimator animY = ObjectAnimator.ofFloat(coin, "translationY", -50f, 0f);
        animY.setDuration(500);//1sec
        animY.setInterpolator(new BounceInterpolator());
        animY.start();
    }
    public void changeMoney(int value){
        int m = parseInt(money.getText().toString());
        m += value;
        if(m >= 0){
            money.setText("" + m);
            sendMsg(money.getText().toString());
            saveAll();
        }
        else{
            sendMsg("Nincs elég pénzed!");
        }
        loadAll();
    }
    @Override
    protected void onResume() {
        loadAll();
        startTamagotchiService();
        super.onResume();
    }
    public void sendMsg(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void loadAll(){
        money = findViewById(R.id.money);
        hungerProg = findViewById(R.id.feedProg);
        gameProg = findViewById(R.id.gameProg);
        coin = findViewById(R.id.coin2);


        money.setText("" + prefs.getInt(MONEY, 50));
        hungerProg.setProgress(prefs.getInt(HUNGER, 50));
        gameProg.setProgress(prefs.getInt(GAME, 50));
    }
    public void saveAll() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(GAME, gameProg.getProgress());
        editor.putInt(HUNGER, hungerProg.getProgress());
        editor.putInt(MONEY, parseInt(money.getText().toString()));
        editor.commit();
    }

    public void deleteAll(View v){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(GAME, 50);
        editor.putInt(HUNGER, 50);
        editor.putInt(MONEY, 100);
        editor.putInt("hungerLevel", 1);
        editor.putInt("gameLevel", 1);
        editor.putInt("hungerPrice", 500);
        editor.putInt("gamePrice", 500);
        editor.commit();
        loadAll();
        sendMsg("törölve");
    }

    @Override
    protected void onPause() {
        saveAll();
        startTamagotchiService();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        saveAll();
        startTamagotchiService();
        super.onDestroy();
    }
    private void registerMyReceiver() {

        try
        {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BROADCAST_ACTION);
            registerReceiver(myBroadCastReceiver, intentFilter);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }
    class MyBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try
            {
                loadAll();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
}
