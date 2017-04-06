package com.example.deng_00_subway;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;

import DealMapDate.LocationDate;
import wifiChat.activity.WifiApActivity;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView locationTV;
    private TextView MapTV;
    private TextView tvCity;
    private TextView tvGame;
    private TextView tvChat;
    private LocationDate locationDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
         initView();

        locationDate=new LocationDate(this,locationListener);
        locationDate.startLocation();

    }
  private void initView(){
      locationTV=(TextView) findViewById(R.id.location);
      locationTV.setOnClickListener(this);
      MapTV=(TextView)findViewById(R.id.mapNavigation);
      MapTV.setOnClickListener(this);
      tvCity=(TextView) findViewById(R.id.tvCity);
      tvGame=(TextView) findViewById(R.id.game);
      tvGame.setOnClickListener(this);
      tvChat=(TextView)findViewById(R.id.chat);
      tvChat.setOnClickListener(this);

  }
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation loc) {

            if (null != loc) {
                //解析定位结果
                String result = locationDate.getCityLocation(loc);
                tvCity.setText(result);
            } else {
                tvCity.setText("未知");
            }
        }
    };
    @Override
    public void onClick(View v) {
        Intent intent=new Intent();
        switch (v.getId()){
            case R.id.location:
                intent.setClass(this,RunViewActivity.class);
                startActivity(intent);
                break;
            case R.id.mapNavigation:


                break;
            case R.id.game:
                intent.setClass(this,GameViewActivity.class);
                startActivity(intent);
                break;
            case R.id.chat:
                intent.setClass(this, WifiApActivity.class);
                startActivity(intent);
                default:
                    break;
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationDate.stopLocation();
    }
}
