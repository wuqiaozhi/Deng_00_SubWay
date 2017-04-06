package com.example.deng_00_subway;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class GameViewActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv2048;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_view);
        tv2048=(TextView)findViewById(R.id.game2048);
        tv2048.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent starAtyIntent=new Intent();
        switch (v.getId()){
            case R.id.game2048 :
                starAtyIntent.setClass(this,game.game2048.MainActivity.class);
                startActivity(starAtyIntent);
        }
    }
}
