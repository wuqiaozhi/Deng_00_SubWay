package com.example.deng_00_subway;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import DealJsonDate.JsonTimeSetOff;

/**
 * Created by 夏日寒风 on 2016/10/4.
 */

public class WriteSetOffTimeAty extends Activity {
    private EditText editCityNameTime,editTimeInterval,editMinuStop,
            editSubwayNum,editHourGo,editMinuGo,editHourStop;
    private int subwayNo=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_setoff_time_activity);
        editCityNameTime= (EditText) findViewById(R.id.editCityNameTime);

        editSubwayNum=(EditText)findViewById(R.id.EditSubwayNumTime);
        editSubwayNum.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        editSubwayNum.setText(""+subwayNo);

        editTimeInterval=(EditText) findViewById(R.id.editMinuIntrerval);
        editTimeInterval.setInputType(EditorInfo.TYPE_CLASS_PHONE);

        editHourGo=(EditText) findViewById(R.id.editHourGo);
        editHourGo.setInputType(EditorInfo.TYPE_CLASS_PHONE);

        editHourStop=(EditText)findViewById(R.id.editHourStop);
        editHourStop.setInputType(EditorInfo.TYPE_CLASS_PHONE);

        editMinuGo=(EditText) findViewById(R.id.editMinuGo);
        editMinuGo.setInputType(EditorInfo.TYPE_CLASS_PHONE);

        editMinuStop=(EditText) findViewById(R.id.editMinuStop);
        editMinuStop.setInputType(EditorInfo.TYPE_CLASS_PHONE);
    }
    private int hourGo,minuGo,hourStop,minuStop,timeInterval,subwayNum;
    private String cityName;
    private JsonTimeSetOff time_subwaySetOff;

    public void click_complete_add_Date(View v) throws IOException {

        if(TextUtils.isEmpty(editCityNameTime.getText())||
                TextUtils.isEmpty(editTimeInterval.getText())||
                TextUtils.isEmpty(editMinuStop.getText())||
                TextUtils.isEmpty(editHourStop.getText())||
                TextUtils.isEmpty(editHourGo.getText())||
                TextUtils.isEmpty(editMinuGo.getText())||
                TextUtils.isEmpty(editSubwayNum.getText())){


            Toast.makeText(this,"输入完整数据",Toast.LENGTH_SHORT).show();
        }else {

            timeInterval=Integer.parseInt(editTimeInterval.getText().toString());
            cityName=editCityNameTime.getText().toString();
            minuStop=Integer.parseInt(editMinuStop.getText().toString());
            hourStop=Integer.parseInt(editHourStop.getText().toString());
            minuGo=Integer.parseInt(editMinuGo.getText().toString());
            subwayNum=Integer.parseInt(editSubwayNum.getText().toString());
            hourGo=Integer.parseInt(editHourGo.getText().toString());


            editMinuGo.setText("");
//            editCityNameTime.setText("");
            editMinuStop.setText("");
            editHourStop.setText("");
            editHourGo.setText("");
            editSubwayNum.setText(""+(++subwayNo));
            editTimeInterval.setText("");
        }
        time_subwaySetOff=new JsonTimeSetOff();
        time_subwaySetOff.ClickToWriteJson(hourGo,minuGo,timeInterval,hourStop,minuStop,cityName,subwayNum);
    }
}
