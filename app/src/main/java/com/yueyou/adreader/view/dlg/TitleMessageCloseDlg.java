package com.yueyou.adreader.view.dlg;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.yueyou.adreader.R;


/**
 * Created by zy on 2017/5/15.
 */

public class TitleMessageCloseDlg {
    public static void show(Context context, String title, String content, TitleMessageCloseDlg.MessageDlgListener messageDlgListener) {
        AlertDialog dialog = new AlertDialog.Builder(context).create() ;
        initView(dialog,title,content,messageDlgListener) ;
    }

    public static void initView(AlertDialog dialog,String title, String content, final TitleMessageCloseDlg.MessageDlgListener messageDlgListener) {
        LayoutInflater inflater = LayoutInflater.from(dialog.getContext());
        View vRoot = inflater.inflate(R.layout.title_message_close_dlg, null);
        TextView txTitle = vRoot.findViewById(R.id.dlg_title);
        TextView txContent = vRoot.findViewById(R.id.dlg_content);
        txTitle.setText(title);
        txContent.setText(content);
        vRoot.findViewById(R.id.dlg_close).setOnClickListener(v -> {
            dialog.dismiss();
            if (messageDlgListener != null) {
                messageDlgListener.onResult(false);
            }
        });
        vRoot.findViewById(R.id.dlg_login).setOnClickListener(v -> {
            dialog.dismiss();
            if (messageDlgListener != null) {
                messageDlgListener.onResult(true);
            }
        });

        dialog.show();
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT) ;
        params.gravity = Gravity.CENTER ;
        dialog.getWindow().addContentView(vRoot,params);
    }

    public interface MessageDlgListener {
        void onResult(boolean result);
    }
}
