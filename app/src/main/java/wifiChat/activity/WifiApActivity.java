package wifiChat.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deng_00_subway.R;

import java.util.ArrayList;
import java.util.List;


import wifiChat.commom.dialog.BaseDialog;
import wifiChat.commom.dialog.ConnWifiDialog;
import wifiChat.util.Logger;
import wifiChat.util.TextUtils;
import wifiChat.consts.WifiApConst;
import wifiChat.util.WifiUtils;
import wifiChat.adapter.WifiapAdapter;
import wifiChat.broadcast.WifiapBroadcast;

/**
 * Created by 夏日寒风 on 2017/3/17.
 */

public class WifiApActivity extends Activity implements View.OnClickListener, WifiapBroadcast.NetWorkChangeListener,
        AdapterView.OnItemClickListener{

    private static final Logger logger = Logger.getLogger(WifiApActivity.class);
    private String localIPaddress; // 本地WifiIP
    private String serverIPaddres; // 热点IP
    private  ApHandler mHandler;
    private WifiapAdapter mWifiApAdapter;
    private ConnWifiDialog mConnWifiDialog; // 连接热点窗口
    private LinearLayout mLlApInfo;
    private TextView mTvStatusInfo;
    private TextView mTvApSSID;
    private ListView mLvWifiList;
    private Button mBtnBack;
    private Button mBtnCreateAp;
    private Button mBtnNext;
    private BaseDialog mHintDialog; // 提示窗口
    private SearchWifiThread mSearchWifiThread;
    private ArrayList<ScanResult> mWifiList; // 符合条件的热点列表
    private WifiapBroadcast mWifiapBroadcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifiap);

        initBroadcast();
        initViews();
        initEvents();
        initAction();
    }

    /**
     * 设置IP地址信息
     *
     * @param isClient
     *            是否为客户端
     */
    public void setIPaddress() {
        if (WifiUtils.isWifiApEnabled()) {
            serverIPaddres = localIPaddress = "192.168.43.1";
        }
        else {
            localIPaddress = WifiUtils.getLocalIPAddress();
            serverIPaddres = WifiUtils.getServerIPAddress();
        }
        logger.i("localIPaddress:" + localIPaddress + " serverIPaddres:" + serverIPaddres);
    }
    /**
     * IP地址正确性验证
     *
     * @return boolean 返回是否为正确， 正确(true),不正确(false)
     */
    private boolean isValidated() {

        setIPaddress();
        String nullIP = "0.0.0.0";

        if (nullIP.equals(localIPaddress) || nullIP.equals(serverIPaddres)
                || localIPaddress == null || serverIPaddres == null) {
            showShortToast(R.string.wifiap_toast_connectap_unavailable);
            return false;
        }

        return true;
    }

    /** 执行登陆 **/
    private void doLogin() {
        if (!isValidated()) {
            return;
        }
        putAsyncTask(new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoadingDialog(getString(R.string.wifiap_dialog_login_saveInfo));
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    mSqlDBOperate = new SqlDBOperate(mContext);

                    String IMEI = SessionUtils.getIMEI();
                    String nickname = SessionUtils.getNickname();
                    String gender = SessionUtils.getGender();
                    String constellation = SessionUtils.getConstellation();
                    String device = getPhoneModel();
                    int age = SessionUtils.getAge();
                    int avatar = SessionUtils.getAvatar();
                    int onlineStateInt = SessionUtils.getOnlineStateInt();

                    String logintime = DateUtils.getNowtime();

                    // 录入数据库
                    // 若数据库中有IMEI对应的用户记录，则更新此记录; 无则创建新用户
                    if ((mUserInfo = mSqlDBOperate.getUserInfoByIMEI(IMEI)) != null) {
                        mUserInfo.setIPAddr(localIPaddress);
                        mUserInfo.setAvater(avatar);
                        mUserInfo.setOnlineState(onlineStateInt);
                        mUserInfo.setName(nickname);
                        mUserInfo.setSex(gender);
                        mUserInfo.setAge(age);
                        mUserInfo.setDevice(device);
                        mUserInfo.setConstellation(constellation);
                        mUserInfo.setLastDate(logintime);
                        mSqlDBOperate.updateUserInfo(mUserInfo);
                    }
                    else {
                        mUserInfo = new UserInfo(nickname, age, gender, IMEI, localIPaddress,
                                onlineStateInt, avatar);
                        mUserInfo.setLastDate(logintime);
                        mUserInfo.setDevice(device);
                        mUserInfo.setConstellation(constellation);
                        mSqlDBOperate.addUserInfo(mUserInfo);
                    }

                    int usserID = mSqlDBOperate.getIDByIMEI(IMEI); // 获取用户id
                    // 设置用户Session
                    SessionUtils.setLocalUserID(usserID);
                    SessionUtils.setDevice(device);
                    SessionUtils.setIsClient(!WifiUtils.isWifiApEnabled());
                    SessionUtils.setLocalIPaddress(localIPaddress);
                    SessionUtils.setServerIPaddress(serverIPaddres);
                    SessionUtils.setLoginTime(logintime);

                    // 在SD卡中存储登陆信息
                    SharePreferenceUtils mSPutUtils = new SharePreferenceUtils();
                    SharedPreferences.Editor mEditor = mSPutUtils.getEditor();
                    mEditor.putString(Users.IMEI, IMEI).putString(Users.DEVICE, device)
                            .putString(Users.NICKNAME, nickname).putString(Users.GENDER, gender)
                            .putInt(Users.AVATAR, avatar).putInt(Users.AGE, age)
                            .putString(Users.BIRTHDAY, SessionUtils.getBirthday())
                            .putInt(Users.ONLINESTATEINT, onlineStateInt)
                            .putString(Users.CONSTELLATION, constellation)
                            .putString(Users.LOGINTIME, logintime);
                    mEditor.commit();
                    return true;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    if (null != mSqlDBOperate) {
                        mSqlDBOperate.close();
                        mSqlDBOperate = null;
                    }
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                dismissLoadingDialog();
                if (result) {
                    startActivity(MainTabActivity.class);
                    finish();
                }
                else {
                    showShortToast("操作失败,请检查网络是否正常。");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            // 创建热点
            case R.id.wifiap_btn_createap:

                // 如果不支持热点创建
                if (WifiUtils.getWifiApStateInt() == 4) {
                    showShortToast(R.string.wifiap_dialog_createap_nonsupport);
                    return;
                }

                // 如果wifi正打开着的，就提醒用户
                if (WifiUtils.isWifiEnabled()) {
                    mHintDialog
                            .setMessage(getString(R.string.wifiap_dialog_createap_closewifi_confirm));
                    mHintDialog.show();
                    return;
                }

                // 如果存在一个共享热点
                if (((WifiUtils.getWifiApStateInt() == 3) || (WifiUtils.getWifiApStateInt() == 13))
                        && (WifiUtils.getApSSID().startsWith(WifiApConst.WIFI_AP_HEADER))) {
                    mHintDialog.setMessage(getString(R.string.wifiap_dialog_closeap_confirm));
                    mHintDialog.show();
                    return;
                }

                mHintDialog
                        .setMessage(getString(R.string.wifiap_dialog_createap_closewifi_confirm));
                mHintDialog.show();
                return;

            // 返回按钮
            case R.id.wifiap_btn_back:
                if (mHintDialog.isShowing()) {
                    mHintDialog.dismiss();
                }
                finish();
                break;

            // 下一步按钮
            case R.id.wifiap_btn_next:
                if (mHintDialog.isShowing()) {
                    mHintDialog.dismiss();
                }
                doLogin();
                break;

        }
    }

    /** 初始化视图 获取控件对象 **/
    protected void initViews() {
        mLlApInfo = (LinearLayout) findViewById(R.id.wifiap_lv_create_ok);
        mTvStatusInfo = (TextView) findViewById(R.id.wifiap_tv_wifistatus);
        mTvApSSID = (TextView) findViewById(R.id.wifiap_tv_createap_ssid);
        mLvWifiList = (ListView) findViewById(R.id.wifiap_lv_wifi);
        mBtnBack = (Button) findViewById(R.id.wifiap_btn_back);
        mBtnCreateAp = (Button) findViewById(R.id.wifiap_btn_createap);
        mBtnNext = (Button) findViewById(R.id.wifiap_btn_next);
    }

    public void initEvents(){

        mWifiList = new ArrayList<>();
        mWifiApAdapter = new WifiapAdapter(this, mWifiList);
        mLvWifiList.setAdapter(mWifiApAdapter);

        mHandler=new ApHandler();
        mSearchWifiThread = new SearchWifiThread(mHandler);
        mConnWifiDialog = new ConnWifiDialog(this, mHandler);

        hintDialogOnClick hintClick = new hintDialogOnClick();
        mHintDialog = BaseDialog.getDialog(WifiApActivity.this, R.string.dialog_tips, "",
                getString(R.string.btn_yes), hintClick, getString(R.string.btn_cancel), hintClick);
        mLvWifiList.setOnItemClickListener(this);
        mBtnCreateAp.setOnClickListener(this);
        mBtnBack.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
    }

    /** 动态注册广播 */
    public void initBroadcast() {
        mWifiapBroadcast = new WifiapBroadcast(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(mWifiapBroadcast, filter);
    }

    @Override
    public void WifiConnected() {
        mHandler.sendEmptyMessage(WifiApConst.WiFiConnectSuccess);
    }

    @Override
    public void wifiStatusChange() {
        mHandler.sendEmptyMessage(WifiApConst.NetworkChanged);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        ScanResult ap = mWifiList.get(position);
        if (ap.SSID.startsWith(WifiApConst.WIFI_AP_HEADER)) {
            mTvStatusInfo.setText(getString(R.string.wifiap_btn_connecting) + ap.SSID);
            // 连接网络
            boolean connFlag = WifiUtils.connectWifi(ap.SSID, WifiApConst.WIFI_AP_PASSWORD,
                    WifiUtils.WifiCipherType.WIFICIPHER_WPA);
            if (!connFlag) {
                mTvStatusInfo.setText(getString(R.string.wifiap_toast_connectap_error_1));
                mHandler.sendEmptyMessage(WifiApConst.WiFiConnectError);
            }
        }
        else if (!WifiUtils.isWifiConnect() || !ap.BSSID.equals(WifiUtils.getBSSID())) {
            mConnWifiDialog.setTitle(ap.SSID);
            mConnWifiDialog.setScanResult(ap);
            mConnWifiDialog.show();
        }



    }

    private class ApHandler extends Handler {

        private boolean isRespond = true;

        public ApHandler() {
        }

        public void setRespondFlag(boolean flag) {
            isRespond = flag;
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case WifiApConst.ApScanResult: // 扫描Wifi列表
                    if (isRespond) {
                        getWifiList();
                        refreshAdapter(mWifiList);
                    }
                    break;

                case WifiApConst.ApCreateApSuccess: // 创建热点成功
                    mSearchWifiThread.stop();
                    mTvStatusInfo.setText(getString(R.string.wifiap_text_createap_succeed));
                    mLvWifiList.setVisibility(View.GONE);
                    mLlApInfo.setVisibility(View.VISIBLE);
                    mTvApSSID.setText("SSID: " + WifiUtils.getApSSID());
                    mBtnCreateAp.setText(getString(R.string.wifiap_btn_closeap));
                    mBtnBack.setClickable(true);
                    mBtnCreateAp.setClickable(true);
                    mBtnNext.setClickable(true);
                    break;

                case WifiApConst.WiFiConnectSuccess: // 连接热点成功
                    String str = getString(R.string.wifiap_text_wifi_connected)
                            + WifiUtils.getSSID();
                    mTvStatusInfo.setText(str);
                    showShortToast(str);
                    break;

                case WifiApConst.WiFiConnectError: // 连接热点错误
                    showShortToast(R.string.wifiap_toast_connectap_error);
                    break;

                case WifiApConst.NetworkChanged: // Wifi状态变化
                    if (WifiUtils.isWifiEnabled()) {
                        mTvStatusInfo.setText(getString(R.string.wifiap_text_wifi_1_0));
                    }
                    else {
                        mTvStatusInfo.setText(getString(R.string.wifiap_text_wifi_0));
                        showShortToast(R.string.wifiap_text_wifi_disconnect);
                    }

                default:
                    break;
            }
        }
    }

    /**
     * 获取Wifi热点名
     *
     * <p>
     * BuildBRAND 系统定制商 ； BuildMODEL 版本
     * </p>
     *
     * @return 返回 定制商+版本 (String类型),用于创建热点。
     */
    public String getLocalHostName() {
        String str1 = Build.BRAND;
        String str2 = TextUtils.getRandomNumStr(3);
        return str1 + "_" + str2;
    }

    private void getWifiList() {
        mWifiList.clear();
        WifiUtils.startScan();
        List<ScanResult> scanResults = WifiUtils.getScanResults();
        mWifiList.addAll(scanResults);

    }

    /**
     * 刷新热点列表UI
     *
     * @param list
     */
    public void refreshAdapter(List<ScanResult> list) {
        mWifiApAdapter.setData(list);
        mWifiApAdapter.notifyDataSetChanged();
    }

    /** 短暂显示Toast提示(来自String) **/
    public void showShortToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    /** 短暂显示Toast提示(来自res) **/
    public void showShortToast(int resId) {
        Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
    }

    /**
     * 定时刷新Wifi列表信息
     */
    class SearchWifiThread implements Runnable {
        private boolean running = false;
        private Thread thread = null;
        private Handler handler = null;

        SearchWifiThread(Handler handler) {
            this.handler = handler;
        }

        public void run() {
            while (!WifiUtils.isWifiApEnabled()) {
                if (!this.running)
                    return;
                try {
                    Thread.sleep(2000); // 扫描间隔
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(WifiApConst.ApScanResult);
            }
        }

        public void start() {
            try {
                this.thread = new Thread(this);
                this.running = true;
                this.thread.start();
            }
            finally {
            }
        }

        public void stop() {
            try {
                this.running = false;
                this.thread = null;
            }
            finally {
            }
        }
    }

    /** 初始化控件设置 **/
    protected void initAction() {

        if (!WifiUtils.isWifiConnect() && !WifiUtils.isWifiApEnabled()) { // 无开启热点无连接WIFI
            WifiUtils.OpenWifi();
        }

        if (WifiUtils.isWifiConnect()) { // Wifi已连接
            mTvStatusInfo.setText(getString(R.string.wifiap_text_wifi_connected)
                    + WifiUtils.getSSID());

        }

        if (WifiUtils.isWifiApEnabled()) { // 已开启热点
            if (WifiUtils.getApSSID().startsWith(WifiApConst.WIFI_AP_HEADER)) {
                mTvStatusInfo.setText(getString(R.string.wifiap_text_ap_1));
                mLvWifiList.setVisibility(View.GONE);
                mLlApInfo.setVisibility(View.VISIBLE);
                mTvApSSID.setText("SSID: " + WifiUtils.getApSSID());
                mBtnCreateAp.setText(getString(R.string.wifiap_btn_closeap));
            }
            else {
                WifiUtils.closeWifiAp();
                WifiUtils.OpenWifi();
                mTvStatusInfo.setText(getString(R.string.wifiap_text_wifi_1_0));
            }
        }

        if (WifiUtils.isWifiEnabled() && !WifiUtils.isWifiConnect()) { // Wifi已开启，未连接
            mTvStatusInfo.setText(getString(R.string.wifiap_text_wifi_1_0));
        }

        mSearchWifiThread.start();
    }

    public class hintDialogOnClick implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface hintDialog, int which) {
            switch (which) {

                // 确定
                case 0:
                    hintDialog.dismiss();
                    if (WifiUtils.isWifiApEnabled()) {

                        // 执行关闭热点事件
                        WifiUtils.closeWifiAp();
                        WifiUtils.OpenWifi();

                        showShortToast(R.string.wifiap_text_ap_0);
                        mTvStatusInfo.setText(getString(R.string.wifiap_text_wifi_1_0));
                        mBtnCreateAp.setText(getString(R.string.wifiap_btn_createap));
                        mLlApInfo.setVisibility(View.GONE);
                        mLvWifiList.setVisibility(View.VISIBLE);

//                        localIPaddress = null;
//                        serverIPaddres = null;

                        mSearchWifiThread.start();
                    }
                    else {
                        // 创建热点
                        mTvStatusInfo.setText(getString(R.string.wifiap_text_createap_creating));
                        mBtnBack.setClickable(false);
                        mBtnCreateAp.setClickable(false);
                        mBtnNext.setClickable(false);

                        WifiUtils.startWifiAp(WifiApConst.WIFI_AP_HEADER + getLocalHostName(),
                                WifiApConst.WIFI_AP_PASSWORD, mHandler);
                    }
                    break;

                // 取消
                case 1:
                    hintDialog.cancel();
                    break;
            }
        }

    }



    @Override
    protected void onDestroy() {
        unregisterReceiver(mWifiapBroadcast); // 撤销广播
        mSearchWifiThread.stop();
        mSearchWifiThread = null;
        super.onDestroy();
    }



}
