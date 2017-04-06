package DealJsonDate;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import subwayView.MyXYPoint;

/**
 * Created by 夏日寒风 on 2016/11/5.
 */

public class FirstXY {

    private List<List<WriteStationDate>> listOfListDate;
    private List<List<JsonTimeSetOff>> listOfListSetTime;
    private ReadStationJsonDate readStationJsonDate;
    private JsonTimeSetOff timeSet;
    private MyXYPoint myXYPoint;
    private List<WriteStationDate> listOfDate;
    private List<JsonTimeSetOff> listOfTimeSet;
    private AnalyzeDate analyzeDate;
    private List<Integer> timeIntervalList;
    private List<MyXYPoint> listOfFirstXY;
    private int railNum=0;//地铁线路线
    private Calendar calendar;
    private int currentHour;
    private int currentMinu;
    private float currentSecond;
    private List<Integer> listOfRecentTimeSet;
    private int indexOfListTimeSet;//出发时间集合检测到的此时此刻的集合下标
    public FirstXY() throws IOException {
        initDate();
    }

    private void initDate() throws IOException {

        readStationJsonDate=new ReadStationJsonDate();
        timeSet=new JsonTimeSetOff();
        listOfListDate= readStationJsonDate.ReadJsonFiles();//获取地铁数据集合的集合
        railNum=listOfListDate.size();
        listOfListSetTime=timeSet.ReadJsonDateOfSetOffTimes();//获取出发时间集合的集合

        listOfFirstXY=new ArrayList<>();
        listOfRecentTimeSet=new ArrayList<>();
    }

    /**
     * 更新时间
     */
    private void freshTime(){
        calendar=Calendar.getInstance();
        currentHour=calendar.get(Calendar.HOUR_OF_DAY);//获取当前小时
        currentMinu=calendar.get(Calendar.MINUTE);//获取当前分钟
        currentSecond=calendar.get(Calendar.SECOND);//获取当前秒数

    }
    /**
     * 此集合包含每条路线第一架地铁车辆的初始位置
     * @return
     */
    public List<List<MyXYPoint>> getListListOfFirstXY()  {

        freshTime();

        List<List<MyXYPoint>> listList=new ArrayList<>();
        int IndexTimeSet;
        for(int i=0;i<railNum;i++){

            listOfDate=getListOfDate(i);//获取一条地铁数据集合
            listOfTimeSet=getListOfTimeSet(i);//获取一条出发时间数据集合
            analyzeDate=new AnalyzeDate(listOfDate);//分析数据类
            IndexTimeSet= detectSetTimeIndex(currentHour,currentMinu,listOfTimeSet);
            listOfRecentTimeSet=getListOfRecentTimeSet(IndexTimeSet,analyzeDate,listOfTimeSet);
            listOfFirstXY = detectLocation(getDaySecond(),analyzeDate,listOfRecentTimeSet);
            listList.add(listOfFirstXY);
        }
        return listList;
    }


    /**
     * 检测当前系统时间与出发时间比较
     * @param  currentHour
     * @param  currentMinu
     * @return 返回最近出发时间（秒）
     */
    private int detectSetTimeIndex(int currentHour, int currentMinu, List<JsonTimeSetOff> listOfTimeSet) {
        float currentTimeSecond = currentHour * 60 * 60 + currentMinu * 60;
        float timeSecond;
        int size = listOfTimeSet.size();
        for (int i = 0; i < size; i++) {
            timeSecond=listOfTimeSet.get(i).getDepartHour()*3600+listOfTimeSet.get(i).getDepartMinu()*60;
            if (timeSecond == currentTimeSecond) {
                indexOfListTimeSet=i;
                break;
            } else if (timeSecond > currentTimeSecond) {

                indexOfListTimeSet=i-1;
                break;
            }
        }
        return indexOfListTimeSet;

    }

