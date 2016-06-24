package com.byids.hy.byidstest;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.byids.hy.byidstest.activity.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends Activity implements SurfaceHolder.Callback{

    String TAG = "result";
    //登录UI
    private ViewPager viewPager;
    private View v1;
    private View v2;
    private ArrayList<View> vlist = new ArrayList<>();
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer player;
    private EditText username;
    private EditText password;
    private String uname;
    private String pwd;
    public ImageView iv_login;
    //
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        requestQueue = Volley.newRequestQueue(LoginActivity.this);
        initView();//初始化view

    }

    private void initView(){
        //初始化surface 播放背景视频
        surfaceView = (SurfaceView) findViewById(R.id.id_sf);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        viewPager = (ViewPager) findViewById(R.id.id_vp);
        v1 = getLayoutInflater().inflate(R.layout.item_login1,null);
        vlist.add(v1);
        v2 = getLayoutInflater().inflate(R.layout.item_login2,null);
        vlist.add(v2);
        //第二页的控件
        iv_login = (ImageView) v2.findViewById(R.id.iv_login);
        username=(EditText)v2.findViewById(R.id.et_username);
        password=(EditText)v2.findViewById(R.id.et_password);
        viewPager.setAdapter(adapter);
    }

    PagerAdapter adapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return vlist.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(vlist.get(position));
            return vlist.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            container.removeView((View) object);
        }
    };

    //surfaceHolder回调
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // 必须在surface创建后才能初始化MediaPlayer,否则不会显示图像
        player = MediaPlayer.create(this,R.raw.loginmovie);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setDisplay(surfaceHolder);
        player.setLooping(true);
        try {
            player.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        player.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (player.isPlaying()){
            player.stop();
        }
        player.release();
    }

    //第一页点击事件
    public void tologin(View view){
        switch (view.getId()){
            case R.id.btn_goon:
                viewPager.setCurrentItem(1);//切换到第二页

        }
    }

    //第二页点击事件
    public void login(View v){
        switch (v.getId()){
            case R.id.iv_login://登录按钮
                uname = username.getText().toString().trim();
                pwd = password.getText().toString().trim();
                Toast.makeText(this, uname + "," + pwd, Toast.LENGTH_SHORT).show();
                if (TextUtils.isEmpty(uname)|| TextUtils.isEmpty(pwd)) {
                    Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    postAndInitData();
                }
        }
    }

    private void postAndInitData(){
        String url="http://115.29.97.189:2737/api/login";
        Map<String,String> map=new HashMap<String, String>();
        map.put("uname", uname);
        map.put("pwd", pwd);
        JSONObject jsonObject = new JSONObject(map);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "onResponse:--- "+response.toString());
                Toast.makeText(LoginActivity.this, "返回的json："+response.toString(), Toast.LENGTH_SHORT).show();
                doJsonParse(response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "onErrorResponse: --"+error.getMessage());
                Toast.makeText(LoginActivity.this, "错误信息:"+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(request);
    }

    //解析服务器返回的json数据
    private void doJsonParse(String jsonData){
        try {
            JSONObject obj1 = new JSONObject(jsonData);
            JSONObject obj2 = obj1.getJSONObject("55f6797364c0ce976beb0110");
            String address = obj2.getString("address");//客户地址
            String comment = obj2.getString("comment");//注释
            String owner = obj2.getString("owner");//所有人
            JSONObject profile = obj2.getJSONObject("profile");
            JSONObject homeAttr = profile.getJSONObject("homeAttr");
            String reboot_cmd = homeAttr.getString("reboot_cmd");//重启指令
            //萤石
            JSONObject ezviz = homeAttr.getJSONObject("ezviz");
            int ezvizActive = ezviz.getInt("active");
            //门锁
            JSONObject lock = homeAttr.getJSONObject("lock");
            int lockActive = lock.getInt("active");
            String lock_protocol = lock.getString("protocol");
            //影藏门
            JSONObject hiddendoor = homeAttr.getJSONObject("hiddendoor");
            int hiddendoorActive = hiddendoor.getInt("active");
            String hiddendoor_protocol = hiddendoor.getString("protocol");
            String hiddendoor_uuid = hiddendoor.getString("ibeacon_uuid");
            int hiddendoor_major = hiddendoor.getInt("ibeacon_major");
            int hiddendoor_minor = hiddendoor.getInt("ibeacon_minor");
            String hiddendoor_pwd = hiddendoor.getString("pwd");
            //摄像机
            JSONObject camera = homeAttr.getJSONObject("camera");
            int cameraActive = camera.getInt("active");
            String camera_ip = camera.getString("pri_ip");
            String camera_domain = camera.getString("pub_domain");
            String camera_uname = camera.getString("uname");
            String camera_pwd = camera.getString("pwd");
            //安防
            JSONObject securityalarm = homeAttr.getJSONObject("securityalarm");
            String securityalarm_protocol = securityalarm.getString("protocol");
            int securityalarm_active = securityalarm.getInt("active");
            //闹钟
            JSONObject alarmclock = homeAttr.getJSONObject("alarmclock");
            int alarmclock_active = alarmclock.getInt("active");
            //户外喷泉
            JSONObject outdoorwaterflow = homeAttr.getJSONObject("outdoorwaterflow");
            String outdoorwaterflow_protocol = outdoorwaterflow.getString("protocol");
            int outdoorwaterflow_active = outdoorwaterflow.getInt("active");
            //影音室
            JSONObject cinemaroom = homeAttr.getJSONObject("cinemaroom");
            int cinemaroom_active = cinemaroom.getInt("active");
            //音乐
            JSONObject music = homeAttr.getJSONObject("music");
            String music_protocol = music.getString("protocol");
            int music_active = music.getInt("active");

            //房间数据
            JSONArray rooms = homeAttr.getJSONArray("rooms");
            Log.i(TAG, "doJsonParse: -----"+rooms.length());
            int roomsNum = rooms.length();//房间数量
            String[] roomNameList = new String[roomsNum];//房间名字数组
            for (int i=0;i<rooms.length();i++){
                JSONObject roomsObj = rooms.getJSONObject(i);
                String roomName = roomsObj.getString("roomName");
                roomNameList[i] = roomName;
            }

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("roomNameList",roomNameList);
            intent.putExtra("","");
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
