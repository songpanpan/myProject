package com.yueyou.adreader.view.dlg;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.yueyou.adreader.R;
import com.yueyou.adreader.service.analytics.ThirdAnalytics;


/**
 * Created by zy on 2017/4/20.
 */

public class ToastDlg extends Dialog {
    public static void show(Context context, String toast) {
        try {
            show(context, toast, null);
        } catch (Exception e) {
            ThirdAnalytics.reportError(context, e);
        }
    }

    public static void show(Context context, String toast, final ToastDlgListener toastDlgListener){
        ((Activity)context).runOnUiThread(()->{
            ToastDlg toastDlg = new ToastDlg(context);
            toastDlg.setToast(toast);
            toastDlg.show();
            toastDlg.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    if (toastDlgListener != null){
                        toastDlgListener.onDismiss();
                    }
                }
            });
        });
    }

    public ToastDlg(Context context) {
        super(context, R.style.dialog);
        setContentView(R.layout.toast_dlg);
        setCanceledOnTouchOutside(true);
    }

    public void setToast(String toast){
        ((TextView)findViewById(R.id.tost)).setText(toast);
    }

    @Override
    public void show(){
        super.show();
        mHandler.sendEmptyMessageDelayed(1, 2500);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(final Message message) {
            try {
                dismiss();
            }catch (Exception e){

            }
        }
    };

    public interface ToastDlgListener {
        void onDismiss();
    }
}
