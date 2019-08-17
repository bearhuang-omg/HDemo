package com.android.hdemo;

import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.android.hutils.item.SimpleItem;
import com.android.hutils.item.SimpleViewHolder;

/**
 * Created by huangbei on 19-8-17.
 */

public class Item1 implements SimpleItem {

    private String mName;

    public Item1(String name){
        mName = name;
    }

    @Override
    public void bindView(Context context, View convertView) {
        TextView textView = SimpleViewHolder.get(convertView,R.id.text1);
        textView.setText(mName);
    }

    @Override
    public View getView(Context context) {
        return LayoutInflater.from(context).inflate(R.layout.list_item1,null);
    }

    @Override
    public int getViewType() {
        return 0;
    }
}
