package com.android.hdemo;

import android.arch.lifecycle.Lifecycle;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import com.android.hutils.rx.RxDisposableObserver;
import com.android.hutils.rx.RxSchedulers;
import com.android.hutils.rx.RxTaskInfo;

import io.reactivex.Observable;


/**
 * Created by huangbei on 20-1-14.
 */

public class SecondActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second);
        findViewById(R.id.finish).setOnClickListener((view)->{
            finish();
        });

        findViewById(R.id.task).setOnClickListener((view)->{
//            RxTaskInfo taskInfo = RxTaskInfo.normal("test",this, Lifecycle.Event.ON_DESTROY);
            RxTaskInfo taskInfo = RxTaskInfo.immediate("test");
            Observable.fromCallable(()->{
                Log.i("测试","开始callable");
                try{
                    Thread.sleep(5000);
                }catch (Exception e){
                    e.printStackTrace();
                }
                Log.i("测试","结束callable");
                return true;
            }).compose(RxSchedulers.applySchedulers(taskInfo))
                    .subscribeWith(new RxDisposableObserver<Boolean>(){
                        @Override
                        public void onNext(Boolean aBoolean) {
                            super.onNext(aBoolean);
                            Log.i("测试","onnext执行了");
                        }
                    });
        });
    }
}
