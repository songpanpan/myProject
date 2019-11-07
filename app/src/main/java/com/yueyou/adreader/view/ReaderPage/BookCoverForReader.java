package com.yueyou.adreader.view.ReaderPage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yueyou.adreader.R;
import com.yueyou.adreader.service.db.BookFileEngine;
import com.yueyou.adreader.service.model.BookShelfItem;
import com.yueyou.adreader.util.LogUtil;
import com.yueyou.adreader.util.Utils;

public class BookCoverForReader extends RelativeLayout {

    public BookCoverForReader(final Context context) {
        super(context);
    }

    public BookCoverForReader(final Context context, final AttributeSet set) {
        super(context, set);
        ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.book_cover_for_reader, this);
    }


    public void init(BookShelfItem bookShelfItem, int bgColor, int textColor, int barBgColor, boolean parchment) {
        try {
            TextView tv_bookname, tv_author, tv_copyright, tv_statement;
            tv_bookname = findViewById(R.id.tv_bookname);
            tv_author = findViewById(R.id.tv_author);
            tv_copyright = findViewById(R.id.tv_copyright);
            tv_statement = findViewById(R.id.tv_statement);
            tv_bookname.setTextColor(textColor);
            tv_author.setTextColor(textColor);
            tv_copyright.setTextColor(textColor);
            tv_statement.setTextColor(textColor);
            Glide.with(getContext().getApplicationContext()).load(BookFileEngine.getBookCover(getContext(), bookShelfItem.getBookId())).into((ImageView) (findViewById(R.id.iv_cover)));
            tv_bookname.setText(bookShelfItem.getBookName());
            tv_author.setText(bookShelfItem.getAuthor());
            RelativeLayout rl_root = findViewById(R.id.rl_root);
            RelativeLayout rl_circle = findViewById(R.id.rl_circle);
            rl_root.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtil.e("BookCoverForReader ");
                }
            });
            if (parchment) {
                rl_root.setBackgroundResource(R.drawable.parchment);
                GradientDrawable drawable = new GradientDrawable();
                //设置外形为为矩形
                drawable.setShape(GradientDrawable.RECTANGLE);
                //设置圆角角度
                float cornerSize = Utils.dp2px(getContext(), 10);
                float[] radii = new float[]{cornerSize, cornerSize, cornerSize, cornerSize, cornerSize, cornerSize, cornerSize, cornerSize};
                drawable.setCornerRadii(radii);
                //设置背景色
                drawable.setColor(Color.parseColor("#FFE1D3C2"));
                rl_circle.setAlpha(0.5f);
                rl_circle.setBackground(drawable);

            } else {
                rl_root.setBackgroundColor(bgColor);
                GradientDrawable drawable = new GradientDrawable();
                //设置外形为为矩形
                drawable.setShape(GradientDrawable.RECTANGLE);
                //设置圆角角度
                float cornerSize = Utils.dp2px(getContext(), 10);
                float[] radii = new float[]{cornerSize, cornerSize, cornerSize, cornerSize, cornerSize, cornerSize, cornerSize, cornerSize};
                drawable.setCornerRadii(radii);
                //设置背景色
                drawable.setColor(barBgColor);
                rl_circle.setAlpha(1.0f);
                rl_circle.setBackground(drawable);
            }
            String copyright = bookShelfItem.getCopyrightName();
            String copyrightInfo = "本作品已授权客户端版权与发行";
            if (copyright != null && copyright.length() > 0) {
                copyrightInfo = "本作品由" + copyright + "授权客户端版权与发行";
                tv_copyright.setText(copyrightInfo);
            } else {
                findViewById(R.id.rl_copyright).setVisibility(View.GONE);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
