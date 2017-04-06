package subwayView;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by 夏日寒风 on 2016/10/16.
 */

public class Car {
    private mSurfaceView mView;
    private Canvas canvas;
    private Paint paint;
    public Car(){

        initDate();

    }

    private void initDate(){

        mView=mSurfaceView.getMySurfaceView();
        paint=new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
    }

    public void drawCar(float x,float y){
        canvas=mView.getCanvas();//获取画布对象
        canvas.drawCircle(x,y,20,paint);
    }
}
