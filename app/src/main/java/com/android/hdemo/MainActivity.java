package com.android.hdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.android.hutils.item.SimpleAdapter;
import com.android.hutils.item.SimpleItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    List<SimpleItem> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.list);
        mList = new ArrayList<>();
        for(int i=0;i<20;i++){
            mList.add(new Item1("姓名"+i));
        }
        for(int i=0;i<30;i++){
            mList.add(new Item2("地址"+i));
        }
        for(int i=0;i<20;i++){
            mList.add(new Item1("姓名"+i));
        }
        listView.setAdapter(new SimpleAdapter(this,mList));
    }
}
