package com.yueyou.adreader.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yueyou.adreader.R;
import com.yueyou.adreader.view.ReaderPage.ChapterView;
import com.yueyou.adreader.view.Event.BuyBookEvent;
import com.yueyou.adreader.view.ReaderPage.MarkView;
import com.yueyou.adreader.view.ViewPager.AdapterViewPager;
import com.yueyou.adreader.view.ViewPager.ZYViewPager;

import java.util.ArrayList;

public class ChapterActivity extends com.yueyou.adreader.activity.base.BaseActivity implements BuyBookEvent.BuyBookEventListener {
    private ZYViewPager mViewPager;
    private TextView mTvLine;
    private ChapterView mChapterView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initViewPager();
        findViewById(R.id.chapter_back).setOnClickListener((View) -> {
            finish();
        });
    }

    private void initViewPager() {
        String bookId = getIntent().getStringExtra(ReadActivity.KEY_BOOKID);
        String chapterId = getIntent().getStringExtra(ReadActivity.KEY_CHAPTERID);
        mTvLine = (TextView) findViewById(R.id.tvLine);
        findViewById(R.id.chapter_title).setOnClickListener((View v) -> {
            mViewPager.setCurrentItem(0);
        });
        findViewById(R.id.mark_title).setOnClickListener((View v) -> {
            mViewPager.setCurrentItem(1);
        });
        mViewPager = (ZYViewPager) findViewById(R.id.viewpager);
        ArrayList<View> list = new ArrayList<View>();
        mChapterView = new ChapterView(this, Integer.parseInt(bookId), Integer.parseInt(chapterId));
        list.add(mChapterView);
        list.add(new MarkView(this));
        mViewPager.setAdapter(new AdapterViewPager(list));
        mViewPager.setOffscreenPageLimit(list.size());
        mViewPager.setCurrentItem(0);
        mViewPager.addOnPageChangeListener((ViewPager.OnPageChangeListener) new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int position, float arg1, int arg2) {
                float tagerX;
                if (arg1 == 0 && arg2 == 0) {
                    if (position == 1) {
                        tagerX = getX(R.id.mark_title) - getX(R.id.chapter_title);
                    } else
                        tagerX = 0;
                } else {
                    if (mViewPager.getCurrentItem() == 0) {
                        tagerX = (getX(R.id.mark_title) - getX(R.id.chapter_title)) * arg1;
                    } else {
                        tagerX = (getX(R.id.mark_title) - getX(R.id.chapter_title)) * arg1;
                    }
                }
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mTvLine.getLayoutParams();
                layoutParams.leftMargin = (int) tagerX;
                mTvLine.setLayoutParams(layoutParams);
            }

            @Override
            public void onPageSelected(int position) {

            }
        });
    }

    private int getX(int id) {
        int[] location = new int[2];
        findViewById(id).getLocationInWindow(location);
        return location[0];
    }

    @Override
    public void buyBook() {
        mChapterView.buyBook();
    }
}
