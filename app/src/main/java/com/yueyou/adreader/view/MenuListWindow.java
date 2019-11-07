package com.yueyou.adreader.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.yueyou.adreader.R;


/**
 * Created by zy on 2017/4/17.
 */

public class MenuListWindow{
    protected PopupWindow mPopupWindow;
    protected View mPopupWindowView;
    public static MenuListWindow show(Class classz, Activity activity, View anchorView, String items, String selected, MenuListWindowListener menuListWindowListener){
        try {
            if (classz == null)
                classz = MenuListWindow.class;
            MenuListWindow menuListWindow = (MenuListWindow)classz.newInstance();
            menuListWindow.initWindow(activity, anchorView, items, selected, menuListWindowListener);
            return menuListWindow;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static MenuListWindow show(Activity activity, View anchorView, String items, String selected, MenuListWindowListener menuListWindowListener){
        MenuListWindow menuListWindow = new MenuListWindow();
        menuListWindow.initWindow(activity, anchorView, items, selected, menuListWindowListener);
        return menuListWindow;
    }

    public void dismiss(){
        mPopupWindow.dismiss();
    }

    private void initWindow(Activity activity, View anchorView, String items, String selected, final MenuListWindowListener menuListWindowListener){
        mPopupWindowView = activity.getLayoutInflater().inflate(getLayoutId(), null);
        mPopupWindow = new PopupWindow(mPopupWindowView, ViewGroup.LayoutParams.MATCH_PARENT, windowHeight(anchorView), false);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        setLoaction(activity, anchorView);
        String[] strings = items.split("&");
        addMenuItem(activity, strings, selected, menuListWindowListener);
        mPopupWindowView.findViewById(R.id.popupwindow_bg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuListWindowListener.onResult(null);
                mPopupWindow.dismiss();
            }
        });
        initEvent();
    }

    protected void initEvent(){

    }

    protected int getLayoutId(){
        return R.layout.popupwindow_menu_list;
    }

    protected int windowHeight(View anchorView){
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    protected void setLoaction(Activity activity, View anchorView){
        if (anchorView == null){
            mPopupWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.CENTER_VERTICAL, 0, 0);
        }else {
            mPopupWindow.showAtLocation(anchorView, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
        }
    }

    protected void addMenuItem(Context context, String[] items, String selected, final MenuListWindowListener menuListWindowListener){
        for (int i = 0; i < items.length; i++){
            final String item = items[i];
            View view = LayoutInflater.from(context).inflate(R.layout.menu_list_item, null);
            ((TextView)view.findViewById(R.id.title)).setText(item);
            if (item.equals(selected)){
                ((TextView)view.findViewById(R.id.title)).setTextColor(0xffe8554d);
            }
            view.findViewById(R.id.title).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuListWindowListener.onResult(item);
                    mPopupWindow.dismiss();
                }
            });
            ((LinearLayout)mPopupWindowView.findViewById(R.id.menu_group)).addView(view);
        }
    }

    public interface MenuListWindowListener{
        public void onResult(String title);
    }
}
