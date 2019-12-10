package com.android.hdemo.test.presenter;

/**
 * Created by huangbei on 19-11-29.
 */

public abstract class BasePresenter<M,V> {

    public M mModel;
    public V mView;

    public void attachModelView(M pModel, V pView) {
        this.mView = pView;
        this.mModel = pModel;
    }


    public V getView() {
        if (isAttach()) {
            return mView;
        } else {
            return null;
        }
    }

    public boolean isAttach() {
        return null != mView;
    }


    public void onDettach() {
        mView = null;
    }

}
