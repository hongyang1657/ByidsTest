package com.byids.hy.byidstest.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;

import com.byids.hy.byidstest.Bean.SwitchIcon;
import com.byids.hy.byidstest.R;
import com.byids.hy.byidstest.adapter.MyGridAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 主界面
 * Created by hy on 2016/6/23.
 */
public class MainActivity extends Activity{

    private GridView gridView;
    private MyGridAdapter adapter;
    private List<SwitchIcon> iconList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        initView();
    }

    private void initView() {
        iconList = new ArrayList<>();
        SwitchIcon icon = new SwitchIcon(R.drawable.air_conditioning_off);
        iconList.add(icon);
        SwitchIcon icon1 = new SwitchIcon(R.drawable.door_lock_off);
        iconList.add(icon1);
        SwitchIcon icon2 = new SwitchIcon(R.drawable.air_conditioning_off);
        iconList.add(icon2);
        SwitchIcon icon3 = new SwitchIcon(R.drawable.air_conditioning_off);
        iconList.add(icon3);
        SwitchIcon icon4 = new SwitchIcon(R.drawable.air_conditioning_off);
        iconList.add(icon4);
        SwitchIcon icon5 = new SwitchIcon(R.drawable.air_conditioning_off);
        iconList.add(icon5);
        SwitchIcon icon6 = new SwitchIcon(R.drawable.air_conditioning_off);
        iconList.add(icon6);

        gridView = (GridView) findViewById(R.id.id_grid);
        adapter = new MyGridAdapter(this,iconList);
        gridView.setAdapter(adapter);

    }
}
