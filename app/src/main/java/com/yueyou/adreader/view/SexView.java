package com.yueyou.adreader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yueyou.adreader.R;

public class SexView extends LinearLayout {
    private boolean mBoy;
    private SexListener mSexListener;
    public SexView(final Context context) {
        super(context);
    }

    public SexView(final Context context, final AttributeSet set) {
        super(context, set);
        ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.sex, (ViewGroup)this);
        findViewById(R.id.next).setClickable(false);
        findViewById(R.id.boy).setOnClickListener(v->{
            change(true);
        });
        findViewById(R.id.girl).setOnClickListener(v->{
            change(false);
        });
        findViewById(R.id.next).setOnClickListener(v->{
            mSexListener.finish(mBoy);
            setVisibility(GONE);
        });
    }

    public void setListener(SexListener sexListener){
        mSexListener = sexListener;
    }

    private void change(boolean boy){
        mBoy = boy;
        if (boy){
            ((ImageView)findViewById(R.id.boy)).setImageResource(R.drawable.boyed);
            ((ImageView)findViewById(R.id.girl)).setImageResource(R.drawable.girl);
        }else {
            ((ImageView)findViewById(R.id.girl)).setImageResource(R.drawable.girled);
            ((ImageView)findViewById(R.id.boy)).setImageResource(R.drawable.boy);
        }
        mSexListener.finish(mBoy);
        setVisibility(GONE);
        findViewById(R.id.next).setClickable(true);
        ((ImageView)findViewById(R.id.next)).setImageResource(R.drawable.nexted);
    }

    public interface SexListener{
        void finish(boolean boy);
    }
}
