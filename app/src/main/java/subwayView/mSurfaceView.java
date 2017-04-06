package subwayView;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 夏日寒风 on 2016/9/8.
 */
public  class mSurfaceView extends SurfaceView implements SurfaceHolder.Callback {


    private float mapScale=0,mapScaleMax=0,mapScaleMin=0,height,width;
    private  float Xcentre,Ycentre,xCentreLimit,yCentreLimit;
    private MyPoint mPoint;
    private float currentDistance,lastDistance,currentOneClickDistance;

    private static mSurfaceView mf=null;

    /**
     * 单例模式，保持本类变量值
     *
     * @return
     */
    public static mSurfaceView getMySurfaceView() {
        return mf;
    }
    public Canvas getCanvas() {
        return canvas;
    }

    private  Canvas canvas;
    public mSurfaceView(Context context) {
        super(context);
        getHolder().addCallback(this);
        mPoint=new MyPoint();

    }
    public mSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        getHolder().addCallback(this);
        mPoint=new MyPoint();
        mf=this;

    }

    public   class MyPoint{
        public float x=0;
        public float y=0;

        private void setXY(float x, float y) {
            this.x = x;
            this.y = y;
        }
        /**
         * 根据自定义坐标转化成屏幕坐标
         * @return
         */
        public void setMxYcoord(float mx,float my){

            setmY(my);
            setmX(mx);
            //将自定义坐标转为屏幕坐标
            x=Xcentre+getmX()*mapScale;
            y=Ycentre-getmY()*mapScale;

            mPoint.setXY(x,y);
        }
    }
    /************以下为对外提供设定自定义坐标方法**************/
    private  float mX;
    private float mY;

    private float getmX() {
        return mX;
    }
    private float getmY() {
        return mY;
    }
    private void setmY(float mY) {
        this.mY = mY;
    }
    private void setmX(float mX) {
        this.mX = mX;
    }

    /************以上为对外提供设定自定义坐标方法**************/

    /**
     * 对外提供获取MyPoint方法
     *
     * @return
     */
    public MyPoint getmPoint(){

        return mPoint;
    }


    /**
     * 以屏幕中心为坐标原点，画XY坐标轴，X坐标轴范围为 -50至50，Y坐标轴也为-50至50
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureWorH(widthMeasureSpec),measureWorH(heightMeasureSpec));

        height=MeasureSpec.getSize(heightMeasureSpec);
        width=MeasureSpec.getSize(widthMeasureSpec);
        mapScale=height/100;//以屏幕的高将一刻度分为100份
        mapScaleMax=width/20;
        mapScaleMin=mapScale;
        Xcentre=width/2;
        Ycentre=height/2;
        xCentreLimit=width-200;
        yCentreLimit=height-200;
    }

    /**
     * 根据xml布局文件获取长宽
     * @param measureSpec
     * @return
     */
    private int measureWorH(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

   private Timer timer;
    /**
     * 每秒钟刷新一次画布
     */
   private TimerTask timerTask=new TimerTask() {
       @Override
       public void run() {
           drawView();
       }
   };

    public void drawView(){
        //TODO 由子类重写

    }

    //记录最后单点触摸的xy坐标
    private float lastX,downX,moveX,x0,x1,offsetX,Xmin,Xmax,Xc,Xc1;
    private float lastY,downY,moveY,y0,y1,offsetY,Ymin,Ymax,Yc,Yc1;
    private boolean isTwoGesture=false,isTwoLeft=true;
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                lastX= event.getX();
                lastY=event.getY();
                    break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount()>=2) {//双手势
                        isTwoGesture=true;
                        if(isTwoGesture){
                            x0=event.getX(0);
                            x1=event.getX(1);
                            y0=event.getY(0);
                            y1=event.getY(1);
                            if(x0<x1) {Xmin=x0;Xmax=x1;}
                            else {Xmin=x1;Xmax=x0;}

                            if(y0<y1) {Ymin=y0;Ymax=y1;}
                            else {Ymin=y1;Ymax=y0;}

                            Xc=(Xmax-Xmin)/2+Xmin-Xcentre;
                            Yc=Ycentre-((Ymax-Ymin)/2+Ymin);
                            Xc1=(Xmax-Xmin)/2+Xmin;
                            Yc1=(Ymax-Ymin)/2+Ymin;

                           isTwoLeft=false;
                        }

                        //以上为判断两手指间第一次接触时中心屏幕像素坐标
                    offsetX = event.getX(0)-event.getX(1);
                    offsetY = event.getY(0)-event.getY(1);
                    currentDistance = (float) Math.sqrt(offsetX*offsetX+offsetY*offsetY);

                    if (lastDistance<0) {
                        lastDistance = currentDistance;
                    }else{
                        if (currentDistance-lastDistance>5) {

                            if (mapScale<=mapScaleMax) {
                                mapScale*=1.08;//放大
//                              Log.d("XYcentre","原先(Xcentre,Ycentre)--> "+Xcentre+","+Ycentre+" 两指坐标(x,y)--> "+Xc1+","+Yc1);
                                Xcentre= (float) (Xcentre-Xc*(1.08-1));
                                Ycentre= (float) (Ycentre+Yc*(1.08-1));
//                              Log.d("xY","(Xcentre,Ycentre)--> "+Xcentre+","+Ycentre+"  (Xc,Yc)-->"+Xc*1.08+","+Yc*1.08);
                                drawView();

                            }
                            lastDistance = currentDistance;
                        }else if (lastDistance-currentDistance>5) {
                            if (mapScale>=mapScaleMin) {
                                mapScale*=0.93;//缩小
                                Xcentre= (float) (Xcentre-Xc*(0.93-1));
                                Ycentre= (float) (Ycentre+Yc*(0.93-1));
                                drawView();

                            }
                            lastDistance = currentDistance;
                        }
                    }
                }else {//单手势
                    if(isTwoGesture){//判断是否为双手势转入单手势
                        lastX= event.getX();
                        lastY=event.getY();
                    }
                    isTwoGesture=false;
                    downX=event.getX();//获取滑动坐标
                    downY=event.getY();

                    moveX=downX-lastX;
                    moveY=downY-lastY;//计算移动偏移坐标

                    lastX= downX;
                    lastY=downY;
                    currentOneClickDistance=(float) Math.sqrt(moveX*moveX+moveY*moveY);

                    if(currentOneClickDistance>3){
                        float tempX=Xcentre+moveX;//赋值前先判断是否超出条件
                        float tempY=Ycentre+moveY;

                        if((tempX>=0&&tempX<=xCentreLimit)&&(tempY>=0&&tempY<=yCentreLimit)){
                            Xcentre+=moveX;
                            Ycentre+=moveY;
//                            单手势移动
                            drawView();
                        }
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                isTwoLeft=true;
                break;
        }

        return true;
    }


    /******以下为对外提供方法使用画布******/

    public void lockCanvas(){
        canvas=getHolder().lockCanvas();

    }
    public void unLockCanvas(){
        getHolder().unlockCanvasAndPost(canvas);
    }

    /******以上为对外提供方法使用画布******/
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        if(timer==null){
            timer=new Timer();
            timer.schedule(timerTask,0,1000);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
            timer.cancel();

    }
}
