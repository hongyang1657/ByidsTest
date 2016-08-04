package com.byids.hy.byidstest.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.byids.hy.byidstest.Bean.SwitchItem;
import com.byids.hy.byidstest.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hy on 2016/6/24.
 */
public class MyGridAdapter extends BaseAdapter{

    private Context context;
    private LayoutInflater inflater;
    private List<SwitchItem> iconList = new ArrayList<>();

    public MyGridAdapter(Context context,List<SwitchItem> iconList) {
        super();
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.iconList = iconList;
    }

    @Override
    public int getCount() {
        if (iconList==null){
            return 0;
        }else if (iconList.size()%3==0){
            return iconList.size()+3;
        }else if (iconList.size()%3==1){
            return iconList.size()+5;
        }else if (iconList.size()%3==2){
            return iconList.size()+4;
        }else return 0;

    }

    @Override
    public Object getItem(int position) {
        return iconList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView==null){
            convertView = inflater.inflate(R.layout.grid_item,null);
            holder = new ViewHolder();
            holder.linearLayout = (LinearLayout) convertView.findViewById(R.id.linear_do_color);
            holder.imageView = (ImageView) convertView.findViewById(R.id.iv_icon);
            holder.textView = (TextView) convertView.findViewById(R.id.tv_item_name);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        Log.i("result", "getView:--------------- "+iconList.size());
        Log.i("result", "getView:--------------------- "+position);
        holder.imageView.setImageResource(iconList.get(position).getIconImageId());
        holder.textView.setText(iconList.get(position).getItemName());
        if (position%2==0){
            holder.linearLayout.setBackgroundResource(R.color.mainBackGround1);
            holder.imageView.setBackgroundResource(R.color.mainBackGround1);
        }else if (position%2==1){
            holder.linearLayout.setBackgroundResource(R.color.mainBackGround2);
            holder.imageView.setBackgroundResource(R.color.mainBackGround2);
        }
        return convertView;
    }

    class ViewHolder{
        private ImageView imageView;
        private TextView textView;
        private LinearLayout linearLayout;
    }
}
