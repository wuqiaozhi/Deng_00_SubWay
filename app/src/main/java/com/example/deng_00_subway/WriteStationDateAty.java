package com.example.deng_00_subway;

import java.io.IOException;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import DealJsonDate.JsonTimeSetOff;
import DealJsonDate.WriteStationDate;

public class WriteStationDateAty extends Activity  {

	private EditText editStopName,editX,editY,editNo,editCity,editSubwayNum,editHour,editMinu;
	private JsonTimeSetOff timeSetOff;
	private WriteStationDate writeStationDate;
	private String cityName,stopName;
	private float x,y;
	private int orderNo,subwayNum,hour,minu;
	private ListView listView;
	private ArrayAdapter<WriteStationDate> adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.write_date_activity);
		 timeSetOff =new JsonTimeSetOff();
		 writeStationDate =new WriteStationDate();

		editCity= (EditText) findViewById(R.id.EditCity);
		editNo=(EditText)findViewById(R.id.EditNo);
		editNo.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		editNo.setText("0");
		editStopName= (EditText) findViewById(R.id.EditStopName);
		editX=(EditText) findViewById(R.id.EditX);
		editX.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		editY=(EditText) findViewById(R.id.EditY);
		editY.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		editSubwayNum=(EditText) findViewById(R.id.EditSubwayNum);
		editSubwayNum.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		editHour=(EditText)findViewById(R.id.editHour);
		editHour.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		editMinu=(EditText) findViewById(R.id.editMinu);
		editMinu.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		listView=(ListView) findViewById(R.id.listViewStationdate);
		adapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
		listView.setAdapter(adapter);
	}
	public void click_add_XYDate(View v){

		if(TextUtils.isEmpty(editStopName.getText())||
				TextUtils.isEmpty(editCity.getText())||
				TextUtils.isEmpty(editX.getText())||
				TextUtils.isEmpty(editY.getText())||
				TextUtils.isEmpty(editHour.getText())||
				TextUtils.isEmpty(editMinu.getText())||
				TextUtils.isEmpty(editNo.getText())||
				TextUtils.isEmpty(editSubwayNum.getText())){

   				Toast.makeText(this,"输入完整数据",Toast.LENGTH_SHORT).show();
		}else {

			cityName=editCity.getText().toString();
			stopName=editStopName.getText().toString();
			x=Float.parseFloat(editX.getText().toString());
			y=Float.parseFloat(editY.getText().toString());
			orderNo=Integer.parseInt(editNo.getText().toString());
			subwayNum=Integer.parseInt(editSubwayNum.getText().toString());
			hour=Integer.parseInt(editHour.getText().toString());
			minu=Integer.parseInt(editMinu.getText().toString());

			writeStationDate.addDate(new WriteStationDate(stopName,x,y,orderNo,hour,minu));

			adapter.add(new WriteStationDate(stopName,x,y,orderNo,hour,minu));
			editNo.setText(""+(++orderNo));
			editStopName.setText("");
			editX.setText("");
			editY.setText("");
			editMinu.setText("");
		}
	}

	public  void addStationDateCompelete(View v) throws IOException {
		new AlertDialog.Builder(this).setTitle("注意").setMessage("是否添加完成").setPositiveButton("确定",
		new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {

				try {
					writeStationDate.ClickToWriteStationDateJson();
				} catch (IOException e) {
					e.printStackTrace();
				}
				adapter.clear();
				writeStationDate.clearDate();
				editHour.setText("");
				editCity.setText("");
				editNo.setText("");
				editSubwayNum.setText("");
			}
		}).show();

	}
//	public void onClick_writeJson(View v) throws IOException {
//			timeSetOff.ClickToWriteJson();
//	}
//	public void onClick_readJson(View v) throws IOException {
//		timeSetOff.ClickToReadJson();
//	}






}
