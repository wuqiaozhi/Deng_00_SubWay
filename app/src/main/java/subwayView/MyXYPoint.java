package subwayView;

/**
 * Created by 夏日寒风 on 2016/11/3.
 */

public class MyXYPoint {
    public float getY() {
        return y;
    }

    public float getX() {
        return x;
    }

    float x=0;
    float y=0;

    /**
     *
     * @param x 自定义坐标x
     * @param y 自定义坐标y
     */
    public MyXYPoint(float x,float y){
        this.x=x;
        this.y=y;
    }

    @Override
    public String toString() {

        return this.x+"<-x-坐标-y->"+this.y;
    }
}
