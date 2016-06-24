package com.byids.hy.byidstest.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.byids.hy.byidstest.Bean.SwitchIcon;
import com.byids.hy.byidstest.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hy on 2016/6/24.
 */
public class MyGridAdapter extends BaseAdapter{

    private Context context;
    private LayoutInflater inflater;
    private List<SwitchIcon> iconList = new ArrayList<>();

    public MyGridAdapter(Context context,List<SwitchIcon> iconList) {
        super();
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.iconList = iconList;
    }

    @Override
    public int getCount() {
        return iconList==null?0:iconList.size();
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
            holder.imageView = (ImageView) convertView.findViewById(R.id.iv_icon);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        Log.i("result", "getView:------- "+iconList.get(position).getIconImageId());
        holder.imageView.setImageResource(iconList.get(position).getIconImageId());
        return convertView;
    }

    class ViewHolder{
        ImageView imageView;
    }
}
