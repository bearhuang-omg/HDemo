package com.android.hutils.item;

import android.content.Context;
import android.view.View;

/**
 * Created by huangbei on 19-8-17.
 */

public interface SimpleItem {

    public void bindView(Context context, View convertView);

    public View getView(Context context);

    public int getViewType();
}
