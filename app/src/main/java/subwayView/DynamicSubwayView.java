package subwayView;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;

import DealJsonDate.ReadStationJsonDate;
import subwayView.mSurfaceView;

/** Created by 夏日寒风 on 2016/9/9. */
public class DynamicSubwayView extends mSurfaceView {

    private  Canvas canvas;
    private Context context;//TODO
    public DynamicSubwayView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initDate();
    }

    public DynamicSubwayView(Context context) {
        super(context);
        this.context=context;
        initDate();
    }
    private  RailView railView;
    private RunCarView runCarView;

    /**
     * 初始化数据，创建对象
     */
    private void initDate(){

            railView=new RailView(context);

        try {
            runCarView=new RunCarView(context);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    /**
     * 此方法进行绘制
     */
    @Override
    public void drawView()  {
        super.drawView();

        lockCanvas();
        canvas=getCanvas();
        canvas.drawColor(Color.rgb(134,178,223));
        try {
            railView.drawMyRail();
        } catch (IOException e) {
            e.printStackTrace();
        }
        runCarView.drawRunCar();

        unLockCanvas();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        super.surfaceChanged(holder, format, width, height);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);

    }
}
