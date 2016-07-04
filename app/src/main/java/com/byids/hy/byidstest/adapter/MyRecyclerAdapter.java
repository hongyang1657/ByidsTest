package com.byids.hy.byidstest.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.byids.hy.byidstest.R;
import com.byids.hy.byidstest.activity.MainActivity;

import java.util.List;

/**
 * Created by hy on 2016/6/30.
 */
public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder>{

    Context mContext;
    private String[] mlist;
    private LayoutInflater inflater;

    public static final int ITEM_NUM = 5; // 每行拥有的Item数, 必须是奇数
    private int mFrom; // 起始
    private int mTo; // 终止
    private int mHighlight = -1; // 高亮

    public MyRecyclerAdapter(Context context,String[] roomList) {
        this.mContext = context;
        mlist = new String[(roomList.length)*10];
        for (int i=0;i<(roomList.length)*10;i++){
            mlist[i] = roomList[i%roomList.length];
        }
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_layout,parent, false);
        // 设置Item的宽度
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        lp.width = getItemStdWidth();

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvRoomName.setText(mlist[position]);
        // 高亮显示
        if (isSelected(position)) {
            holder.tvRoomName.setTextSize(24);
            holder.tvRoomName.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        } else {
            holder.tvRoomName.setTextSize(16);

        }
    }

    @Override
    public int getItemCount() {
        return mlist.length;
    }

    // 高亮中心, 更新前后位置
    public void highlightItem(int position) {
        mHighlight = position;
        int offset = ITEM_NUM / 2;
        for (int i = position - offset; i <= position + offset; ++i)
            notifyItemChanged(i);
    }

    // 判断是否是高亮
    public boolean isSelected(int position) {
        return mHighlight == position;

    }

    // 获取标准宽度
    public int getItemStdWidth() {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels / ITEM_NUM;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvRoomName;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvRoomName = (TextView) itemView.findViewById(R.id.tv_room_name);
        }
    }
}
