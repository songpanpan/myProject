package com.yueyou.adreader.view.Event;

import com.yueyou.adreader.activity.WebViewActivity;
import com.yueyou.adreader.view.dlg.AlertWindow;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class UserEvent {
    private List<Object> mUserEventListener;
    public static UserEvent mUserEvent;
    public static UserEvent getInstance(){
        if(mUserEvent == null){
            synchronized (UserEvent.class) {
                if(mUserEvent == null){
                    mUserEvent = new UserEvent();
                }
            }
        }
        return mUserEvent;
    }

    public UserEvent() {
        mUserEventListener = new CopyOnWriteArrayList<>();
    }

    public void release() {
        try {
            mUserEventListener.clear();
            mUserEvent = null;
        }catch (Exception e) {

        }
    }

    public void add(Object object) {
        if(get(object) >= 0)
            return;
        mUserEventListener.add(object);
    }

    public void remove(Object object) {
        int index = get(object);
        if (index < 0)
            return;
        mUserEventListener.remove(index);
    }

    private int get(Object object) {
        for (int i = 0; i < mUserEventListener.size(); i++){
            if (mUserEventListener.get(i) == object)
                return i;
        }
        return -1;
    }

    public void loginSuccess() {
        for (Object item : mUserEventListener){
            excec(item, "loginSuccess");
        }
    }

    public void bindSuccess() {
        for (Object item : mUserEventListener){
            excec(item, "bindSuccess");
        }
    }

    public void closeTopWebView() {
        if (mUserEventListener.size() <= 0)
            return;
        Object top = mUserEventListener.get(mUserEventListener.size() - 1);
        if (top instanceof WebViewActivity
                || top instanceof AlertWindow){
            excec(top, "closeView");
        }
    }

    public void disableWebviewRefresh() {
        for (Object item : mUserEventListener){
            excec(item, "disableWebviewRefresh");
        }
    }

    public void pageGoBack() {
        if (mUserEventListener.size() <= 0)
            return;
        Object top = mUserEventListener.get(mUserEventListener.size() - 1);
        if (top instanceof WebViewActivity
                || top instanceof AlertWindow){
            excec(top, "goBack");
        }
    }

    public void buy() {
        for (Object item : mUserEventListener){
            excec(item, "buy");
        }
    }

    public void rechargeSuccess() {
        for (Object item : mUserEventListener){
            excec(item, "rechargeSuccess");
        }
    }

    private void excec(Object object, String methodName, Object... args) {
        Method method = null;
        try {
            method = object.getClass().getMethod(methodName);
            method.invoke(object, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
