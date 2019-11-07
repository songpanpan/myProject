package com.yueyou.adreader.view.dlg;

import android.content.Context;
import android.widget.TextView;
import com.yueyou.adreader.R;


/**
 * Created by zy on 2017/5/15.
 */

public class TitleMessageDlg extends MessageDlg {
    public static void show(Context context, String title, String toast, MessageDlgListener messageDlgListener){
        TitleMessageDlg titleMessageDlg = new TitleMessageDlg(context, title, toast, false, messageDlgListener);
        titleMessageDlg.show();
    }

    public static void showOnlyOk(Context context, String title, String toast, MessageDlgListener messageDlgListener){
        TitleMessageDlg titleMessageDlg = new TitleMessageDlg(context, title, toast, true, messageDlgListener);
        titleMessageDlg.show();
    }

    private TitleMessageDlg(Context context, String title, String toast, boolean showOnlyOk, final MessageDlgListener messageDlgListener) {
        super(context, toast, showOnlyOk, messageDlgListener);
        ((TextView)findViewById(R.id.title)).setText(title);
    }

    @Override
    protected int getLayoutId(){
        return R.layout.title_message_dlg;
    }
}
