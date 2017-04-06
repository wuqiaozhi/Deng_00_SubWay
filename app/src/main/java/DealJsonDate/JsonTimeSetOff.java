package DealJsonDate;

import android.os.Environment;
import android.util.JsonReader;
import android.util.JsonWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 夏日寒风 on 2016/9/16.
 */
public class JsonTimeSetOff {

    private String cityName;
    private int subwayNum;
    private int departHour;
    private int departMinu;

    public String getCityName() {
        return cityName;
    }

    public int getSubwayNum() {
        return subwayNum;
    }

    public int getDepartHour() {
        return departHour;
    }

    public int getDepartMinu() {
        return departMinu;
    }


    @Override
    public String toString() {
        return this.departHour+"-"+this.departMinu;
    }

    public JsonTimeSetOff(){
         //空的构造方法

    }

    /**
     * 构造方法
     * @param cityName 城市名
     * @param subwayNum 地铁线号
     * @param hour 出发小时
     * @param minu 出发分钟
     */
    public JsonTimeSetOff(String cityName, int subwayNum,
                          int hour, int minu) {
        this.cityName = cityName;
        this.subwayNum = subwayNum;
        this.departHour = hour;
        this.departMinu = minu;
    }

    public void ClickToWriteJson(int initHour, int initMinu
            , int timeInterval, int stopHour, int stopMinu,String cityName,int subwayNo) throws
            IOException{
        File sd= Environment.getExternalStorageDirectory();
        String filePath=sd.getPath()+"/HappySubway/Date/"+cityName+"/出发时间";
        File file=new File(filePath);
        if(!file.exists())   file.mkdirs();

        List<JsonTimeSetOff> listOfSubwayTime;
        listOfSubwayTime=calculateTime(initHour, initMinu, timeInterval, stopHour, stopMinu,cityName);

        String jsonFile=file.getAbsolutePath()+"/route_"+subwayNo+"_time.json";
        FileOutputStream fos =new FileOutputStream(jsonFile);
        writeJsonStream(fos, listOfSubwayTime);

    }
    public List<List<JsonTimeSetOff>> ReadJsonDateOfSetOffTimes () throws IOException {

        File sd= Environment.getExternalStorageDirectory();
        String jsonFile=sd.getPath()+"/HappySubway/Date/模拟/出发时间";

        File f3 = new File(jsonFile);
        File[] fs = f3.listFiles();
        List<List<JsonTimeSetOff>> listList=new ArrayList<>();
        for(File f:fs) {
            if (f.getAbsolutePath().endsWith(".json")) {
                FileInputStream fis=new FileInputStream(f.getAbsolutePath());
                List<JsonTimeSetOff> times=readJsonStream(fis);
                listList.add(times);
            }
        }
        return listList;
    }

    /**
     * 此方法用来计算发车时间点
     * @param initHour 首发时间--时
     * @param initMinu 首发分钟--分
     * @param timeInterval 发车时间间隔--分
     * @param stopHour 末班车时间--时
     * @param stopMinu 末班车时间--分
     * @return
     */
    private List<JsonTimeSetOff> calculateTime(int initHour, int initMinu
            , int timeInterval, int stopHour, int stopMinu, String cityName){
        List<JsonTimeSetOff> listOfSubwayTime=new ArrayList<>();
        int startTime2Minu=initHour*60+initMinu;
        int stopTime2Minu=stopHour*60+stopMinu;
        int newMinu=initMinu,newHour=initHour;

        for (int i = startTime2Minu; i < stopTime2Minu; i+=timeInterval) {
            if (newMinu>=60) { newMinu-=60;  newHour+=1; }
            JsonTimeSetOff time=new JsonTimeSetOff(cityName, subwayNum, newHour, newMinu);
            newMinu+=timeInterval;
            listOfSubwayTime.add(time);
        }

        return listOfSubwayTime;
    }

    private JsonTimeSetOff readTime(JsonReader reader) throws IOException {
        String cityName=null;
        int departHour=0,departMinu=0,subwayNum=0;
        reader.beginObject();
        while(reader.hasNext()){
            String field=reader.nextName();
            if(field.equals("cityName"))
                cityName=reader.nextString();
            else if (field.equals("subwayNum")) {
                subwayNum=reader.nextInt();
            }else if (field.equals("departHour")) {
                departHour=reader.nextInt();
            }else if (field.equals("departMinu")) {
                departMinu=reader.nextInt();
            }
            else{
                reader.skipValue();
            }
        }
        reader.endObject();
        return new JsonTimeSetOff(cityName, subwayNum, departHour, departMinu);
    }
    private List<JsonTimeSetOff> readTimeArray(JsonReader reader)
            throws IOException {
        List<JsonTimeSetOff> times=new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            times.add(readTime(reader));
        }
        reader.endArray();
        return times;

    }
    private List<JsonTimeSetOff> readJsonStream(InputStream in)
            throws IOException {

        JsonReader reader=new JsonReader(new InputStreamReader(in,"UTF-8"));
        try{
            return readTimeArray(reader);
        }finally{

            reader.close();
        }
    }

    private void writeJsonStream(OutputStream out, List<JsonTimeSetOff> departTimes)
            throws IOException {
        JsonWriter writer=new JsonWriter(new OutputStreamWriter(out,"UTF-8"));
        writer.setIndent(" ");
        writeArraySubwayDepartTime(writer, departTimes);
        writer.close();

    }
    private void writeSubwayDepartTime(JsonWriter writer, JsonTimeSetOff departTime)
            throws IOException{
        //TODO
        writer.beginObject();
        writer.name("cityName").value(departTime.cityName);
        writer.name("subwayNum").value(departTime.subwayNum);
        writer.name("departHour").value(departTime.departHour);
        writer.name("departMinu").value(departTime.departMinu);
        writer.endObject();
    }
    private void writeArraySubwayDepartTime(JsonWriter writer,List<JsonTimeSetOff> departTimes)
            throws IOException{
        writer.beginArray();
        for (JsonTimeSetOff subwayDepartTime : departTimes) {
            writeSubwayDepartTime(writer, subwayDepartTime);
        }
        writer.endArray();
    }
}
