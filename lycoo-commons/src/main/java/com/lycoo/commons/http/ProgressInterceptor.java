package com.lycoo.commons.http;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * xxx
 *
 * Created by lancy on 2017/12/19
 */
public class ProgressInterceptor implements Interceptor {

    private ProgressListener mProgressListener;

    public ProgressInterceptor(ProgressListener progressListener) {
        this.mProgressListener = progressListener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        return response
                .newBuilder()
                .body(new ProgressResponseBody(response.body(), mProgressListener))
                .build();
    }
}
