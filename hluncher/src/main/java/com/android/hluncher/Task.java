package com.android.hluncher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by huangbei on 19-11-22.
 */

public abstract class Task implements ITask{

    protected CountDownLatch countDownLatch;

    protected List<ITask> dependsOn;

    @Override
    public void addDepends(ITask task) {
        if(dependsOn == null){
            dependsOn = new ArrayList<>();
        }
        dependsOn.add(task);
    }
}
