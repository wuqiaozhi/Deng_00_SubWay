package wifiChat.commom;

import android.app.Application;

/**
 * Created by 夏日寒风 on 2017/3/21.
 */

public class BaseApplication extends Application {

    private static BaseApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();

        if (instance == null) {
            instance = this;
        }
    }
    /**
     * <p>
     * 获取BaseApplication实例
     * <p>
     * 单例模式，返回唯一实例
     *
     * @return instance
     */
    public static BaseApplication getInstance() {
        return instance;
    }
}
