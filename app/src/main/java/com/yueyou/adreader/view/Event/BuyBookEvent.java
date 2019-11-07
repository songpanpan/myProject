package com.yueyou.adreader.view.Event;

import com.yueyou.adreader.service.Action;

public class BuyBookEvent {
    public interface BuyBookEventListener{
        void buyBook();
    }
    private static BuyBookEventListener mBuyBookEventListener;
    public static void setEventListener(BuyBookEventListener buyBookEventListener){
        mBuyBookEventListener = buyBookEventListener;
    }

    public static void buyBook(boolean autoBuy) {
        UserEvent.getInstance().buy();
        Action.getInstance().setAutoBuy(autoBuy);
        mBuyBookEventListener.buyBook();
    }
}
