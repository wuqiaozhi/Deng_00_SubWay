package com.example.deng_00_subway;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;

public class RunViewActivity extends Activity {
	private SurfaceView iv;
	private RelativeLayout root;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		root=(RelativeLayout) findViewById(R.id.relative);
		iv=(SurfaceView) findViewById(R.id.subwayView);

	}
   public void onClick_writeDate(View v){

	   Intent intent=new Intent();
	   intent.setClass(this, WriteStationDateAty.class);
	   startActivity(intent);
	   
   }

	public void onClick_writeSetOffTime(View v){
		Intent intent=new Intent();
		intent.setClass(this, WriteSetOffTimeAty.class);
		startActivity(intent);

			}

}
