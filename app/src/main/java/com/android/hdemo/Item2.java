package com.android.hdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.android.hutils.item.SimpleItem;
import com.android.hutils.item.SimpleViewHolder;

/**
 * Created by huangbei on 19-8-17.
 */

public class Item2 implements SimpleItem {

    private String mAddress;

    public Item2(String address){
        mAddress = address;
    }

    @Override
    public void bindView(Context context, View convertView) {
        TextView textView = SimpleViewHolder.get(convertView,R.id.text2);
        textView.setText(mAddress);
    }

    @Override
    public View getView(Context context) {
        return LayoutInflater.from(context).inflate(R.layout.list_item2,null);
    }

    @Override
    public int getViewType() {
        return 1;
    }
}
