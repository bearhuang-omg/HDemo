package com.android.hutils.item;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by huangbei on 19-8-17.
 */

public class SimpleAdapter extends BaseAdapter {

    protected Context mContext;
    protected List<SimpleItem> mItems;
    protected Set<Integer> mViewTypeSet;
    protected int mViewTypeCount = 0;

    public SimpleAdapter(Context context, List<SimpleItem> items) {
        mContext = context;
        mItems = items;
        mViewTypeSet = new HashSet<>();
        if (items != null) {
            for (SimpleItem item : items) {
                mViewTypeSet.add(item.getViewType());
            }
        }
    }

    public SimpleAdapter(Context context, List<SimpleItem> items, int viewTypeCount) {
        mContext = context;
        mItems = items;
        if (viewTypeCount >= 1) {
            mViewTypeCount = viewTypeCount;
        }
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        SimpleItem item = (SimpleItem) getItem(position);
        if (convertView == null) {
            convertView = item.getView(mContext);
        }
        if (item != null) {
            item.bindView(mContext, convertView);
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).getViewType();
    }

    @Override
    public int getViewTypeCount() {
        if (mViewTypeCount > 0) {
            return mViewTypeCount;
        } else {
            return mViewTypeSet.size() == 0 ? 1 : mViewTypeSet.size();
        }
    }
}

