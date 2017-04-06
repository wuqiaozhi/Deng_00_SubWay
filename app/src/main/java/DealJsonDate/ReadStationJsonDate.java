package DealJsonDate;

import android.os.Environment;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by 夏日寒风 on 2016/9/19.
 */
public class ReadStationJsonDate {


    private WriteStationDate readStationDate(JsonReader reader) throws IOException {
        String stationName=null;
        int timeHour=0,timeMinu=0,orderNum=0;
        float Xcoord=0,Ycoord=0;
        reader.beginObject();
        while(reader.hasNext()){
            String field=reader.nextName();
            if(field.equals("stationName"))
                stationName=reader.nextString();
            else if (field.equals("orderNum")) {
                orderNum=reader.nextInt();
            }else if (field.equals("timeHour")) {
                timeHour=reader.nextInt();
            }else if (field.equals("timeMinu")) {
                timeMinu=reader.nextInt();
            }else if (field.equals("Xcoord")) {
                Xcoord= (float) reader.nextDouble();
            }else if (field.equals("Ycoord")) {
                Ycoord= (float) reader.nextDouble();
            }
            else{
                reader.skipValue();
            }
        }
        reader.endObject();
        return new WriteStationDate(stationName,Xcoord,Ycoord,orderNum,timeHour,timeMinu);
    }

    private List<WriteStationDate> readStationDateArray(JsonReader reader)
            throws IOException {
        List<WriteStationDate> times=new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            times.add(readStationDate(reader));
        }
        reader.endArray();
        return times;

    }

    private List<WriteStationDate> readJsonStream(InputStream in)
            throws IOException {

        JsonReader reader=new JsonReader(new InputStreamReader(in,"UTF-8"));
        try{
            return readStationDateArray(reader);
        }finally{

            reader.close();
        }
    }
    public List<List<WriteStationDate>> ReadJsonFiles() throws IOException {
        File sd= Environment.getExternalStorageDirectory();
        String jsonFile=sd.getPath()+"/HappySubway/Date/模拟";

        File f3 = new File(jsonFile);
        File[] fs = f3.listFiles();

        List<List<WriteStationDate>> listOfStationDateList =new ArrayList<>();
        for(File f:fs) {
            if (f.getAbsolutePath().endsWith(".json")) {
                FileInputStream fis=new FileInputStream(f.getAbsolutePath());
                List<WriteStationDate> times=readJsonStream(fis);
                listOfStationDateList.add(times);
            }
        }
        return  listOfStationDateList;
    }


}
