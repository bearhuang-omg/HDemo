package com.android.hluncher;

/**
 * Created by huangbei on 19-11-22.
 */

public interface ITask extends Runnable{

    public void addDepends(ITask task);

    public boolean condition();

    public boolean runOnMainThread();

    public void satisfy();

    public int priority();

}
