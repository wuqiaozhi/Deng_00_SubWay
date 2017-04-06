package subwayView;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import DealJsonDate.ReadStationJsonDate;
import DealJsonDate.WriteStationDate;

public class RailView {
	    
	private	Paint paintRail;
	private	Paint paintLineStop;
	private Paint paintArc;
	private	Paint paintText;
    private Canvas canvas;
	private List<List<WriteStationDate>> listOfListDate;
	private mSurfaceView mView;
	private mSurfaceView.MyPoint myPoint,myPoint2;
	private  Context context;//TODO
	public RailView(Context context) {


	    paintRail=new Paint();
		paintRail.setColor(Color.rgb(235,211,147));
		paintRail.setStrokeWidth(10);
		paintRail.setAntiAlias(true);
		paintLineStop=new Paint();
		paintLineStop.setColor(Color.rgb(0,166,154));
		paintLineStop.setStrokeWidth(15);
		paintLineStop.setAntiAlias(true);
		paintText=new Paint();
		paintText.setColor(Color.RED);
		paintText.setAntiAlias(true);
		paintText.setTextSize(30);
		paintArc=new Paint();
		paintArc.setColor(Color.rgb(235,211,147));
		paintArc.setAntiAlias(true);
		paintArc.setStrokeWidth(10);
		paintArc.setStyle(Paint.Style.STROKE);

		mView=mSurfaceView.getMySurfaceView();//获取msurfaceView静态单例对象
		this.context=context;
		try {
			listOfListDate=getRailDateList();//获取数据集合
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void drawMyRail() throws IOException {

		 canvas=mView.getCanvas();
		 myPoint=mView.getmPoint();
		 myPoint2=mView.getmPoint();
		 drawRailLines();
		 drawSpotName();
	}

	private List<List<WriteStationDate>> getRailDateList() throws IOException {
			ReadStationJsonDate stationJsonDate=new ReadStationJsonDate();

			List<List<WriteStationDate>> listOfListDate =stationJsonDate.ReadJsonFiles();

		return listOfListDate;
 	}
	private void drawRailLines() {
		drawLines();
	}
	private void drawVerticalLines(float x1,float y1,float x2,float y2,int i,int size){
		//画双轨地铁线
		canvas.drawLine(x1, y1, x2,y2, paintRail);
		canvas.drawLine(x1-50, y1, x2-50,y2, paintRail);
		//画站点圆圈与终点圆弧
		canvas.drawCircle(x1-50,y1,8,paintLineStop);
		canvas.drawCircle(x1,y1,8,paintLineStop);
		if(i==size-2) {
			Path p=new Path();
			p.moveTo(x2,y2);
			p.lineTo(x2-25,y2+25);
			p.lineTo(x2-50,y2);
			canvas.drawPath(p,paintArc);
			//画终圆点
			canvas.drawCircle(x2-50,y2,8,paintLineStop);
			canvas.drawCircle(x2,y2,8,paintLineStop);
		}else if (i==0){
			Path p=new Path();
			p.moveTo(x1,y1);
			p.lineTo(x1-25,y1-25);
			p.lineTo(x1-50,y1);
			canvas.drawPath(p,paintArc);
			//画终圆点
			canvas.drawCircle(x1-50,y1,8,paintLineStop);
			canvas.drawCircle(x1,y1,8,paintLineStop);

		}

	}
	private void drawHorizontalLine(float x1,float y1,float x2,float y2,int i,int size){
		//画双轨地铁线
		canvas.drawLine(x1, y1, x2,y2, paintRail);
		canvas.drawLine(x1, y1+50, x2,y2+50, paintRail);
		//画站点圆圈与终点圆弧
		canvas.drawCircle(x1,y1,8,paintLineStop);
		canvas.drawCircle(x1,y1+50,8,paintLineStop);
		if(i==size-2) {
			Path p=new Path();
			p.moveTo(x2,y2);
			p.lineTo(x2+25,y2+25);
			p.lineTo(x2,y2+50);
			canvas.drawPath(p,paintArc);
			//画终圆点
			canvas.drawCircle(x2,y2,8,paintLineStop);
			canvas.drawCircle(x2,y2+50,8,paintLineStop);
		}else if (i==0){
			Path p=new Path();
			p.moveTo(x1,y1);
			p.lineTo(x1-25,y1+25);
			p.lineTo(x1,y1+50);
			canvas.drawPath(p,paintArc);
			//画终圆点
			canvas.drawCircle(x1,y1,8,paintLineStop);
			canvas.drawCircle(x1,y1+50,8,paintLineStop);

		}


	}
    private void drawCurveLines(float x1,float y1,float x2,float y2,float x3,float y3){
        //画双轨地铁线
        canvas.drawLine(x1, y1, x2,y2, paintRail);
        canvas.drawLine(x1, y1+50, x2-50,y2+50, paintRail);
        canvas.drawLine(x2, y2, x3,y3, paintRail);
        canvas.drawLine(x2-50, y2+50, x3-50,y3, paintRail);
        //画站点圆圈
        canvas.drawCircle(x1,y1,8,paintLineStop);
        canvas.drawCircle(x1,y1+50,8,paintLineStop);

        canvas.drawCircle(x2,y2,8,paintLineStop);
        canvas.drawCircle(x2-50,y2+50,8,paintLineStop);
    }
	private  void drawLines(){

		for (List<WriteStationDate> railDateList: listOfListDate ) {

			int size=railDateList.size();
			for(int i=0;i<size-1;i++){
				float x=railDateList.get(i).getXcoord();
				float y=railDateList.get(i).getYcoord();
				float xNext=railDateList.get(i+1).getXcoord();
				float yNext=railDateList.get(i+1).getYcoord();
                float x3=0;
                float y3=0;
                //判断是否为90度转角
                boolean isCurve=false;
                if(i<size-2){
                    float xThird=railDateList.get(i+2).getXcoord();
                    float yThird=railDateList.get(i+2).getYcoord();
                    myPoint.setMxYcoord(xThird,yThird);
                     x3=myPoint.x;
                     y3=myPoint.y;
                    if(xThird-x!=0&&yThird-y!=0) isCurve=true;

                }
				myPoint.setMxYcoord(x,y);
				float x1=myPoint.x,y1=myPoint.y;
				myPoint.setMxYcoord(xNext,yNext);
				float x2=myPoint.x,y2=myPoint.y;

                if(isCurve){
                    drawCurveLines(x1,y1,x2,y2,x3,y3);
                    i++;
                    isCurve=false;
                }else if(xNext-x==0){
                    drawVerticalLines(x1,y1,x2,y2,i,size);
                }else if(xNext-x!=0){
                    drawHorizontalLine(x1,y1,x2,y2,i,size);
                }

			}

		}
	}

   private void drawSpotName(){
	   for (List<WriteStationDate> railDateList: listOfListDate) {
		   int size=railDateList.size();
		   for(int i=0;i<size;i++){
			   float x=railDateList.get(i).getXcoord();
			   float xTime=x;
			   float y=railDateList.get(i).getYcoord();

			   myPoint.setMxYcoord(x,y);
			   myPoint2.setMxYcoord(xTime,y);
			   int orderNum=railDateList.get(i).getOrderNum();
			   canvas.drawText(orderNum+"", myPoint.x-30, myPoint.y, paintText);
		   }
	   }

   }
}
