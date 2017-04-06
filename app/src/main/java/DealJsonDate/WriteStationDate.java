package DealJsonDate;


import android.os.Environment;
import android.util.JsonWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class WriteStationDate {

	private String stationName;
	private	float Xcoord;
	private	float Ycoord;
	private	int orderNum;

    public int getTimeMinu() {
        return timeMinu;
    }

    public int getTimeHour() {
        return timeHour;
    }

    private	int timeMinu;
    private int timeHour;

	/**
	 *
	 * 构造方法
     * @param stationName 站名
     * @param xcoord    x坐标(自定义)
     * @param ycoord    y坐标（自定义）
     * @param orderNum    站点顺序
     * @param timeHour   到达时间（小时）
     * @param timeMinu  到达时间（分钟）
     */
	public WriteStationDate(String stationName, float xcoord,float ycoord, int orderNum, int timeHour, int timeMinu) {
		this.stationName = stationName;
		this.Xcoord = xcoord;
		this.Ycoord = ycoord;
		this.orderNum = orderNum;
		this.timeMinu=timeMinu;
        this.timeHour=timeHour;
	}
    public WriteStationDate(){ }//空构造方法

    /**
     * 返回当天所在秒数
     * @return
     */
	public int getArriveTime(){
		return timeHour*60*60+timeMinu*60;
	}
	public int getOrderNum() {
		return orderNum;
	}

	public String getStationName() {
		return stationName;
	}
	public float getXcoord() {
		return Xcoord;
	}
	public float getYcoord() {
		return Ycoord;
	}
    private String getCityName() {
        return cityName;
    }
    private int getSubwayNum() {
        return subwayNum;
    }

    private void writeStationDate(JsonWriter writer, WriteStationDate writeStationDate)
			throws IOException {
		//TODO
		writer.beginObject();
		writer.name("stationName").value(writeStationDate.stationName);
		writer.name("Xcoord").value(writeStationDate.Xcoord);
		writer.name("Ycoord").value(writeStationDate.Ycoord);
		writer.name("orderNum").value(writeStationDate.orderNum);
		writer.name("timeHour").value(writeStationDate.timeHour);
		writer.name("timeMinu").value(writeStationDate.timeMinu);
		writer.endObject();
	}

    private void writeArraystationDate(JsonWriter writer,List<WriteStationDate> writeStationDates)
            throws IOException{
        writer.beginArray();
        for (WriteStationDate writeStationDate : writeStationDates) {
            writeStationDate(writer, writeStationDate);
        }
        writer.endArray();
    }
    private void writeJsonStream(OutputStream out, List<WriteStationDate> writeStationDates)
            throws IOException {
        JsonWriter writer=new JsonWriter(new OutputStreamWriter(out,"UTF-8"));
        writer.setIndent(" ");
        writeArraystationDate(writer, writeStationDates);
        writer.close();

    }

    private  String cityName ="";
    private int subwayNum=0;
    private  String filePath="";
    public static String jsonFile="";

    private static  List<WriteStationDate> writeStationDates =new ArrayList<>();

    public void addDate(WriteStationDate writeStationDate){

        writeStationDates.add(writeStationDate);
    }

    public void clearDate(){

        writeStationDates.clear();

    }
    public void ClickToWriteStationDateJson() throws
            IOException{
        File sd= Environment.getExternalStorageDirectory();
        filePath=sd.getPath()+"/HappySubway/Date/"+getCityName();
        File file=new File(filePath);

        if(!file.exists())   file.mkdirs();

        jsonFile=file.getAbsolutePath()+"/route_"+getSubwayNum()+".json";

        FileOutputStream fos =new FileOutputStream(jsonFile);
        writeJsonStream(fos, writeStationDates);

    }

    @Override
    public String toString() {


        return getCityName()+" "+getStationName()+"站 ("+getXcoord()+","+getYcoord()+") "
                +getTimeHour()+"时 "+getTimeMinu()+"分 no:"+getOrderNum();
    }
}
