package subwayView;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import DealJsonDate.FirstXY;
import DealJsonDate.JsonTimeSetOff;

/**
 * Created by 夏日寒风 on 2016/10/7.
 */

public class RunCarView {
    private mSurfaceView mView;
    private mSurfaceView.MyPoint myPoint;
    private Context context;
    private Car car;
    private List<MyXYPoint> listOfFirsXY;
    private FirstXY firstXY;
    private List<List<MyXYPoint>> listListOfXY;
    public RunCarView(Context context) throws IOException {

        this.context=context;//第二个构造方法
        initDate();

    }

    /**
     * 初始化各种数据
     * @throws IOException
     */
    private void initDate() throws IOException {

        firstXY=new FirstXY();

        mView=mSurfaceView.getMySurfaceView();
        myPoint=mView.getmPoint();//获取自定义坐标点
//        listOfFirsXY=firstXY.getListOfFirstXY();//获取每路线上第一架地铁的初始位置集合
        listListOfXY=new ArrayList<>();
        car=new Car();//创建地铁实例对象

    }


    /**算法设计
     * 【核心：根据时间得出坐标，以时刻转换成坐标】
     * 【首先检测有几条线路，判断用户选择哪条线路，靠近那个地铁站，选择给用户呈现站点左右几架地铁】
     * 【检测手机系统现在时刻，精确至秒】
     * 【遍历出发时间集合，找到与此时相对应的出发时间】
     * 【再计算出此时路线上已有几架地铁，计算出初始化时对应时刻的地铁位置】
     * 【根据速度，每秒更新已有地铁在线路上的位置，同时检查出发时间，绘制出新地铁】
     *  【确定初始位置的方法只执行一次，以便优化内存】
     */


    public void drawRunCar()  {
         listListOfXY=firstXY.getListListOfFirstXY();

        for (List<MyXYPoint> listOfFirsXY :listListOfXY) {

            for (MyXYPoint myXYPoint: listOfFirsXY ) {

                myPoint.setMxYcoord(myXYPoint.getX(),myXYPoint.getY());
                car.drawCar(myPoint.x,myPoint.y);

//                System.out.println("--------------"+(++i));
            }
//            System.out.println("绘制集合分割--------------");
        }
        listListOfXY.clear();
    }








}
