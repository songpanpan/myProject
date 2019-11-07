package com.yueyou.adreader.view.webview;

import android.content.Context;
import android.util.AttributeSet;

import com.yueyou.adreader.view.pullToRefresh.PullToRefreshBase;


public class PullToRefreshWebView extends PullToRefreshBase<CustomWebView>
{
    private final PullToRefreshBase.OnRefreshListener defaultOnRefreshListener ;

    public PullToRefreshWebView(final Context context) {
        super(context);
        this.setOnRefreshListener(this.defaultOnRefreshListener = new OnRefreshListener() {
            @Override
            public void onRefresh() {
                CustomWebView webView = PullToRefreshWebView.this.getRefreshableView();
                webView.reload();
            }
        });
    }

    public PullToRefreshWebView(final Context context, final AttributeSet set) {
        super(context, set);
        this.setOnRefreshListener(this.defaultOnRefreshListener = new OnRefreshListener() {
            @Override
            public void onRefresh() {
            	CustomWebView webView = PullToRefreshWebView.this.getRefreshableView();
            	webView.reload();
            }
        });
    }
    
    @Override
    protected CustomWebView createRefreshableView(final Context context, final AttributeSet set) {
        return new CustomWebView(context, set);
    }
    
	@Override
	protected boolean isReadyForPullDown() {
		return refreshableView.getScrollY() == 0;
	}

	@Override
	protected boolean isReadyForPullUp() {
		return refreshableView.getScrollY() >= (refreshableView.getContentHeight() - refreshableView.getHeight());
	}
}
