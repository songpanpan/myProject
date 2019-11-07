package com.yueyou.adreader.service;

import android.content.Context;

import com.yueyou.adreader.BuildConfig;
import com.yueyou.adreader.activity.base.BaseActivity;
import com.yueyou.adreader.view.dlg.ProgressDlg;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by zy on 2017/3/30.
 */

public class HttpEngine {
    private static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");
    private OkHttpClient mOkHttpClient;
    private HttpEngineListener mHttpEngineListener;

    public void init(HttpEngineListener httpEngineListener) {
        try {
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS);
            if (BuildConfig.DEBUG) {
                builder.addInterceptor(new LoggingInterceptor());
            }
            mOkHttpClient =
//                    new OkHttpClient();
                    builder.build();
            mHttpEngineListener = httpEngineListener;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object postRequest(Context ctx, String url, Map<String, String> params, List<String> imgList, boolean showProgress) {
        try {
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            for (int i = 0; imgList != null && i < imgList.size(); i++) {
                File f = new File(imgList.get(i));
                String key = "img";
//                if (imgList.size() > 1)
//                    key += i;
                builder.addFormDataPart(key, imgList.get(i), RequestBody.create(MEDIA_TYPE_JPG, f));
            }
            if (params != null && params.size() >= 0) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    builder.addFormDataPart(entry.getKey(), entry.getValue());
                }
            } else {
                builder.addFormDataPart("tmp", "");
            }
            MultipartBody requestBody = builder.build();
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            return executeSync(ctx, request, showProgress);
        } catch (Exception e) {
        }
        return null;
    }

    public Object getRequest(Context ctx, String url, boolean showProgress) {
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            return executeSync(ctx, request, showProgress);
        } catch (Exception e) {
            return null;
        }
    }

    public Object getImgRequest(Context ctx, String url, boolean showProgress) {
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            return executeImgSync(ctx, request, showProgress);
        } catch (Exception e) {
            return null;
        }
    }

    private Object executeImgSync(final Context ctx, okhttp3.Request request, final boolean showProgress) {
        if (showProgress) {
            ((BaseActivity) ctx).progressDlg().show("正在请求数据，请稍后！");
        }
        Call call = mOkHttpClient.newCall(request);
        try {
            Response response = call.execute();
            if (!response.isSuccessful()) {
                return null;
            }
//            String contentType = response.header("Content-Type");
//            if (contentType != null && contentType.contains("application/json")) {
//                return response.body().string();
//            }
//            if (contentType != null && contentType.contains("text/plain")) {
//                return response.body().string();
//            }
//            if (contentType != null && contentType.toLowerCase().contains("image/jpeg")) {
//                return response.body().bytes();
//            }
            return response.body().bytes();
        } catch (IOException e) {
            return null;
        } finally {
            if (showProgress) {
                ((BaseActivity) ctx).progressDlg().hide();
            }
        }
    }

    private Object executeSync(final Context ctx, okhttp3.Request request, final boolean showProgress) {
        if (showProgress) {
            ((BaseActivity) ctx).progressDlg().show("正在请求数据，请稍后！");
        }
        Call call = mOkHttpClient.newCall(request);
        try {
            Response response = call.execute();
            if (!response.isSuccessful()) {
                return null;
            }
            String contentType = response.header("Content-Type");
            if (contentType != null && contentType.contains("application/json")) {
                return response.body().string();
            }
            if (contentType != null && contentType.contains("text/plain")) {
                return response.body().string();
            }
            if (contentType != null && contentType.toLowerCase().contains("image/jpeg")) {
                return response.body().bytes();
            }
            return response.body().bytes();
        } catch (IOException e) {
            return null;
        } finally {
            if (showProgress) {
                ((BaseActivity) ctx).progressDlg().hide();
            }
        }
    }

    public void postRequest(Context ctx, String url, Map<String, String> params, Object userData, boolean showProgress) {
        try {
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            if (params != null && params.size() >= 0) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    builder.addFormDataPart(entry.getKey(), entry.getValue());
                }
            } else {
                builder.addFormDataPart("tmp", "");
            }
            MultipartBody requestBody = builder.build();
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            if (showProgress) {
                execute(ctx, request, userData, true);
            } else {
                execute(ctx, request, userData);
            }
        } catch (Exception e) {
        }
    }

    private void execute(final Context ctx, okhttp3.Request request, Object userData) {
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call var1, IOException var2) {
                mHttpEngineListener.onResult(ctx, false, null, userData, false);
            }

            @Override
            public void onResponse(Call var1, Response var2) throws IOException {
                try {
                    String url = request.url().url().toString();
                    if (var2.isSuccessful()) {
                        String contentType = var2.header("Content-Type");
                        if (contentType != null && contentType.equalsIgnoreCase("image/jpeg")) {
                            mHttpEngineListener.onResult(ctx, true, var2.body().bytes(), userData, false);
                        } else {
                            String result = var2.body().string();
                            mHttpEngineListener.onResult(ctx, true, result, userData, false);
                        }
                    } else {
                        mHttpEngineListener.onResult(ctx, false, null, userData, false);
                    }
                } catch (Exception e) {
                    mHttpEngineListener.onResult(ctx, false, null, userData, false);
                }
            }
        });
    }

    private void execute(final Context ctx, okhttp3.Request request, Object userData, boolean showProgress) {
        final ProgressDlg progressDlg = new ProgressDlg(ctx);
        if (showProgress) {
            progressDlg.show("正在请求数据，请稍后！");
        }
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call var1, IOException var2) {
                if (showProgress) {
                    progressDlg.dismiss();
                }
                mHttpEngineListener.onResult(ctx, false, null, userData, showProgress);
            }

            @Override
            public void onResponse(Call var1, Response var2) throws IOException {
                if (showProgress) {
                    progressDlg.dismiss();
                }
                try {
                    String url = request.url().url().toString();
                    if (var2.isSuccessful()) {
                        String contentType = var2.header("Content-Type");
                        if (contentType != null && contentType.equalsIgnoreCase("image/jpeg")) {
                            mHttpEngineListener.onResult(ctx, true, var2.body().bytes(), userData, showProgress);
                        } else {
                            String result = var2.body().string();
                            mHttpEngineListener.onResult(ctx, true, result, userData, showProgress);
                        }
                    } else {
                        mHttpEngineListener.onResult(ctx, false, null, userData, showProgress);
                    }
                } catch (Exception e) {
                    mHttpEngineListener.onResult(ctx, false, null, userData, showProgress);
                }
            }
        });
    }

    private static LoggingInterceptor loggingInterceptor = new LoggingInterceptor();

    private static OkHttpClient okHttpClientGet ;

    public static void get(Context ctx, String url, HttpEngineListener httpEngineListener, boolean showProgress) {
        ProgressDlg progressDlg = new ProgressDlg(ctx);
        if (showProgress) {
            progressDlg.show("正在请求数据，请稍后！");
        }
        if(okHttpClientGet==null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.addInterceptor(loggingInterceptor);
            okHttpClientGet = builder.build() ;
        }
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = okHttpClientGet.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call var1, IOException var2) {
                if (showProgress) {
                    progressDlg.dismiss();
                }
                if (httpEngineListener != null)
                    httpEngineListener.onResult(null, false, null, null, showProgress);
            }

            @Override
            public void onResponse(Call var1, Response var2) {
                if (showProgress) {
                    progressDlg.dismiss();
                }
                try {
                    if (var2.isSuccessful()) {
                        String contentType = var2.header("Content-Type");
                        if (contentType != null && contentType.equalsIgnoreCase("image/jpeg")) {
                            if (httpEngineListener != null)
                                httpEngineListener.onResult(null, true, var2.body().bytes(), null, showProgress);
                        } else {
                            String result = var2.body().string();
                            if (httpEngineListener != null)
                                httpEngineListener.onResult(null, true, result, null, showProgress);
                        }
                    } else {
                        if (httpEngineListener != null)
                            httpEngineListener.onResult(null, false, null, null, showProgress);
                    }
                } catch (Exception e) {
                    if (httpEngineListener != null)
                        httpEngineListener.onResult(null, false, e.getMessage(), null, showProgress);
                }
            }
        });
    }

    public interface HttpEngineListener {
        void onResult(Context ctx, boolean isSuccessed, Object resultData, Object userData, boolean showProgress);
    }
}
