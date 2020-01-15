package com.android.hdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.hbind.CompilerBindClick;
import com.android.hbind.CompilerBindView;

@CompilerBindView(R.layout.first)
public class MainActivity extends AppCompatActivity {

    @CompilerBindView(R.id.jump)
    public Button jump;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BindHelper.inject(this);
    }

    @CompilerBindClick({R.id.jump,R.id.jump2})
    public void onClick(View view){
        Intent intent = new Intent(this,SecondActivity.class);
        startActivity(intent);
    }
}
