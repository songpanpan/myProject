package com.yueyou.adreader.activity;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.yueyou.adreader.R;
import com.yueyou.adreader.activity.refreshload.RefreshLoadLayout;
import com.yueyou.adreader.service.Url;
import com.yueyou.adreader.util.Widget;
import com.yueyou.adreader.view.webview.CustomWebView;
import com.yueyou.adreader.view.webview.PullToRefreshWebView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SearchActivity extends WebViewActivity implements EditText.OnEditorActionListener, TextWatcher {
    private EditText mEditText;
    private View mClearEdit;
    private List<String> mDefaultSearch;
    RefreshLoadLayout refreshLoadLayout;
    @Override
    public void init() {
        setContentView(R.layout.activity_search);
        mDefaultSearch = new ArrayList<>();
        mCustomWebView = ((CustomWebView) findViewById(R.id.webview));
        mCustomWebView.init(this);
        mCustomWebView.loadUrl(Url.URL_SEARCH);
        refreshLoadLayout=findViewById(R.id.rll_sj);
        refreshLoadLayout.setRefreshLoadListener(new RefreshLoadLayout.SimpleRefreshLoadListener(){
            @Override
            public void onRefresh() {
                super.onRefresh();
                mCustomWebView.reload();
                refreshLoadLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLoadLayout.finish();
                    }
                }, 600);

            }
        });
        mEditText = (EditText) findViewById(R.id.search_edit);
        mClearEdit = findViewById(R.id.clear_edit);
        mClearEdit.setVisibility(View.GONE);
        findViewById(R.id.search_back).setOnClickListener(mOnClickListener);
        mClearEdit.setOnClickListener(mOnClickListener);
        mEditText.setOnEditorActionListener(this);
        mEditText.addTextChangedListener(this);
        mEditText.setFocusable(true);
        mEditText.setFocusableInTouchMode(true);
        mEditText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    public void onClickView(View v) {
        if (v.getId() == R.id.search_back) {
            finish();
        } else if (v.getId() == R.id.clear_edit) {
            mEditText.getText().clear();
        }
    }

    @Override
    public void onPageFinished(String title, boolean canGoBack) {
        super.onPageFinished(title, canGoBack);
        mCustomWebView.evaluateJavascript("javascript:getSearchInputWords();", (String value) -> {
            mDefaultSearch.clear();
            value = value.replace("&quot;", "\"");
            value = value.replace("\"[", "[");
            value = value.replace("]\"", "]");
            List<String> tmp = (List<String>) Widget.stringToObject(value, new TypeToken<List<String>>() {
            }.getType());
            if (tmp != null)
                mDefaultSearch.addAll(tmp);
            setDefaultInputWords();
        });
    }

    private void setDefaultInputWords() {
        if (mDefaultSearch.size() == 0) {
            return;
        }
        Random rand = new Random();
        int index = rand.nextInt(mDefaultSearch.size());
        mEditText.setHint(mDefaultSearch.get(index));
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        String word = mEditText.getText().toString();
        word = word.trim();
        if (word.length() == 0) {
            word = (String) mEditText.getHint();
            if (word == null || word.length() == 0) {
                Toast.makeText(SearchActivity.this, "搜索内容不能为空", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        String fun = String.format("javascript:searchWord('%s');", word);
        hideInput();
        mCustomWebView.evaluateJavascript(fun, null);
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (mEditText.getText().toString().length() == 0) {
            mClearEdit.setVisibility(View.GONE);
            setDefaultInputWords();
        } else {
            mClearEdit.setVisibility(View.VISIBLE);
            mHandler.sendEmptyMessageDelayed(1, 500);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeMessages(1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(final Message message) {
            if (mEditText.getText().toString().length() < 2)
                return;
            String word = mEditText.getText().toString();
            word = word.trim();
            if (word.length() == 0) {
                word = (String) mEditText.getHint();
                if (word == null || word.length() == 0) {
                    Toast.makeText(SearchActivity.this, "搜索内容不能为空", Toast.LENGTH_SHORT).show();
                }
            }else{
                String fun = String.format("javascript:searchAssociateWords('%s');", mEditText.getText().toString());
                mCustomWebView.evaluateJavascript(fun, null);
            }
        }
    };

    /**
     * 隐藏键盘
     */
    protected void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

}
