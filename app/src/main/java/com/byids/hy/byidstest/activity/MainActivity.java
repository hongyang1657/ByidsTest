package com.byids.hy.byidstest.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.byids.hy.byidstest.Bean.HomeAttr;
import com.byids.hy.byidstest.Bean.SwitchItem;
import com.byids.hy.byidstest.R;
import com.byids.hy.byidstest.TCPLongSocketCallback;
import com.byids.hy.byidstest.TcpLongSocket;
import com.byids.hy.byidstest.adapter.MyGridAdapter;
import com.byids.hy.byidstest.adapter.MyRecyclerAdapter;
import com.byids.hy.byidstest.constants.MyConstants;
import com.byids.hy.byidstest.utils.CommandJsonUtils;
import com.byids.hy.byidstest.utils.Encrypt;
import com.byids.hy.byidstest.utils.VibratorUtil;
import com.byids.hy.byidstest.view.WheelView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 主界面
 * Created by hy on 2016/6/23.
 */
public class MainActivity extends Activity{
    private Context mContext;
    private int flag = 0;

    private final String KETING = "客厅";
    private final String CANTING = "餐厅";
    private final String SHUFANG = "书房";
    private final String WOSHI = "卧室";
    private final String LINYUJIAN = "淋浴间";


    private String TAG = "result";
    private HomeAttr homeAttr = new HomeAttr();
    private final int ACTIVE = 1;//家里是否有该功能的组件，有1，无0
    private final int INACTIVE = 0;
    private String ip; //home  ip地址
    private String uname;
    private String pwd;
    private String hid;

    private GridView gridView;
    private MyGridAdapter adapter;
    private List<SwitchItem> iconList;

    private PopupWindow popupWindow;
    private WheelView wv_rooms;
    private TextView tv_rooms;//点击弹出popupwindow

    private String[] roomNameList;
    private ArrayList<String> roomNames;
    private List<SwitchItem> beforeIconList;//刷新后的list
    private Button btn_sure;
    private RecyclerView recyclerView;
    private MyRecyclerAdapter myRecyclerAdapter;

    //----------------socket---------------------
    public static final int DEFAULT_PORT = 57816;
    private TcpLongSocket tcplongSocket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        uname = getIntent().getStringExtra("uname");
        pwd = getIntent().getStringExtra("pwd");
        hid = getIntent().getStringExtra("hid");
        ip=getIntent().getStringExtra("ip");
        if (ip ==null) {
            Toast.makeText(MainActivity.this, "找不到主机", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(MainActivity.this, "找到主机", Toast.LENGTH_LONG).show();
            tcplongSocket=new TcpLongSocket(new ConnectTcp());
            tcplongSocket.startConnect(ip,DEFAULT_PORT);
        }
        initData();
        initView();

    }

    private class ConnectTcp implements TCPLongSocketCallback {


        @Override
        public void connected() {
            Log.i("MAIN", String.valueOf(tcplongSocket.getConnectStatus()));
            VibratorUtil.Vibrate(MainActivity.this,300);
            JSONObject checkCommandData=new JSONObject();
            try {
                checkCommandData.put("kong","keys");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String  checkJson = CommandJsonUtils.getCommandJson(1,checkCommandData,hid,uname,pwd, String.valueOf(System.currentTimeMillis()));
            Log.i("result","check"+checkJson);
            tcplongSocket.writeDate(Encrypt.encrypt(checkJson));

        }

        @Override
        public void receive(byte[] buffer) {
            Log.i("收到数据","--------");
            if("Hello client"==buffer.toString()){
                Log.i("心跳", "心跳"+String.valueOf(tcplongSocket.getConnectStatus()));
            }

        }

        @Override
        public void disconnect() {
            tcplongSocket.close();
        }
    }

    private void initData() {
        roomNameList = getIntent().getStringArrayExtra("roomNameList");
        homeAttr = (HomeAttr) getIntent().getSerializableExtra("homeAttr");
        String roomAttr = getIntent().getStringExtra("roomAttr");
        roomNames = new ArrayList<String>();
        for (int i=0;i<roomNameList.length;i++){
            roomNames.add(roomNameList[i]);//把房间名添加到数组
        }

        iconList = new ArrayList<>();
        //初始化home数据
        initHomeData(iconList);

        //初始化默认客厅的数据
        initKeTingData();
    }

