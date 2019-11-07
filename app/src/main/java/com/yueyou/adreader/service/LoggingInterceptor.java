package com.yueyou.adreader.service;


import android.util.Log;

import com.yueyou.adreader.BuildConfig;
import com.yueyou.adreader.util.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;

public class LoggingInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    private void log(String message) {
        Log.i("HTTP", message);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (!BuildConfig.DEBUG) {
            return chain.proceed(request);
        }

        //请求日志拦截
        logForRequest(request, chain.connection());

        //执行请求，计算请求时间
        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            log("<-- HTTP FAILED: " + e);
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        //响应日志拦截
        return logForResponse(response, tookMs);
    }

    private void logForRequest(Request request, Connection connection) throws IOException {
        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;

        try {
            String requestStartMessage = "--> " + request.method() + ' ' + request.url() + ' ' + protocol;
            log(requestStartMessage);

            if (hasRequestBody) {
                // Request body headers are only present when installed as a network interceptor. Force
                // them to be included (when available) so there values are known.
                if (requestBody.contentType() != null) {
                    log("\tContent-Type: " + requestBody.contentType());
                }
                if (requestBody.contentLength() != -1) {
                    log("\tContent-Length: " + requestBody.contentLength());
                }
            }
            Headers headers = request.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                String name = headers.name(i);
                // Skip headers from the request body as they are explicitly logged above.
                if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                    log("\t" + name + ": " + headers.value(i));
                }
            }

            log(" ");
            if (hasRequestBody) {
                if (isPlaintext(requestBody.contentType())) {
                    bodyToString(request);
                } else {
                    log("\tbody: maybe [binary body], omitted!");
                }
            }
        } catch (Exception e) {
            Utils.logError(e, "logForRequest");
        } finally {
            log("--> END " + request.method());
        }
    }

    private Response logForResponse(Response response, long tookMs) {
        Response.Builder builder = response.newBuilder();
        Response clone = builder.build();
        ResponseBody responseBody = clone.body();

        try {
            log("<-- " + clone.code() + ' ' + clone.message() + ' ' + clone.request().url() + " (" + tookMs + "ms）");
            Headers headers = clone.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                log("\t" + headers.name(i) + ": " + headers.value(i));
            }
            log(" ");
            if (HttpHeaders.hasBody(clone)) {
                if (responseBody == null) return response;

                if (isPlaintext(responseBody.contentType())) {
                    byte[] bytes = toByteArray(responseBody.byteStream());
                    MediaType contentType = responseBody.contentType();
                    String body = new String(bytes, getCharset(contentType));
                    log("\tbody:" + body);
                    responseBody = ResponseBody.create(responseBody.contentType(), bytes);
                    return response.newBuilder().body(responseBody).build();
                } else {
                    log("\tbody: maybe [binary body], omitted!");
                }
            }
        } catch (Exception e) {
            Utils.logError(e, "logForResponse");
        } finally {
            log("<-- END HTTP");
        }
        return response;
    }

    private byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        write(input, output);
        output.close();
        return output.toByteArray();
    }

    public void write(InputStream inputStream, OutputStream outputStream) throws IOException {
        int len;
        byte[] buffer = new byte[4096];
        while ((len = inputStream.read(buffer)) != -1) outputStream.write(buffer, 0, len);
    }

    private Charset getCharset(MediaType contentType) {
        Charset charset = contentType != null ? contentType.charset(UTF8) : UTF8;
        if (charset == null) charset = UTF8;
        return charset;
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    private boolean isPlaintext(MediaType mediaType) {
        if (mediaType == null) return false;
        log("MediaType type : " + mediaType.type() + " subType : " + mediaType.subtype());
        if (mediaType.type() != null && mediaType.type().equals("text")) {
            return true;
        }
//        if (mediaType.type() != null && mediaType.type().equals("multipart")) {
//            return true;
//        }
        String subtype = mediaType.subtype();
        if (subtype != null) {
            subtype = subtype.toLowerCase();
            if (subtype.contains("x-www-form-urlencoded") || subtype.contains("json") || subtype.contains("xml") || subtype.contains("html")) //
                return true;
        }
        return false;
    }

    private void bodyToString(Request request) {
        try {
            Request copy = request.newBuilder().build();
            RequestBody body = copy.body();
            if (body == null) return;
            Buffer buffer = new Buffer();
            body.writeTo(buffer);
            Charset charset = getCharset(body.contentType());
            log("\tbody:" + buffer.readString(charset));
        } catch (Exception e) {
            Utils.logError(e, "bodyToString");
        }
    }
}
