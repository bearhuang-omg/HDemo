package com.android.hdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.android.hutils.item.SimpleAdapter;
import com.android.hutils.item.SimpleItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first);

        findViewById(R.id.jump).setOnClickListener((view)->{
           Intent intent = new Intent(this,SecondActivity.class);
           startActivity(intent);
        });
    }
}
