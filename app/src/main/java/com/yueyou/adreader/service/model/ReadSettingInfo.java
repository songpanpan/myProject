package com.yueyou.adreader.service.model;

import android.content.Context;

import com.yueyou.adreader.service.db.DataSHP;

public class ReadSettingInfo {
    private int fontSize;
    private int lineSpace;
    private int bgColor;
    private int textColor;
    private int barBgColor;
    private boolean isNight;
    private int brightness;
    private int flipPageMode;
    private boolean systemBrightness;
    private int version;
    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public int getLineSpace() {
        return lineSpace;
    }

    public void setLineSpace(int lineSpace) {
        this.lineSpace = lineSpace;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public boolean isNight() {
        return isNight;
    }

    public void setNight(boolean night) {
        isNight = night;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    public int getFlipPageMode() {
        return flipPageMode;
    }

    public void setFlipPageMode(int flipPageMode) {
        this.flipPageMode = flipPageMode;
    }

    public int getBarBgColor() {
        return barBgColor;
    }

    public void setBarBgColor(int barBgColor) {
        this.barBgColor = barBgColor;
    }

    public void save(Context context){
        DataSHP.saveReadSettingInfo(context, this);
    }

    public boolean isSystemBrightness() {
        return systemBrightness;
    }

    public void setSystemBrightness(boolean systemBrightness) {
        this.systemBrightness = systemBrightness;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