    //初始化home数据
    private void initHomeData(List<SwitchItem> miconList){

        if (homeAttr.getAlarmclock().getActive()==1){
            SwitchItem icon = new SwitchItem("闹钟");
            miconList.add(icon);
        }if (homeAttr.getCamera().getActive()==1){
            SwitchItem icon = new SwitchItem("摄像机",R.drawable.monitor);
            miconList.add(icon);
        }
        if (homeAttr.getCinemaroom().getActive()==1){
            SwitchItem icon = new SwitchItem("影音室");
            miconList.add(icon);
        }if (homeAttr.getEzvizCamera().getActive()==1){
            SwitchItem icon = new SwitchItem("萤石");
            miconList.add(icon);
        }if (homeAttr.getHiddendoor().getActive()==1){
            SwitchItem icon = new SwitchItem("影藏门");
            miconList.add(icon);
        }if (homeAttr.getLock().getActive()==1){
            SwitchItem icon = new SwitchItem("门锁",R.drawable.door_lock_off);
            miconList.add(icon);
        }if (homeAttr.getMusic().getActive()==1){
            SwitchItem icon = new SwitchItem("音乐",R.drawable.music_off);
            miconList.add(icon);
        }if (homeAttr.getOutdoorwaterflow().getActive()==1){
            SwitchItem icon = new SwitchItem("户外喷泉");
            miconList.add(icon);
        }if (homeAttr.getSecurityalarm().getActive()==1){
            SwitchItem icon = new SwitchItem("安防",R.drawable.security_off);
            miconList.add(icon);
        }
    }

    private void initKeTingData(){
        //初始化默认客厅的数据
        if (homeAttr.getRooms().get(0).getRoomAttr().getAircondition().getActive()==1){
            SwitchItem icon = new SwitchItem("客厅空调");
            iconList.add(icon);
        }if (homeAttr.getRooms().get(0).getRoomAttr().getCamera_indoor().getActive()==1){
            SwitchItem icon = new SwitchItem("客厅摄像头");
            iconList.add(icon);
        }if (homeAttr.getRooms().get(0).getRoomAttr().getCurtain().getActive()==1){
            SwitchItem icon = new SwitchItem("客厅窗帘");
            iconList.add(icon);
        }if (homeAttr.getRooms().get(0).getRoomAttr().getIbeacon().getActive()==1){
            SwitchItem icon = new SwitchItem("客厅Ibeacon");
            iconList.add(icon);
        }if (homeAttr.getRooms().get(0).getRoomAttr().getLight().getActive()==1){
            SwitchItem icon = new SwitchItem("客厅灯");
            iconList.add(icon);
        }if (homeAttr.getRooms().get(0).getRoomAttr().getLightbelt().getActive()==1){
            SwitchItem icon = new SwitchItem("客厅灯带");
            iconList.add(icon);
        }if (homeAttr.getRooms().get(0).getRoomAttr().getPanel().getActive()==1){
            SwitchItem icon = new SwitchItem("客厅面板");
            iconList.add(icon);
        }if (homeAttr.getRooms().get(0).getRoomAttr().getSence().getActive()==1){
            SwitchItem icon = new SwitchItem("客厅Sence");
            iconList.add(icon);
        }
    }

    private void initView() {
        tv_rooms = (TextView) findViewById(R.id.tv_rooms);
        tv_rooms.setOnClickListener(openPopwindow);
        gridView = (GridView) findViewById(R.id.id_grid);
        adapter = new MyGridAdapter(this,iconList);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(itemListener);

    }

