package com.yueyou.adreader.view.ReaderPage;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yueyou.adreader.R;
import com.yueyou.adreader.activity.ReadActivity;
import com.yueyou.adreader.service.db.DataSHP;
import com.yueyou.adreader.service.model.ReadSettingInfo;
import com.yueyou.adreader.util.Widget;
import com.yueyou.adreader.view.ToolBar;

public class ReadMenu extends LinearLayout implements SeekBar.OnSeekBarChangeListener {
    private MenuListener mMenuListener;
    private ReadSettingInfo mReadSettingInfo;
    Activity activity;

    public ReadMenu(final Context context) {
        super(context);
    }

    public ReadMenu(final Context context, final AttributeSet set) {
        super(context, set);
        mMenuListener = (MenuListener) context;
        if (context instanceof Activity) {
            this.activity = (Activity) context;
        }
        ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.read_menu, this);
        findViewById(R.id.back).setOnClickListener(mOnClickListener);
        findViewById(R.id.book_mark).setOnClickListener(mOnClickListener);
        findViewById(R.id.chapter).setOnClickListener(mOnClickListener);
        findViewById(R.id.brightness).setOnClickListener(mOnClickListener);
        findViewById(R.id.night).setOnClickListener(mOnClickListener);
        findViewById(R.id.option).setOnClickListener(mOnClickListener);
        findViewById(R.id.pre_chapter).setOnClickListener(mOnClickListener);
        findViewById(R.id.next_chapter).setOnClickListener(mOnClickListener);
        findViewById(R.id.flip_null).setOnClickListener(mOnClickListener);
        findViewById(R.id.flip_overlay).setOnClickListener(mOnClickListener);
        findViewById(R.id.font_size_dec).setOnClickListener(mOnClickListener);
        findViewById(R.id.font_size_add).setOnClickListener(mOnClickListener);
        findViewById(R.id.line_space_small).setOnClickListener(mOnClickListener);
        findViewById(R.id.line_space_normal).setOnClickListener(mOnClickListener);
        findViewById(R.id.line_space_large).setOnClickListener(mOnClickListener);
        ((Skin) findViewById(R.id.skin1)).setSkinListener(mSkinListener);
        ((Skin) findViewById(R.id.skin2)).setSkinListener(mSkinListener);
        ((Skin) findViewById(R.id.skin3)).setSkinListener(mSkinListener);
        ((Skin) findViewById(R.id.skin4)).setSkinListener(mSkinListener);
        ((Skin) findViewById(R.id.skin5)).setSkinListener(mSkinListener);
        ((SeekBar) findViewById(R.id.read_progress)).setOnSeekBarChangeListener(this);
        ((SeekBar) findViewById(R.id.brightness_progress)).setOnSeekBarChangeListener(this);
        findViewById(R.id.system_brightness).setOnClickListener(mOnClickListener);
    }

    public void hideNavigationBar() {
        findViewById(R.id.navigation_bar_height).setVisibility(View.GONE);
    }

    public void initSetting() {
        mReadSettingInfo = DataSHP.getReadSettingInfo(getContext());
        if (mReadSettingInfo == null) {
            mReadSettingInfo = new ReadSettingInfo();
            mReadSettingInfo.setVersion(Widget.getAppVersionId(getContext()));
            mReadSettingInfo.setFontSize(22);
            mReadSettingInfo.setLineSpace(40);
            mReadSettingInfo.setNight(false);
            mReadSettingInfo.setFlipPageMode(1);
            mReadSettingInfo.setBgColor(((Skin) findViewById(R.id.skin2)).getBgColor());
            mReadSettingInfo.setTextColor(((Skin) findViewById(R.id.skin2)).getTextColor());
            mReadSettingInfo.setBarBgColor(((Skin) findViewById(R.id.skin2)).getBarBgColor());
            try {
                int systemBrightness = Settings.System.getInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
                mReadSettingInfo.setBrightness(systemBrightness);
                mReadSettingInfo.setSystemBrightness(true);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            if (mReadSettingInfo.getBarBgColor() == 0) {
                mReadSettingInfo.setBgColor(((Skin) findViewById(R.id.skin2)).getBgColor());
                mReadSettingInfo.setTextColor(((Skin) findViewById(R.id.skin2)).getTextColor());
                mReadSettingInfo.setBarBgColor(((Skin) findViewById(R.id.skin2)).getBarBgColor());
            }
            if (mReadSettingInfo.getVersion() < 35) {
                mReadSettingInfo.setFontSize(mReadSettingInfo.getFontSize() + 15);
                mReadSettingInfo.setLineSpace(40);
                mReadSettingInfo.setVersion(Widget.getAppVersionId(getContext()));
                mReadSettingInfo.save(getContext());
            }
        }
        setBrightness(mReadSettingInfo.getBrightness());
        mMenuListener.onClickFont(mReadSettingInfo.getFontSize());
        setLineSpace(mReadSettingInfo.getLineSpace());
        setFontSize(mReadSettingInfo.getFontSize());
        setSkin();
        setFlipPage(mReadSettingInfo.getFlipPageMode());
    }

    public void click() {
        if (isShown()) {
            setVisibility(GONE);
        } else {
            findViewById(R.id.main_menu).setVisibility(VISIBLE);
            findViewById(R.id.navigation_bar_height).setVisibility(View.VISIBLE);
            findViewById(R.id.brightness_menu).setVisibility(GONE);
            findViewById(R.id.option_menu).setVisibility(GONE);
            setVisibility(VISIBLE);
            setSkinView();
            ((SeekBar) findViewById(R.id.read_progress)).setProgress(mMenuListener.getReadProgress());
            if (mReadSettingInfo != null) {
                ((SeekBar) findViewById(R.id.brightness_progress)).setProgress(mReadSettingInfo.getBrightness());
                setLineSpaceView(mReadSettingInfo.getLineSpace());
                ((TextView) findViewById(R.id.font_size)).setText(mReadSettingInfo.getFontSize() + "");
            }
            if (mMenuListener.isMark()) {
                ((ImageView) findViewById(R.id.book_mark)).setBackgroundResource(R.drawable.remove_bookmark);
            } else {
                ((ImageView) findViewById(R.id.book_mark)).setBackgroundResource(R.drawable.add_bookmark);
            }
        }
    }

    public boolean isNight() {
        if (mReadSettingInfo == null) return false;
        return mReadSettingInfo.isNight();
    }

    private Skin.SkinListener mSkinListener = (int bgColor, int textColor, int barBgColor) -> {
        mReadSettingInfo.setNight(false);
        mReadSettingInfo.setBgColor(bgColor);
        mReadSettingInfo.setTextColor(textColor);
        mReadSettingInfo.setBarBgColor(barBgColor);
        mReadSettingInfo.save(getContext());
        setSkin();
        setSkinView();
    };

    private OnClickListener mOnClickListener = (View v) -> {
        switch (v.getId()) {
            case R.id.back:
                mMenuListener.onClickBack();
                break;
            case R.id.book_mark:
                mMenuListener.onClickMark();
                if (activity != null)
                    activity.getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                break;
            case R.id.chapter:
                setVisibility(GONE);
                mMenuListener.onClickChapter();
                break;
            case R.id.brightness:
                findViewById(R.id.main_menu).setVisibility(GONE);
                findViewById(R.id.brightness_menu).setVisibility(VISIBLE);
                if (activity != null)
                    activity.getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                break;
            case R.id.system_brightness:
                mReadSettingInfo.setSystemBrightness(!mReadSettingInfo.isSystemBrightness());
                setBrightness(mReadSettingInfo.getBrightness());
                break;
            case R.id.night:
                mReadSettingInfo.setNight(!mReadSettingInfo.isNight());
                mReadSettingInfo.save(getContext());
                setSkin();
                ReadActivity.setStatusNavBarColor(activity, Color.TRANSPARENT, Color.TRANSPARENT);
                break;
            case R.id.option:
                findViewById(R.id.main_menu).setVisibility(GONE);
                findViewById(R.id.option_menu).setVisibility(VISIBLE);
                if (activity != null)
                    activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//                ReadActivity.setStatusNavBarColor(activity, Color.TRANSPARENT, Color.TRANSPARENT);
                break;
            case R.id.pre_chapter:
                mMenuListener.onClickPreChapter();
                break;
            case R.id.next_chapter:
                mMenuListener.onClickNextChapter();
                break;
            case R.id.flip_null:
                setFlipPage(0);
                break;
            case R.id.flip_overlay:
                setFlipPage(1);
                break;
            case R.id.font_size_dec:
                setFontSize(false);
                break;
            case R.id.font_size_add:
                setFontSize(true);
                break;
            case R.id.line_space_small:
                setLineSpace(20);
                setLineSpaceView(20);
                break;
            case R.id.line_space_normal:
                setLineSpace(40);
                setLineSpaceView(40);
                break;
            case R.id.line_space_large:
                setLineSpace(60);
                setLineSpaceView(60);
                break;
            default:
                break;
        }
    };

    private void refreshSystemBrightnessView(boolean selected) {
        TextView view = findViewById(R.id.system_brightness);
        int bottom = view.getPaddingBottom();
        int top = view.getPaddingTop();
        int right = view.getPaddingRight();
        int left = view.getPaddingLeft();
        if (selected) {
            view.setTextColor(getContext().getResources().getColor(R.color.flip_mode_selected_text_color));
            view.setBackgroundResource(R.drawable.shape_flip_selected);
        } else {
            view.setTextColor(getContext().getResources().getColor(R.color.flip_mode_text_color));
            view.setBackgroundResource(R.drawable.shape_flip);
        }
        view.setPadding(left, top, right, bottom);
    }

    private void setFlipPage(int model) {
        int bottom = findViewById(R.id.flip_null).getPaddingBottom();
        int top = findViewById(R.id.flip_null).getPaddingTop();
        int right = findViewById(R.id.flip_null).getPaddingRight();
        int left = findViewById(R.id.flip_null).getPaddingLeft();
        if (model == 0) {
            ((TextView) findViewById(R.id.flip_null)).setTextColor(getContext().getResources().getColor(R.color.flip_mode_selected_text_color));
            ((TextView) findViewById(R.id.flip_overlay)).setTextColor(getContext().getResources().getColor(R.color.flip_mode_text_color));
            findViewById(R.id.flip_null).setBackgroundResource(R.drawable.shape_flip_selected);
            findViewById(R.id.flip_overlay).setBackgroundResource(R.drawable.shape_flip);
        } else if (model == 1) {
            ((TextView) findViewById(R.id.flip_overlay)).setTextColor(getContext().getResources().getColor(R.color.flip_mode_selected_text_color));
            ((TextView) findViewById(R.id.flip_null)).setTextColor(getContext().getResources().getColor(R.color.flip_mode_text_color));
            findViewById(R.id.flip_overlay).setBackgroundResource(R.drawable.shape_flip_selected);
            findViewById(R.id.flip_null).setBackgroundResource(R.drawable.shape_flip);
        }
        findViewById(R.id.flip_null).setPadding(left, top, right, bottom);
        findViewById(R.id.flip_overlay).setPadding(left, top, right, bottom);
        mMenuListener.onFlipPageModel(model);
        mReadSettingInfo.setFlipPageMode(model);
        mReadSettingInfo.save(getContext());
    }

    private void setSkin() {
        if (mReadSettingInfo.isNight()) {
            mMenuListener.onClickSkin(((Skin) findViewById(R.id.skin_night)).getBgColor(),
                    ((Skin) findViewById(R.id.skin_night)).getTextColor(), ((Skin) findViewById(R.id.skin_night)).getBarBgColor(), false);
            ((ToolBar) findViewById(R.id.night)).setTitle(getResources().getString(R.string.read_menu_style_daytime));
            ((ToolBar) findViewById(R.id.night)).setImg(R.drawable.day);
        } else {
            mMenuListener.onClickSkin(mReadSettingInfo.getBgColor(), mReadSettingInfo.getTextColor(), mReadSettingInfo.getBarBgColor(),
                    mReadSettingInfo.getBgColor() == ((Skin) findViewById(R.id.skin2)).getBgColor());
            ((ToolBar) findViewById(R.id.night)).setTitle(getResources().getString(R.string.read_menu_style_night));
            ((ToolBar) findViewById(R.id.night)).setImg(R.drawable.night);
        }
    }

    private void setSkinView() {
        ((Skin) findViewById(R.id.skin1)).setSelected(false);
        ((Skin) findViewById(R.id.skin2)).setSelected(false);
        ((Skin) findViewById(R.id.skin3)).setSelected(false);
        ((Skin) findViewById(R.id.skin4)).setSelected(false);
        ((Skin) findViewById(R.id.skin5)).setSelected(false);
        if (((Skin) findViewById(R.id.skin1)).getBgColor() == mReadSettingInfo.getBgColor()) {
            ((Skin) findViewById(R.id.skin1)).setSelected(true);
        } else if (((Skin) findViewById(R.id.skin3)).getBgColor() == mReadSettingInfo.getBgColor()) {
            ((Skin) findViewById(R.id.skin3)).setSelected(true);
        } else if (((Skin) findViewById(R.id.skin4)).getBgColor() == mReadSettingInfo.getBgColor()) {
            ((Skin) findViewById(R.id.skin4)).setSelected(true);
        } else if (((Skin) findViewById(R.id.skin5)).getBgColor() == mReadSettingInfo.getBgColor()) {
            ((Skin) findViewById(R.id.skin5)).setSelected(true);
        } else ((Skin) findViewById(R.id.skin2)).setSelected(true);
    }

    private void setFontSize(boolean add) {
        int size = mReadSettingInfo.getFontSize();
        if (add && size < 29)
            size++;
        if (!add && size > 15)
            size--;
        mReadSettingInfo.setFontSize(size);
        mReadSettingInfo.save(getContext());
        ((TextView) findViewById(R.id.font_size)).setText(size + "");
        setFontSize(size);
    }

    private void setFontSize(int value) {
        mMenuListener.onClickFont(value);
    }

    private void setLineSpaceView(int value) {
        findViewById(R.id.line_space_small).setBackgroundResource(R.drawable.shape_flip);
        findViewById(R.id.line_space_normal).setBackgroundResource(R.drawable.shape_flip);
        findViewById(R.id.line_space_large).setBackgroundResource(R.drawable.shape_flip);
        if (value == 20) {
            findViewById(R.id.line_space_small).setBackgroundResource(R.drawable.shape_flip_selected);
        } else if (value == 40) {
            findViewById(R.id.line_space_normal).setBackgroundResource(R.drawable.shape_flip_selected);
        } else {
            findViewById(R.id.line_space_large).setBackgroundResource(R.drawable.shape_flip_selected);
        }
    }

    private void setLineSpace(int value) {
        mMenuListener.onClickLine(value);
        mReadSettingInfo.setLineSpace(value);
        mReadSettingInfo.save(getContext());
    }

    private void setBrightness(int value) {
        refreshSystemBrightnessView(mReadSettingInfo.isSystemBrightness());
        if (mReadSettingInfo.isSystemBrightness()) {
            Widget.systemBrightness(getContext());
        } else {
            Widget.setBrightness(getContext(), (float) value / 255);
        }
    }

    @Override
    public void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser)
            return;
        if (seekBar.getId() == R.id.read_progress) {
            mMenuListener.onClickGoto(progress);
        } else if (seekBar.getId() == R.id.brightness_progress) {
            mReadSettingInfo.setSystemBrightness(false);
            setBrightness(progress);
            mReadSettingInfo.setBrightness(progress);
            mReadSettingInfo.save(getContext());
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public interface MenuListener {
        void onClickBack();

        void onClickChapter();

        void onClickMark();

        void onClickGoto(float progress);

        void onClickPreChapter();

        void onClickNextChapter();

        void onClickFont(int value);

        void onClickLine(int value);

        void onClickSkin(int bgColor, int textColor, int barBgColor, boolean parchment);

        void onFlipPageModel(int model);

        int getReadProgress();

        boolean isMark();
    }
}
