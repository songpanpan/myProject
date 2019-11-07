package com.yueyou.adreader.view.dlg;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.yueyou.adreader.R;


/**
 * Created by zy on 2017/4/1.
 */

public class MessageDlg extends Dialog{
    public static void show(Context context, String toast, MessageDlgListener messageDlgListener){
        MessageDlg messageDlg = new MessageDlg(context, toast, false,messageDlgListener);
        messageDlg.show();
    }

    public static void showOnlyOk(Context context, String toast, MessageDlgListener messageDlgListener){
        MessageDlg messageDlg = new MessageDlg(context, toast, true, messageDlgListener);
        messageDlg.show();
    }

    public MessageDlg(Context context, String toast, boolean onlyOk, final MessageDlgListener messageDlgListener) {
        super(context, R.style.dialog);
        setContentView(getLayoutId());
        setCanceledOnTouchOutside(false);
        ((TextView)findViewById(R.id.toast)).setText(toast);
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (messageDlgListener != null) {
                    messageDlgListener.onResult(false);
                }
            }
        });
        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (messageDlgListener != null) {
                    messageDlgListener.onResult(true);
                }
            }
        });
        if (onlyOk){
            findViewById(R.id.cancel).setVisibility(View.GONE);
            setCancelable(false);
        }
    }

    protected int getLayoutId(){
        return R.layout.message_dlg;
    }

    public interface MessageDlgListener {
        void onResult(boolean result);
    }
}