    /**
     *
     * @param indexOfListTimeSet 当前出发时间集合下标
     * @param analyzeDate 数据分析类
     * @param listOfTimeSet 出发时间集合
     * @return 返回最近的出发时间集合
     */
    private List<Integer> getListOfRecentTimeSet(int indexOfListTimeSet,AnalyzeDate analyzeDate,List<JsonTimeSetOff> listOfTimeSet){
        List<Integer> listOfRecentTimeSet=new ArrayList<>();
        int timeInRail=analyzeDate.getRailTime();
        int timeInSec;
        JsonTimeSetOff timeSetOff;

        timeIntervalList=analyzeDate.getTimeInterval();
        timeSetOff=listOfTimeSet.get(indexOfListTimeSet);

        int currentTimeSet=timeSetOff.getDepartHour()*60*60+timeSetOff.getDepartMinu()*60;

        for(int i=indexOfListTimeSet,k=0;i>=0;i--,k++){

            timeSetOff=listOfTimeSet.get(i);
            timeInSec=timeSetOff.getDepartHour()*60*60+timeSetOff.getDepartMinu()*60;


            if(currentTimeSet-timeInSec>timeInRail) break;

            listOfRecentTimeSet.add(timeInSec);

        }

        return listOfRecentTimeSet;
    }
    /**
     * 依据时间检测初始位置
     * @param currentTime 当前天数所在秒数
     * @return 返回xy自定义坐标
     */
    private List<MyXYPoint> detectLocation( float currentTime, AnalyzeDate analyzeDate,List<Integer> RecentTimeSet){
        float timePassed;
        float timeSet;
        float timeInterval=0,timeOverlay=0;
        float timeSegment;
        float segmentPercent=0;//在段位上的位置比例
        timeIntervalList=analyzeDate.getTimeInterval();
        int sizeOfTimeIterval=timeIntervalList.size();
        int sizeOfTimeSet=RecentTimeSet.size();
        int locationNum;//记录地铁在哪个段位
        List<MyXYPoint> myXYPoints=new ArrayList<>();

        for(int i=0;i<sizeOfTimeSet;i++){

            timeSet=RecentTimeSet.get(i);
            timePassed=currentTime-timeSet;

            for(locationNum=0;locationNum<sizeOfTimeIterval;locationNum++){

                timeInterval=timeIntervalList.get(locationNum);
                timeOverlay+=timeInterval;

                if(timeOverlay>=timePassed) {  break;}
            }

            if(locationNum==sizeOfTimeIterval) {locationNum--; }

            timeSegment=timePassed-(timeOverlay-timeInterval);

            segmentPercent=timeSegment/timeInterval;
            if(segmentPercent>1) break;
            myXYPoints.add(getXYInitLocation(locationNum,segmentPercent));

            timeOverlay=0;

        }
        return myXYPoints;
    }

    /**
     *  返回地铁所在在路段的坐标
     * @param i 地铁所在的初始路段 i从0开始
     * @param segmentPercent 地铁所在的初始路段位置占此路段的百分比
     * @return
     */
    private MyXYPoint getXYInitLocation(int i,float segmentPercent){
        float x1=listOfDate.get(i).getXcoord();
        float x2=listOfDate.get(i+1).getXcoord();
        float y1=listOfDate.get(i).getYcoord();
        float y2=listOfDate.get(i+1).getYcoord();
        float xd=x2-x1;
        float yd=y2-y1;
        float locationX=x1+xd*segmentPercent;
        float locationY=y1+yd*segmentPercent;
        myXYPoint=new MyXYPoint(locationX,locationY);
        return myXYPoint;
    }
    private float getDaySecond(){

        float timeSec=currentHour*60*60+currentMinu*60+currentSecond;

        return timeSec;
    }
    private List<WriteStationDate> getListOfDate(int railNum) {
        List<WriteStationDate> listOfDate=listOfListDate.get(railNum);
        return listOfDate;
    }

    private List<JsonTimeSetOff> getListOfTimeSet(int railNum){
        List<JsonTimeSetOff> listOfTimeSet=listOfListSetTime.get(railNum);
        return listOfTimeSet;
    }
}