    //----------------------------弹出popupWindow--------------------------
    View.OnClickListener openPopwindow = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            initPopupWindow();//初始化popupWindow
            //initRecyclerView();//初始化recyclerView
        }
    };

    private void initPopupWindow() {
        Log.i("加载popupwindow", "test");
        final View popupwindow = View.inflate(this, R.layout.popupwindow_wheel, null);
        wv_rooms = (WheelView) popupwindow.findViewById(R.id.wheel);
        wv_rooms.setData(roomNames);
        wv_rooms.setDefault(0);
        wv_rooms.setOnSelectListener(new WheelView.OnSelectListener() {
            @Override
            public void endSelect(int id, String text) {
                if (TextUtils.isEmpty(text)) {
                    return;
                }
            }

            @Override
            public void selecting(int id, String text) {

            }
        });
        //点击确定后把数据加载到界面
        btn_sure = (Button) popupwindow.findViewById(R.id.btn_sure);
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedroomName = wv_rooms.getSelectedText();
                tv_rooms.setText(selectedroomName);
                switchRoom(selectedroomName);//选择房间
                popupWindow.dismiss();
                iconList.clear();
                iconList.addAll(beforeIconList);
                adapter.notifyDataSetChanged();//刷新adapter

            }
        });
        //初始化一个popupWindows
        popupWindow = new PopupWindow(popupwindow, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setFocusable(true);
        //设置popupWindow弹出窗体的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(false);
        popupWindow.showAtLocation(tv_rooms, Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);


    }

    /*//用recyclerview时的popupwindow初始化
    private void initPopupWindow() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.popupwindow_layout,null);
        popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
        popupWindow.setContentView(contentView);
        recyclerView = (RecyclerView) contentView.findViewById(R.id.id_recycler);

        popupWindow.setFocusable(true);
        Drawable popBackGround = getResources().getDrawable(R.color.popBackGround);
        popupWindow.setBackgroundDrawable(popBackGround);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(tv_rooms, Gravity.BOTTOM, 0, 0);//设置在哪显示
    }*/

   /* private void initRecyclerView(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        myRecyclerAdapter = new MyRecyclerAdapter(this,roomNameList);
        recyclerView.setAdapter(myRecyclerAdapter);
        //滚动事件监听
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mBDownStep.setEnabled(false);

                // 效果在暂停时显示, 否则会导致重绘异常
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    myRecyclerAdapter.highlightItem(getMiddlePosition());
                    recyclerView.scrollToPosition(getScrollPosition());
                    mLastValue = getMiddlePosition();
                    UserInfoManager.setAge(getMiddlePosition() + 2);

                    mBDownStep.setEnabled(true); // 滑动时不可用, 停止时才可以
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // 值是实时增加
                mTvAgeValue.setText(String.valueOf(getMiddlePosition() + 2));
            }
        });
        myRecyclerAdapter.highlightItem(getMiddlePosition());
    }

    *//**
     * 获取中间位置
     *
     * @return 当前值
     *//*
    private int getMiddlePosition() {
        return getScrollPosition() + (myRecyclerAdapter.ITEM_NUM / 2);
    }

    *//**
     * 获取滑动值, 滑动偏移 / 每个格子宽度
     *
     * @return 当前值
     *//*
    private int getScrollPosition() {
        return (int) ((double) recyclerView.computeHorizontalScrollOffset()
                / (double) myRecyclerAdapter.getItemStdWidth());
    }
*/

    private void switchRoom(String selectedroomName){
        int index = 0;
        //房间信息数组
        String[] roomNames = new String[homeAttr.getRooms().size()];
        Map roomMap = new HashMap();
        for (int i=0;i<homeAttr.getRooms().size();i++){
            roomNames[i] = homeAttr.getRooms().get(i).getRoomName();
            Log.i(TAG, "initData: "+roomNames[i]);
            roomMap.put(roomNames[i],i);
        }
        switch (selectedroomName){
            case KETING:
                index = (int) roomMap.get(KETING);
                getRoomsData(index,KETING);

                Toast.makeText(MainActivity.this, KETING, Toast.LENGTH_SHORT).show();
                break;
            case CANTING:
                index = (int) roomMap.get(CANTING);
                getRoomsData(index,CANTING);
                Toast.makeText(MainActivity.this, CANTING, Toast.LENGTH_SHORT).show();
                break;
            case SHUFANG:
                index = (int) roomMap.get(SHUFANG);
                getRoomsData(index,SHUFANG);
                Toast.makeText(MainActivity.this, SHUFANG, Toast.LENGTH_SHORT).show();
                break;
            case WOSHI:
                index = (int) roomMap.get(WOSHI);
                getRoomsData(index,WOSHI);
                Toast.makeText(MainActivity.this, WOSHI, Toast.LENGTH_SHORT).show();
                break;
            case LINYUJIAN:
                index = (int) roomMap.get(LINYUJIAN);
                getRoomsData(index,LINYUJIAN);
                Toast.makeText(MainActivity.this, LINYUJIAN, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void getRoomsData(int index,String roomName){
        beforeIconList = new ArrayList<>();
        initHomeData(beforeIconList);
        if (homeAttr.getRooms().get(index).getRoomAttr().getLight().getActive()==1){
            SwitchItem icon = new SwitchItem(roomName+"灯");
            beforeIconList.add(icon);
        }if (homeAttr.getRooms().get(index).getRoomAttr().getCurtain().getActive()==1){
            SwitchItem icon = new SwitchItem(roomName+"窗帘");
            beforeIconList.add(icon);
        }if (homeAttr.getRooms().get(index).getRoomAttr().getCamera_indoor().getActive()==1){
            SwitchItem icon = new SwitchItem(roomName+"摄像头");
            beforeIconList.add(icon);
        }if (homeAttr.getRooms().get(index).getRoomAttr().getAircondition().getActive()==1){
            SwitchItem icon = new SwitchItem(roomName+"空调");
            beforeIconList.add(icon);
        }if (homeAttr.getRooms().get(index).getRoomAttr().getIbeacon().getActive()==1){
            SwitchItem icon = new SwitchItem(roomName+"Ibeacon");
            beforeIconList.add(icon);
        }if (homeAttr.getRooms().get(index).getRoomAttr().getLightbelt().getActive()==1){
            SwitchItem icon = new SwitchItem(roomName+"灯带");
            beforeIconList.add(icon);
        }if (homeAttr.getRooms().get(index).getRoomAttr().getPanel().getActive()==1){
            SwitchItem icon = new SwitchItem(roomName+"面板");
            beforeIconList.add(icon);
        }if (homeAttr.getRooms().get(index).getRoomAttr().getSence().getActive()==1){
            SwitchItem icon = new SwitchItem(roomName+"Sence");
            beforeIconList.add(icon);
        }

    }


//-----------------------------item点击事件---------------------------------
    AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TextView tvName = (TextView) view.findViewById(R.id.tv_item_name);
            ImageView ivIcon = (ImageView) view.findViewById(R.id.iv_icon);

            switch (tvName.getText().toString()){
                case MyConstants.EZVIZ:
                    Toast.makeText(MainActivity.this, "萤石", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.LOCK:
                    Toast.makeText(MainActivity.this, "门锁", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.HIDDENDOOR:
                    Toast.makeText(MainActivity.this, "影藏门", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.CAMERA:
                    Toast.makeText(MainActivity.this, "摄像机", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.SECURITYALARM:
                    Toast.makeText(MainActivity.this, "安防", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.ALARMCLOCK:
                    Toast.makeText(MainActivity.this, "闹钟", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.OUTDOORWATERFLOW:
                    Toast.makeText(MainActivity.this, "户外喷泉", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.CINEMAROOM:
                    Toast.makeText(MainActivity.this, "影音室", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.MUSIC:
                    Toast.makeText(MainActivity.this, "音乐", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.KETINGPANEL:
                    Toast.makeText(MainActivity.this, "客厅面板", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.KETINGIBEACON:
                    Toast.makeText(MainActivity.this, "客厅ibeacon", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.KETINGCAMERA_INDOOR:
                    Toast.makeText(MainActivity.this, "客厅摄像头", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.KETINGAIRCONDITION:
                    Toast.makeText(MainActivity.this, "客厅空调", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.KETINGCURTAIN:
                    Toast.makeText(MainActivity.this, "客厅窗帘", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.KETINGLIGHT:
                    Toast.makeText(MainActivity.this, "客厅灯", Toast.LENGTH_SHORT).show();

                    //测试控制灯
                    if (flag == 0) {
                        ivIcon.setImageResource(R.drawable.lights_on);
                        JSONObject lightOnCommandData=new JSONObject();
                        JSONObject controlData=new JSONObject();
                        try {
                            lightOnCommandData.put("controlProtocol","hdl");
                            lightOnCommandData.put("machineName","light");
                            lightOnCommandData.put("controlData",controlData);
                            controlData.put("lightValue","100");
                            controlData.put("isServerAUTO","0");
                            lightOnCommandData.put("controlSence","all");
                            lightOnCommandData.put("houseDBName","keting");
                            String  lightJson=CommandJsonUtils.getCommandJson(0,lightOnCommandData,hid,uname,pwd, String.valueOf(System.currentTimeMillis()));
                            tcplongSocket.writeDate(Encrypt.encrypt(lightJson));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        flag = 1;
                    } else if (flag == 1) {
                        ivIcon.setImageResource(R.drawable.lights_off);
                        JSONObject lightOffCommandData=new JSONObject();
                        JSONObject controlData=new JSONObject();
                        try {
                            lightOffCommandData.put("controlProtocol","hdl");
                            lightOffCommandData.put("machineName","light");
                            lightOffCommandData.put("controlData",controlData);
                            controlData.put("lightValue","0");
                            controlData.put("isServerAUTO","0");
                            lightOffCommandData.put("controlSence","all");
                            lightOffCommandData.put("houseDBName","keting");
                            String  lightJson=CommandJsonUtils.getCommandJson(0,lightOffCommandData,hid,uname,pwd, String.valueOf(System.currentTimeMillis()));
                            tcplongSocket.writeDate(Encrypt.encrypt(lightJson));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        flag = 0;
                    }
                    break;
                case MyConstants.KETINGLIGHTBELT:
                    Toast.makeText(MainActivity.this, "客厅灯带", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.KETINGSENCE:
                    Toast.makeText(MainActivity.this, "客厅Sence", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.CANTINGAIRCONDITION:
                    Toast.makeText(MainActivity.this, "餐厅空调", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.CANTINGCAMERA_INDOOR:
                    Toast.makeText(MainActivity.this, "餐厅摄像头", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.CANTINGCURTAIN:
                    Toast.makeText(MainActivity.this, "餐厅窗帘", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.CANTINGIBEACON:
                    Toast.makeText(MainActivity.this, "餐厅Ibeacon", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.CANTINGLIGHT:
                    Toast.makeText(MainActivity.this, "餐厅灯", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.CANTINGLIGHTBELT:
                    Toast.makeText(MainActivity.this, "餐厅灯带", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.CANTINGPANEL:
                    Toast.makeText(MainActivity.this, "餐厅面板", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.CANTINGSENCE:
                    Toast.makeText(MainActivity.this, "餐厅Sence", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.SHUFANGAIRCONDITION:
                    Toast.makeText(MainActivity.this, "书房空调", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.SHUFANGCAMERA_INDOOR:
                    Toast.makeText(MainActivity.this, "书房摄像头", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.SHUFANGCURTAIN:
                    Toast.makeText(MainActivity.this, "书房窗帘", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.SHUFANGIBEACON:
                    Toast.makeText(MainActivity.this, "书房Ibeacon", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.SHUFANGLIGHT:
                    Toast.makeText(MainActivity.this, "书房灯", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.SHUFANGLIGHTBELT:
                    Toast.makeText(MainActivity.this, "书房灯带", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.SHUFANGPANEL:
                    Toast.makeText(MainActivity.this, "书房面板", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.SHUFANGSENCE:
                    Toast.makeText(MainActivity.this, "书房Sence", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.WOSHIAIRCONDITION:
                    Toast.makeText(MainActivity.this, "卧室空调", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.WOSHICAMERA_INDOOR:
                    Toast.makeText(MainActivity.this, "卧室摄像头", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.WOSHICURTAIN:
                    Toast.makeText(MainActivity.this, "卧室窗帘", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.WOSHIIBEACON:
                    Toast.makeText(MainActivity.this, "卧室Ibeacon", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.WOSHILIGHT:
                    Toast.makeText(MainActivity.this, "卧室灯", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.WOSHILIGHTBELT:
                    Toast.makeText(MainActivity.this, "卧室灯带", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.WOSHIPANEL:
                    Toast.makeText(MainActivity.this, "卧室面板", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.WOSHISENCE:
                    Toast.makeText(MainActivity.this, "卧室Sence", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.LINYUJIANAIRCONDITION:
                    Toast.makeText(MainActivity.this, "淋浴间空调", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.LINYUJIANCAMERA_INDOOR:
                    Toast.makeText(MainActivity.this, "淋浴间摄像头", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.LINYUJIANCURTAIN:
                    Toast.makeText(MainActivity.this, "淋浴间窗帘", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.LINYUJIANIBEACON:
                    Toast.makeText(MainActivity.this, "淋浴间Ibeacon", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.LINYUJIANLIGHT:
                    Toast.makeText(MainActivity.this, "淋浴间灯", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.LINYUJIANLIGHTBELT:
                    Toast.makeText(MainActivity.this, "淋浴间灯带", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.LINYUJIANPANEL:
                    Toast.makeText(MainActivity.this, "淋浴间面板", Toast.LENGTH_SHORT).show();
                    break;
                case MyConstants.LINYUJIANSENCE:
                    Toast.makeText(MainActivity.this, "淋浴间Sence", Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };

}
