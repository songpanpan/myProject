package com.yueyou.adreader.view.dlg;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.yueyou.adreader.R;

/**
 * Created by zy on 2017/4/20.
 */

public class ProgressDlg extends Dialog{
    private final int MESSAGE_DIAN = 0xec;
    private final int MESSAGE_SHOW = 0xed;
    private final int MESSAGE_HIDE = 0xee;
    private String mTitle;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
        if (msg.what == MESSAGE_SHOW){
            if(getOwnerActivity() != null && !getOwnerActivity().isFinishing()) {
                ((TextView) findViewById(R.id.tost)).setText(mTitle);
                show();
            }
        }else if (msg.what == MESSAGE_HIDE){
            if (getOwnerActivity() != null && !getOwnerActivity().isFinishing() && isShowing())
                dismiss();
        }
        }
    };
    public void show(String toast){
        mTitle = toast;
        mHandler.sendEmptyMessage(MESSAGE_SHOW);
    }

    public void hide(){
        mHandler.sendEmptyMessage(MESSAGE_HIDE);
    }

    public ProgressDlg(Context context) {
        super(context, R.style.dialog);
        setContentView(R.layout.progress_dlg);
        setCanceledOnTouchOutside(false);
        setCancelable(true);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mHandler.removeMessages(MESSAGE_DIAN);
            }
        });
    }
}
